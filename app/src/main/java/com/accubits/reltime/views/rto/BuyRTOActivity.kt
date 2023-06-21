package com.accubits.reltime.views.rto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityBuyRtoactivityBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.CheckoutApiRequest
import com.accubits.reltime.models.CheckoutSuccessModel
import com.accubits.reltime.models.WyreCheckout
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.home.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BuyRTOActivity : AppCompatActivity() {
    private lateinit var activityBuyRtoactivityBinding: ActivityBuyRtoactivityBinding
    private val viewModel by viewModels<BuyRtoViewModel>()
    private val spinnerItemArray = arrayListOf(
        "EUR",
        /*  "BTC",
          "ETH",*/
    )
    lateinit var checkoutSuccessModel: CheckoutSuccessModel
    private lateinit var wyreCheckout: WyreCheckout
    private val dashboardViewModel by viewModels<DashboardViewModel>()

    @Inject
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBuyRtoactivityBinding = ActivityBuyRtoactivityBinding.inflate(layoutInflater)
        setContentView(activityBuyRtoactivityBinding.root)
        activityBuyRtoactivityBinding.ivBack.setOnClickListener {
            finish()
        }
        activityBuyRtoactivityBinding.etRto.setRawInputType(InputType.TYPE_CLASS_NUMBER)

        activityBuyRtoactivityBinding.btBuy.setOnClickListener {
            if (activityBuyRtoactivityBinding.etEuro.text.toString()
                    .trim() != "" && activityBuyRtoactivityBinding.etEuro.text.toString() != "0" /*&& !activityBuyRtoactivityBinding.etEuro.text.toString()
                    .contains(".") */ && !activityBuyRtoactivityBinding.etEuro.text.toString()
                    .contains(",")
            ) {
                if (activityBuyRtoactivityBinding.etRto.text.toString().toDouble() > 0) {
                    if (activityBuyRtoactivityBinding.etRto.text.toString().toDouble() <= 99999) {
                        if (activityBuyRtoactivityBinding.tvDestCurrency.text.toString() == "EUR")
                            doWyreCheckout()//doBuy()
                        else
                            doSendWyre()
                    } else {
                        Toast.makeText(
                            this,
                            "Amount can't be greater than 99999",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Amount can't be 0", Toast.LENGTH_LONG).show()

                }

            } else if (activityBuyRtoactivityBinding.tvDestCurrency.text.toString() == "EUR") {
                when {
                    activityBuyRtoactivityBinding.etEuro.text.toString().contains(".") -> {
                        Toast.makeText(this, "Enter valid amount", Toast.LENGTH_LONG).show()
                    }
                    activityBuyRtoactivityBinding.etEuro.text.toString().contains(".") -> {
                        Toast.makeText(this, "Enter valid amount", Toast.LENGTH_LONG).show()
                    }
                    activityBuyRtoactivityBinding.etEuro.text.toString() == "0" -> {
                        Toast.makeText(this, "Amount can't be 0", Toast.LENGTH_LONG).show()

                    }
                    else -> {
                        Toast.makeText(this, "Enter the amount", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
        activityBuyRtoactivityBinding.etRto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.toString().trim() != "" && !s.toString().trim().contains(".") && !s.toString()
                        .trim()
                        .contains(",") && s.toString() != "-" && !s.toString()
                        .contains("-") && !s.toString().contains(" ")
                ) {
                    if (s.toString().trim().toDouble() >= 0) {
                        if (activityBuyRtoactivityBinding.tvDestCurrency.text != "EUR")
                            viewModel.convertCurrency(
                                preferenceManager.getApiKey(),
                                activityBuyRtoactivityBinding.tvDestCurrency.text.toString(),
                                activityBuyRtoactivityBinding.etRto.text.toString()
                            )
                        else
                            setData(activityBuyRtoactivityBinding.etEuro, s.toString())
                        activityBuyRtoactivityBinding.btBuy.visibility = View.VISIBLE
                    } else {
                        activityBuyRtoactivityBinding.btBuy.visibility = View.GONE
                    }
                } else {
                    if (s.toString().trim() == "") {
                        activityBuyRtoactivityBinding.etEuro.text = s.toString().trim()
                    }
                    activityBuyRtoactivityBinding.btBuy.visibility = View.GONE
                }
            }

        })

        collectResponse()
        collectBalance()
        dashboardViewModel.getDashboardDetails(preferenceManager.getApiKey())
    }

    private fun collectBalance() {
        lifecycleScope.launch {
            dashboardViewModel.walletSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityBuyRtoactivityBinding.progressBarBalance.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityBuyRtoactivityBinding.progressBarBalance.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            activityBuyRtoactivityBinding.tvBal.text =
                                resources.getString(
                                    R.string.n_n_,
                                    resources.getString(R.string.balance),
                                    Utils.getRTOAmount(
                                        response.data.result.wallet_balance
                                    )
                                )
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityBuyRtoactivityBinding.progressBarBalance.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectResponse() {
        collectAddCheckout()
        collectGetCheckout()
        collectCurrencyConversion()
        collectWyreCheckout()
        collectWyreStatus()
    }

    private fun collectWyreCheckout() {
        lifecycleScope.launch {
            viewModel.wyreCheckoutResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.VISIBLE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.GONE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(true)
                        if (response.data!!.status == 200 && response.data.success) {
                            wyreCheckout = response.data.result
                            val intent =
                                Intent(this@BuyRTOActivity, WyreWebViewActivity::class.java)
                            intent.putExtra(WyreWebViewActivity.EXTRA_URL, wyreCheckout.url)
                            openWyreWebviewLauncher.launch(intent)
                        } else
                            response.data.message?.let { showToast(it) }
                    }
                    ApiCallStatus.ERROR -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.GONE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(true)
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectWyreStatus() {
        lifecycleScope.launch {
            viewModel.wyreCheckoutStatusResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.VISIBLE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.GONE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(true)
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null) {
                            response.data.message?.let {
                                showToast(it)
                                if (it.contains("completed"))
                                    finish()
                            }
                        } else if (response.data?.status == 400) response.data.message?.let {
                            showToast(
                                it
                            )
                        }
                        else response.errorMessage?.let { showToast(it) }

                        /*  if (response.data!!.status == 200 && response.data.success) {
                         // success
                              showAlertDialog(title = resources.getString(R.string.deposit_completed_title),
                                  description=resources.getString(R.string.deposit_completed),
                                  positiveButton =resources.getString(R.string.ok),
                                  negativeButton =resources.getString(R.string.cancel),
                              icon = R.drawable.ic_success_deposit, cancelable = true){ dialogInterface, i ->
                                  dialogInterface.dismiss()
                                  finish()
                              }
                          }
                          else if (response.data.status == 400 && response.data.success
                              && response.data.message?.contains("Pending") == true
                          ) {
                              showAlertDialog(title = resources.getString(R.string.deposit_pending_title),
                                  description=resources.getString(R.string.deposit_pending),
                                  positiveButton =resources.getString(R.string.go_to_payment),
                                  negativeButton =resources.getString(R.string.cancel),
                                  cancelable = true){ dialogInterface, i ->
                                  dialogInterface.dismiss()
                                  if (i==DIALOG_POSITIVE){
                                      val intent =
                                          Intent(this@BuyRTOActivity, WyreWebViewActivity::class.java)
                                      intent.putExtra(WyreWebViewActivity.EXTRA_URL, wyreCheckout.url)
                                      openWyreWebviewLauncher.launch(intent)
                                  }
                              }
                          }
                          else if (response.data.status == 400 && response.data.success
                              && response.data.message?.contains("cancelled") == true
                          ) {
                              showAlertDialog(title = resources.getString(R.string.deposit_cancelled_title),
                                  description=resources.getString(R.string.deposit_cancelled),
                                  positiveButton =resources.getString(R.string.ok),
                                  negativeButton =resources.getString(R.string.cancel),
                                  cancelable = true){ dialogInterface, i ->
                                  dialogInterface.dismiss()
                              }

                          }
                          else
                              response.data.message?.let { showToast(it) }*/
                    }
                    ApiCallStatus.ERROR -> {
                        activityBuyRtoactivityBinding.progressBar.visibility = View.GONE
                        activityBuyRtoactivityBinding.btBuy.setButtonEnable(true)
                        response.errorMessage?.let { showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            activityBuyRtoactivityBinding.progressBar.visibility = View.VISIBLE
        } else {
            activityBuyRtoactivityBinding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun setData(view: TextView, data: String) {
        view.text = data
    }

    private fun doWyreCheckout() {
        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.createWyreDepositCheckout(
                preferenceManager.getApiKey(),
                activityBuyRtoactivityBinding.etRto.text.toString().trim(),
                "RTO"//activityBuyRtoactivityBinding.textView58.text.toString()
            )
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun doSendWyre() {
        if (Utils.isNetworkAvailable(this)!!) {
            showProgress(true)
            viewModel.walletAddCheckout(
                preferenceManager.getApiKey(),
                CheckoutApiRequest(
                    activityBuyRtoactivityBinding.etRto.text.toString(),
                    activityBuyRtoactivityBinding.tvDestCurrency.text.toString(),
                    "RTO"
                )
            )
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun collectAddCheckout() {
        lifecycleScope.launch {
            viewModel.addCheckoutResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        showProgress(false)
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            showProgress(true)
                            checkoutSuccessModel = data
                            viewModel.walletGetCheckout(
                                preferenceManager.getApiKey(),
                                data.result?.id!!
                            )
                        } else {
                            showProgress(false)
                            showToast(data?.message ?: "Error server response")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showProgress(false)
                        showToast(
                            response.errorMessage
                                ?: "Some error occurred while checkout"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectGetCheckout() {
        lifecycleScope.launch {
            viewModel.getCheckoutResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        showProgress(false)
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            checkoutSuccessModel = data
                            val intent =
                                Intent(this@BuyRTOActivity, RTOSendWyreSuccessActivity::class.java)
                            intent.putExtra("checkoutSuccessModel", checkoutSuccessModel)
                            startActivity(intent)
                            finish()
                        } else {
                            showProgress(false)
                            showToast(data?.message ?: "Error server response")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showProgress(false)
                        showToast(
                            response.errorMessage
                                ?: "Some error occurred while checkout"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun collectCurrencyConversion() {
        lifecycleScope.launch {
            viewModel.currencyConversionResponseFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                    }
                    ApiCallStatus.SUCCESS -> {
                        showProgress(false)
                        val data = response.data
                        if (data != null && data.success && data.status == 200) {
                            for (item in data.result!!) {
                                if (item.coinId.equals(activityBuyRtoactivityBinding.tvDestCurrency.text.toString())) {
                                    activityBuyRtoactivityBinding.etEuro.text =
                                        item.convertedRate.removeSuffix(item.coinId)
                                            .removeSuffix(" ")
                                }
                            }
                        } else {
                            showProgress(false)
                            showToast(data?.message ?: "Error server response")
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showProgress(false)
                        showToast(
                            response.errorMessage
                                ?: "Some error occurred while send wyre connection"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private var openWyreWebviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.checkWyreDepositStatus(preferenceManager.getApiKey(), wyreCheckout.id)
        if (result.resultCode == Activity.RESULT_OK) {

        }
    }
}