package com.accubits.reltime.views.rto

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityRtosendWyreSuccessBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.CheckoutSuccessModel
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RTOSendWyreSuccessActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CHECKOUT_ID = "checkout_id"
        const val CHECKOUT_API_INTERVAL = 20000L
    }

    private lateinit var binding: ActivityRtosendWyreSuccessBinding
    private lateinit var checkoutSuccessModel: CheckoutSuccessModel
    private val viewModel by viewModels<BuyRtoViewModel>()
    private var checkOutId = ""
    private var isDoneVisible = true

    @Inject
    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityRtosendWyreSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        collectGetCheckout()
        getCheckOutData()
    }

    private fun init() {
        intent.getParcelableExtra<CheckoutSuccessModel>("checkoutSuccessModel")?.let {
            checkoutSuccessModel = it
            checkoutSuccessModel.result?.id?.let { id -> checkOutId = id.toString() }
            setUpUI()
        } ?: intent.getStringExtra(EXTRA_CHECKOUT_ID)?.let {
            checkOutId = it
            isDoneVisible=false
        }

    }

    private fun setUpUI() {
        binding.tvInstructions.text =
            checkoutSuccessModel.result?.paymentInstructions
        binding.tvSourceCurrency.text =
            checkoutSuccessModel.result?.sourceCurrency.toString()

        binding.tvAmount.text =
            checkoutSuccessModel.result?.amount.toString()

        binding.tvDestCurrency.text =
            checkoutSuccessModel.result?.destCurrency.toString()

        binding.tvConvertedAmount.text =
            checkoutSuccessModel.result?.displayValue.toString()

        binding.tvDestAddress.text =
            checkoutSuccessModel.result?.destCurrencyAddress.toString()

        binding.tvSourceAddress.text =
            checkoutSuccessModel.result?.sourceCurrencyAddress.toString()

        binding.tvId.text =
            checkoutSuccessModel.result?.id.toString()
        setDepositStatus()
    }

    private fun setDepositStatus() {
            when(checkoutSuccessModel.result?.txnStatus){
                0->{//pending
                    binding.tvTimer.text = resources.getString(R.string.pending)
                    binding.ivStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.rto_deposit_pending
                        )
                    )
                }
                1->{//complete
                    binding.tvTimer.text = resources.getString(R.string.completed)
                    binding.ivStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.rto_deposit_success
                        )
                    )
                }
                2->{//cancel
                    binding.tvTimer.text = resources.getString(R.string.cancelled)
                    binding.ivStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.rto_deposit_cancelled
                        )
                    )                }
            }
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnDone.setOnClickListener { finish() }
        binding.ivCopy.setOnClickListener {
            this.copyToClipBoard(
                "Address",
                checkoutSuccessModel.result?.sourceCurrencyAddress!!.toString()
            )
        }

        binding.ivCopyAmt.setOnClickListener {
            this.copyToClipBoard(
                "Converted Amount",
                checkoutSuccessModel.result?.displayValue!!.toString()
            )
        }
    }

    private fun collectGetCheckout() {
        lifecycleScope.launch {
            viewModel.getCheckoutResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        if (!::checkoutSuccessModel.isInitialized) {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.scrollView.visibility = View.GONE
                            binding.btnDone.visibility = View.GONE
                        }
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            binding.scrollView.visibility = View.VISIBLE
                            if (isDoneVisible)
                            binding.btnDone.visibility = View.VISIBLE
                            checkoutSuccessModel = data
                            setUpUI()
                            if (checkoutSuccessModel.result?.txnStatus ==0) {
                                delay(CHECKOUT_API_INTERVAL)
                                getCheckOutData()
                            }
                        } else {
                            data?.message?.let { showToast(it) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }


    private fun getCheckOutData() {
        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.walletGetCheckout(
                preferenceManager.getApiKey(),
                checkOutId.toInt()
            )
        }
    }
}