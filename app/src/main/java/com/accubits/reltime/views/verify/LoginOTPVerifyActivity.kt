package com.accubits.reltime.views.verify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityLoginotpverifyBinding
import com.accubits.reltime.helpers.IPreferenceHelper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.helpers.Utils.OTP_MAX_TIME
import com.accubits.reltime.models.ForgotRequestModel
import com.accubits.reltime.models.VerifyRequestEmail
import com.accubits.reltime.utils.Extensions.getDeviceId
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.forgot.ForgotPasswordActivity
import com.accubits.reltime.views.forgot.ForgotViewModel
import com.accubits.reltime.views.mpin.MPINCreateActivity
import com.accubits.reltime.views.reset.ResetPasswordActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginOTPVerifyActivity : AppCompatActivity() {
    private lateinit var activityLoginBinding: ActivityLoginotpverifyBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    private val forgotViewModel by viewModels<ForgotViewModel>()
    private val isForgotPassword by lazy {
        intent.getBooleanExtra(
            ForgotPasswordActivity.EXTRA_IS_FORGOT_PASSWORD,
            false
        )
    }
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private var countDownTimer = object : CountDownTimer(OTP_MAX_TIME, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val minutes =
                millisUntilFinished / ReltimeConstants.oneSecondInMillis / ReltimeConstants.seconds
            val seconds =
                (millisUntilFinished / ReltimeConstants.oneSecondInMillis % ReltimeConstants.seconds).toString()
            val timerText =
                "$minutes:${seconds.let { if (it.toLong() < ReltimeConstants.TEN) "0$it" else it }}"
            activityLoginBinding.tvTimer.text =
                resources.getString(R.string.your_code_expires_in_n_minute_s, timerText)
        }

        override fun onFinish() {
            activityLoginBinding.tvTimer.text = "Expired!"
            activityLoginBinding.tvResend.visibility = View.VISIBLE
            activityLoginBinding.btVerify.setButtonEnable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginotpverifyBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        val stringExtra = intent.getStringExtra("medium")
        if (stringExtra == "email") {
            activityLoginBinding.tvSent.text =
                getString(R.string.enter_the_otp_we_just_sent_you_on_your_email_address)
        } else {
            activityLoginBinding.tvSent.text =
                getString(R.string.enter_the_otp_we_just_sent_you_on_your_phone)
        }
        activityLoginBinding.btVerify.setOnClickListener {
            submitOtp()
        }
        observeLoginData()
        startTimer()

        activityLoginBinding.tvResend.setOnClickListener {
            dorendOtp()
        }
        activityLoginBinding.ivBack.setOnClickListener { finish() }
    }

    private fun startTimer() {
        activityLoginBinding.tvResend.visibility = View.GONE
        activityLoginBinding.btVerify.setButtonEnable(true)
        countDownTimer.start()
    }

    private fun dorendOtp() {
        if (Utils.isNetworkAvailable(this)!!) {
            activityLoginBinding.progressBar.visibility = View.VISIBLE
            val forgotRequestModel =
                ForgotRequestModel(intent.getStringExtra("emailorphone").toString().trim())
            forgotViewModel.otpGenerate(token = if (isForgotPassword) null else preferenceManager.getApiKey().ifEmpty { null }, deviceId = getDeviceId(), forgotRequestModel = forgotRequestModel)
        } else showToast(getString(R.string.please_check_net))
    }

    fun submitOtp() {
        val otp = activityLoginBinding.pinView.value
        if (otp.isNotEmpty()) {
            if (Utils.isNetworkAvailable(this)!!) {
                val otpverify =
                    VerifyRequestEmail(intent.getStringExtra("emailorphone").toString().trim(), otp)
                forgotViewModel.doVerifyEmail(otpverify)
            } else showToast(getString(R.string.please_check_net))
        } else showToast("Enter the otp")
    }

    private fun observeLoginData() {
        lifecycleScope.launch {
            forgotViewModel.otpVerificationFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityLoginBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityLoginBinding.progressBar.visibility = View.GONE
                        if (response.data?.status == 200 && response.data.success) {
                            if (!isForgotPassword) {
                                val intents =
                                    Intent(
                                        this@LoginOTPVerifyActivity,
                                        MPINCreateActivity::class.java
                                    )
                                intents.putExtra(MPINCreateActivity.EXTRA_IS_CREATE_PIN,false)
                                intents.putExtra(
                                    "emailorphone",
                                    intent.getStringExtra("emailorphone").toString()
                                )
                                startActivity(intents)
                                finish()
                            } else {
                                val intents =
                                    Intent(
                                        this@LoginOTPVerifyActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                intents.putExtra(
                                    "emailorphone",
                                    intent.getStringExtra("emailorphone").toString()
                                )
                                startActivity(intents)
                                finish()
                            }
                        } else response.data?.message?.let {
                            showToast(it)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityLoginBinding.progressBar.visibility = View.GONE
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            forgotViewModel.otpGenerateFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityLoginBinding.tvResend.setButtonEnable(false)
                        activityLoginBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityLoginBinding.tvResend.setButtonEnable(true)
                        activityLoginBinding.progressBar.visibility = View.GONE
                        if (response.data?.status == 200 && response.data.success) {
                            startTimer()
                            showToast(response.data.message)
                        } else response.data?.message?.let {
                            showToast(it)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityLoginBinding.tvResend.setButtonEnable(true)
                        response.errorMessage?.let { showToast(it) }
                        activityLoginBinding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }

    }
}