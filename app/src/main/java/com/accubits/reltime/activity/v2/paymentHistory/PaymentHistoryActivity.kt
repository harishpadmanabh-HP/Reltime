package com.accubits.reltime.activity.v2.paymentHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.myloan.LoanViewHolder
import com.accubits.reltime.activity.v2.myloan.MyLoanViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityMyLoanBinding
import com.accubits.reltime.databinding.ActivityPaymentHistoryBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.MyPaymentHistory
import com.accubits.reltime.models.RowMyBorrowings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smartadapter.SmartEndlessScrollRecyclerAdapter
import smartadapter.viewevent.listener.OnCustomViewEventListener
import javax.inject.Inject

@AndroidEntryPoint
class PaymentHistoryActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<PaymentHistoryViewModel>()

    lateinit var adapter: SmartEndlessScrollRecyclerAdapter

    var currentPage: Int = 0;
    private lateinit var dataBinding: ActivityPaymentHistoryBinding

    var id: Int? = null
    var lending: Int? = null
    var totalInstalments: String? = null
    var paidInstalment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)

        dataBinding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)


        id = intent.getIntExtra("id", 0)
        lending = intent.getIntExtra("lending", 0)
        if (intent.hasExtra("totalInstalments"))
            totalInstalments = intent.getStringExtra("totalInstalments")
        if (intent.hasExtra("paidInstalment"))
            paidInstalment = intent.getStringExtra("paidInstalment")

        if (totalInstalments == null) {
            dataBinding.tvPaymentHistoryCount.visibility = View.GONE
        } else {
            dataBinding.tvPaymentHistoryCount.text =
                "${(totalInstalments!!.toInt() - paidInstalment!!.toInt())} Left (Out of $totalInstalments)"

        }
        dataBinding.tvTitle.text = if (lending == 0) {
            "Loan payment history"
        } else {
            "Borrow ID #${id}"
        }

        dataBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        adapter = SmartEndlessScrollRecyclerAdapter
            .empty()
            .setEndlessScrollEnabled(true)
            .setAutoLoadMoreEnabled(true)
            .setOnLoadMoreListener { adapter, loadMoreViewHolder ->
                viewModel.getMyLoanHistory(
                    preferenceManager.getApiKey(),
                    "${currentPage + 1}",
                    "${id}"
                )
                paymentHistoryObserver()
            }
            .setLoadMoreLayoutResource(R.layout.custom_loadmore_view)
            .map(MyPaymentHistory::class, PaymentHistoryViewHolder::class)
            .setLayoutManager(LinearLayoutManager(this@PaymentHistoryActivity))
            .add(OnCustomViewEventListener { event ->
                /*  if (event.view.id==R.id.btnPayNow) {
                      showDocumentBottomSheet(false,event.adapter.getItem(event.position) as RowMyBorrowings)
                  }else if(event.view.id==R.id.btnPaymentHistory){
                      showDocumentBottomSheet(true,event.adapter.getItem(event.position) as RowMyBorrowings)
                  }*/
            }).into(dataBinding.rvPaymentHistory)

    }

    fun paymentHistoryObserver() {
        lifecycleScope.launch {
            viewModel.paymentHistorySuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        //     notificationBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data!!.status == 200) {
                            if (response.data.result.num_pages == response.data.result.current_page) {
                                adapter.isEndlessScrollEnabled = false
                            }
                            if (!response.data.result.row.isNullOrEmpty()) {
                                currentPage++

                                if (adapter.itemCount > 0) {
                                    adapter.addItems(response.data.result.row)
                                } else {
                                    adapter.setItems(response.data.result.row.toMutableList())
                                }
                                /*    Handler().postDelayed({
                                        activityMyBorrowBinding.tvTotalBorrows.text =
                                            getString(
                                                R.string.lbl_tvTotalBorrows,
                                                "${adapter.itemCount}"
                                            )

                                    }, 1000)*/
                            } else {
                                //need to show the empty view here
                            }
                        }
                    }
                    ApiCallStatus.ERROR -> {

                    }

                    else -> {}
                }
            }
        }
    }

}