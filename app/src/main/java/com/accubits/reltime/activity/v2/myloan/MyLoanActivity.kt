package com.accubits.reltime.activity.v2.myloan

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.paymentHistory.PaymentHistoryActivity
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivityMyLoanBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.*
import com.accubits.reltime.stripe.StripeViewModel
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.views.lend.LendSuccessActivity
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smartadapter.SmartEndlessScrollRecyclerAdapter
import smartadapter.viewevent.listener.OnCustomViewEventListener
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MyLoanActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private var from = ""

    private val viewModel by viewModels<MyLoanViewModel>()
    private val viewModelsecond by viewModels<StripeViewModel>()
    private var acceptReject: Boolean = false
    private var id: Int = 0
    private var amountData: String = "0.00"


    private var first = false
    private var adapter: SmartEndlessScrollRecyclerAdapter? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var resultForSuccessLauncher: ActivityResultLauncher<Intent>? = null

    var accountList = mutableListOf<GetLoanAccountSuccessModel.AccountModel>()
    var currentPage: Int = 0;
    var showOpenLoans = true

    var selectedAccount: GetLoanAccountSuccessModel.AccountModel? = null
    var selectedLoan: RowMyBorrowings? = null
    var isToCloseLoan: Boolean = false
    private lateinit var activityMyBorrowBinding: ActivityMyLoanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMyBorrowBinding = ActivityMyLoanBinding.inflate(layoutInflater)
        setContentView(activityMyBorrowBinding.root)

        if (resultLauncher == null) {
            resultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {

                        selectedAccount =
                            activityResult.data!!.getParcelableExtra<GetLoanAccountSuccessModel.AccountModel>(
                                "selectedAccount"
                            )
                        showDocumentBottomSheet(isToCloseLoan, selectedLoan!!)


                    }
                }
        }
        if (resultForSuccessLauncher == null) {
            resultForSuccessLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (adapter != null) {
                        activityMyBorrowBinding.tvTotalBorrows.text = ""
                        currentPage = 0
                        adapter?.clear()
                        adapter?.isEndlessScrollEnabled = true
                    }
                }
        }
        adapter = SmartEndlessScrollRecyclerAdapter
            .empty()
            .setEndlessScrollEnabled(true)
            .setAutoLoadMoreEnabled(true)
            .setOnLoadMoreListener { adapter, loadMoreViewHolder ->
                viewModel.getMyBorrow(preferenceManager.getApiKey(), "${currentPage + 1}")
                myBorrowingsObserver()
            }
            .setLoadMoreLayoutResource(R.layout.custom_loadmore_view)
            .map(RowMyBorrowings::class, LoanViewHolder::class)
            .setLayoutManager(LinearLayoutManager(this@MyLoanActivity))
            .add(OnCustomViewEventListener { event ->
                if (event.view.id == R.id.btnPayNow) {
                    showDocumentBottomSheet(
                        false,
                        event.adapter.getItem(event.position) as RowMyBorrowings
                    )
                } else if (event.view.id == R.id.btnPaymentHistory) {
                    showDocumentBottomSheet(
                        true,
                        event.adapter.getItem(event.position) as RowMyBorrowings
                    )
                } else if (event.view.id == R.id.tvClickHere) {
                    var intent = Intent(this, PaymentHistoryActivity::class.java)
                    var details = event.adapter.getItem(event.position) as RowMyBorrowings
                    intent.putExtra("id", details.id)
                    intent.putExtra("lending", details.lending)
                    intent.putExtra("totalInstalments", details.instalments)
                    intent.putExtra("paidInstalment", details.instalmentsPaid)
                    startActivity(intent)
                }
            }).into(activityMyBorrowBinding.rvLoan)


        if (showOpenLoans) {
            activityMyBorrowBinding.tvOpen.isSelected = true
            activityMyBorrowBinding.tvClosed.isSelected = false
        } else {
            activityMyBorrowBinding.tvClosed.isSelected = true
            activityMyBorrowBinding.tvOpen.isSelected = false
        }
        setSelected()

        activityMyBorrowBinding.tvOpen.setOnClickListener {
            showOpenLoans = true
            setSelected()
        }
        activityMyBorrowBinding.tvClosed.setOnClickListener {
            showOpenLoans = false
            setSelected()
        }

        /*  borrowTokenListPagedAdapter.setOnPayNowClickListener {
              showDocumentBottomSheet(false, it)
              *//*  Log.d("status!!", it.status)
              if (it.status == "Pending" && it.collateral == "ON") {
                  createAlert(it)
              } else if (it.status == "Pending" && it.collateral == "OFF") {
                  createAlertSecond(it)
              } else {
                  val intent = Intent(this, BorrowDetailFromListingActivity::class.java)
                  intent.putExtra(ReltimeConstants.TransactionId, it.id.toString())
                  startActivity(intent)
              }*//*
        }
        borrowTokenListPagedAdapter.setOnCloseClickListener {
            showDocumentBottomSheet(true, it)
            *//*  Log.d("status!!", it.status)
              if (it.status == "Pending" && it.collateral == "ON") {
                  createAlert(it)
              } else if (it.status == "Pending" && it.collateral == "OFF") {
                  createAlertSecond(it)
              } else {
                  val intent = Intent(this, BorrowDetailFromListingActivity::class.java)
                  intent.putExtra(ReltimeConstants.TransactionId, it.id.toString())
                  startActivity(intent)
              }*//*
        }*/

        activityMyBorrowBinding.ivBack.setOnClickListener {
            onBackPressed()
        }


        /* borrowPagingSource.getCountItem {
             if (it == 0) {
                 // activityMyBorrowBinding.tvNoData.visibility = View.VISIBLE
                 activityMyBorrowBinding.tvTotalBorrows.text =
                     String.format(resources.getString(R.string.lbl_tvTotalBorrows), "$it")
                 activityMyBorrowBinding.rvLoan.visibility = View.GONE
             } else {
                 activityMyBorrowBinding.tvTotalBorrows.text =
                     String.format(resources.getString(R.string.lbl_tvTotalBorrows), "$it")
                 //  activityMyBorrowBinding.tvNoData.visibility = View.GONE
                 activityMyBorrowBinding.rvLoan.visibility = View.VISIBLE
             }
         }*/


        viewModel.getMyAccount(preferenceManager.getApiKey())
        updateUiForGetLoanPayAccounts()
    }

    fun myBorrowingsObserver() {
        lifecycleScope.launch {
            viewModel.myBorrowSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        //     notificationBinding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data!!.status == 200) {
                            if (response.data.result.num_pages == response.data.result.current_page) {
                                adapter?.isEndlessScrollEnabled = false
                            }
                            if (!response.data.result.row.isNullOrEmpty()) {
                                currentPage++
                                var filteredData = response.data.result.row.filter {
                                    if (showOpenLoans) {
                                        it.status == "Active"
                                    } else {
                                        it.status == "Closed"
                                    }

                                }

                                if (filteredData.isNullOrEmpty() && adapter?.itemCount == 1) {
                                    adapter?.setItems(mutableListOf<GetLoanAccountSuccessModel.AccountModel>())
                                    if (response.data.result.num_pages != response.data.result.current_page) {
                                        viewModel.getMyBorrow(
                                            preferenceManager.getApiKey(),
                                            "${currentPage + 1}"
                                        )
                                        myBorrowingsObserver()
                                    }
                                } else {
                                    if (adapter!!.itemCount > 0) {
                                        adapter?.addItems(filteredData.toMutableList())
                                    } else {
                                        adapter?.setItems(filteredData.toMutableList())
                                    }
                                    Handler().postDelayed({
                                        activityMyBorrowBinding.tvTotalBorrows.text =
                                            getString(
                                                R.string.lbl_tvTotalBorrows,
                                                "${adapter!!.itemCount}"
                                            )

                                    }, 1000)
                                }

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

    fun setSelected() {
        if (showOpenLoans) {
            activityMyBorrowBinding.tvOpen.isSelected = true
            activityMyBorrowBinding.tvClosed.isSelected = false
        } else {
            activityMyBorrowBinding.tvClosed.isSelected = true
            activityMyBorrowBinding.tvOpen.isSelected = false
        }
        activityMyBorrowBinding.tvTotalBorrows.text = ""
        currentPage = 0
        adapter?.clear()
        adapter?.isEndlessScrollEnabled = true
    }


    private fun updateUiForGetLoanPayAccounts() {
        lifecycleScope.launch {
            viewModel.myAccountsSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        //   activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        // activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            response.data.result.wallets.isWallet = true
                            selectedAccount = response.data.result.wallets
                            accountList.add(response.data.result.wallets)
                            accountList.addAll(response.data.result.jointAccounts)
                        } else {
                            Toast.makeText(
                                this@MyLoanActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        //  activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        /*Toast.makeText(
                            activity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.transactionApprovalSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        if (response.data != null && response.data.status == 200 && response.data.success && response.data.result != null) {

                            var keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            viewModel.doSiginTransaction(
                                preferenceManager.getApiKey(),
                                SignTransactionRequest(
                                    keyHash,
                                    "confirmBorrowRequest",
                                    response.data.result!!.Id,
                                    response.data.success
                                )
                            )
                            acceptReject = false
                            siginTransaction()


                        } else {
                            response.data?.let { showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        /*Toast.makeText(
                            activity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }


    private fun updateApproveUi() {
        lifecycleScope.launch {
            viewModel.transactionApprovalSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        if (response.data != null && response.data.status == 200 && response.data.success && response.data.result != null) {
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            viewModel.doSiginTransaction(
                                preferenceManager.getApiKey(),
                                SignTransactionRequest(
                                    keyHash,
                                    null,
                                    null,
                                    null
                                )
                            )
                            siginTransaction()


                        } else {
                            response.data?.let { showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        response.errorMessage?.let { showToast(it) }

                    }
                    else -> {}
                }
            }
        }
    }

    private fun getData() {

        //need to reset the data
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Utils.ToDateDis = ""
        Utils.ToDate = ""
        Utils.FromDateDis = ""
        Utils.FromDate = ""
        Utils.rangeOne = ""
        Utils.rangeTwo = ""
        Utils.tenure = ""
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.ToDateDis = ""
        Utils.ToDate = ""
        Utils.FromDateDis = ""
        Utils.FromDate = ""
        Utils.rangeOne = ""
        Utils.rangeTwo = ""
        Utils.tenure = ""
    }

    private fun siginTransaction() {
        lifecycleScope.launch {
            viewModel.siginInTransactionSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            if (from.equals("close")) {
                                if (!first) {
                                    viewModelsecond.doCloseLoanv2(
                                        preferenceManager.getApiKey(),
                                        NewInstallementRequest(
                                            borrowing_id = selectedLoan!!.id,
                                            payment_amount = amountData.toDouble(),
                                            address = selectedAccount!!.address,
                                            transaction_for = "CloseLoan",
                                            method = if (selectedAccount?.isWallet == true) "Wallet" else "JointAccount"
                                        )
                                    )
                                    updateCloseLoan()
                                } else {
                                    first = false
                                    openSuccessPage(
                                        response.data.result.details, resources.getString(
                                            R.string.loan_closed_
                                        )
                                    )
                                }
                            } else if (from.equals("due")) {
                                if (!first) {
                                    viewModelsecond.doCloseLoanv2(
                                        preferenceManager.getApiKey(),
                                        NewInstallementRequest(
                                            borrowing_id = selectedLoan!!.id,
                                            payment_amount = amountData.toDouble(),
                                            address = selectedAccount!!.address,
                                            transaction_for = "LoanRepayment",
                                            method = if (selectedAccount?.isWallet == true) "Wallet" else "JointAccount"
                                        )
                                    )

                                    updateCloseLoan()
                                } else {
                                    first = false
//                                    openSuccessPage(
//                                        response.data.result.details, resources.getString(
//                                            R.string.payment_received
//                                        )
//                                    )
                                    openSuccessPage(
                                        response.data.result.details, resources.getString(
                                            R.string.payment_successful
                                        )
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    this@MyLoanActivity,
                                    response.data.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                if (acceptReject) {
                                    viewModel.doCloseOrAcceptColatral(
                                        preferenceManager.getApiKey(),
                                        CollatralRequestModel(
                                            id,
                                            true,
                                            null
                                        )
                                    )
                                    updateUi()
                                } else {
                                    activityMyBorrowBinding.progressBarContainer.visibility =
                                        View.GONE
                                    getData()
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@MyLoanActivity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()

                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        Toast.makeText(
                            this@MyLoanActivity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun openSuccessPage(details: Details?, title: String) {
        val intent =
            Intent(
                this@MyLoanActivity,
                LendSuccessActivity::class.java
            )

        intent.putExtra(
            LendSuccessActivity.MONTHLY_PAY,
            resources.getString(R.string.n_n,
                details?.coinCode?.let { Utils.coinCodeWithSymbol(this, it,details.symbol) }, details?.installment_amount)
        )
        intent.putExtra(
            LendSuccessActivity.TITLE, title
        )
        intent.putExtra(
            LendSuccessActivity.TRANSACTION_ID,
            details?.loan_id.toString()
        )
        intent.putExtra(
            LendSuccessActivity.AMOUNT,
            resources.getString(R.string.n_n,  details?.coinCode?.let { Utils.coinCodeWithSymbol(this, it,details.symbol) }, details?.rto_amount)
        )
        intent.putExtra(
            LendSuccessActivity.INSTALLMENT,
            details?.installments.toString()
        )
        intent.putExtra(
            LendSuccessActivity.INTEREST,
            details?.interest_rate
        )
        intent.putExtra(
            LendSuccessActivity.COLLATERAL,
            details?.collateral
        )
        intent.putExtra(
            LendSuccessActivity.DATE,
            details?.timestamp?.let { Utils.getDateCurrentTimeZone1(it) }
        )
        resultForSuccessLauncher?.launch(intent)
    }

    fun showDocumentBottomSheet(toCloseLoan: Boolean, data: RowMyBorrowings) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pay_loan)

        selectedLoan = data

        isToCloseLoan = toCloseLoan

        val tvBorrowdAmount = dialog.findViewById<AppCompatTextView>(R.id.tvBorrowedAmountValue)
        val tvTitle = dialog.findViewById<AppCompatTextView>(R.id.tvConfirmPaymentTitle)
        val tvMonthlyInstallmentsTitle =
            dialog.findViewById<AppCompatTextView>(R.id.tvMonthlyInstallments)
        val tvBorrowedAmountTitle = dialog.findViewById<AppCompatTextView>(R.id.tvBorrowedAmount)
        val tvMonthlyInstallments =
            dialog.findViewById<AppCompatTextView>(R.id.tvMonthlyInstallmentsValue)


        val tvAccountName =
            dialog.findViewById<AppCompatTextView>(R.id.tvAccountName)
        val tvBalance =
            dialog.findViewById<AppCompatTextView>(R.id.tvBalance)
        val vSelectedAccount =
            dialog.findViewById<ConstraintLayout>(R.id.vSelectedAccount)
        val tvRTO =
            dialog.findViewById<AppCompatTextView>(R.id.tvRTO)
        tvAccountName.text = selectedAccount?.name?.convertRTOtoEURO()
        tvBalance.text = selectedAccount?.rto_balance
        tvRTO.text = selectedAccount?.coinCode?.let {
            Utils.coinCodeWithSymbol(tvRTO.context,
                it,selectedAccount?.symbol)
        }

        val btnPay = dialog.findViewById<AppCompatButton>(R.id.btnPay)
        val ivClose = dialog.findViewById<AppCompatImageView>(R.id.ivClose)

        vSelectedAccount.setOnClickListener {

            var list = arrayListOf<GetLoanAccountSuccessModel.AccountModel>()
            list.addAll(accountList)
            var intent = Intent(this, SelectAccountActivity::class.java)
            list.forEach {
                it.isSelected = it.id == selectedAccount?.id
            }
            intent.putParcelableArrayListExtra("data", list)
            // intent.putExtra("selectedAccount",selectedAccount)
            resultLauncher?.launch(intent)
            dialog.dismiss()
        }
        /* val rvAccount = dialog.findViewById<RecyclerView>(R.id.rvAccounts)
         SmartRecyclerAdapter
             .items(accountList)
             .map(GetLoanAccountSuccessModel.AccountModel::class, LoanAccountsViewHolder::class)
             .add(OnCustomViewEventListener { event ->
                 selectedAccount =
                     event.adapter.getItem(event.position) as GetLoanAccountSuccessModel.AccountModel
             })
             .into<SmartRecyclerAdapter>(rvAccount)*/

        if (toCloseLoan) {
            tvTitle.setText(R.string.lbl_closeLoan)
            tvBorrowedAmountTitle.setText(R.string.lbl_tvBorrowedAmount)
            tvMonthlyInstallmentsTitle.setText(R.string.lbl_remaining_amount)
            data.pending_amount?.let { amountData = it }
            id = data.id
            tvMonthlyInstallments.text =
                "${Utils.coinCodeWithSymbol(tvMonthlyInstallments.context,data.coinCode,data.symbol)} ${data.pending_amount}"
            tvBorrowdAmount.text = "${Utils.coinCodeWithSymbol(tvMonthlyInstallments.context,data.coinCode,data.symbol)} ${data.rto_amount}"
            btnPay.setText(R.string.proceed_to_close_loan)
        } else {
            btnPay.setText(R.string.proceed_to_pay)
            tvBorrowedAmountTitle.setText(R.string.lbl_last_payment_on)
            if (data.last_instalment_paid == null) {
                tvBorrowdAmount.text = "--"
            } else {
                tvBorrowdAmount.text = Utils.getDateCurrentTimeZone1(data.last_instalment_paid)
            }

            tvTitle.setText(R.string.lbl_tvConfirmPaymentTitle)
            //  tvBorrowedAmountTitle.visibility=View.INVISIBLE
            tvMonthlyInstallmentsTitle.setText(R.string.lbl_tvMonthlyInstallments)
            amountData = data.instalment_amount ?: "0.00"
            id = data.id
            tvMonthlyInstallments.text =
                "${
                    Utils.coinCodeWithSymbol(
                        tvMonthlyInstallments.context,
                        data.coinCode,
                        data.symbol
                    )
                } ${amountData}"
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        btnPay.setOnClickListener {
            if (selectedAccount == null) {
                Toast.makeText(this, "Please select account", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (amountData.toDouble() > selectedAccount!!.rto_balance.toDouble()) {
                Toast.makeText(this, "No sufficient balance", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            from = if (toCloseLoan) {
                "close"
            } else {
                "due"
            }
           // viewModel.performCheckMpin(preferenceManager.getApiKey())
            validateMPIN()
            dialog.dismiss()
        }
        dialog.apply {
            show()
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.BottomDialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }
    }

    private fun validateMPIN(){
        val intents = Intent(
            this@MyLoanActivity,
            MpinValidateActivity::class.java
        )
        intents.putExtra(ReltimeConstants.RETURN_VALUE, true)
        mpinVerificationLauncher.launch(intents)
    }

    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            doCloseLoan(amountData.toDouble(), selectedAccount!!, selectedLoan!!)
        }
    }

    private fun doCloseLoan(
        amount: Double,
        accountDetails: GetLoanAccountSuccessModel.AccountModel,
        data: RowMyBorrowings
    ) {
        if (Utils.isNetworkAvailable(this)!!) {

            if (selectedAccount?.isWallet == true) {
                viewModel.doWalletApprovalInstallement(
                    preferenceManager.getApiKey(),
                    InstallementRequest(amount, selectedAccount!!.coinCode, "instalment")
                )
            } else {
                viewModel.doJointAccountApprovalForInstallments(
                    preferenceManager.getApiKey(),
                    InstallementRequest(
                        amount,
                        selectedAccount!!.coinCode,
                        joint_account = selectedAccount!!.address
                    )
                )
            }

            upadteSuccess()
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }

    }

    private fun upadteSuccess() {
        lifecycleScope.launch {
            viewModel.transactionApprovalSuccessModelInstalment.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        when (response.data!!.status) {
                            200 -> {
                                Log.d("Toast_Msg", response.data.message)
                                response.data.result?.let {
                                    val keyHash =
                                        Utils.getKeyHasForTransaction(
                                            it.data,
                                            preferenceManager
                                        )
                                    viewModel.doSiginTransaction(
                                        preferenceManager.getApiKey(),
                                        SignTransactionRequest(
                                            keyHash,
                                            null,
                                            null,
                                            null
                                        )
                                    )
                                    siginTransaction()
                                }
                            }
                            400 -> {
                                Log.d("Toast_Msg_Error", response.data.message)
                                Toast.makeText(
                                    this@MyLoanActivity,
                                    response.data.message,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                            else -> {
                                Toast.makeText(
                                    this@MyLoanActivity,
                                    response.data.message,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateCloseLoan() {
        lifecycleScope.launch {
            viewModelsecond.loanCloseSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        when (response.data!!.status) {
                            200 -> {
                                /* Toast.makeText(
                                     this@BorrowDetailFromListingActivity,
                                     "Instalment Paid Successfully",
                                     Toast.LENGTH_LONG
                                 )
                                     .show()*/
                                if (response.data.success && response.data.result != null) {
                                    first = true
                                    val keyHash =
                                        Utils.getKeyHasForTransaction(
                                            response.data.result!!.data,
                                            preferenceManager
                                        )


                                    if (from == "close") {
                                        viewModel.doSiginTransaction(
                                            preferenceManager.getApiKey(),
                                            SignTransactionRequest(
                                                keyHash,
                                                "closeLoanV2",
                                                response.data.result!!.Id,
                                                response.data.result!!.success
                                            )
                                        )
                                    } else {
                                        viewModel.doSiginTransaction(
                                            preferenceManager.getApiKey(),
                                            SignTransactionRequest(
                                                keyHash,
                                                "loanRepaymentV2",
                                                response.data.result!!.Id,
                                                response.data.result!!.success
                                            )
                                        )
                                    }
                                    siginTransaction()


                                } else {
                                    activityMyBorrowBinding.progressBarContainer.visibility =
                                        View.GONE
                                    Toast.makeText(
                                        this@MyLoanActivity,
                                        response.data.message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                            400 -> {
                                activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                                Log.d("Toast", "Faild status 400 = " + response.data.message)
                                if (response.data.message != null) {
                                    Toast.makeText(
                                        this@MyLoanActivity,
                                        response.data.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else -> {
                                activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                                Toast.makeText(
                                    this@MyLoanActivity,
                                    response.data.message,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        activityMyBorrowBinding.progressBarContainer.visibility = View.GONE
                        /*Toast.makeText(
                            this@CheckoutActivity,
                            getString(R.string.some_thing_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()*/
                    }
                    else -> {}
                }
            }
        }
    }

}
