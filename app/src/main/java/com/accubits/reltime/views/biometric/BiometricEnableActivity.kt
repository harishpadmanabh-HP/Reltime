package com.accubits.reltime.views.biometric

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityBiometricEnableBinding
import com.accubits.reltime.helpers.BiometricPromptUtils
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.BioMetricRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BiometricEnableActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var biometricPromptUtils: BiometricPromptUtils
    private val viewModel by viewModels<BiometricViewModel>()
    private lateinit var activityBiometricEnableBinding: ActivityBiometricEnableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBiometricEnableBinding = ActivityBiometricEnableBinding.inflate(layoutInflater)
        setContentView(activityBiometricEnableBinding.root)

        activityBiometricEnableBinding.btEnable.setOnClickListener {
            val createBiometricPrompt = biometricPromptUtils.createBiometricPrompt(this)
            val createPromptInfo = biometricPromptUtils.createPromptInfo(this)
            createBiometricPrompt.authenticate(createPromptInfo)

        }
        activityBiometricEnableBinding.btnNot.setOnClickListener {
            finish()
        }

        biometricPromptUtils.setOnItemClickListener {
            setDataToServer()
        }
    }

    private fun setDataToServer() {
        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.doEnableOrDisableBiometric(
                preferenceManager.getApiKey(),
                BioMetricRequest(true)
            )
            updateUi()
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.createMpinSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityBiometricEnableBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityBiometricEnableBinding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            preferenceManager.setBiometric(true)
                            Toast.makeText(
                                this@BiometricEnableActivity,
                                "Biometric enabled",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@BiometricEnableActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityBiometricEnableBinding.progressBar.visibility = View.GONE
                        /*Toast.makeText(
                            this@BiometricEnableActivity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }
}