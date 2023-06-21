package com.accubits.reltime.activity.v2.loan

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityLendingDetail1Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.helpers.Utils.getRTOAmount
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.lend.LendViewModel
import com.accubits.reltime.views.lend.ListedTokenViewModel
import com.accubits.reltime.views.lend.pageing.LendingHistoryListPagedAdapter
import com.accubits.reltime.views.lend.pageing.LendingHistoryPagingSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LendingDetailActivity1 : AppCompatActivity() {
    private lateinit var activitylendDetailBinding: ActivityLendingDetail1Binding

    @Inject
    lateinit var lendingHistoryPagingSource: LendingHistoryPagingSource
    @Inject
    lateinit var lendingHistoryListPagedAdapter: LendingHistoryListPagedAdapter
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val listedTokenViewModel by viewModels<ListedTokenViewModel>()
    private val viewModel by viewModels<LendViewModel>()

    private val mLendId:String by lazy { intent.getStringExtra(ReltimeConstants.TransactionId).toString() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitylendDetailBinding = ActivityLendingDetail1Binding.inflate(layoutInflater)
        setContentView(activitylendDetailBinding.root)

        Utils.LendId = mLendId
        activitylendDetailBinding.rvPaymentHistory.layoutManager =
            LinearLayoutManager(this@LendingDetailActivity1, LinearLayoutManager.VERTICAL, false)


        activitylendDetailBinding.ivBack.setOnClickListener {
            finish()
        }
        funTransactionApproval()

    }

    private fun getPagedData() {
        if (Utils.isNetworkAvailable(this@LendingDetailActivity1)!!) {
           activitylendDetailBinding.rvPaymentHistory.adapter = lendingHistoryListPagedAdapter
            activitylendDetailBinding.progressBar.visibility = View.GONE
            getData()

        } else {
            Toast.makeText(this@LendingDetailActivity1, getString(R.string.please_check_net), Toast.LENGTH_LONG)
                .show()
        }
    }
    private fun getData() {
        lifecycleScope.launch {
            listedTokenViewModel.getLendingHistoryPagedData().collectLatest {
                activitylendDetailBinding.progressBar.visibility = View.GONE
                lendingHistoryListPagedAdapter.submitData(it)
            }


        }
    }
    private fun funTransactionApproval() {
        listedTokenViewModel.getLendDetail(
            preferenceManager.getApiKey(),
            mLendId
        )
        getLendDetail()
    }
    private fun getLendDetail() {
        lifecycleScope.launch {
            listedTokenViewModel.getLendingSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activitylendDetailBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activitylendDetailBinding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            // Utils.validated = false
                            if(!response.data.result.row.isEmpty()) {
                                getPagedData()
                                val data = response.data.result.row[0]
                                val date = Utils.getDateCurrentTimeZone1(data.timestamp.toDouble())
                                    .toString()
                                activitylendDetailBinding.tvTitle.text =
                                    "" + resources.getText(R.string.loan) + " #" +mLendId // data.id
                                activitylendDetailBinding.tvId.text =
                                    "#" + data.id
                                activitylendDetailBinding.tvSmartContractDate.text = date
                                activitylendDetailBinding.tvRto.text =
                                    resources.getString(R.string.n_n,Utils.coinCodeWithSymbol(this@LendingDetailActivity1,
                                        data.coinCode,data.symbol), data.rto_amount.toString())
                                activitylendDetailBinding.tvDebitFrom.text = data.borrowed_by
                                activitylendDetailBinding.tvMonthlyInstallments.text =
                                    data.instalment_amount.toString().toDoubleOrNull()
                                        ?.let { getRTOAmount(it) }
                                activitylendDetailBinding.tvOutstandingBalance.text = data.pending_amount?.let {
                                    getRTOAmount(
                                        it
                                    )
                                }
                                 activitylendDetailBinding.tvIntrest.text = data.interest_rate + "%"
                                activitylendDetailBinding.tvLoanTerm.text =
                                    data.instalments + " months"
                                activitylendDetailBinding.tvCollateral.text = Utils.getCollateralValue(this@LendingDetailActivity1,data.collateral,data.collateral_amount,
                                    Utils.coinCodeWithSymbol(this@LendingDetailActivity1,data.coinCode,data.symbol))

                                activitylendDetailBinding.tvPaymentHistoryCount.text=resources.getString(R.string.n_left_out_of_m
                                ,data.pending_installment,data.instalments)

                            }

                        } else showToast(response.data.toString())
                    }
                    ApiCallStatus.ERROR -> {
                        activitylendDetailBinding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@LendingDetailActivity1,
                            response.errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

