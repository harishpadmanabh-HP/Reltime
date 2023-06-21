package com.accubits.reltime.activity.v2.welcomeScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.v2.login.BiometricLoginV2Activity
import com.accubits.reltime.activity.v2.login.LoginMobileNumberBottomSheet
import com.accubits.reltime.activity.v2.signUp.SignUpPhoneNumberActivity
import com.accubits.reltime.activity.v2.welcomeScreen.adapter.PageAdapter
import com.accubits.reltime.activity.v2.welcomeScreen.fragment.WelcomeScreenFour
import com.accubits.reltime.activity.v2.welcomeScreen.fragment.WelcomeScreenOne
import com.accubits.reltime.activity.v2.welcomeScreen.fragment.WelcomeScreenThree
import com.accubits.reltime.activity.v2.welcomeScreen.fragment.WelcomeScreenTwo
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityWelcomeScreenBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.getDefaultCountry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeScreen : AppCompatActivity() {

    private lateinit var binder: ActivityWelcomeScreenBinding
    private val regionSelectionViewModel by viewModels<RegionSelectionViewModel>()
    private val loginBottomSheet by lazy { LoginMobileNumberBottomSheet(this) }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preferenceManager.getUserId() != 0 && preferenceManager.getRefreshToken()
                .isNotEmpty() && preferenceManager.getName()
                .isNotEmpty() && preferenceManager.getLoggedIn()
        ) {
            val intent = Intent(this, BiometricLoginV2Activity::class.java)
            startActivity(intent)
            finish()
        }
        binder = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.viewPager.adapter = PageAdapter(
            supportFragmentManager,
            listOf(
                WelcomeScreenOne(),
                WelcomeScreenTwo(),
                WelcomeScreenThree(),
                WelcomeScreenFour()
            )
        )

        binder.tlWelcomeScren.setupWithViewPager(binder.viewPager)

        binder.llLoginContainer.setOnClickListener {
            loginBottomSheet.show()
        }
        binder.btCreatAccount.setOnClickListener {
            val intent = Intent(this, SignUpPhoneNumberActivity::class.java)
            startActivity(intent)
        }
        observerCountryList()
        loadCountriesFromJson()
    }

    private fun loadCountriesFromJson() {
        regionSelectionViewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))
    }

    private fun observerCountryList() {
        regionSelectionViewModel.countryList.observe(this) {
            val countryList = ReltimeConstants.countries
            if (countryList != null && countryList.isNotEmpty()) {
                getDefaultCountry()?.let {code->
                    code.emojiString =
                        EmojiCompat.get().process(Utils.convertToEmoji(code.countryISOCode))
                            .toString() + " " + code.dialCode
                    loginBottomSheet.observeDefaultCountry(code)
                }
            }
        }
    }


}