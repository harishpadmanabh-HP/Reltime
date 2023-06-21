package com.accubits.reltime.activity.v2.signUp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.settings.popup.CountryPopupDialog
import com.accubits.reltime.activity.signup.viewmodel.SignupActivityViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivitySignUpPhoneNumberBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.AppConfig
import com.accubits.reltime.utils.Extensions.getDefaultCountry
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignUpPhoneNumberActivity : AppCompatActivity() {
    private lateinit var binder: ActivitySignUpPhoneNumberBinding
    private lateinit var selectedCountry: Country
    private val regionSelectionViewModel by viewModels<RegionSelectionViewModel>()
    private val viewModel by viewModels<SignupActivityViewModel>()

    private lateinit var verifiedPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySignUpPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binder.root)
        init()
        setListeners()
        observerCountryList()
        observeOtpResponse()
        loadCountriesFromJson()
    }

    private fun init() {
        binder.tvPhoneNumber.markRequiredInRed()
    }

    private fun observeOtpResponse() {
        lifecycleScope.launch {
            viewModel.loginResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binder.progressBar.visibility = View.VISIBLE
                        binder.btnNext.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binder.progressBar.visibility = View.GONE
                        binder.btnNext.setButtonEnable(true)

                        if (response.data != null) {
                            val data = response.data
                            if (!data.already_exits!!) {
                                if (data.is_verified!!)
                                    moveToSetupActivity()
                                else
                                    initializeRecaptchaClient()
                            } else {
                                binder.tvError.text =
                                    resources.getString(R.string.phone_number_already_exist)
                                binder.tvError.visibility = View.VISIBLE
                            }
                        } else {
                            response.errorMessage?.let { showToast(it) }
                        }
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

    private fun moveToSetupActivity() {
        val intents = Intent(this, SetUpProfileConfirmActivity::class.java)
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_PHONE, verifiedPhoneNumber)
        intents.putExtra(SetUpProfileConfirmActivity.EXTRA_LOCATION, selectedCountry.countryName)
        startActivity(intents)
    }

    private fun moveToOtpActivity(token:String?) {
        val intents = Intent(this, OTPVerificationActivity::class.java)
        intents.putExtra(OTPVerificationActivity.EXTRA_PHONE_NUMBER, verifiedPhoneNumber)
        intents.putExtra(OTPVerificationActivity.EXTRA_RECAPTCHA_TOKEN, token)
        otpVerificationLauncher.launch(intents)
    }

    private fun setListeners() {
        binder.tvEmoji.setOnClickListener {
            CountryPopupDialog {
                setCountry(it)
            }
        }
        binder.btnNext.setOnClickListener {
            validate()
        }
        binder.ibBack.setOnClickListener {
            onBackPressed()
        }

        binder.etPhone.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                validate()
                true
            } else false
        }
        binder.etPhone.addTextChangedListener {
            binder.tvError.visibility = View.GONE
        }
    }


    private fun loadCountriesFromJson() {
        regionSelectionViewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))
    }

    private fun observerCountryList() {
        regionSelectionViewModel.countryList.observe(this) {
            getDefaultCountry()?.let { code ->
                if (!::selectedCountry.isInitialized) {
                    code.emojiString =
                        EmojiCompat.get().process(Utils.convertToEmoji(code.countryISOCode))
                            .toString() + " " + code.dialCode
                    setCountry(code)
                }
            }
        }
    }

    private fun setCountry(country: Country) {
        selectedCountry = country
        binder.tvEmoji.text = country.dialCode//country.emojiString
    }

    private fun validate() {
        hideSoftKeyboard()
        if (!::selectedCountry.isInitialized) {
            binder.tvError.text =
                resources.getString(R.string.cc_select_country_code)
            binder.tvError.visibility = View.VISIBLE
        } else if (!Patterns.PHONE.matcher(binder.etPhone.text.toString().trim()).matches()) {
            binder.tvError.text =
                resources.getString(R.string.phone_error)
            binder.tvError.visibility = View.VISIBLE
        } else if (!Utils.isValidPhoneLength(
                this,
                binder.etPhone.text.toString().trim(),
                selectedCountry.countryISOCode
            )
        ) {
            binder.tvError.text =
                resources.getString(R.string.cc_phone_error)
            binder.tvError.visibility = View.VISIBLE
        } else {
            binder.tvError.visibility = View.GONE
            callOtp()
        }
    }

    private fun callOtp() {
        if (Utils.isNetworkAvailable(this) == true) {
            verifiedPhoneNumber = selectedCountry.dialCode + binder.etPhone.text.toString().trim()
            viewModel.validateUser(
                verifiedPhoneNumber
            )
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private var otpVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, SetUpProfileActivity::class.java)
            intent.putExtra(SetUpProfileConfirmActivity.EXTRA_PHONE, verifiedPhoneNumber)
            intent.putExtra(SetUpProfileConfirmActivity.EXTRA_LOCATION, selectedCountry.countryName)
            startActivity(intent)
        }
    }


    private fun initializeRecaptchaClient() {
        SafetyNet.getClient(this@SignUpPhoneNumberActivity)
            .verifyWithRecaptcha(AppConfig.getSiteKey())
            .addOnSuccessListener(this) { response ->
                if (response.tokenResult?.isNotEmpty() == true) {
                    moveToOtpActivity(response.tokenResult)
                } else showToast(resources.getString(R.string.some_thing_went_wrong))
            }
            .addOnFailureListener(this) { e ->
                showToast(e.message)
            }
    }


}