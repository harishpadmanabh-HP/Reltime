package com.accubits.reltime.activity.v2.loan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.BottomSheetDialogConfirmBorrowBinding
import com.accubits.reltime.databinding.FragmentBorrowBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.*
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.borrow.MyBorrowViewModel
import com.accubits.reltime.views.lend.ListedTokenViewModel
import com.accubits.reltime.views.lend.pageing.AllLendingPagingSource
import com.accubits.reltime.views.lend.pageing.AllLendingTokenListPagedAdapter1
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BorrowFragment : Fragment() {

    private var _binding: FragmentBorrowBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsViewModel: NotificationsViewModel

    private val progressDialog by lazy {
        Utils.getProgressDialog(requireContext())
    }

    private val filterBottomSheet by lazy {
        BorrowFilterBottomSheetDialog(requireContext(), {
            allLendingPagingSource.setData(
                "",
                Utils.amount_from,
                Utils.amount_to,
                Utils.installments_from,
                Utils.installments_to,
                Utils.interest_rate,
                Utils.no_collateral,
                Utils.collateral_from,
                Utils.collateral_to,
                Utils.amount_sort,
                Utils.interest_rate_sort,
                Utils.loan_term_sort
            )
            getData()
        }, {
            allLendingPagingSource.setData(
                "",
                Utils.amount_from,
                Utils.amount_to,
                Utils.installments_from,
                Utils.installments_to,
                Utils.interest_rate,
                Utils.no_collateral,
                Utils.collateral_from,
                Utils.collateral_to,
                "", "", ""
            )
            getData()
        })
    }


    private val sortBottomSheet by lazy {
        BorrowSortBottomSheet(requireContext()) {
            allLendingPagingSource.setData(
                "",
                Utils.amount_from,
                Utils.amount_to,
                Utils.installments_from,
                Utils.installments_to,
                Utils.interest_rate,
                Utils.no_collateral,
                Utils.collateral_from,
                Utils.collateral_to,
                Utils.amount_sort,
                Utils.interest_rate_sort,
                Utils.loan_term_sort
            )
            getData()
        }
    }

    @Inject
    lateinit var allLendingPagingSource: AllLendingPagingSource

    @Inject
    lateinit var allLendingTokenListPagedAdapter: AllLendingTokenListPagedAdapter1

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private lateinit var confirmBorrowBottomSheet: ConfirmBorrowBottomSheet
    private lateinit var rowMyBorrowings: RowAllLending

    private lateinit var bottomDialogBinding: BottomSheetDialogConfirmBorrowBinding

    private var accept: Boolean = false
    private var reject: Boolean = false
    private var normal: Boolean = false
    private val viewModel by viewModels<ListedTokenViewModel>()
    private val normalBorrowViewModel by viewModels<MyBorrowViewModel>()

    private var amount1: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]
        Utils.borrow = true
        _binding = FragmentBorrowBinding.inflate(inflater, container, false)
        confirmBorrowBottomSheet = ConfirmBorrowBottomSheet(requireActivity())
        bottomDialogBinding = confirmBorrowBottomSheet.binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        updateUi()
    }

    private fun setListeners() {
        binding.tvFilter.setOnClickListener {
            if (!filterBottomSheet.isShowing)
                filterBottomSheet.show()
        }

        binding.tvSortBy.setOnClickListener {
            if (!sortBottomSheet.isShowing)
                sortBottomSheet.show()
        }


        allLendingTokenListPagedAdapter.clickOnBorrowbtn {
            rowMyBorrowings = it
            confirmBorrowBottomSheet.show()
            bottomDialogBinding.tvTitle.text =resources.getString(R.string.confirm_borrow)
            if (it.collateral == "ON")
                bottomDialogBinding.tvDesc.text =
                    resources.getString(
                        R.string.confirm_borrow_with_collateral,
                        it.published_by,
                        Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                        it.rto_amount,
                        it.collateral_amount
                    )
            else
                bottomDialogBinding.tvDesc.text =
                    resources.getString(
                        R.string.confirm_borrow_without_collateral,
                        it.published_by,
                        Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                        it.rto_amount
                    )
            normal = true
            accept = false
            reject = false
        }
        allLendingTokenListPagedAdapter.clickOnAcceptbtn {
            rowMyBorrowings = it
            confirmBorrowBottomSheet.show()
            bottomDialogBinding.tvTitle.text =resources.getString(R.string.confirm_borrow)
            if (it.collateral == "ON")
                bottomDialogBinding.tvDesc.text =
                    resources.getString(
                        R.string.confirm_borrow_with_collateral,
                        it.published_by,
                        Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                        it.rto_amount,
                        it.collateral_amount
                    )
            else
                bottomDialogBinding.tvDesc.text =
                    resources.getString(
                        R.string.confirm_borrow_without_collateral,
                        it.published_by,
                        Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                        it.rto_amount
                    )

            normal = false
            accept = true
            reject = false
        }
        allLendingTokenListPagedAdapter.clickOnRejectbtn {
            rowMyBorrowings = it
            confirmBorrowBottomSheet.show()
            bottomDialogBinding.tvTitle.text =resources.getString(R.string.reject_borrow)
            if (it.collateral == "ON")
                bottomDialogBinding.tvDesc.text =
                    resources.getString(
                        R.string.reject_borrow_with_collateral,
                        it.published_by,
                        Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                        it.rto_amount,
                        it.collateral_amount
                    )
                   // "Are you sure you want to reject the Borrow from ${it.published_by} amount of ${Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol)} ${it.rto_amount} with collateral amount ${it.coinCode} ${it.collateral_amount}?"
            else
                bottomDialogBinding.tvDesc.text =resources.getString(
                    R.string.reject_borrow_without_collateral,
                    it.published_by,
                    Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol),
                    it.rto_amount
                )
                 //   "Are you sure you want to reject the Borrow from ${it.published_by} amount of ${Utils.coinCodeWithSymbol(requireContext(),it.coinCode,it.symbol)} ${it.rto_amount}?"

            normal = false
            accept = false
            reject = true
        }

        bottomDialogBinding.btnCancel.setOnClickListener {

            confirmBorrowBottomSheet.cancel()
        }
        bottomDialogBinding.btnAgree.setOnClickListener {
            confirmBorrowBottomSheet.cancel()
            callMpin(rowMyBorrowings)
        }
        bottomDialogBinding.ivClose.setOnClickListener {

            confirmBorrowBottomSheet.cancel()
        }
    }

    private fun callMpin(item: Any) {
        if (preferenceManager.getMpin()) {
            val intents = Intent(
                requireActivity(),
                MpinValidateActivity::class.java
            )
            if (item is Row)
                intents.putExtra(MpinValidateActivity.EXTRA_SERIALIZABLE_ITEM, item)
            else if (item is RowAllLending)
                intents.putExtra(MpinValidateActivity.EXTRA_SERIALIZABLE_ITEM, item)

            intents.putExtra(ReltimeConstants.RETURN_VALUE, true)
            mpinVerificationLauncher.launch(intents)
        } else activity?.showToast(resources.getString(R.string.please_create_transaction_pin))
    }

    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val item =
                result.data?.getSerializableExtra(MpinValidateActivity.EXTRA_SERIALIZABLE_ITEM)
            view?.post {
                if (item is RowAllLending) {
                    if (item.collateral == "ON"&&!reject)
                        doWalletApproval(item)
                    else switchLendActionAPIs(item)
                }
            }
        }
    }

    private fun doWalletApproval(rowMyBorrowings: RowAllLending) {
       /* val pers = 100 + (2 * rowMyBorrowings.interest_rate.toDouble())
        amount1 = rowMyBorrowings.rto_amount.toDouble() * (pers / 100)*/
        amount1=rowMyBorrowings.collateral_amount.toDouble()
        normalBorrowViewModel.doWalletApproval(
            preferenceManager.getApiKey(),
            WalletCollatral(amount1, rowMyBorrowings.coinCode)
        )
        updateApproveUi()
    }

    private fun updateApproveUi() {
        lifecycleScope.launch {
            normalBorrowViewModel.transactionApprovalSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress()
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data != null && response.data.status == 200 && response.data.result != null) {
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            normalBorrowViewModel.doSiginTransaction(
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
                            hideProgress()
                            response.data?.let { activity?.showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        hideProgress()
                        response.errorMessage?.let { activity?.showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun siginTransaction() {
        lifecycleScope.launch {
            normalBorrowViewModel.siginInTransactionSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress()
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200) {
                            if (response.data.result == null) {
                                activity?.showToast(response.data.message)
                            }
                            if (normal || reject || accept) switchLendActionAPIs(rowMyBorrowings)
                            else {
                                hideProgress()
                                getData()
                            }
                        } else {
                            hideProgress()
                            response.data?.let { activity?.showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        hideProgress()
                        response.errorMessage?.let { activity?.showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUi1() {
        lifecycleScope.launch {
            normalBorrowViewModel.transactionApprovalSuccessModel.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress()
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data != null && response.data.status == 200 && response.data.result != null) {
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            normalBorrowViewModel.doSiginTransaction(
                                preferenceManager.getApiKey(),
                                SignTransactionRequest(
                                    keyHash,
                                    if (normal) "borrow" else "confirmBorrowRequest",
                                    response.data.result!!.Id,
                                    response.data.success
                                )
                            )
                            normal = false
                            accept = false
                            reject = false
                            siginTransaction()
                        } else {
                            hideProgress()
                            response.data?.let { activity?.showToast(it.message) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        hideProgress()
                        response.errorMessage?.let { activity?.showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUi() {
        _binding!!.rvBorrow.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isNetworkAvailable(requireContext())!!) {
            _binding!!.rvBorrow.adapter = allLendingTokenListPagedAdapter
            allLendingPagingSource.setData(
                viewModel.search.value,
                Utils.amount_from,
                Utils.amount_to,
                Utils.installments_from,
                Utils.installments_to,
                Utils.interest_rate,
                Utils.no_collateral,
                Utils.collateral_from,
                Utils.collateral_to,
                Utils.amount_sort,
                Utils.interest_rate_sort,
                Utils.loan_term_sort,
            )
            binding.progressBar.visibility = View.GONE
            getData()

        } else {
            Toast.makeText(activity, getString(R.string.please_check_net), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.getAllLendingPagedData().collectLatest {
                binding.progressBar.visibility = View.GONE
                allLendingTokenListPagedAdapter.submitData(it)
            }


        }
    }

    private fun showProgress() {
        if (!progressDialog.isShowing)
            progressDialog.show()
    }

    private fun hideProgress() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    private fun switchLendActionAPIs(rowMyBorrowings: RowAllLending) {
        if (normal) {
            normalBorrowViewModel.doAcceptOrRejectBorrow(
                preferenceManager.getApiKey(),
                NormalBorrowRequestModel(
                    rowMyBorrowings.id.toString()
                )
            )
            updateUi1()
        } else if (accept) {
            normalBorrowViewModel.doCloseOrAcceptColatral(
                preferenceManager.getApiKey(),
                CollatralRequestModel(
                    rowMyBorrowings.id,
                    true,
                    null
                )
            )
            updateUi1()
        } else if (reject) {

            normalBorrowViewModel.doCloseOrAcceptColatral(
                preferenceManager.getApiKey(),
                CollatralRequestModel(
                    rowMyBorrowings.id,
                    false,
                    null
                )
            )
            updateUi1()
        }
    }

}