package com.accubits.reltime.activity.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.kyc.KYCDetailsActivity
import com.accubits.reltime.activity.settings.viewmodel.ProfileViewModel
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivitySettingsProfileBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.loadImageWithUrl
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binder: ActivitySettingsProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private var address1: String = ""
    private var address2: String = ""
    private var state: String = ""
    private var city: String = ""

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsProfileBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.layout1.tvFullnameValue.text = preferenceManager.getName()
        binder.ivBack.setOnClickListener {
            finish()
        }
        setListeners()
        setObservers()
    }


    private fun setListeners() {
        binder.layout1.tvKycVerification.setOnClickListener(this)
        binder.layout2.tvEditLabel.setOnClickListener(this)
        binder.layout1.tvUpdatePhone.setOnClickListener(this)
        binder.layout1.tvUpdateEmail.setOnClickListener(this)
        binder.layout1.tvPasswordValue.setOnClickListener(this)
    }

    private fun setKycLabel(kycStatus: String) {
        binder.layout1.tvKycVerification.apply {
            text = kycStatus
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvKycVerification -> {
                if (!preferenceManager.isKycCreated()) {
                    openActivity(KYCDetailsActivity::class.java)
                }
            }
            R.id.tv_edit_label -> {
                openActivity(SettingsEditAddressActivity::class.java) {
                    putString(ReltimeConstants.STATE, state)
                    putString(ReltimeConstants.CITY, city)
                    putString(ReltimeConstants.ADDRESS1, address1)
                    putString(ReltimeConstants.ADDRESS2, address2)
                }
            }
            R.id.tvUpdateEmail -> {
                openActivity(SettingsUpdateEmailMobileNumberActivity::class.java) {
                    putSerializable(
                        "changeSelection",
                        com.accubits.reltime.utils.Utils.Credentials.EMAIL
                    )
                }
            }
            R.id.tvUpdatePhone -> {
                openActivity(SettingsUpdateEmailMobileNumberActivity::class.java) {
                    putSerializable(
                        "changeSelection",
                        com.accubits.reltime.utils.Utils.Credentials.PHONE
                    )
                }
            }
            R.id.tv_password_value -> {
              showToast(resources.getString(R.string.this_feature_will_available_soon)) // openActivity(SettingsChangePasswordActivity::class.java)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isNetworkAvailable(this)!!) {
            binder.progressBar.visibility = View.VISIBLE
            viewModel.getProfile(preferenceManager.getApiKey())
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }

    }

    private fun setObservers() {
        viewModel.userSuccessModel.observe(this) { model ->
            var responseMessage: String = ReltimeConstants.EMPTY
            binder.progressBar.visibility = View.GONE
            if (model.success) {
                if (model.result != null) {
                    binder.layout1.tvFullnameValue.text = model.result!!.full_name
                    if (model.result!!.email != null)
                        binder.layout1.tvEmailValue.text = model.result!!.email
                    if (model.result!!.date_of_birth != null)
                        binder.layout1.tvDobValue.text = model.result!!.date_of_birth
                    if (model.result!!.contact_number != null)
                        binder.layout1.tvPhoneValue.text = model.result!!.contact_number
                    if (model.result!!.location != null)
                        binder.layout1.tvCountryValue.text = model.result!!.location
                    if (model.result!!.state != null) {
                        binder.layout2.tvState.text = model.result!!.state
                        state = model.result!!.state!!
                    }
                    if (model.result!!.address1 != null) {
                        binder.layout2.tvAddress.text = model.result!!.address1
                        address1 = model.result!!.address1!!
                    }
                    if (model.result!!.address2 != null) {
                        binder.layout2.tvAddressTwo.text = model.result!!.address2
                        address2 = model.result!!.address2!!
                    }
                    if (model.result!!.city != null) {
                        binder.layout2.tvCity.text = model.result!!.city
                        city = model.result!!.city!!
                    }
                    preferenceManager.setKycApproved(model?.result?.kyc_approved ?: false)
                    preferenceManager.setKycCreated(model?.result?.kyc_created ?: false)

                    setKycLabel(
                        if (preferenceManager.isKycCreated())
                            model?.result?.kyc_status?.let { resources.getString(R.string.kyc_status,it) } ?: resources.getString(R.string.kyc_not_created) else resources.getString(R.string.complete_kyc))

                    if (model.result!!.image != null) {
                        binder.ivProfilePic.loadImageWithUrl(model.result!!.image) { isError ->
                            if (isError) {
                                binder.ivProfilePic.setImageResource(R.drawable.ic_profile)
                            } else {

                            }
                        }
                    }
                }

            } else {
                when (model.status) {
                    400 -> {
                        responseMessage = resources.getString(R.string.otp_not_correct)
                        Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    }
                    401 -> {
                        responseMessage = resources.getString(R.string.otp_retry)
                        Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}