package com.accubits.reltime.activity.v2.signUp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.signUp.model.SignUpRequestModel
import com.accubits.reltime.activity.v2.signUp.viewmodel.SignUpViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivitySetUpProfileConfirmBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.home.ContainerActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SetUpProfileConfirmActivity : AppCompatActivity() {
    private lateinit var binder: ActivitySetUpProfileConfirmBinding
    private val viewModel by viewModels<SignUpViewModel>()

    companion object {
        const val EXTRA_PHONE = "phone"
        const val EXTRA_LOCATION = "location"
        const val EXTRA_FIRST_NAME = "first_name"
        const val EXTRA_LAST_NAME = "last_name"
        const val EXTRA_EMAIL = "email"
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateFCMToken()
        binder = ActivitySetUpProfileConfirmBinding.inflate(layoutInflater)
        setContentView(binder.root)

        init()
        setListeners()
        observeSignUp()
    }

    private fun init(){
        binder.tvPassword.markRequiredInRed()
        binder.tvConfirmPassword.markRequiredInRed()
    }

    private fun setListeners() {
        binder.ibBack.setOnClickListener { onBackPressed() }
       /* binder.etConfirmPassword.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                validate()
                true
            } else false
        }*/
        binder.btnNext.setOnClickListener { validate() }

        binder.ivPassword.setOnClickListener {
            if (binder.etPassword.transformationMethod != HideReturnsTransformationMethod.getInstance()) {
                binder.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binder.ivPassword.setImageDrawable(getDrawable(R.drawable.ic_eye))
            } else {
                binder.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binder.ivPassword.setImageDrawable(getDrawable(R.drawable.password_toggle_eye_close))
            }
        }
        binder.ivConfirmPassword.setOnClickListener {
            if (binder.etConfirmPassword.transformationMethod != HideReturnsTransformationMethod.getInstance()) {
                binder.etConfirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binder.ivConfirmPassword.setImageDrawable(getDrawable(R.drawable.ic_eye))
            } else {
                binder.etConfirmPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binder.ivConfirmPassword.setImageDrawable(getDrawable(R.drawable.password_toggle_eye_close))
            }
        }
    }

    private fun observeSignUp() {
        lifecycleScope.launch {
            viewModel.signUpFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnNext.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)
                        if (response.data != null && response.data.status == 200 && response.data.success) {
                            val loginSuccesModel = response.data
                            Utils.setUpBiometricLoginFlags(preferenceManager)
                            Utils.onClearMnemonics(preferenceManager)
                            preferenceManager.setKycCreated(loginSuccesModel.result.userDetails.kycCreated)
                            preferenceManager.setMnemonicCreated(loginSuccesModel.result.userDetails.mnemonicCreated)
                            preferenceManager.setKycApproved(loginSuccesModel.result.userDetails.kycApproved)
                            loginSuccesModel.result.userDetails.biometricsEnabled?.let {preferenceManager.setBiometric(it)}
                            preferenceManager.setLastLogin(loginSuccesModel.result.userDetails.new_last_login.toString())
                            preferenceManager.setUserId(loginSuccesModel.result.userDetails.id!!)
                            preferenceManager.setApiKey(loginSuccesModel.result.access)
                            preferenceManager.setRefreshToken(loginSuccesModel.result.refresh)
                            preferenceManager.setLoggedIn(true)
                            preferenceManager.setLoginBiometricEnabled(loginSuccesModel.result.userDetails.biometricLogin)//binder.swBiometric.isChecked)
                            loginSuccesModel.result.userDetails.email?.let {preferenceManager.setEmail(it)}
                            loginSuccesModel.result.userDetails.referralCode?.let {preferenceManager.setReferalCodeFromLogin(it)}

                            preferenceManager.setMpin(loginSuccesModel.result.userDetails.mpinCreated)
                            preferenceManager.setName(loginSuccesModel.result.userDetails.name)
                            loginSuccesModel.result.userDetails.contactNumber?.let {preferenceManager.setPhone(it)}
                            val intent = Intent(
                                this@SetUpProfileConfirmActivity,
                                ContainerActivity::class.java
                            )
                            intent.putExtra(ContainerActivity.EXTRA_IS_FIRST_LOGIN,true)
                            startActivity(intent)
                            finishAffinity()
                        } else if (response.data != null)
                            showToast(response.data.message)
                        else
                            showToast(response.errorMessage)
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(response.errorMessage)
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validate() {
        if (binder.etPassword.text.toString().isEmpty()) {
            binder.tvErrorPassword.text=resources.getString(R.string.please_enter_your_password)
            binder.tvErrorPassword.visibility = View.VISIBLE
            return
        } else binder.tvErrorPassword.visibility = View.GONE
        if (binder.etConfirmPassword.text.toString().isEmpty()) {
            binder.tvErrorConfirmPassword.text =
                resources.getString(R.string.please_enter_confirm_your_password)
            binder.tvErrorConfirmPassword.visibility = View.VISIBLE
            return
        } else if (binder.etPassword.text.toString() != binder.etConfirmPassword.text.toString()) {
            binder.tvErrorConfirmPassword.text =
                resources.getString(R.string.password_and_confirm_password_should_be_same)
            binder.tvErrorConfirmPassword.visibility = View.VISIBLE
            return
        } else binder.tvErrorConfirmPassword.visibility = View.GONE
        callSignUpAPI()
    }

    private fun callSignUpAPI() {
        if (Utils.isNetworkAvailable(this) == true) {
            hideSoftKeyboard()
            viewModel.signUpV2(
                SignUpRequestModel(
                    firstName = intent.getStringExtra(EXTRA_FIRST_NAME),
                    lastName = intent.getStringExtra(EXTRA_LAST_NAME),
                    email = intent.getStringExtra(EXTRA_EMAIL),
                    phone = intent.getStringExtra(EXTRA_PHONE),
                    location = intent.getStringExtra(EXTRA_LOCATION),
                    password = binder.etPassword.text.toString(),
                    password2 = binder.etConfirmPassword.text.toString(),
                            fcmToken = preferenceManager.getFCMToken(),
                    biometricLogin=binder.swBiometric.isChecked
                )
            )
        } else showToast(getString(R.string.please_check_net))

    }

    private fun updateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            preferenceManager.setFCMToken(token)
        })
    }

}