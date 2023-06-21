package com.accubits.reltime.activity.kyc

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityKycvideoCaptureBinding
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "KYCVideoCaptureActivity"

@AndroidEntryPoint
class KYCVideoCaptureActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binder: ActivityKycvideoCaptureBinding
    private val permissionRequests =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val PERMISSION_REQUEST_CODE = 1001
    private var videoCapture2: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var timer: CountDownTimer? = null
    private var timeLength: Long = 15000
    private var isRecording: Boolean = false
    private var mediaStoreOutputOptions: MediaStoreOutputOptions? = null
    private var contentValues: ContentValues? = null

    val qualitySelector = QualitySelector.fromOrderedList(
        listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityKycvideoCaptureBinding.inflate(layoutInflater)
        setContentView(binder.root)
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            showToast("No camera found on this device")
            return
        }
        // checkPermissions()
        setupListeners()
        binder.apply {
            ivDeleteRecord.visibility = View.INVISIBLE
            ivSelectVideo.visibility = View.INVISIBLE
            ivStartRecord.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        deleteRecording()
    }

    private fun setupListeners() {
        binder.apply {
            ivStartRecord.setOnClickListener(this@KYCVideoCaptureActivity)
            ivDeleteRecord.setOnClickListener(this@KYCVideoCaptureActivity)
            ivSelectVideo.setOnClickListener(this@KYCVideoCaptureActivity)
        }
    }

    // Implements VideoCapture use case, including start and stop capturing.
    @SuppressLint("RestrictedApi", "CheckResult")
    private fun captureVideo() {

        this.videoCapture2 ?: return

//        binder.ivStartRecord.isEnabled = false

        contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "kyc_verification")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Reltime")
            }
        }
        mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues!!)
            .build()
        /* val param = VideoCapture.OutputFileOptions.Builder(
         mediaStoreOutputOptions.contentResolver, mediaStoreOutputOptions.collectionUri, mediaStoreOutputOptions.contentValues
     ).build()*/
        if (recording == null) {
            recording = videoCapture2!!.output.prepareRecording(this, mediaStoreOutputOptions!!)
                .start(ContextCompat.getMainExecutor(this)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {

                        }
                        is VideoRecordEvent.Finalize -> {
                            if (event.hasError()) {
                                showToast("Some error occured!")
                                recording = null
                            } else {
                                showToast("Recording finished")
                                videoCapture2?.camera?.close()
                                binder.apply {
                                    ivDeleteRecord.visibility = View.VISIBLE
                                    ivSelectVideo.visibility = View.VISIBLE
                                    ivStartRecord.visibility = View.INVISIBLE
                                }
                            }
                        }
                        else -> {
                            recording = null
                        }
                    }
                }
        }
        isRecording = true
        timer?.start()
    }

    private fun deleteVideoFile() {
        mediaStoreOutputOptions?.apply {
            val cursor: Cursor? =
                contentResolver.query(collectionUri, null, null, null, null)
            if (cursor?.count != 0) {
                contentResolver.delete(collectionUri, null, null)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binder.previewView.surfaceProvider)
                }

            // Select front camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
//                videoCaptureBuilder = VideoCapture.Builder().setVideoFrameRate(30).build()
                val recorder = Recorder.Builder().setQualitySelector(qualitySelector)
                    .setExecutor(ContextCompat.getMainExecutor(this)).build()
                videoCapture2 = VideoCapture.withOutput(recorder)
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture2
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupTimer() {
        binder.tvTimerText.text = "00:00"
        timeLength = 15000L
        timer = object : CountDownTimer(timeLength, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                timeLength -= 1000L
                val seconds = (timeLength / 1000)
                binder.tvTimerText.text = "00:${if (seconds < 10) "0$seconds" else "$seconds"}"
            }

            override fun onFinish() {
                binder.tvTimerText.text = "00:00"
                timeLength = 15000
                recording?.stop()
                isRecording = false
            }
        }
        timer?.start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivStartRecord -> {
                if (!isRecording) {
                    setupTimer()
                    captureVideo()
                    binder.apply {
                        ivStartRecord.visibility = View.INVISIBLE
                        ivDeleteRecord.visibility = View.VISIBLE
                        ivSelectVideo.visibility = View.INVISIBLE
                    }
                }
            }

            R.id.ivDeleteRecord -> {
                deleteRecording()
            }

            R.id.ivSelectVideo -> {
                processRecordedVideo()
            }
        }
    }

    private fun deleteRecording() {
        if (isRecording) {
            timer?.onFinish()
            timer?.cancel()
            recording?.stop()
            recording?.close()
            isRecording = false
            recording = null
            deleteVideoFile()
        }
        binder.apply {
            ivStartRecord.visibility = View.VISIBLE
            ivDeleteRecord.visibility = View.INVISIBLE
            ivSelectVideo.visibility = View.INVISIBLE
        }
    }


    private fun processRecordedVideo() {

    }


    private fun checkPermissions() {
        var askPermissions = false
        permissionRequests.forEach {
            if (ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askPermissions = true
            }
        }
        if (askPermissions) {
            requestPermissions(
                permissionRequests,
                PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
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
                var permissionNotEnabled = false
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        permissionNotEnabled = true
                    }
                }
                if (permissionNotEnabled) {
                    showToast(getString(R.string.kycin_ask_permission_camera_storage))
                } else {
                    startCamera()
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

    override fun onDestroy() {
        super.onDestroy()
        recording?.stop()
    }
}