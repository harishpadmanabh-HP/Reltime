package com.accubits.reltime.activity.v2.common.walletpicker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.walletpicker.viewmodel.WalletAddressViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityWalletAddressPickerV2Binding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.makeVisibility
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletAddressPickerV2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletAddressPickerV2Binding

    private val viewModel by viewModels<WalletAddressViewModel>()

    companion object {
        const val EXTRA_WALLET_ADDRESS = "wallet_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletAddressPickerV2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setAction()
        observeFlow()
    }

    private fun setAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnContinue.setOnClickListener {
            val value = binding.edtWalletAddress.text.toString().trim()
            setError(
                value.isEmpty(),
                resources.getString(R.string.please_enter_a_valid_wallet_address)
            )
            if (value.isEmpty())
                return@setOnClickListener
            if (Utils.isNetworkAvailable(this) == true) {
                viewModel.walletAddressValidation("", "")
            } else showToast(getString(R.string.please_check_net))

            /*
            val returnIntent = Intent()
            returnIntent.putExtra(
                EXTRA_WALLET_ADDRESS,
                value
            )
            setResult(RESULT_OK, returnIntent)
            finish()*/
        }
    }

    private fun observeFlow() {
        lifecycleScope.launch {
            viewModel.walletValidateResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.btnContinue.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.btnContinue.setButtonEnable(true)
                        response.data?.message?.let {
                            binding.tvError.text = it
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binding.btnContinue.setButtonEnable(true)
                        response.errorMessage?.let {
                            binding.tvError.text = it
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setError(isError: Boolean, value: String?) {
        value?.let { binding.tvError.text = it }
        binding.tvError.makeVisibility(isError)
        binding.edtWalletAddress.background = ContextCompat.getDrawable(
            this,
            if (isError) R.drawable.basic_corner_error_outline else R.drawable.basic_corner
        )
    }
}