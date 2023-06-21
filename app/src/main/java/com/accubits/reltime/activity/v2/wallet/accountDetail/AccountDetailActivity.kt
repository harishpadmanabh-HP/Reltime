package com.accubits.reltime.activity.v2.wallet.accountDetail

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Member
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.Result
import com.accubits.reltime.activity.v2.wallet.TransactionPagedV2Adapter
import com.accubits.reltime.activity.v2.wallet.jointaccount.JointAccountMemberListActivity
import com.accubits.reltime.activity.v2.wallet.transactiondetail.TransactionDetailV2Activity
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.ActivityAccountDetailV2Binding
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.AccountDetailResult
import com.accubits.reltime.utils.Extensions.copyToClipBoard
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.views.rto.RTOSendWyreSuccessActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailV2Binding
    private val viewModel by viewModels<AccountDetailViewModel>()

    @Inject
    lateinit var transactionPagingSource: AccountTransactionPagingSourceV2

    @Inject
    lateinit var transactionPagedAdapter: TransactionPagedV2Adapter

    private var search = ""
    private var accountId: Int? = null
    private var accountType: String? = null
    private var jointAccountDetails: Result? = null
    private val memberList: ArrayList<Member> = ArrayList()

    private val transactionFilterBottomSheet by lazy {
        AccountDetailTransactionBottomSheet(this, {
            transactionPagingSource.setData(
                "",
                Utils.account_date_type, accountId,
                accountType!!,
                Utils.account_transaction_status,
               // Utils.account_date_type
            )
            getTransactionData()
        }, {
            transactionPagingSource.setData(
                "",
                Utils.account_date_type, accountId,
                accountType!!,
                Utils.account_transaction_status,
                //Utils.account_date_type
            )
            getTransactionData()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        observeData()
    }

    override fun onResume() {
        super.onResume()

        if (Utils.isNetworkAvailable(this)!!) {
            binding.rvTransactionHistory.adapter = transactionPagedAdapter
            transactionPagingSource.setData(
                search,
                Utils.account_date_type,
                accountId!!,
                accountType!!, Utils.account_transaction_status,
                //Utils.account_date_type
            )
            getTransactionData()
        } else showToast(getString(R.string.please_check_net))
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.account_transaction_status = ""
        Utils.account_date_type = null
    }

    private fun init() {
        accountType = intent.getStringExtra("accountType")
        accountId = intent.getIntExtra("accountId", 0)
        Utils.account_transaction_status = ""
        Utils.account_date_type = null
        binding.layoutAccountDetailData.imgEditName.visibility = View.GONE
        binding.layoutAccountDetailData.tvAllMembers.visibility = View.GONE
        binding.layoutTitle.imgShare.visibility = View.GONE

        if (accountType == "wallets" || accountType == "cryptoWallet" || accountType == "bankAccounts") {
            if (accountType == "bankAccounts") {
                binding.layoutTitle.tvTitle.text = getString(R.string.bank)
                binding.layoutAccountDetailData.tvMyWalletAccount.text =
                    resources.getString(R.string.bank_bank_account)
                binding.layoutAccountDetailData.tvAccountNumberTitle.text =
                    resources.getString(R.string.bank_account_number_)
            } else binding.layoutTitle.tvTitle.text = getString(R.string.wallet)
            binding.layoutAccountDetailData.tvMembersTitle.visibility = View.GONE
            binding.layoutAccountDetailData.tvMembers.visibility = View.GONE
            binding.layoutAccountDetailData.lineMembers.visibility = View.GONE
            accountType?.let { viewModel.getAccountDetails(accountId!!, it) }
        } else if (accountType == "jointAccounts") {
            binding.layoutTitle.tvTitle.text = getString(R.string.joint_account)
            binding.layoutAccountDetailData.lineBalance.visibility = View.VISIBLE
            binding.layoutAccountDetailData.tvMembersTitle.visibility = View.VISIBLE
            binding.layoutAccountDetailData.tvMembers.visibility = View.VISIBLE
            binding.layoutAccountDetailData.lineMembers.visibility = View.VISIBLE
            binding.layoutAccountDetailData.tvEmail.visibility = View.GONE
            binding.layoutAccountDetailData.tvEmailTitle.visibility = View.GONE
            binding.layoutAccountDetailData.lineEmail.visibility = View.GONE
            viewModel.fetchJointAccounts(accountId!!)
        }

        binding.rvTransactionHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        transactionPagedAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        transactionPagingSource.getCountItem {
            if (it == 0) {
                binding.rvTransactionHistory.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvTransactionHistory.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            }
        }

        transactionPagedAdapter.setOnItemClickListener {
//            if (it.checkoutId == null) {
//                val intent = Intent(this, TransactionDetailActivity::class.java).apply {
//                    putExtra("ITEM", it)
//                }
//                startActivity(intent)
//            } else {//means it is crypto
//                val intent = Intent(this, RTOSendWyreSuccessActivity::class.java).apply {
//                    putExtra(RTOSendWyreSuccessActivity.EXTRA_CHECKOUT_ID, it.checkoutId)
//                }
//                startActivity(intent)
//            }
        }
    }

    private fun getTransactionData() {
        lifecycleScope.launch {
            viewModel.getPagedTransactionData().collectLatest {
                transactionPagedAdapter.submitData(it)
            }
        }
    }

    private fun setListeners() {
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.layoutAccountDetailData.tvAllMembers.setOnClickListener {
            if (memberList.size > 0) {
                val intent = Intent(this, JointAccountMemberListActivity::class.java).apply {
                    putParcelableArrayListExtra("memberList", jointAccountDetails?.members)
                    putExtra("name", jointAccountDetails?.name)
                    putExtra("jointAccountId", jointAccountDetails?.id)
                }
                startActivity(intent)
            }
        }

        binding.layoutFilter.setOnClickListener {
            if (!transactionFilterBottomSheet.isShowing)
                transactionFilterBottomSheet.show()
        }
        transactionPagedAdapter.setOnItemClickListener {
            if (it.checkoutId == null) {
                val intent = Intent(this, TransactionDetailV2Activity::class.java).apply {
                    putExtra("ITEM", it)
                }
                startActivity(intent)
            } else {//means it is crypto
                val intent = Intent(this, RTOSendWyreSuccessActivity::class.java).apply {
                    putExtra(RTOSendWyreSuccessActivity.EXTRA_CHECKOUT_ID, it.checkoutId)
                }
                startActivity(intent)
            }
        }
        binding.layoutAccountDetailData.ivCopy.setOnClickListener {
            copyToClipBoard(
                binding.layoutAccountDetailData.tvAccountNumberTitle.text.toString(),
                binding.layoutAccountDetailData.tvAccountNumber.text.toString()
            )
        }
        binding.bottomSheet.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                binding.rvTransactionHistory.onTouchEvent(p1)
                return true
            }
        })
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.accountDetailResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success!!
                        if (responseOk) {
                            if (response.data != null) {
                                populateValues(response.data.result)
                            }
                        }
                    }

                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.jointAccountDetailsFlow.collectLatest { responseModel ->
                when (responseModel.status) {
                    ApiCallStatus.LOADING -> {

                    }
                    ApiCallStatus.SUCCESS -> {
                        val responseOk =
                            responseModel.data?.status == 200 && responseModel.data.success
                        if (responseOk) {
                            jointAccountDetails = responseModel.data!!.result
                            jointAccountDetails.apply {
                                updateJointAccountUi()
                            }
                        } else {
                            showToast(
                                responseModel.data?.message
                            )
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        showToast(
                            responseModel.errorMessage
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateJointAccountUi() {
        if (jointAccountDetails != null) {
            if (jointAccountDetails!!.members.size > 0) {
                for (item in jointAccountDetails!!.members) {
                    if (item.isAccepted)
                        memberList.add(item)
                }
            }
            binding.layoutAccountDetailData.tvAccountName.text = jointAccountDetails!!.name.convertRTOtoEURO().convertRTOtoEURO()
            binding.layoutAccountDetailData.tvAccountNumber.text = jointAccountDetails!!.address
            binding.layoutAccountDetailData.tvOpenedOn.text =
                Utils.getDateCurrentTimeZone1(jointAccountDetails!!.createdAt, Utils.DATE_FORMAT_DEFAULT)
            binding.layoutAccountDetailData.tvMembers.text=  resources.getString(
                    if (memberList.size <= 1) R.string.n_person else R.string.n_persons,
                    memberList.size.toString()
                )

            if (jointAccountDetails!!.coinCode != null) {
                createStyledAmount(
                    binding.layoutAccountDetailData.tvBalance, Utils.coinCodeWithSymbol(
                        context = this,
                        coinCode = jointAccountDetails!!.coinCode!!,
                        symbol = jointAccountDetails!!.symbol
                    ), jointAccountDetails!!.rtoBalance
                )
            }
        }
    }

    private fun populateValues(result: AccountDetailResult?) {
        if (result != null) {
            binding.layoutTitle.tvTitle.text = result.accountTypeName
            binding.layoutAccountDetailData.tvEmail.text = result.email
            binding.layoutAccountDetailData.tvOpenedOn.text =
                result.createdAt?.let { Utils.getDateCurrentTimeZone1(it, Utils.DATE_FORMAT_DEFAULT) }
            if (accountType == "bankAccounts") {
                binding.layoutAccountDetailData.tvAccountName.text = result.bank_name?.convertRTOtoEURO()?.convertRTOtoEURO()
                binding.layoutAccountDetailData.tvAccountNumber.text = result.account_number

                binding.layoutAccountDetailData.tvBalanceTitle.visibility = View.GONE
                binding.layoutAccountDetailData.tvBalance.visibility = View.GONE
                binding.layoutAccountDetailData.lineBalance.visibility = View.GONE
            }
            else
            {
                binding.layoutAccountDetailData.tvAccountName.text = result.name?.convertRTOtoEURO()?.convertRTOtoEURO()
                binding.layoutAccountDetailData.tvAccountNumber.text = result.address

                if (result.coinCode != null) {
                    createStyledAmount(
                        binding.layoutAccountDetailData.tvBalance, Utils.coinCodeWithSymbol(
                            context = this,
                            coinCode = result.coinCode, symbol = result.symbol
                        ), result.balance!!
                    )
                }
            }
        }
    }

    private fun createStyledAmount(tv: TextView, val1: String, val2: String) {
        val builder = SpannableStringBuilder()
        builder.append(
            buildStyledSpannable(
                val1, size = .9f
            )
        )
        builder.append(" ")
        builder.append(val2)
        tv.setText(builder, TextView.BufferType.SPANNABLE)
    }

    private fun buildStyledSpannable(
        string: String,
        size: Float? = null,
        color: Int? = null
    ): SpannableString {
        val str = SpannableString(string)
        color?.let {
            str.setSpan(
                ForegroundColorSpan(it),
                0,
                str.length,
                0
            )
        }
        size?.let {
            str.setSpan(
                RelativeSizeSpan(it),
                0,
                str.length,
                0
            )
        }
        return str
    }
}