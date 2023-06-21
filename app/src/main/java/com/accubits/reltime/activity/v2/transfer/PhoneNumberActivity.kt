package com.accubits.reltime.activity.v2.transfer

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.region.model.Country
import com.accubits.reltime.activity.region.viewmodel.RegionSelectionViewModel
import com.accubits.reltime.activity.settings.popup.CountryPopupDialog
import com.accubits.reltime.activity.v2.common.contactPicker.ContactPickerComposeActivity
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.database.Contact
import com.accubits.reltime.databinding.ActivityPhoneNumberBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.SearchUserResponseModel
import com.accubits.reltime.utils.Extensions.getDefaultCountry
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneNumberBinding
    private lateinit var selectedCountry: Country

    private val viewModel by viewModels<TransferViewModel>()
    private val regionSelectionViewModel by viewModels<RegionSelectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populate()
        action()
        observer()


    }

    private fun action() {
        val contactListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<Contact>(ContactPickerComposeActivity.EXTRA_SELECTED_CONTACT)
                    ?.let { any ->
                        onFromContactSelection(any)
                    }
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivContactList.setOnClickListener {
            val intents = Intent(this, ContactPickerComposeActivity::class.java)
            intents.putExtra(ContactPickerComposeActivity.EXTRA_SHOW_ALL_CONTACTS, false)
            contactListLauncher.launch(intents)
        }
        binding.tvTapToChoose.setOnClickListener {
            openActivity(SendAmountActivity::class.java, shouldFinish = true) {
                putString(TransferObject.RECEIVER, viewModel.mobileNumber)
                putString(TransferObject.NAME, viewModel.userName)
                putString(TransferObject.USERID, viewModel.userId)
                putString(TransferObject.PROFILE_IMAGE, viewModel.profileImage)
                putString(
                    TransferObject.CONTACT_TYPE,
                    Utils.TransferContactType.PHONE.contactType
                )
            }
        }

        binding.etPhone.doOnTextChanged { text, _, _, _ ->
            callContactSearch()
        }


    }

    private fun callContactSearch(){
        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.searchContact(selectedCountry.dialCode + binding.etPhone.text.toString())
        } else
            showToast(getString(R.string.please_check_net))
    }

    private fun populate() {
        binding.tvPhoneNumberTitle.markRequiredInRed()
        binding.tvEmoji.setOnClickListener {
            CountryPopupDialog {
                setCountry(it)
                callContactSearch()
            }
        }
        loadCountriesFromJson()
    }

    private fun loadCountriesFromJson() {
        regionSelectionViewModel.extractCountryFromJson(resources.openRawResource(R.raw.country_codes))
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.searchContactResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.clProgress.visibility = View.VISIBLE
                        binding.clContact.visibility = View.INVISIBLE

                    }
                    ApiCallStatus.SUCCESS -> {

                        if (response.data!!.success) {
                            binding.clProgress.visibility = View.INVISIBLE
                            binding.clContact.visibility = View.VISIBLE
                            showData(response)

                        } else {
                            binding.clProgress.visibility = View.INVISIBLE
                            binding.clContact.visibility = View.INVISIBLE
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binding.clProgress.visibility = View.INVISIBLE
                        binding.clContact.visibility = View.INVISIBLE
                    }
                    else -> {}
                }
            }
        }

        observerCountryList()
    }

    private fun observerCountryList() {
        regionSelectionViewModel.countryList.observe(this) {
            getDefaultCountry()?.let {code->
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
        binding.tvEmoji.text =  country.dialCode//country.emojiString
    }


    private fun showData(response: ApiMapper<SearchUserResponseModel>) {
        response.data?.result?.let { it ->
            binding.tvUsername.text = it.name
            binding.tvUserNumber.text = it.contactNumber
            viewModel.userName = it.name
            viewModel.profileImage = it.profileImage.toString()
            viewModel.userId = it.userId
            viewModel.mobileNumber = it.contactNumber

            when (it.profileImage) {
                null -> {
                    binding.ivuserprofileimage.setFirstLetter(viewModel.userName)

                }

                else -> {
                    binding.ivuserprofileimage.setUserProfileImage(viewModel.profileImage)

                }
            }


        }

    }

    private fun onFromContactSelection(contact: Contact) {
        openActivity(SendAmountActivity::class.java, shouldFinish = true) {
            putString(TransferObject.RECEIVER, contact.contactNumber)
            putString(TransferObject.NAME, contact.contactName)
            putString(TransferObject.USERID, contact.userId)
            putString(TransferObject.PROFILE_IMAGE, contact.contactImageUri)
            putString(
                TransferObject.CONTACT_TYPE,
                Utils.TransferContactType.PHONE.contactType
            )

        }

    }

}