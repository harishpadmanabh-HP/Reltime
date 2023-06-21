package com.accubits.reltime.activity.v2.transfer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.transfer.adapter.TokenCoinAdapter
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityWalletAddressBinding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.convertRTOtoEURO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletAddressBinding
    private val viewModel by viewModels<TransferViewModel>()
    private lateinit var tokenCoinAdapter: TokenCoinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_address)
        binding = ActivityWalletAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        actions()
        observer()

        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.getTokenCoins()

        } else showToast( getString(R.string.please_check_net))

    }

    private fun init(){
        binding.tvWalletAddressTitle.markRequiredInRed()
    }

    private fun actions() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val walletAddress = binding.edtWalletAddress.text.toString().trim()
            if (walletAddress != "") {
                viewModel.walletAddressValidation(
                    walletAddress,
                    viewModel.coinType
                )
            } else {
                showToast(getString(R.string.enter_wallet_addresss))
            }
        }

        binding.tvToken.setOnClickListener {binding.spToken.performClick() }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.walletValidateResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.btnContinue.setButtonEnable(false)
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.btnContinue.setButtonEnable(true)
                        binding.progressBar.visibility = View.GONE
                        if (response.data?.status == 200) {
                            openActivity(SendAmountActivity::class.java, shouldFinish = true) {
                                putString(
                                    TransferObject.RECEIVER,
                                    response.data.result.walletAddress
                                )
                                putString(
                                    TransferObject.NAME,
                                    response.data.result.name
                                )
                                putString(
                                    TransferObject.USERID,
                                    response.data.result.userId
                                )
                                putString(
                                    TransferObject.PROFILE_IMAGE,
                                    response.data.result.profileImage
                                )
                                putString(
                                    TransferObject.CONTACT_TYPE,
                                    Utils.TransferContactType.WALLET.contactType
                                )
                            }
                        } else showToast(response.data?.message)

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

        lifecycleScope.launch {
            viewModel.tokenResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {

                    }
                    ApiCallStatus.SUCCESS -> {
                        loadDataToSpinner(response.data?.result?.coinType as ArrayList<String>)
                    }

                    ApiCallStatus.ERROR -> {

                    }
                    else -> {

                    }
                }
            }
        }

    }

    private fun loadDataToSpinner(coinTypeList: ArrayList<String>?) {
        if (coinTypeList != null) {
            tokenCoinAdapter = TokenCoinAdapter(this, coinTypeList)
            binding.spToken.adapter = tokenCoinAdapter

            binding.spToken.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {

                    viewModel.coinType = coinTypeList[position]
                    binding.tvToken.text=resources.getString(R.string.reltime_n,coinTypeList[position]).convertRTOtoEURO()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}