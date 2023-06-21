package com.accubits.reltime.activity.v2.wallet.qrCode

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.databinding.FragmentQrCodeV2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.showToast
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


@AndroidEntryPoint
class QRCodeFragmentV2 : Fragment() {

    private var _binding: FragmentQrCodeV2Binding? = null
    private val binding get() = _binding!!
    private lateinit var bitmap: Bitmap

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var qrCodeValue: String
    private lateinit var phoneNumber: String

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10

        var PERMISSIONS = arrayOf(
            // Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeV2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setListeners()
    }

    private fun init() {
        qrCodeValue = preferenceManager.getPublicKeyFromLogin()
        phoneNumber = preferenceManager.getPhone()
        binding.tvWalletId.text = qrCodeValue
        binding.tvPhoneNumber.text = phoneNumber
        val manager =
            requireActivity().getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
        val display: Display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4
        val qrgEncoder = QRGEncoder(
            qrCodeValue,
            null,
            QRGContents.Type.TEXT,
            smallerDimension
        )
        qrgEncoder.colorBlack = ContextCompat.getColor(requireActivity(), R.color.black)
        qrgEncoder.colorWhite = ContextCompat.getColor(requireActivity(), R.color.whitepure)

        try {
            // Getting QR-Code as Bitmap
            val generatedBitmap = qrgEncoder.bitmap
            // Setting Bitmap to ImageView
            val yourLogo = BitmapFactory.decodeResource(resources, R.drawable.nagra_round_small)

            bitmap = mergeBitmaps(yourLogo, generatedBitmap)!!
            binding.ivQrCode.setImageBitmap(bitmap)
//            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun mergeBitmaps(logo: Bitmap?, qrcode: Bitmap): Bitmap? {
        val combined = Bitmap.createBitmap(qrcode.width, qrcode.height, qrcode.config)
        val canvas = Canvas(combined)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        canvas.drawBitmap(qrcode, Matrix(), null)
        val resizeLogo = Bitmap.createScaledBitmap(logo!!, canvasWidth / 7, canvasHeight / 7, true)
        val centreX: Float = ((canvasWidth - resizeLogo.width) / 2).toFloat()
        val centreY: Float = ((canvasHeight - resizeLogo.height) / 2).toFloat()
        canvas.drawBitmap(resizeLogo, centreX, centreY, null)
        return combined
    }

    private fun setListeners() {
        binding.btnDownload.setOnClickListener {
            /* if (!checkPermission()) {
                 onBtnSavePng()
             } else {
                 if (checkPermission()) {
                     requestPermissionAndContinue()
                 } else {
                     onBtnSavePng()
                 }
             }*/
            saveQRCode()
        }
        binding.btnShare.setOnClickListener {
            val imageFile = Utils.storeFile(requireActivity(), bitmap)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND

            val uri = imageFile?.let { it1 ->
                FileProvider.getUriForFile(
                    requireActivity().getApplicationContext(),
                    requireActivity().getPackageName() + ".fileprovider",
                    it1
                )
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(
                Intent.EXTRA_TEXT,
                resources.getString(
                    R.string.n_n,
                    binding.tvWalletIdTitle.text.toString(),
                    qrCodeValue
                )
            )
            intent.setType("image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        binding.ivCopyWalletId.setOnClickListener {
            requireActivity().copyToClipBoard(
                resources.getString(R.string.wallet_id_title),
                qrCodeValue
            )
        }
        binding.ivCopyPhoneNumber.setOnClickListener {
            requireActivity().copyToClipBoard(
                resources.getString(R.string.phone_number_),
                binding.tvPhoneNumber.text.toString()
            )
        }
    }


    fun onBtnSavePng() {
        try {
            val filename = "${System.currentTimeMillis()}.jpg"

            //Output stream
            var fos: OutputStream? = null

            //For devices running android >= Q
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //getting the contentResolver
                requireActivity().contentResolver?.also { resolver ->

                    //Content resolver will process the contentvalues
                    val contentValues = ContentValues().apply {

                        //putting file information in content values
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    }

                    //Inserting the contentValues to contentResolver and getting the Uri
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    //Opening an outputstream with the Uri that we got
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                //These for devices running on android < Q
                //So I don't think an explanation is needed here
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                MediaScannerConnection.scanFile(
                    requireActivity(),
                    arrayOf(imagesDir.path + "/" + filename),
                    arrayOf("image/jpeg"),
                    null
                )
            }
            fos?.use {
                //Finally writing the bitmap to the output stream that we opened
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                requireActivity().showToast(resources.getString(R.string.qr_code_downloaded))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value) {
                    onBtnSavePng()
                    return@forEach
                }
            }
        }

    private fun saveQRCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onBtnSavePng()
            return
        }
        activity?.let {
            if (Utils.hasPermissions(activity as Context, PERMISSIONS)) {
                onBtnSavePng()
            } else {
                permReqLauncher.launch(
                    PERMISSIONS
                )
            }
        }
    }

}