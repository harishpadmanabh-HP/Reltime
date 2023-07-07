package com.accubits.reltime.activity.v2.splash

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityMainBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.openPlayStore
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.cryptoutils.BitcoinUtils
import com.accubits.reltime.views.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionRequest =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    @Inject
    lateinit var bitcoinUtils: BitcoinUtils

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<AppInitViewModel>()

    private var isTimerFinished = false
    private var isAPILoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            isTimerFinished = true
            navigateTo()
        }, 2000)

        getInitData()
        observe()
    }

    private fun getInitData() {
        if (Utils.isNetworkAvailable(this) == true)
            viewModel.getInitData()
        else showToast(getString(R.string.please_check_net))
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.initDataResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success && response.data.result != null) {
                            preferenceManager.setPrivacyPolicy("https://www.nagra.com/privacy-notice")
                            preferenceManager.setTermsAndConditions("https://www.nagra.com/terms-use")
                            if (BuildConfig.VERSION_CODE < response.data.result.android_version.immediate.toLong()) {
                                //TODO immediate update
                                showDialog(
                                    isImmediateUpdate = true,
                                    appPackage = response.data.result.android_version.app_package,
                                    updateAction = response.data.result.android_version.update_action,
                                    title = response.data.result.android_version.immediate_title,
                                    description = response.data.result.android_version.immediate_description
                                )
                            } else if (BuildConfig.VERSION_CODE < response.data.result.android_version.flexible.toLong()) {
                                //TODO flexible
                                showDialog(
                                    isImmediateUpdate = false,
                                    appPackage = response.data.result.android_version.app_package,
                                    updateAction = response.data.result.android_version.update_action,
                                    title = null,
                                    description = null
                                )
                            } else {
                                isAPILoaded = true
                                navigateTo()
                            }
                        } else
                            response.data?.message?.let { showToast(it) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateTo() {
        if (!isAPILoaded || !isTimerFinished)
            return
        when {
            !allPermissionsGranted() -> {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                val intent = Intent(this, WelcomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = permissionRequest.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun showDialog(
        isImmediateUpdate: Boolean, appPackage: String?, updateAction: String?,
        title: String?, description: String?
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_update)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setDimAmount(.3f)

        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val tvDesc = dialog.findViewById(R.id.tvDesc) as TextView
        val tvNoThanks = dialog.findViewById(R.id.tvNoThanks) as TextView
        val tvUpdate = dialog.findViewById(R.id.tvUpdate) as TextView

        title?.let { tvTitle.text = it }
        description?.let { tvDesc.text = it }
        tvNoThanks.visibility = if (isImmediateUpdate) View.GONE else View.VISIBLE
        updateAction?.let { tvUpdate.text = it }
        tvNoThanks.setOnClickListener {
            dialog.dismiss()
        }
        tvUpdate.setOnClickListener {
             openPlayStore(appPackage)
        }


        dialog.setOnDismissListener {
            if (isImmediateUpdate)
                finish()
            else {
                isAPILoaded = true
                navigateTo()
            }
        }
        dialog.show()

    }

}