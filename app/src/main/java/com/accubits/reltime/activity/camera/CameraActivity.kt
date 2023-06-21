package com.accubits.reltime.activity.camera

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.os.Build.VERSION
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.camera.facedetector.BitmapUtils
import com.accubits.reltime.activity.camera.facedetector.FaceDetectorProcessor
import com.accubits.reltime.activity.camera.facedetector.VisionImageProcessor
import com.accubits.reltime.databinding.ActivityCameraBinding
import com.accubits.reltime.helpers.getNameString
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity(), FacePositionDetection {
    private lateinit var viewBinding: ActivityCameraBinding

    private val TAG = "CameraActivity"
    private var quality = Quality.HD
    private var videoCapture: VideoCapture<Recorder>? = null
    private var currentRecording: Recording? = null
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private lateinit var recordingState: VideoRecordEvent
    private val captureLiveStatus = MutableLiveData<String>()
    private var enumerationDeferred: Deferred<Unit>? = null
    private lateinit var countDownTimer: CountDownTimer
    private var imageProcessor: VisionImageProcessor? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    var cameraProvider: ProcessCameraProvider? = null
    var isRecordingStarted = false
    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    var finalSavedVideoURI: Uri? = null
    var instructionDialog : InstructionFragment?=null
    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
        RECOVERY    // For future use.
    }

    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
        enableUI(true)

        instructionDialog = InstructionFragment.newInstance()
        instructionDialog?.show(supportFragmentManager, InstructionFragment::class.java.name)
        //  viewBinding.ivGIF.visibility=View.VISIBLE
        countDownTimer = object : CountDownTimer(15000, 1000) {

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                showTimeOutBottomSheet()
            }
        }
        viewBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        viewBinding.captureButton.apply {
            setOnClickListener {
                if (!this@CameraActivity::recordingState.isInitialized ||
                    recordingState is VideoRecordEvent.Finalize
                ) {
                    startRecording()
                } else {
                    when (recordingState) {
                        is VideoRecordEvent.Start -> {
                            currentRecording?.pause()
                            viewBinding.stopButton.visibility = View.VISIBLE
                        }
                        is VideoRecordEvent.Pause -> currentRecording?.resume()
                        is VideoRecordEvent.Resume -> currentRecording?.pause()
                        else -> throw IllegalStateException("recordingState in unknown state")
                    }
                }
            }
        }
        viewBinding.stopButton.apply {
            setOnClickListener {
                // stopping: hide it after getting a click before we go to viewing fragment
                if (finalSavedVideoURI == null) {
                    setResult(RESULT_CANCELED)
                } else {
                    if (leftFaceDetected && rightFaceDetected && straightFaceDetected) {
                        setResult(RESULT_OK, Intent().setData(finalSavedVideoURI))
                    } else {
                        setResult(RESULT_CANCELED)
                    }

                }
                finish()
                // viewBinding.captureButton.setImageResource(R.drawable.ic_start)
            }
            // ensure the stop button is initialized disabled & invisible
        }

        captureLiveStatus.value = getString(com.accubits.reltime.R.string.Idle)
        if (allPermissionsGranted()) {
            lifecycleScope.launch {
                if (enumerationDeferred != null) {
                    enumerationDeferred!!.await()
                    enumerationDeferred = null
                }
            }

            ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[CameraXViewModel::class.java]
                .processCameraProvider
                .observe(this, { provider ->
                    cameraProvider = provider
                    startCamera()
                    analysisUseCase()
                })

        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun stopRecording() {
        if (!isRecordingStarted)
            return
        isRecordingStarted = false
        if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
            return
        }

        val recording = currentRecording
        if (recording != null) {
            recording.stop()
            currentRecording = null
        }
        countDownTimer.cancel()
    }

    private fun enableUI(enable: Boolean) {
        arrayOf(
            viewBinding.captureButton,
            viewBinding.stopButton,
        ).forEach {
            it.isEnabled = enable
        }
    }

    private fun startCamera() {

        if (cameraProvider == null) {
            return
        }
        if (videoCapture != null) {
            cameraProvider!!.unbind(videoCapture)
        }

        // Used to bind the lifecycle of cameras to the lifecycle owner

        val qualitySelector = QualitySelector.from(Quality.LOWEST)

        // Select back camera as a default

        /*   viewBinding.previewView.updateLayoutParams<ConstraintLayout.LayoutParams> {
               val orientation = resources.configuration.orientation
               dimensionRatio = quality.getAspectRatioString(
                   quality,
                   (orientation == Configuration.ORIENTATION_PORTRAIT)
               )
           }
           // Preview
           val previewBuilder = Preview.Builder()
           previewBuilder.setTargetResolution(android.util.Size.parseSize("640x480"))
           val preview = previewBuilder.build().also {
               it.setSurfaceProvider(viewBinding.previewView.surfaceProvider)
           }*/


        Handler().postDelayed({
            val recorder = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            try {
                //   cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    videoCapture
                )
                // Unbind use cases before rebinding


            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, 2000)

    }

    @ExperimentalGetImage
    private fun analysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }

        val optionsBuilder = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setMinFaceSize(0.1f)
        optionsBuilder.enableTracking()

        val faceDetectorOptions = optionsBuilder.build()
        imageProcessor = FaceDetectorProcessor(this, faceDetectorOptions, this)

        val builder = ImageAnalysis.Builder()
        builder.setTargetResolution(android.util.Size.parseSize("640x480"))

        analysisUseCase = builder.build()

        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {

                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        viewBinding.graphicOverlay.setImageSourceInfo(
                            imageProxy.width,
                            imageProxy.height,
                            true
                        )
                    } else {

                        viewBinding.graphicOverlay.setImageSourceInfo(
                            imageProxy.height,
                            imageProxy.width,
                            true
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    viewBinding.imagePreview.setImageBitmap(BitmapUtils.getBitmap(imageProxy))
                    imageProcessor!!.processImageProxy(imageProxy, viewBinding.graphicOverlay)
                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

        try {
            //  cameraProvider?.unbind(analysisUseCase)
            cameraProvider?.bindToLifecycle(
                this,
                cameraSelector,
                analysisUseCase
            )
            // Unbind use cases before rebinding


        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun startRecording() {
        isRecordingStarted = true
        //     viewBinding.ivGIF.visibility=View.GONE
        viewBinding.tvMoveFace.visibility = View.VISIBLE
        viewBinding.tvMoveFace.text = "Turn Straight"
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "kyc-video-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture?.output
            ?.prepareRecording(this, mediaStoreOutput)
            // .apply { if (audioEnabled) withAudioEnabled() }
            ?.start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }

    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            Log.d(TAG, "event.outputResults.outputUri: ${event.outputResults.outputUri}")
            finalSavedVideoURI = event.outputResults.outputUri

        }
    }

    private fun updateUI(event: VideoRecordEvent) {
        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString()
        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> {
                showUI(UiState.RECORDING, event.getNameString())
            }
            is VideoRecordEvent.Finalize -> {
                showUI(UiState.FINALIZED, event.getNameString())
            }
            is VideoRecordEvent.Pause -> {
                //  captureViewBinding.captureButton.setImageResource(R.drawable.ic_resume)
            }
            is VideoRecordEvent.Resume -> {
                //     captureViewBinding.captureButton.setImageResource(R.drawable.ic_pause)
            }
        }

        val stats = event.recordingStats
        val size = stats.numBytesRecorded / 1000
        val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
        var text = "${state}: recorded ${size}KB, in ${time}second"
        if (event is VideoRecordEvent.Finalize)
            text = "${text}\nFile saved to: ${event.outputResults.outputUri}"

        captureLiveStatus.value = text
        Log.i(TAG, "recording event: $text")
    }

    private fun showUI(state: UiState, status: String = "idle") {
        viewBinding.let {
            when (state) {
                UiState.IDLE -> {
                    // it.captureButton.setImageResource(com.accubits.reltime.R.drawable.ic_start)
                    // it.stopButton.visibility = View.GONE

                }
                UiState.RECORDING -> {

                    //   it.captureButton.setImageResource(R.drawable.ic_pause)
                    it.captureButton.isEnabled = false
                    it.stopButton.visibility = View.GONE
                    it.captureButton.visibility = View.GONE
                    countDownTimer.start()
                }
                UiState.FINALIZED -> {

                    //  it.captureButton.setImageResource(com.accubits.reltime.R.drawable.ic_start)asd
                    it.stopButton.visibility = View.VISIBLE
                    it.stopButton.alpha=1f
                    it.ivPose.visibility = View.GONE
                    it.tvMoveFace.text = "Tap on save to continue."
                }
                else -> {
                    val errorMsg = "Error: showUI($state) is not supported"
                    Log.e(TAG, errorMsg)
                    return
                }
            }
        }
    }

    @ExperimentalGetImage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                lifecycleScope.launch {
                    if (enumerationDeferred != null) {
                        enumerationDeferred!!.await()
                        enumerationDeferred = null
                    }
                    // initializeQualitySectionsUI()
                    startCamera()
                    analysisUseCase()

                }

            } else {
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            if (VERSION.SDK_INT < Build.VERSION_CODES.Q)
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
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    var leftFaceDetected = false
    var rightFaceDetected = false
    var straightFaceDetected = false
    override fun leftFaceDetected() {
        Log.e(TAG, "leftFaceDetected:${leftFaceDetected} isRecordingStarted: $isRecordingStarted")
        if (!leftFaceDetected && isRecordingStarted && finalSavedVideoURI == null) {
            leftFaceDetected = true

        }
        allSideDetectedStopRecording()
    }

    fun allSideDetectedStopRecording() {
        if (leftFaceDetected && rightFaceDetected && straightFaceDetected && finalSavedVideoURI == null) {
            viewBinding.tvMoveFace.text = "Saving video for KYC."
            Handler().postDelayed({
                stopRecording()
            }, 2000)

        }
    }

    override fun rightFaceDetected() {

        Log.e(TAG, "rightFaceDetected: $rightFaceDetected isRecordingStarted: $isRecordingStarted")
        if (!rightFaceDetected && isRecordingStarted && finalSavedVideoURI == null) {
            rightFaceDetected = true
            //   Toast.makeText(this,"Turn left detected",Toast.LENGTH_SHORT).show()
            //  Toast.makeText(this,"Turn right detected",Toast.LENGTH_SHORT).show()
            viewBinding.tvMoveFace.text = "Please Turn Right"
            viewBinding.ivPose.setImageDrawable(resources.getDrawable(R.drawable.ic_turn_right))

        }
        allSideDetectedStopRecording()
    }

    override fun straightFaceDetected() {
        Log.e(
            TAG,
            "straightFaceDetected: $rightFaceDetected isRecordingStarted: $isRecordingStarted"
        )

        if (!straightFaceDetected && isRecordingStarted && finalSavedVideoURI == null) {
            straightFaceDetected = true
            //  Toast.makeText(this,"Straight face detected",Toast.LENGTH_SHORT).show()
            viewBinding.tvMoveFace.text = " Face Detected"
            Handler().postDelayed({
                viewBinding.tvMoveFace.text = "Please Turn Left"
                viewBinding.ivPose.setImageDrawable(resources.getDrawable(R.drawable.ic_turn_left))
            },2000)

        }
        allSideDetectedStopRecording()
    }

    override fun onBackPressed() {
        showDocumentBottomSheet()
    }

    fun showDocumentBottomSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_back_camera)
        val tvCamera = dialog.findViewById<TextView>(R.id.tvCopyAddress)
        val tvFilePicker = dialog.findViewById<TextView>(R.id.tvSendAddress)
        dialog.setCancelable(false)
        tvCamera.setOnClickListener {
            if (currentRecording == null) {
             finish()
            } else {
                countDownTimer.cancel()
                stopRecordingAndFinishActivity()
            }
            dialog.dismiss()
        }
        tvFilePicker.setOnClickListener {
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

    fun stopRecordingAndFinishActivity() {
        isRecordingStarted = false
        countDownTimer.cancel()
        if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
            return
        }

        val recording = currentRecording
        if (recording != null) {
            recording.stop()
            currentRecording = null
        }
        finish()
    }

    fun showTimeOutBottomSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_timeup_camera)
        val tvCamera = dialog.findViewById<TextView>(R.id.tvSendAddress)
        dialog.setCancelable(false)
        tvCamera.setOnClickListener {
            stopRecordingAndFinishActivity()
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

    override fun onStop() {
        currentRecording?.stop()
        countDownTimer.cancel()
        cameraProvider?.unbindAll()
        cameraProvider = null
        super.onStop()

    }

}