package com.accubits.reltime.activity.kyc


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.camera.CameraActivity
import com.accubits.reltime.activity.kyc.viewmodel.KYCViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityKycdetailsBinding
import com.accubits.reltime.utils.Extensions.getProgressDialog
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.openDatePicker
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "KYCDetailsActivity"

@AndroidEntryPoint
class KYCDetailsActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        var isVideoCaptured: Boolean = false
        var videoURI: Uri? = null
        var isDocumentSelected: Boolean = false
        var videoFileName: String? = "video.mp4"
    }

    lateinit var binder: ActivityKycdetailsBinding
    private val spinnerItemArray = arrayListOf(
        "Driving Licence",
        "Passport",
        "ID Card",
    )
    private val viewModel by viewModels<KYCViewModel>()
    private val permissionRequest = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    else
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    val PERMISSION_REQUEST_CODE = 1001
    private var isReadPermissionEnabled: Boolean = false
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraResultLauncher: ActivityResultLauncher<Intent>? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var filePickerIntent: Intent? = null
    private var cameraIntent: Intent? = null
    private var selectedFileUri: Uri? = null
    private var progress: ProgressDialog? = null
    private var fileName: String? = null
    var photoFile: File? = null
    var isRequestedForKycVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityKycdetailsBinding.inflate(layoutInflater)
        setContentView(binder.root)
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            spinnerItemArray
        )
        binder.layoutPrepareUploadVideo.tvUploadLabel.text = "Upload video"
        binder.layoutPrepareUploadDocument.tvUploadLabel.text = "Upload document"
        binder.spIddoc.adapter = adapter
        init()
        prepareRegisters()
        setListeners()
        checkPermissions()
        //showAfterUploadUI(true)
    }

    private fun init() {
        binder.tvDocTypeLabel.markRequiredInRed()
        binder.tvNameLabel.markRequiredInRed()
        binder.tvDocumentIdLabel.markRequiredInRed()
        binder.tvCityLabel.markRequiredInRed()
        binder.tvExpdateLabel.markRequiredInRed()
        binder.tvIssuedateLabel.markRequiredInRed()
        binder.tvUploadDocStar.markRequiredInRed()
        binder.tvUploadUploadStar.markRequiredInRed()
    }

    override fun onResume() {
        super.onResume()
        if (videoURI != null) {
            showAfterUploadUiVideo()
        }
    }

    private fun setListeners() {
        binder.spIddoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.kycDetails.value.documentType = spinnerItemArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        binder.layoutPrepareUploadDocument.llUploadContainer.setOnClickListener(this)
        binder.edDobInput.setOnClickListener(this)
        binder.edDocumentId.setOnClickListener(this)
        binder.edIssuedateInput.setOnClickListener(this)
        binder.edExpdateInput.setOnClickListener(this)
        binder.btSubmit.setOnClickListener(this)
        binder.layoutAfterUploadDocument.let {
            it.ivCloseUpload.setOnClickListener(this)
            it.tvReupload.setOnClickListener(this)
            it.btResubmit.setOnClickListener(this)
        }
        binder.layoutAfterUploadVideo.let {
            it.ivCloseUploadVideo.setOnClickListener(this)
            it.tvReupload.setOnClickListener(this)
            it.btResubmit.setOnClickListener(this)
        }
        binder.layoutPrepareUploadVideo.llUploadContainer.setOnClickListener(this)
        collectFlows()
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            viewModel.kycResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        progress?.cancel()
                        val responseData = response.data
                        openActivity(
                            KYCVerificationProgressActivity::class.java,
                            shouldFinish = true
                        )
                    }
                    ApiCallStatus.ERROR -> {
                        progress?.cancel()
                        showToast(
                            response.errorMessage ?: "Something went wrong while updating data"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ed_dob_input -> {
                openDatePicker {
                    binder.edDobInput.setText(it)
                    viewModel.kycDetails.value.dateOfBirth = it
                }
            }
            R.id.ed_issuedate_input -> {
                openDatePicker {
                    binder.edIssuedateInput.setText(it)
                    viewModel.kycDetails.value.issueDate = it
                }
            }

            R.id.ed_expdate_input -> {
                openDatePicker {
                    binder.edExpdateInput.setText(it)
                    viewModel.kycDetails.value.expiryDate = it
                }
            }
            R.id.layoutPrepareUploadDocument -> {
                if (!isReadPermissionEnabled) {
                    isRequestedForKycVideo = false
                    checkPermissions(showPopup = true)
                } else {
                    showDocumentBottomSheet()
                }
            }

            R.id.layoutPrepareUploadVideo -> {
                isVideoCaptured = false
                //  startActivity(Intent(this@KYCDetailsActivity,KYCVideoResultLauncherActivity::class.java))
                //KYCVideoResultLauncherActivity.kt
                if (!isReadPermissionEnabled) {
                    isRequestedForKycVideo = true
                    checkPermissions(showPopup = true)
                } else {
                    val intent = Intent(this@KYCDetailsActivity, CameraActivity::class.java)
                    resultLauncher?.launch(intent)
                }

            }

            binder.layoutAfterUploadDocument.ivCloseUpload.id -> {
                binder.layoutAfterUploadDocument.tvPickItemLabel.text = ""
                binder.layoutPrepareUploadDocument.llUploadContainer.visibility = View.VISIBLE
                binder.layoutAfterUploadDocument.llAfteruploadParent.visibility = View.GONE
                isDocumentSelected = false
            }

            binder.layoutAfterUploadVideo.ivCloseUploadVideo.id -> {
                binder.layoutAfterUploadVideo.tvPickItemLabel.text = ""
                binder.layoutPrepareUploadVideo.llUploadContainer.visibility = View.VISIBLE
                binder.layoutAfterUploadVideo.llAfteruploadParent.visibility = View.GONE
                videoURI = null; videoFileName = null
                isVideoCaptured = false
            }

            R.id.tvReupload -> {

            }

            R.id.btSubmit -> {
                viewModel.kycDetails.value.fullName = binder.edNameInput.text.toString().trim()
                viewModel.kycDetails.value.documentId = binder.edDocumentId.text.toString().trim()

                viewModel.validateFields { success, message ->
                    if (!success) {
                        showToast(message)
                    } else if (!isDocumentSelected) {
                        showToast("Please upload the document")
                    } else if (!isVideoCaptured) {
                        showToast("Please capture a video of yourself")
                    } else if (success && selectedFileUri != null
                        && fileName != null && videoURI != null
                    ) {
                        progress = getProgressDialog()
                        progress?.show()
                        prepareUpload()
                    }
                }
            }

            R.id.btResubmit -> {

            }
        }
    }

    private fun prepareUpload() {
        if (!isNetworkAvailable()) {
            showToast(getString(R.string.activity_network_not_available))
            return
        }
        val documentInputStream = contentResolver.openInputStream(selectedFileUri!!)
        val videoInputStream = contentResolver.openInputStream(videoURI!!)
        if (documentInputStream != null && videoInputStream != null) {
            viewModel.uploadKyc(
                documentFile = documentInputStream.readBytes(),
                videoFile = videoInputStream.readBytes(),
                documentFileName = fileName!!,
                videoFileName = videoFileName!!
            )
            videoInputStream.close()
            documentInputStream.close()
        } else {
            showToast("Couldn't parse the selected document/video. Please try again.")
        }
    }


    private fun showAfterUploadUiDocument(isError: Boolean = false) {
        binder.layoutPrepareUploadDocument.llUploadContainer.visibility = View.GONE
        binder.layoutAfterUploadDocument.apply {
            llAfteruploadParent.visibility = View.VISIBLE
            rlUploadItemName.visibility = View.VISIBLE
            tvPickItemLabel.text = fileName ?: "null"
            if (isError) {
                btResubmit.visibility = View.VISIBLE
                rlUploadItemName.background = resources.getDrawable(R.drawable.error, null)
                tvUploadError.visibility = View.VISIBLE
                tvReupload.visibility = View.VISIBLE
            } else {
                rlUploadItemName.background = resources.getDrawable(R.drawable.basic_corner, null)
                rlUploadItemName.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.card_color, null))
                btResubmit.visibility = View.GONE
                tvUploadError.visibility = View.GONE
                tvReupload.visibility = View.GONE
            }
        }
    }

    private fun showAfterUploadUiVideo(isError: Boolean = false) {
        binder.layoutPrepareUploadVideo.llUploadContainer.visibility = View.GONE
        binder.layoutAfterUploadVideo.apply {
            llAfteruploadParent.visibility = View.VISIBLE
            rlUploadItemName.visibility = View.VISIBLE
            tvPickItemLabel.text = videoFileName ?: "null"
            if (isError) {
                btResubmit.visibility = View.VISIBLE
                rlUploadItemName.background = resources.getDrawable(R.drawable.error, null)
                tvUploadError.visibility = View.VISIBLE
                tvReupload.visibility = View.VISIBLE
            } else {
                rlUploadItemName.background = resources.getDrawable(R.drawable.basic_corner, null)
                rlUploadItemName.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.card_color, null))
                btResubmit.visibility = View.GONE
                tvUploadError.visibility = View.GONE
                tvReupload.visibility = View.GONE
            }
        }
    }

    private fun prepareRegisters() {
        if (activityResultLauncher == null) {
            activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    isDocumentSelected = false
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        val intent = activityResult.data
                        selectedFileUri = intent?.data
                        selectedFileUri?.let {
                            fileName = getFileName(it)
                            isDocumentSelected = true
                            showAfterUploadUiDocument()
                        }
                    }
                }
        }
        if (cameraResultLauncher == null) {
            cameraResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    isDocumentSelected = false
                    if (activityResult.resultCode == Activity.RESULT_OK) {

                        selectedFileUri = Uri.fromFile(photoFile)
                        selectedFileUri?.let {
                            fileName = photoFile?.name
                            isDocumentSelected = true
                            showAfterUploadUiDocument()
                        }
                    }
                }
        }
        filePickerIntent = Intent.createChooser(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            val mimes = arrayOf("image/jpeg", "image/jpg", "image/png")
            //putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            putExtra(Intent.EXTRA_MIME_TYPES, mimes);
        }, "Select File")

        cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri("${System.currentTimeMillis()}.jpg")
        val fileProvider: Uri =
            FileProvider.getUriForFile(this, "com.accubits.reltime.fileprovider", photoFile!!)
        cameraIntent!!.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data

                videoURI = intent?.data
                videoURI?.let { returnUri ->
                    run {
                        contentResolver.query(returnUri, null, null, null, null)
                    }
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    val sizeInMb = cursor.getLong(sizeIndex) / 1024
                    isVideoCaptured = true
                    videoFileName = cursor.getString(nameIndex)
                }
                showToast("Video will be saved for KYC.")
            }
        }
    }

    fun showDocumentBottomSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.filepicker_bottom_sheet)
        val tvCamera = dialog.findViewById<TextView>(R.id.tvCopyAddress)
        val tvFilePicker = dialog.findViewById<TextView>(R.id.tvSendAddress)
        tvCamera.setOnClickListener {
            //copyToClipBoard("wallet", publicKey)
            cameraResultLauncher?.launch(cameraIntent)
            dialog.dismiss()
        }
        tvFilePicker.setOnClickListener {
            activityResultLauncher?.launch(filePickerIntent)
            dialog.dismiss()
        }
        dialog.apply {
            show()
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.BottomDialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }

    }

    private fun checkPermissions(showPopup: Boolean = false) {
        if (allPermissionsGranted()) {
            isReadPermissionEnabled = true
        } else {
            if (showPopup) {
                requestPermissions(
                    permissionRequest,
                    PERMISSION_REQUEST_CODE
                )
            }
        }

    }

    private fun allPermissionsGranted() = permissionRequest.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    isReadPermissionEnabled = true
                    if (isRequestedForKycVideo) {
                        val intent = Intent(this@KYCDetailsActivity, CameraActivity::class.java)
                        resultLauncher?.launch(intent)
                    } else {
                        showDocumentBottomSheet()
                    }

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    showToast(getString(R.string.kyc_readstorage_permission))
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        // Obtain a cursor with information regarding this uri
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            if (cursor.count <= 0) {
                cursor.close()
                throw IllegalArgumentException("Can't obtain file name, cursor is empty")
            }
            cursor.moveToFirst()
            val fileName =
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            cursor.close()
            return fileName
        } else {
            return "fileName.png"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isVideoCaptured = false
        videoURI = null
        isDocumentSelected = false
        videoFileName = null
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "APP_TAG")

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("APP_TAG", "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }
}