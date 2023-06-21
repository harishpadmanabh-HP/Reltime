package com.accubits.reltime.activity.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.settings.popup.CountryPopupDialog
import com.accubits.reltime.activity.settings.viewmodel.SettingEmailPhoneViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivitySettingsUpdateMobileNumberBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.AppConfig
import com.accubits.reltime.utils.Extensions.getDefaultCountry
import com.accubits.reltime.utils.Extensions.getDeviceId
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.accubits.reltime.utils.Extensions.isNetworkAvailable
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.Utils.Credentials
import com.google.android.gms.safetynet.SafetyNet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsUpdateEmailMobileNumberActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binder: ActivitySettingsUpdateMobileNumberBinding
    private var changeSelection: Credentials? = Credentials.EMAIL
    private val viewModel by viewModels<SettingEmailPhoneViewModel>()
    private val regionSelectionViewModel by viewModels<RegionSelectionViewModel>()
    private var selectedCountry: Country? = null

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsUpdateMobileNumberBinding.inflate(layoutInflater)
        setContentView(binder.root)
        changeSelection = intent.getSerializableExtra("changeSelection") as Credentials?
        if (changeSelection != null) {
            binder.btVerify.visibility = View.VISIBLE
            when (changeSelection) {
                Credentials.EMAIL -> {
                    updateUiForEmail()
                }
                Credentials.PHONE -> {
                    updateUiForPhone()
                }
                else -> {}
            }
        } else {
            showToast("Invalid option"); finish()
        }
        init()
        loadCountriesFromJson()
        collectOtpFlow()
        observerCountryList()
        setListeners()
        binder.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        binder.tvOldEmailLabel.markRequiredInRed()
        binder.tvNewEmailLabel.markRequiredInRed()

        binder.tvOldPhoneLabel.markRequiredInRed()
        binder.tvNewPhoneLabel.markRequiredInRed()
    }

    private fun setListeners() {
        binder.btVerify.setOnClickListener(this)
        binder.tvEmoji.setOnClickListener(this)
    }

    private fun updateUiForEmail() {
        binder.tvTitleLabel.text = getString(R.string.settings_profile_update_email_title)
        binder.clEmailContainer.visibility = View.VISIBLE
        binder.clPhoneContainer.visibility = View.GONE
        binder.edCurrentPhoneNumber.setText(preferenceManager.getEmail())
    }

    private fun loadCountriesFromJson() {
        regionSelectionViewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))
    }

    private fun updateUiForPhone() {
        binder.tvTitleLabel.text = getString(R.string.settings_profile_update_phone_title)
        binder.clEmailContainer.visibility = View.GONE
        binder.clPhoneContainer.visibility = View.VISIBLE
        binder.edCurrentPhoneNumber.setText(preferenceManager.getPhone())
    }

    private fun collectOtpFlow() {
        lifecycleScope.launch {
            viewModel.updationStateFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        val serverStatus = response.data?.success ?: false
                        if (serverStatus) {
                            if (response.data != null && response.data.status == 200 && response.data.success) {
                                showToast(response.data.message)
                                openActivity(SettingsOTPActivity::class.java) {
                                    putSerializable("changeSelection", changeSelection)
                                    when (changeSelection) {
                                        Credentials.EMAIL -> {
                                            putString(
                                                "phoneOrEmail",
                                                viewModel.phoneEmailModel.value.newEmailId
                                            )
                                        }
                                        Credentials.PHONE -> {
                                            putString(
                                                "phoneOrEmail",
                                                viewModel.phoneEmailModel.value.newPhoneNumber
                                            )
                                        }
                                        else -> {}
                                    }
                                }
                            } else {
                                updateErrorUI(response.data?.message ?: "Something went wrong")
                            }
                        } else {
                            updateErrorUI(response.data?.message ?: "Something went wrong")
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

    private fun observerCountryList() {
        regionSelectionViewModel.countryList.observe(this) {
            getDefaultCountry()?.let { code ->
                if (selectedCountry == null) {
                    code.emojiString =
                        EmojiCompat.get().process(Utils.convertToEmoji(code.countryISOCode))
                            .toString() + " " + code.dialCode
                    setCountry(code)
                }
            }
        }
    }

    private fun updateErrorUI(errorText: String) {
        showToast(errorText)
        when (changeSelection) {
            Credentials.EMAIL -> {
                binder.llLinearLayout.backgroundTintList = ColorStateList.valueOf(
                    getColor(
                        R.color
                            .error_red60
                    )
                )
                binder.tvEmailErrorText.text = errorText
            }
            Credentials.PHONE -> {
                binder.tvPhoneErrorText.visibility = View.VISIBLE
                binder.edNewEmail.background = getDrawable(R.drawable.error)
                binder.tvPhoneErrorText.text = errorText
            }
            else -> {}
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btVerify -> {
                when (changeSelection) {
                    Credentials.EMAIL -> {
                        viewModel.phoneEmailModel.value.apply {
                            newEmailId = binder.edNewEmail.text.toString().trim()
                            otp = null; newPhoneNumber = null
                            viewModel.onEmailValidated { success, message ->
                                if (success) {
                                    //call api for email change
                                    // viewModel.generateOtp()
                                } else {
                                    showToast(message)
                                }
                            }
                        }
                    }
                    Credentials.PHONE -> {
                        viewModel.phoneEmailModel.value.apply {
                            val dialCode = selectedCountry?.dialCode?.trim()
                            val countryISOCode = selectedCountry?.countryISOCode?.trim()
                            val phoneNumber = binder.edNewPhone.text.toString().trim()
                            hideSoftKeyboard()
                            if (countryISOCode.isNullOrEmpty()) {
                                showToast( resources.getString(R.string.cc_select_country_code))
                            } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                                showToast( resources.getString(R.string.phone_error))
                            } else if (!Utils.isValidPhoneLength(
                                    this@SettingsUpdateEmailMobileNumberActivity,
                                    phoneNumber,
                                    countryISOCode
                                )
                            ) {
                                showToast( resources.getString(R.string.cc_phone_error))
                            } else {
                                newPhoneNumber = dialCode + phoneNumber
                                otp = null; newEmailId = null
                                binder.tvPhoneErrorText.visibility = View.INVISIBLE
                                if (isNetworkAvailable()) {
                                    initializeRecaptchaClient()
                                } else {
                                    showToast(getString(R.string.activity_network_not_available))
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }

            R.id.tvEmoji -> {
                CountryPopupDialog {
                    setCountry(it)
                }
            }
        }
    }

    private fun setCountry(country: Country) {
        selectedCountry = country
        binder.tvEmoji.text = country.dialCode//country.emojiString
    }

    private fun initializeRecaptchaClient() {
        SafetyNet.getClient(this@SettingsUpdateEmailMobileNumberActivity)
            .verifyWithRecaptcha(AppConfig.getSiteKey())
            .addOnSuccessListener(this) { response ->
                if (response.tokenResult?.isNotEmpty() == true) {
                    viewModel.generateOtp(getDeviceId(),response.tokenResult)
                } else showToast(resources.getString(R.string.some_thing_went_wrong))
            }
            .addOnFailureListener(this) { e ->
                showToast(e.message)
            }
    }
}