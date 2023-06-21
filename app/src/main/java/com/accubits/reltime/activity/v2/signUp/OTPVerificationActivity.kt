package com.accubits.reltime.activity.v2.signUp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.signup.model.body.OtpData
import com.accubits.reltime.activity.signup.viewmodel.SignupOtpViewModel
import com.accubits.reltime.activity.v2.fetchotp.broadcast.SmsReceiver
import com.accubits.reltime.activity.v2.fetchotp.interfaces.SmsListener
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityOtpverificationBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.helpers.Utils.OTP_MAX_TIME
import com.accubits.reltime.utils.Extensions.getDeviceId
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OTPVerificationActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PHONE_NUMBER = "phone_number"
        const val EXTRA_RECAPTCHA_TOKEN = "re_captcha_token"
    }

    private val viewModel by viewModels<SignupOtpViewModel>()
    private lateinit var binder: ActivityOtpverificationBinding
    private val verifiedPhoneNumber by lazy {
        intent.getStringExtra(EXTRA_PHONE_NUMBER)
    }

    private var countDownTimer: CountDownTimer =
        object : CountDownTimer(OTP_MAX_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes =
                    millisUntilFinished / ReltimeConstants.oneSecondInMillis / ReltimeConstants.seconds
                val seconds =
                    (millisUntilFinished / ReltimeConstants.oneSecondInMillis % ReltimeConstants.seconds).toString()
                val timerText =
                    "$minutes:${seconds.let { if (it.toLong() < ReltimeConstants.TEN) "0$it" else it }}"
                binder.tvTimer.text =
                    resources.getString(R.string.your_code_expires_in_n_minute_s, timerText)
            }

            override fun onFinish() {
                binder.tvResendOTP.visibility = View.VISIBLE
                binder.btnNext.setButtonEnable(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binder.root)

        setListeners()
        observeOTPValidation()
        observeOTPRegeneration()
        // Init Sms Retriever >>>>
        initBroadCast()
        initSmsListener()
        regenerateOtp(intent.getStringExtra(EXTRA_RECAPTCHA_TOKEN))
    }

    private fun initSmsListener() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(this)
        client.startSmsRetriever()

    }

    private fun initBroadCast() {
        SmsReceiver.bindListener(object : SmsListener {
            override fun messageReceived(messageText: String?) {
                binder.pinView.value = messageText!!
                Handler(Looper.myLooper()!!).postDelayed({
                    validateOtp()
                },2000)
            }
        })
    }

    private fun setListeners() {
        binder.ibBack.setOnClickListener {
            onBackPressed()
        }
        binder.tvResendOTP.setOnClickListener {
            val intents = Intent()
            setResult(RESULT_CANCELED, intents)
            finish()
        }
        binder.btnNext.setOnClickListener {
            validateOtp()
        }
    }

    private fun observeOTPValidation() {
        lifecycleScope.launch {
            viewModel.otpValidateFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnNext.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.btnNext.setButtonEnable(true)
                        binder.progressBar.visibility = View.GONE
                        if (response.data != null && response.data.status == 200 && response.data.success) {
                            val intents = Intent()
                            setResult(RESULT_OK, intents)
                            finish()
                        } else if (response.data != null)
                            response.data.message.let { showToast(it) }
                        else
                            response.errorMessage?.let { showToast(it) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }


    private fun observeOTPRegeneration() {
        lifecycleScope.launch {
            viewModel.generateOTPFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnNext.setButtonEnable(false)
                        binder.tvResendOTP.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)
                        binder.tvResendOTP.setButtonEnable(true)
                        if (response.data != null && response.data.status == 200 && response.data.success) {
                            startCountDown()
                        } else if (response.data != null)
                            response.data.message.let { showToast(it) }
                        else
                            response.errorMessage?.let { showToast(it) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)
                        binder.tvResendOTP.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }

    }

    private fun startCountDown() {
        binder.tvResendOTP.visibility = View.GONE
        binder.btnNext.setButtonEnable(true)
        countDownTimer.start()
    }

    private fun validateOtp() {
        if (binder.pinView.value.length < 6)
            showToast(resources.getString(R.string.field_cant_empty))
        else {
            val otpData = OtpData(verifiedPhoneNumber, binder.pinView.value)
            viewModel.validateOTPV2(otpData)
        }
    }

    private fun regenerateOtp(token:String?) {
        if (Utils.isNetworkAvailable(this)!!) {
            val otpData = OtpData(emailOrPhone = verifiedPhoneNumber, token =token )
            viewModel.regenerateOtpV2(otpData,getDeviceId())
        } else showToast( getString(R.string.please_check_net))
    }

}