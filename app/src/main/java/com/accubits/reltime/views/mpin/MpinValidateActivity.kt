package com.accubits.reltime.views.mpin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityMpinValidateBinding
import com.accubits.reltime.helpers.BiometricPromptUtils
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.MpinValidateRequestModel
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.biometric.BiometricEnableActivity
import com.accubits.reltime.views.forgot.ForgotPasswordActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MpinValidateActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var activityMpinConfirmBinding: ActivityMpinValidateBinding

    @Inject
    lateinit var biometricPromptUtils: BiometricPromptUtils
    private var activityName: String = ""
    private val viewModel by viewModels<MpinValidateViewModel>()

    companion object{
        const val EXTRA_SERIALIZABLE_ITEM="item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMpinConfirmBinding = ActivityMpinValidateBinding.inflate(layoutInflater)
        setContentView(activityMpinConfirmBinding.root)
        activityMpinConfirmBinding.btVerify.setOnClickListener {
            if (!preferenceManager.getMpin()) {
                showToast(resources.getString(R.string.transaction_pin_not_set_error))
                return@setOnClickListener
            }
            if (activityMpinConfirmBinding.pinView.value
                    .trim() != ""
            ) {
                val mpin =
                    activityMpinConfirmBinding.pinView.value
                viewModel.perfomMpinValidation(
                    preferenceManager.getApiKey(),
                    MpinValidateRequestModel(mpin)
                )
                updateUi()
            } else
                showToast(resources.getString(R.string.please_enter_pin))
        }
        activityMpinConfirmBinding.tvBiometric.setOnClickListener {
            if (!preferenceManager.getBiometric()) {
                val intent = Intent(this, BiometricEnableActivity::class.java)
                startActivity(intent)
            } else {
                val createBiometricPrompt = biometricPromptUtils.createBiometricPrompt(this)
                val createPromptInfo = biometricPromptUtils.createPromptInfo(this)
                createBiometricPrompt.authenticate(createPromptInfo)
            }
        }
        biometricPromptUtils.setOnBioSuccess {
            checkActivityComingFrom()
        }
        activityMpinConfirmBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        activityMpinConfirmBinding.textView63.setOnClickListener {
            openActivity(ForgotPasswordActivity::class.java){
                this.putBoolean(ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,false)
            }
        }
    }

    private fun checkActivityComingFrom() {
        activityName = intent.getStringExtra(ReltimeConstants.ACTIVITY).toString()
        if (intent.getBooleanExtra(ReltimeConstants.RETURN_VALUE,false)) {
            val intents = Intent()
            intents.putExtra(
                ReltimeConstants.AMOUNT,
                intent.getStringExtra(ReltimeConstants.AMOUNT)
            )
            intent.getSerializableExtra(EXTRA_SERIALIZABLE_ITEM)?.let {
                intents.putExtra(EXTRA_SERIALIZABLE_ITEM,it)
            }
            setResult(RESULT_OK, intents)
            finish()
        }

    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.createMpinSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMpinConfirmBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMpinConfirmBinding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            Toast.makeText(
                                this@MpinValidateActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                            checkActivityComingFrom()
                        } else showToast(response.data.message)
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage)
                    }
                    else -> {}
                }
            }
        }
    }
}