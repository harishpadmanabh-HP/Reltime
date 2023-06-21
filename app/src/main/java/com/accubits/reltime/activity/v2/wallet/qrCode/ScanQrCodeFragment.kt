package com.accubits.reltime.activity.v2.wallet.qrCode

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.FragmentScanQrCodeBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class ScanQrCodeFragment : Fragment() {

    private var _binding: FragmentScanQrCodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ScanQrCodeViewModel>()

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    private var isBarCodeDetected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        observeData()
    }

   /* override fun onPause() {
        super.onPause()
        cameraSource?.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
        isBarCodeDetected = false
    }
*/
    override fun onStart() {
        super.onStart()
        initialiseDetectorsAndSources()
        isBarCodeDetected = false
    }

    override fun onStop() {
        super.onStop()
        cameraSource?.release()
    }

    private fun clickListener() {
        binding.layoutPhoneNumber.setOnClickListener {
            activity?.openActivity(PhoneNumberActivity::class.java)
        }

        binding.layoutEmail.setOnClickListener {
            activity?.openActivity(EmailAddressActivity::class.java)
        }

        binding.layoutWalletAddress.setOnClickListener {
            activity?.openActivity(WalletAddressActivity::class.java)
        }

        binding.btnGallery.setOnClickListener {
            binding.btnGallery.post {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val pickIntent = Intent(Intent.ACTION_PICK)
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    launchGallery.launch(pickIntent)
                } else {
                    fileManagerPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    val fileManagerPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            launchGallery.launch(pickIntent)
        }
        else {
           requireActivity().showToast("Permission needed to continue")
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.walletValidateResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        if (response.data?.status == 200) {
                            requireActivity().openActivity(SendAmountActivity::class.java) {
                                putString(
                                    TransferObject.RECEIVER,
                                    response.data.result.walletAddress
                                )
                                putString(
                                    TransferObject.NAME,
                                    response.data.result.name
                                )
                                putString(
                                    TransferObject.USERID,
                                    response.data.result.userId
                                )
                                putString(
                                    TransferObject.PROFILE_IMAGE,
                                    response.data?.result?.profileImage
                                )

                                putString(
                                    TransferObject.CONTACT_TYPE,
                                    Utils.TransferContactType.WALLET.contactType
                                )
                                putString(
                                    TransferObject.CONTACT_TYPE,
                                    Utils.TransferContactType.WALLET.contactType
                                )
                            }
                        } else {
                            requireActivity().showToast(response.data?.message)

                        }

                    }

                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private var launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                if (intent.data == null) {
                    return@registerForActivityResult
                }
                val uri: Uri = intent.data!!
                try {
                    val inputStream: InputStream? =
                        requireActivity().getContentResolver().openInputStream(uri)
                    var bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap == null) {
                        return@registerForActivityResult
                    }
                    val width = bitmap!!.width
                    val height = bitmap.height
                    val pixels = IntArray(width * height)
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                    bitmap.recycle()
                    bitmap = null
                    val source = RGBLuminanceSource(width, height, pixels)
                    val bBitmap = BinaryBitmap(HybridBinarizer(source))
                    val reader = MultiFormatReader()
                    try {
                        val result: Result = reader.decode(bBitmap)
                        validateWalletAddress(result.getText())
                    } catch (e: NotFoundException) {
//                        Log.e("TAG", "decode exception", e)
                    }
                } catch (e: FileNotFoundException) {
//                    Log.e("TAG", "can not open file" + uri.toString(), e)
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initialiseDetectorsAndSources()
//            cameraSource?.start(binding.surfaceView!!.holder)
        }
        else {
            requireActivity().showToast("Permission needed to continue")
        }
    }

    private fun initialiseDetectorsAndSources() {
        barcodeDetector = BarcodeDetector.Builder(requireActivity())
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(requireActivity(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(binding.surfaceView.holder)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                        ActivityCompat.requestPermissions(
//                            requireActivity(),
//                            arrayOf(Manifest.permission.CAMERA),
//                            REQUEST_CAMERA_PERMISSION
//                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode?> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode?>) {
                if (!isBarCodeDetected) {
                    val barcodes: SparseArray<Barcode?> = detections.detectedItems
                    if (barcodes.size() != 0) {
                        validateWalletAddress(barcodes.valueAt(0)!!.displayValue)

                    }
                }
            }
        })
    }

    private fun validateWalletAddress(walletAddress: String) {
        isBarCodeDetected = true
        if (walletAddress != "") {
            viewModel.walletAddressValidation(
                walletAddress,
                viewModel.coinType
            )
        } else {
            requireActivity().showToast(getString(R.string.enter_wallet_addresss))
        }
    }
}