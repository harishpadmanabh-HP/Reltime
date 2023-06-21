package com.accubits.reltime.activity.v2.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.text.clearSpans
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.login.fragments.LoginPINFragment
import com.accubits.reltime.activity.v2.login.fragments.PasswordFragment
import com.accubits.reltime.activity.v2.login.viewmodel.BiometricLoginViewModel
import com.accubits.reltime.activity.v2.welcomeScreen.adapter.PageAdapter
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityBiometricLoginV2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.biometricUtils.BiometricUtilities
import com.accubits.reltime.views.home.ContainerActivity
import com.accubits.reltime.views.privacy.PrivacyActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject


@AndroidEntryPoint
class BiometricLoginV2Activity : AppCompatActivity() {
    companion object {
        const val EXTRA_PHONE_NUMBER = "phone_number"
    }

    private lateinit var binder: ActivityBiometricLoginV2Binding
    private val passwordFragment by lazy { PasswordFragment() }
    private val loginPINFragment by lazy { LoginPINFragment() }
    private val viewModel by viewModels<BiometricLoginViewModel>()

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var onUserInteraction = false

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateFCMToken()
        binder = ActivityBiometricLoginV2Binding.inflate(layoutInflater)
        setContentView(binder.root)
        init()
        setListeners()
        collectData()
        setTermsAndPrivacyPolicyText()
    }

    private fun init() {
        setPrompt()
        binder.tvHead.text =
            if (intent.getStringExtra(EXTRA_PHONE_NUMBER) == null&&preferenceManager.getName().isNotEmpty()) resources.getString(
                R.string.greetings,
                Utils.getGreetingMessage(this),
                preferenceManager.getName()
            )
            else Utils.getGreetingMessage(this)
        val fragmentList = ArrayList<Fragment>()
        val titleList = ArrayList<String>()
        fragmentList.add(passwordFragment)
        titleList.add(resources.getString(R.string.password_smal))
        if (intent.getStringExtra(EXTRA_PHONE_NUMBER) == null&&preferenceManager.getLoginPINEnabled()) {
            fragmentList.add(loginPINFragment)
            titleList.add(resources.getString(R.string.pin))
            binder.tvDesc.text=resources.getString(R.string.login_in_with_your_password_or_use_your_login_pin)
        } else {
            binder.tabLayout.visibility = View.GONE
        }

        binder.viewPager.adapter = PageAdapter(
            supportFragmentManager, fragmentList,
            titleList
        )
        binder.tabLayout.setupWithViewPager(binder.viewPager)


    }

    private fun setListeners() {
        binder.btnLogin.setOnClickListener {
            validate()
        }
//        binder.tvPrivacyPolicy.setOnClickListener {
//            val intent = Intent(this, PrivacyActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun setTermsAndPrivacyPolicyText() {
        val spanString = SpannableString(
            getString(R.string.i_agree_terms_and_conditions_and_privacy_policy)
        )

        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val mIntent = Intent(this@BiometricLoginV2Activity, PrivacyActivity::class.java)
                mIntent.putExtra(PrivacyActivity.URL,PrivacyActivity.TERMS_CONDITIONS)
                startActivity(mIntent)
            }
        }

        val privacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val mIntent = Intent(this@BiometricLoginV2Activity, PrivacyActivity::class.java)
                mIntent.putExtra(PrivacyActivity.URL,PrivacyActivity.PRIVACY_POLICY)
                startActivity(mIntent)
            }
        }

        spanString.setSpan(termsAndCondition, 19, 38, 0)
        spanString.setSpan(privacy, 44, 59, 0)
        spanString.setSpan(ForegroundColorSpan(getColor(R.color.linear_gradient_base)), 19, 39, 0)
        spanString.setSpan(ForegroundColorSpan(getColor(R.color.linear_gradient_base)), 44, 59, 0)
        spanString.setSpan(UnderlineSpan(), 19, 39, 0)
        spanString.setSpan(UnderlineSpan(), 44, 59, 0)
        binder.tvPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
        binder.tvPrivacyPolicy.setText(spanString, BufferType.SPANNABLE)
        binder.tvPrivacyPolicy.isSelected = true
        spanString.clearSpans()
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.loginResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnLogin.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnLogin.visibility = View.VISIBLE
                        binder.btnLogin.setButtonEnable(true)
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            val loginSuccesModel = response.data
                            if (preferenceManager.getUserId() == 0 || preferenceManager.getUserId() != loginSuccesModel.result.userDetails.id!!)
                                Utils.setUpBiometricLoginFlags(preferenceManager)
                            if (loginSuccesModel.result.userDetails.publicKey !=preferenceManager.getPublicKeyFromLogin())
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
                            preferenceManager.setLoginBiometricEnabled(loginSuccesModel.result.userDetails.biometricLogin)

                            loginSuccesModel.result.userDetails.email?.let {preferenceManager.setEmail(it)}
                            loginSuccesModel.result.userDetails.referralCode?.let {preferenceManager.setReferalCodeFromLogin(it)}
                            preferenceManager.setMpin(loginSuccesModel.result.userDetails.mpinCreated)
                            preferenceManager.setName(loginSuccesModel.result.userDetails.name)
                            loginSuccesModel.result.userDetails.contactNumber?.let { preferenceManager.setPhone(it)}
                            if (loginSuccesModel.result.userDetails.publicKey != null && loginSuccesModel.result.userDetails.publicKey != "NA") {
                                loginSuccesModel.result.userDetails.publicKey?.let { preferenceManager.setPublicKeyFromLogin(it) }
                            }
                            val intent = Intent(
                                this@BiometricLoginV2Activity,
                                ContainerActivity::class.java
                            )
                            intent.putExtra(ContainerActivity.EXTRA_IS_FIRST_LOGIN,intent.getStringExtra(EXTRA_PHONE_NUMBER) != null)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            response.data?.let { showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binder.progressBar.visibility = View.GONE
                        binder.btnLogin.visibility = View.VISIBLE
                        binder.btnLogin.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }


    private fun validate() {
        if (binder.viewPager.currentItem == 0) {
            if (passwordFragment.getValue().isEmpty())
                showToast(resources.getString(R.string.please_enter_your_password))
            else callSmartLoginAPI(passwordFragment.getValue(), 1)
        } else if (loginPINFragment.getValue().length < 4)
            showToast(resources.getString(R.string.please_enter_in))
        else callSmartLoginAPI(loginPINFragment.getValue(), 2)
    }

    private fun callSmartLoginAPI(text: String?, type: Int) {
        if (Utils.isNetworkAvailable(this) == true) {
            hideSoftKeyboard()
            viewModel.callBiometricLogin(
                intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: preferenceManager.getPhone(),
                text = text,
                type = type
            )
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this,  ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED && errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON)
                        showToast(
                            resources.getString(
                                R.string.n_n,
                                resources.getString(R.string.authentication_error),
                                errString
                            )
                        )
                    if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                        if (BiometricUtilities.deviceHasPasswordPinLock(this@BiometricLoginV2Activity)) {
                            // pin password available
                            /*binder.clBiometric.visibility =
                                View.VISIBLE*/
                            initBiometricPrompt(
                                resources.getString(R.string.auth),
                                resources.getString(R.string.auth_title),
                                resources.getString(R.string.auth_desc),
                                true
                            )
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            /* binder.clBiometric.visibility =
                                 View.GONE*/
                        }
                    }

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    callSmartLoginAPI(null, 3)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast(resources.getString(R.string.authentication_failed))
                }
            })
    }

    private fun initBiometricPrompt() {
        val isBiometricAvailable = BiometricUtilities.isBiometricHardWareAvailable(this)
        if (isBiometricAvailable) {
            //fingerprint available
            initBiometricPrompt(
                resources.getString(R.string.auth),
                resources.getString(R.string.auth_title),
                resources.getString(R.string.auth_desc),
                false
            )
        } else {
            // finger print not available
            if (BiometricUtilities.deviceHasPasswordPinLock(this)) {
                // pin password available
                initBiometricPrompt(
                    resources.getString(R.string.auth),
                    resources.getString(R.string.auth_title),
                    resources.getString(R.string.auth_desc),
                    true
                )
            }
        }


    }

    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean
    ) {
        if (setDeviceCred) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val authFlag =
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setAllowedAuthenticators(authFlag)
                    .build()
            } else {
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setDeviceCredentialAllowed(true)
                    .build()
            }
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(resources.getString(R.string.cancel))
                .build()
        }
        openBiometricAutomatically()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        onUserInteraction = true
    }

    private fun openBiometricAutomatically() {
        if (!onUserInteraction)
            biometricPrompt.authenticate(promptInfo)
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

    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra(EXTRA_PHONE_NUMBER) == null&&preferenceManager.getLoginBiometricEnabled()) initBiometricPrompt()
    }
}