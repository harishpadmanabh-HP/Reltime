package com.accubits.reltime.activity.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.settings.viewmodel.SettingsOtpViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityLoginotpverifyBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils.OTP_MAX_TIME
import com.accubits.reltime.utils.Extensions.getDeviceId
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsOTPActivity : AppCompatActivity() {
    private lateinit var binder: ActivityLoginotpverifyBinding
    private val viewModel by viewModels<SettingsOtpViewModel>()
    var changeSelection: Utils.Credentials? = null
    var phoneOrEmail: String? = null

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityLoginotpverifyBinding.inflate(layoutInflater)
        setContentView(binder.root)
        changeSelection = intent.getSerializableExtra("changeSelection") as Utils.Credentials?
        if (changeSelection == Utils.Credentials.EMAIL) {
            binder.tvSent.text =
                getString(R.string.enter_the_otp_we_just_sent_you_on_your_email_address)
        } else if (changeSelection == Utils.Credentials.PHONE) {
            binder.tvSent.text =
                getString(R.string.enter_the_otp_we_just_sent_you_on_your_phone)
        } else {
            showToast("Invalid option")
            finish()
        }
        phoneOrEmail = intent.getStringExtra("phoneOrEmail")
        collectOtpResendResponse()
        collectDataUpdateResponse()
        setupTimer()
        setListeners()
    }

    private fun collectOtpResendResponse() {
        lifecycleScope.launch {
            viewModel.resendOtpFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> binder.tvResend.setButtonEnable(false)
                    ApiCallStatus.SUCCESS -> {
                        binder.tvResend.setButtonEnable(true)
                        val serverStatus = response.data?.success ?: false
                        if (serverStatus) {
                            val responseCode = response.data?.status
                            if (responseCode == 200) {
                                setupTimer()
                                showToast(response.data.message)
                            } else {
                                showToast(response.data?.message ?: "Something went wrong")
                            }
                        } else {
                            showToast(response.data?.message ?: "Something went wrong")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binder.tvResend.setButtonEnable(true)
                        showToast(response.errorMessage ?: "Server error")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectDataUpdateResponse() {
        lifecycleScope.launch {
            viewModel.otpResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        val serverStatus = response.data?.success ?: false
                        if (serverStatus) {
                            val responseCode = response.data?.status
                            if (responseCode == 200) {
                                showToast(response?.data?.message)
                                when (changeSelection) {
                                    Utils.Credentials.PHONE -> {
                                        preferenceManager.setPhone(phoneOrEmail!!)
                                    }
                                    Utils.Credentials.EMAIL -> {
                                        preferenceManager.setEmail(phoneOrEmail!!)
                                    }
                                    else -> {}
                                }
                                openActivity(
                                    SettingsProfileActivity::class.java,
                                    shouldFinish = true
                                )
                            } else {
                                showToast(response.data?.message ?: "Something went wrong")
                            }
                        } else {
                            showToast(response.data?.message ?: "Something went wrong")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage ?: "Server error")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupTimer() {
        binder.tvResend.visibility = View.GONE
        binder.btVerify.setButtonEnable(true)
        countDownTimer.start()
    }

    private var countDownTimer = object : CountDownTimer(OTP_MAX_TIME, 1000) {
        @SuppressLint("SetTextI18n")
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
            binder.tvTimer.text = "Expired!"
            binder.tvResend.visibility = View.VISIBLE
            binder.btVerify.setButtonEnable(false)
        }
    }

    private fun setListeners() {

        binder.btVerify.setOnClickListener {
            val otp = binder.pinView.value
            if (otp.isEmpty() || otp.length != 4) {
                showToast("Enter otp to continue")
            } else {
                if (isNetworkAvailable()) {
                    when (changeSelection) {
                        Utils.Credentials.PHONE -> {
                            viewModel.updateEmailorPhone(otp, phoneOrEmail, null)
                        }
                        Utils.Credentials.EMAIL -> {
                            viewModel.updateEmailorPhone(otp, null, phoneOrEmail)
                        }
                        else -> {
                        }
                    }
                } else
                    showToast(getString(R.string.activity_network_not_available))
            }
        }

        binder.tvResend.setOnClickListener {
            if (isNetworkAvailable()) {
                if (changeSelection == Utils.Credentials.PHONE)
                    finish()//viewModel.resendOtp(phoneOrEmail, null, getDeviceId())
                else if (changeSelection == Utils.Credentials.EMAIL)
                    viewModel.resendOtp(null, phoneOrEmail, getDeviceId())
            } else {
                showToast(getString(R.string.activity_network_not_available))
            }
        }

        binder.ivBack.setOnClickListener { finish() }
    }

}