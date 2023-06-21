package com.accubits.reltime.views.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.databinding.ActivityPermissionBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.privacy.PrivacyActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private val permissionRequest =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    companion object {
        const val PERMISSION_REQUEST_CODE = 1011

    }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.btLogin.setOnClickListener {
            checkPermissions()
        }
        binding.tvPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyActivity::class.java)
            intent.putExtra(PrivacyActivity.URL,PrivacyActivity.PRIVACY_POLICY)
            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        if (allPermissionsGranted()) {
            checkRedirections()
        } else {
            requestPermissions(
                permissionRequest,
                PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun allPermissionsGranted() = permissionRequest.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkRedirections() {
        val intent = Intent(this, WelcomeScreen::class.java)
        startActivity(intent)
        finish()
        /*   when {
             preferenceManager.getOnBoardingCompleted() != "" -> {
                  val intent = Intent(this, LoginActivity::class.java)
                  startActivity(intent)
                  finish()
              }
            else -> {
                val intent = Intent(this, WelcomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                checkRedirections()
                return
            }
            else -> {

            }
        }
    }
}