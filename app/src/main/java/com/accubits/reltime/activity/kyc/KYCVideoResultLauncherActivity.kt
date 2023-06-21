package com.accubits.reltime.activity.kyc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.accubits.reltime.R
import com.accubits.reltime.utils.Extensions.sdkBelow29
import com.accubits.reltime.utils.Extensions.showToast

private const val TAG = "KYCVideoResultLauncherA"

class KYCVideoResultLauncherActivity : AppCompatActivity() {

    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraIntent: Intent? = null
    private var selectedFileUri: Uri? = null
    private var permissionRequests =
        arrayOf(Manifest.permission.CAMERA)
    private val ALL_PERMISSIONS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            showToast("No camera found on this device")
            finish()
            return
        }
        prepareRegisters()

        // for api versions less than Q we need "write permission" to write the file to storage
        sdkBelow29 {
            permissionRequests = permissionRequests.plus(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasPermissions()) {
            requestPermissions(permissionRequests, ALL_PERMISSIONS);
        } else {
            launchCamera()
        }
    }

    private fun prepareRegisters() {
        if (activityResultLauncher == null) {
            activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        val intent = activityResult.data
                        selectedFileUri = intent?.data
                        selectedFileUri?.let { returnUri ->
                            run {
                                KYCDetailsActivity.videoURI = returnUri
                                contentResolver.query(returnUri, null, null, null, null)
                            }
                        }?.use { cursor ->
                            /*
                             * Get the column indexes of the data in the Cursor,
                             * move to the first row in the Cursor, get the data,
                             * and display it.
                             */
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                            cursor.moveToFirst()
                            val sizeInMb = cursor.getLong(sizeIndex) / 1024
                            /*if (sizeInMb > 50) {
                                showToast("You video should be less than 50 MB")
                            } else {

                            }
                            cursor.moveToFirst()
                            Log.e(TAG, "videoSize is: $sizeInMb MB ", )*/
                            KYCDetailsActivity.isVideoCaptured = true
                            KYCDetailsActivity.videoFileName = cursor.getString(nameIndex)
                        }
                    } else {
                        showToast("Some error occurred while capturing the video")
                    }
                    finish()
                }
        }
        cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        cameraIntent?.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15)
    }

    private fun launchCamera() {
        activityResultLauncher?.launch(cameraIntent)
    }

    private fun hasPermissions(): Boolean = permissionRequests.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ALL_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions()) {
                    showToast(getString(R.string.kycin_ask_permission_camera_storage))
                    finish()
                } else {
                    launchCamera()
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
    }
}