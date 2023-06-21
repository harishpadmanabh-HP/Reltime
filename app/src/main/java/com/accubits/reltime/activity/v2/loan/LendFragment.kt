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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.lending.DirectLendingV2Activity
import com.accubits.reltime.activity.v2.login.ConfirmBottomSheetDialog
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.FragmentLendBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.Row
import com.accubits.reltime.models.RowAllLending
import com.accubits.reltime.models.SignTransactionRequest
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.home.ui.dashboard.DashboardViewModel
import com.accubits.reltime.views.lend.LendViewModel
import com.accubits.reltime.views.lend.ListedTokenViewModel
import com.accubits.reltime.views.lend.pageing.LendingTokenListPagedAdapter1
import com.accubits.reltime.views.lend.pageing.TokenPagingSource
import com.accubits.reltime.views.mpin.MpinValidateActivity
import com.accubits.reltime.views.rto.BuyRTOActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LendFragment : Fragment() {

    private var _binding: FragmentLendBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenPagingSource: TokenPagingSource

    @Inject
    lateinit var lendingTokenLitPagerAdapter: LendingTokenListPagedAdapter1

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val lendViewModel by viewModels<LendViewModel>()
    private val dashboardViewModel by viewModels<DashboardViewModel>()
    private val viewModel by viewModels<ListedTokenViewModel>()

    private var rtoCount: String = "0"

    private val progressDialog by lazy {
        Utils.getProgressDialog(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
        setListeners()
    }

    private fun setListeners() {
        binding.ivAddRtc.setOnClickListener {
            openDeposit()
        }
        binding.tvRto.setOnClickListener {
            openDeposit()
        }
        binding.tvLendToken.setOnClickListener {
            startLendingActivity(DirectLendingV2Activity.LENDING_MARKETPLACE)
        }
        binding.imgLendToMarketPlace.setOnClickListener {
            startLendingActivity(DirectLendingV2Activity.LENDING_MARKETPLACE)
        }

        binding.tvDirectDes.setOnClickListener {
            startLendingActivity(DirectLendingV2Activity.LENDING_CONTACTS)
        }
        binding.imgLendToContacts.setOnClickListener {
            startLendingActivity(DirectLendingV2Activity.LENDING_CONTACTS)
        }

        lendingTokenLitPagerAdapter.clickOnRemoveBtn {
            val desc = if (it.collateral == "ON")
                resources.getString(
                    R.string.remove_confirm_dialog_with_collateral,
                    it.coinCode,
                    it.rto_amount,
                    it.collateral_amount
                )
            else
                resources.getString(
                    R.string.remove_confirm_dialog_without_collateral,
                    it.coinCode,
                    it.rto_amount
                )
            ConfirmBottomSheetDialog(
                requireContext(),
                resources.getString(R.string.confirm_delete),
                desc
            ) { _ ->
                callMpin(it)
            }
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
                if (item is Row)
                    deleteLending(item.id)
            }
        }
    }

    private fun deleteLending(id: Int) {
        viewModel.deleteItemID(
            preferenceManager.getApiKey(),
            id.toString()
        )
        deleteUi()
    }


    private fun deleteUi() {
        lifecycleScope.launch {
            viewModel.transactionApprovalSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress()
                    }
                    ApiCallStatus.SUCCESS -> {
                        if (response.data != null && response.data.status == 200 && response.data.result != null) {
//                            activity?.showToast(response.data.message)
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            lendViewModel.doTransactionSiginIn(
                                preferenceManager.getApiKey(),
                                SignTransactionRequest(
                                    keyHash,
                                    "removeLending",
                                    response.data.result!!.Id,
                                    response.data.success,
                                )
                            )
                            deleteSignTransaction()

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

    private fun deleteSignTransaction() {
        lifecycleScope.launch {
            lendViewModel.siginInTransctionSuccess.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        showProgress()
                    }
                    ApiCallStatus.SUCCESS -> {
                        hideProgress()
                        if (response.data != null && response.data.status == 200) {
                            requireActivity().showToast(response.data.message)
                            onResume()
                        } else {
                            response.data?.message?.let { requireActivity().showToast(it) }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        hideProgress()
                        response.errorMessage?.let { requireActivity().showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun openDeposit() {
        if (!preferenceManager.isKycApproved())
            context?.showToast(resources.getString(R.string.kyc_not_available_error))
        else if (!preferenceManager.getMomic())
            context?.showToast(resources.getString(R.string.wallet_not_available_error))
        else {
            val intent = Intent(activity, BuyRTOActivity::class.java)
            startActivity(intent)
            //  requireActivity().openActivity(DepositActivity::class.java)
        }
    }

    private fun updateUi() {
        binding.rvMyLending.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {

            dashboardViewModel.walletSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        _binding?.progressBar?.visibility = View.VISIBLE
                        _binding?.progressBar?.bringToFront()
                    }
                    ApiCallStatus.SUCCESS -> {
                        _binding?.progressBar?.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            _binding?.tvMyEarning?.text = response.data.result.earnings.all_time.toString()
                            _binding?.tvRto?.text = response.data.result.wallet_balance.toString()
                        } else {
                            if (response.data.message != ReltimeConstants.WALLET_ERROR)
                                activity?.showToast(response.data.message)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        _binding?.progressBar?.visibility = View.GONE
                        response.errorMessage?.let { activity?.showToast(it) }
                    }
                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            dashboardViewModel.walletAmountSuccessModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        _binding?.progressBar?.visibility = View.VISIBLE
                        _binding?.progressBar?.bringToFront()
                    }
                    ApiCallStatus.SUCCESS -> {
                        _binding?.progressBar?.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            response.data.result[0].RTO?.let {
                                it.balance?.let { balace ->
                                    rtoCount = balace
                                }

                            }
                            val rtoBalance = java.lang.String.format("%.2f", rtoCount.toDouble())
                            _binding?.tvRto?.text = rtoBalance.toString()


                        } else {
                            if (response.data.message != ReltimeConstants.WALLET_ERROR)
                                activity?.showToast(response.data.message)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        _binding?.progressBar?.visibility = View.GONE
                        response.errorMessage?.let { activity?.showToast(it) }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.getDashboardDetails(preferenceManager.getApiKey())
        if (activity?.let { Utils.isNetworkAvailable(it.applicationContext) }!!) {
            _binding?.rvMyLending?.adapter = lendingTokenLitPagerAdapter
            tokenPagingSource.setData(
                viewModel.search.value,
                Utils.rangeOne!!,
                Utils.rangeTwo!!,
                Utils.FromDate!!,
                Utils.ToDate!!,
                Utils.tenure!!
            )

            getData1()

        } else {
            Toast.makeText(activity, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun getData1() {
        lifecycleScope.launch {
            viewModel.getPagedData().collectLatest {
                lendingTokenLitPagerAdapter.submitData(it)
            }
        }
    }

    private fun startLendingActivity(ledingType: Int) {
        if (preferenceManager.isKycApproved()) {
            val intent = Intent(activity, DirectLendingV2Activity::class.java)
            intent.putExtra(DirectLendingV2Activity.EXTRA_LENDING_TYPE, ledingType)
            startActivity(intent)
        } else
            context?.showToast(resources.getString(R.string.kyc_error_lending_feature))
    }

    private fun showProgress() {
        if (!progressDialog.isShowing)
            progressDialog.show()
    }

    private fun hideProgress() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }
}