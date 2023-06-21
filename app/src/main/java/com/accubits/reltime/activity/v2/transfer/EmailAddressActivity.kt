package com.accubits.reltime.activity.v2.transfer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityEmailAddressBinding
import com.accubits.reltime.databinding.ActivityEmailAddressBinding.*
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.SearchUserResponseModel
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class EmailAddressActivity : AppCompatActivity() {
    private val viewModel by viewModels<TransferViewModel>()
    private lateinit var binding: ActivityEmailAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        observer()
        actions()

    }

    private fun actions() {
        binding.appCompatTextView2.markRequiredInRed()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnContinue.setOnClickListener {
            val emailAddress = binding.etEmail.text.toString().lowercase()
            if (!Utils.isNetworkAvailable(this)!!) {
                showToast(getString(R.string.please_check_net))
            } else if (emailAddress.isEmpty()) {
                showToast(getString(R.string.please_enter_email_address))
            } else if (!Utils.isValidEmail(emailAddress)) {
                showToast(getString(R.string.please_enter_valid_Email))
            } else if (emailAddress.isEmpty()) {
                showToast(getString(R.string.enter_wallet_addresss))
            } else {
                viewModel.verifyEmail(emailAddress)
            }
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.verifyEmailFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.btnContinue.setButtonEnable(false)
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.btnContinue.setButtonEnable(true)
                        binding.progressBar.visibility = View.GONE
                        if (response.data?.status == 200) {
                            response.data.result.let { result ->
                                responseData(result)
                            }

                        } else {
                            showToast(response.data?.message)
                        }
                    }

                    ApiCallStatus.ERROR -> {
                        binding.btnContinue.setButtonEnable(true)
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {

                    }
                }
            }
        }


    }

    private fun responseData(result: SearchUserResponseModel.Result) {
        openActivity(SendAmountActivity::class.java, shouldFinish = true) {
            putString(
                TransferObject.RECEIVER,
                result.email
            )
            putString(
                TransferObject.NAME,
                result.name
            )
            putString(
                TransferObject.USERID,
                result.userId
            )
            putString(
                TransferObject.PROFILE_IMAGE,
                result.profileImage
            )
            putString(
                TransferObject.CONTACT_TYPE,
                Utils.TransferContactType.EMAIL.contactType
            )

        }
    }

}