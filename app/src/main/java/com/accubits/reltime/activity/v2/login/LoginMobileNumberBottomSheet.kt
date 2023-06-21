package com.accubits.reltime.activity.v2.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.emoji.text.EmojiCompat
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.settings.popup.CountryPopupDialog
import com.accubits.reltime.databinding.BottomSheetDialogLoginBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.hideSoftKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.text.method.PasswordTransformationMethod
import com.accubits.reltime.utils.Extensions.markRequiredInRed


class LoginMobileNumberBottomSheet(
  val  activity: AppCompatActivity
) : BottomSheetDialog(activity, R.style.TransparentBottomSheetDialogTheme) {
    private var binding: BottomSheetDialogLoginBinding =
        BottomSheetDialogLoginBinding.inflate(layoutInflater)
    private lateinit var selectedCountry: Country

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        init()
        setListeners()
    }

    private fun init() {
binding.tvPhoneNumber.markRequiredInRed()
    }
    private fun setListeners() {
        binding.tvEmoji.setOnClickListener {
            activity.CountryPopupDialog {
                setCountry(it)
            }
        }
        binding.btnNext.setOnClickListener {
            validate()
        }
        binding.etPhone.keyListener = DigitsKeyListener.getInstance("1234567890")
        binding.etPhone.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etPhone.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                validate()
                true
            } else false
        }
        binding.ivClose.setOnClickListener { dismiss() }

        binding.etPhone.addTextChangedListener {
            binding.tvError.visibility = View.GONE
        }
    }

    private fun validate() {
        if (!::selectedCountry.isInitialized) {
            binding.tvError.text =
                activity.resources.getString(R.string.cc_select_country_code)
            binding.tvError.visibility = View.VISIBLE
        } else if (!Patterns.PHONE.matcher(binding.etPhone.text.toString().trim()).matches()) {
            binding.tvError.text =
                activity.resources.getString(R.string.phone_error)
            binding.tvError.visibility = View.VISIBLE
        } else if (!Utils.isValidPhoneLength(activity,
                binding.etPhone.text.toString().trim(),
                selectedCountry.countryISOCode
            )
        ) {
            binding.tvError.text =
                activity.resources.getString(R.string.cc_phone_error)
            binding.tvError.visibility = View.VISIBLE
        }
        else {
            binding.tvError.visibility = View.GONE
            val intent = Intent(context, BiometricLoginV2Activity::class.java)
            intent.putExtra(BiometricLoginV2Activity.EXTRA_PHONE_NUMBER, selectedCountry.dialCode +binding.etPhone.text.toString().trim())
            context.startActivity(intent)
            binding.etPhone.postDelayed({dismiss()},1000)
        }
    }

    private fun setCountry(country: Country) {
        binding.tvError.visibility = View.GONE
        selectedCountry = country
        binding.tvEmoji.text =  country.dialCode//country.emojiString
    }

    fun observeDefaultCountry(code:Country?){
        if (code != null && !::selectedCountry.isInitialized) {
            try {
                code.emojiString =
                    EmojiCompat.get().process(Utils.convertToEmoji(code.countryISOCode))
                        .toString() + " " + code.dialCode
                setCountry(code)
            } catch (e: Exception) {

            }
        }
    }

}