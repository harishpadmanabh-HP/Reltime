package com.accubits.reltime.views.forgot

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityForgotPasswordBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.ForgotRequestModel
import com.accubits.reltime.utils.Extensions.getDeviceId
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.verify.LoginOTPVerifyActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_IS_FORGOT_PASSWORD = "extra_forgot_password"
    }

    private val isForgotPassword by lazy {
        intent.extras?.getBoolean(
            EXTRA_IS_FORGOT_PASSWORD,
            false
        ) ?: false
    }

    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding
    private val forgotViewModel by viewModels<ForgotViewModel>()

    @Inject
    lateinit var preferenceManager: PreferenceManager
    var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(activityForgotPasswordBinding.root)
        Utils.setStatusBarColour(this, ContextCompat.getColor(this, R.color.darkBlack))
        activityForgotPasswordBinding.textView5.markRequiredInRed()
        activityForgotPasswordBinding.btSendOtp.setOnClickListener {
            submitEmailOrPhone()
        }
        activityForgotPasswordBinding.ivBack.setOnClickListener {
            finish()
        }

        if (isForgotPassword != true)
            activityForgotPasswordBinding.textView11.text = resources.getText(R.string.forgot_mpin_)
        else
            activityForgotPasswordBinding.textView11.text =
                resources.getText(R.string.forgot_password_new)


        activityForgotPasswordBinding.etEmail.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                setErrorBox(false)
            }
        activityForgotPasswordBinding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (activityForgotPasswordBinding.tvErrorEmail.visibility == View.VISIBLE) {
                    setErrorBox(false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {
                }
            }
        })
        observeLoginData()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
            return
        } else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun submitEmailOrPhone() {
        activityForgotPasswordBinding.tvErrorEmail.visibility = View.GONE
        if (Utils.isNetworkAvailable(this)!!) {
            if (activityForgotPasswordBinding.etEmail.text.toString() != "" && Utils.isValidEmail(
                    activityForgotPasswordBinding.etEmail.text.toString()
                )
            ) {
                activityForgotPasswordBinding.tvErrorEmail.visibility = View.GONE
                activityForgotPasswordBinding.progressBar.visibility = View.VISIBLE
                val forgotRequestModel =
                    ForgotRequestModel(activityForgotPasswordBinding.etEmail.text.toString())
                forgotViewModel.otpGenerate(
                    token = if (isForgotPassword) null else preferenceManager.getApiKey()
                        .ifEmpty { null }, deviceId = getDeviceId(), forgotRequestModel = forgotRequestModel
                )
            } else {
                activityForgotPasswordBinding.tvErrorEmail.text =
                    resources.getString(R.string.enter_registered_email_id)
                setErrorBox(true)
            }
        }
    }

    private fun observeLoginData() {
        lifecycleScope.launch {
            forgotViewModel.otpGenerateFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityForgotPasswordBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityForgotPasswordBinding.progressBar.visibility = View.GONE
                        showToast(response.data?.message)
                        if (response.data?.status == 200 && response.data.success) {
                            val intent = Intent(
                                this@ForgotPasswordActivity, LoginOTPVerifyActivity::class.java
                            )
                            intent.putExtra(EXTRA_IS_FORGOT_PASSWORD, isForgotPassword)
                            intent.putExtra(
                                "emailorphone",
                                activityForgotPasswordBinding.etEmail.text.toString().trim()
                            )
                            if (Utils.isValidEmail(
                                    activityForgotPasswordBinding.etEmail.text.toString().trim()
                                )
                            ) {
                                intent.putExtra("medium", "email")
                            } else {
                                intent.putExtra("medium", "phone")
                            }
                            startActivity(intent)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        activityForgotPasswordBinding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }

    }


    private fun setErrorBox(isError: Boolean) {
        if (isError) {
            activityForgotPasswordBinding.cardView3.strokeWidth = 1
            activityForgotPasswordBinding.cardView3.strokeColor = getColor(R.color.dark_red)
            activityForgotPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.error_red60))
            activityForgotPasswordBinding.tvErrorEmail.visibility = View.VISIBLE
        } else {
            activityForgotPasswordBinding.cardView3.setCardBackgroundColor(getColor(R.color.card_color))
            activityForgotPasswordBinding.tvErrorEmail.visibility = View.GONE
            activityForgotPasswordBinding.cardView3.strokeWidth = 1
            activityForgotPasswordBinding.cardView3.strokeColor = getColor(R.color.darkblue)
        }
    }


}