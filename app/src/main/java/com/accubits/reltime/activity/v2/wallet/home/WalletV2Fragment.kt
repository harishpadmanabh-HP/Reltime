package com.accubits.reltime.activity.v2.wallet.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.accubits.reltime.R
import com.accubits.reltime.activity.settings.MyAccountStartActivity
import com.accubits.reltime.activity.v2.home.adapter.HomeControlsAdapter
import com.accubits.reltime.activity.v2.wallet.MyAccountsV2Adapter
import com.accubits.reltime.activity.v2.wallet.TransactionPagedV2Adapter
import com.accubits.reltime.activity.v2.wallet.TransactionPagingV2Source
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountDetailActivity
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountDetailTransactionBottomSheet
import com.accubits.reltime.activity.v2.wallet.home.viewmodel.WalletV2ViewModel
import com.accubits.reltime.activity.v2.wallet.myaccounts.MyAccountsComposeActivity
import com.accubits.reltime.activity.v2.wallet.mywallet.WalletDetailV2Activity
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.databinding.FragmentWalletV2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.views.rto.RTOSendWyreSuccessActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.accubits.reltime.activity.v2.wallet.transactiondetail.TransactionDetailV2Activity
import com.accubits.reltime.mnemonic.ImportMemonicActivity
import com.accubits.reltime.mnemonic.MnemonicActivity


@AndroidEntryPoint
class WalletV2Fragment : Fragment() {

    private val viewModel by viewModels<WalletV2ViewModel>()
    private var _binding: FragmentWalletV2Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var transactionPagingSource: TransactionPagingV2Source

    @Inject
    lateinit var transactionPagedAdapter: TransactionPagedV2Adapter
    private val myAccountAdapter: MyAccountsV2Adapter by lazy {
        MyAccountsV2Adapter {
            val intent = Intent(activity, AccountDetailActivity::class.java)
            intent.putExtra("accountType", it.type)
            intent.putExtra("accountId", it.id)
            startActivity(intent)
        }
    }

    private var search = ""

    private val transactionFilterBottomSheet by lazy {
        AccountDetailTransactionBottomSheet(requireActivity(), {
            transactionPagingSource.setData(
                "",
                Utils.account_date_type, null,
                "",
                Utils.account_transaction_status,
                //Utils.account_date_type
            )
            getTransactionData()
        }, {
            transactionPagingSource.setData(
                "",
                Utils.account_date_type, null,
                "",
                Utils.transaction_status,
              //  Utils.date_type
            )
            getTransactionData()
        }, isPaidRequired = true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletV2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setAdapter()
        setListeners()
        updateUi()
    }

    override fun onResume() {
        super.onResume()
        getWalletDetails()

        if (Utils.isNetworkAvailable(requireActivity())!!) {
            _binding?.rvTransactionHistory?.adapter = transactionPagedAdapter
            transactionPagingSource.setData(
                search,
                Utils.account_date_type, null, "", Utils.transaction_status,
              //  Utils.date_type
            )
            getTransactionData()
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.please_check_net),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        _binding?.bottomSheet?.setOnTouchListener { p0, p1 ->
            _binding?.rvTransactionHistory?.onTouchEvent(p1)
            true
        }
        _binding?.rvTransactionHistory?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        transactionPagedAdapter.addLoadStateListener { loadState ->

            if (loadState.refresh is LoadState.Loading) {
                _binding?.progressBar?.visibility = View.VISIBLE
            } else {
                _binding?.progressBar?.visibility = View.GONE

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
            if (it.checkoutId == null) {
                val intent = Intent(requireContext(), TransactionDetailV2Activity::class.java).apply {
                    putExtra("ITEM", it)
                }
                startActivity(intent)
            } else {//means it is crypto
                val intent = Intent(requireContext(), RTOSendWyreSuccessActivity::class.java).apply {
                    putExtra(RTOSendWyreSuccessActivity.EXTRA_CHECKOUT_ID, it.checkoutId)
                }
                startActivity(intent)
            }
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

        binding.layoutWalletTop.homeAccounts.imgAddAccount.setOnClickListener {
            val intent = Intent(activity, MyAccountStartActivity::class.java)
            startActivity(intent)
        }

        binding.layoutWalletTop.homeAccounts.tvAddAccount.setOnClickListener {
            val intent = Intent(activity, MyAccountStartActivity::class.java)
            startActivity(intent)
        }

        binding.layoutFilter.setOnClickListener {
            if (!transactionFilterBottomSheet.isShowing)
                transactionFilterBottomSheet.show()
        }

        binding.layoutWalletTop.homeAccounts.tvSeeAllAccount.setOnClickListener {
            startActivity(Intent(requireContext(), MyAccountsComposeActivity::class.java))
        }

        binding.tvWalletDetails.setOnClickListener {
            requireActivity().openActivity(WalletDetailV2Activity::class.java){
                this.putString(WalletDetailV2Activity.TITLE,resources.getString(R.string.my_wallet_details))
            }
        }
        binding.layoutWalletTop.btnReltimeAccount.setOnClickListener {
            startActivity(Intent(requireActivity(), MnemonicActivity::class.java))
        }
        binding.layoutWalletTop.restoreReltimeAccount.setOnClickListener {
            context?.openActivity(ImportMemonicActivity::class.java)
        }
    }

    private fun updateUi() {
        lifecycleScope.launch {
            viewModel.walletDetailsFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        _binding?.progressBar?.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        _binding?.progressBar?.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (response.data != null && responseOk) {

                            response.data.result?.assest?.let {
                                _binding?.layoutWalletTop?.tvRTOBalance?.text =
                                    Utils.buildStyledAmount(context = context, it)
                            }

                            response.data.result?.accounts?.let {
                                myAccountAdapter.setItems(it)
                            }
                            response.data.result?.allAccounts?.let {
                                if (it > 3) {
                                    _binding?.layoutWalletTop?.homeAccounts?.lineDivider?.visibility =
                                        View.VISIBLE
                                    _binding?.layoutWalletTop?.homeAccounts?.tvSeeAllAccount?.visibility =
                                        View.VISIBLE
                                    _binding?.layoutWalletTop?.homeAccounts?.tvSeeAllAccount?.text =
                                        resources.getString(R.string.see_all_n_accounts, it)
                                } else {
                                    _binding?.layoutWalletTop?.homeAccounts?.lineDivider?.visibility =
                                        View.GONE
                                    _binding?.layoutWalletTop?.homeAccounts?.tvSeeAllAccount?.visibility =
                                        View.GONE
                                }
                            }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        _binding?.progressBar?.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }

    }

    private fun getWalletDetails() {
        if (preferenceManager.getMomic()) {
            _binding?.layoutWalletTop?.layoutWalletData?.visibility = View.VISIBLE
            _binding?.bottomSheet?.visibility = View.VISIBLE
            _binding?.layoutWalletTop?.layoutAddWallet?.visibility = View.GONE
            _binding?.layoutWalletTop?.layoutTitle1?.visibility = View.GONE
            _binding?.toolbar?.visibility = View.VISIBLE
            if (activity?.let { Utils.isNetworkAvailable(it) }!!)
                viewModel.getWalletHomeDetails()
             else
                Toast.makeText(activity, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        } else {
            _binding?.layoutWalletTop?.layoutWalletData?.visibility = View.GONE
            _binding?.bottomSheet?.visibility = View.GONE
            _binding?.layoutWalletTop?.layoutAddWallet?.visibility = View.VISIBLE
            _binding?.layoutWalletTop?.layoutTitle1?.visibility = View.VISIBLE
            _binding?.layoutWalletTop?.tvMyWallet1?.visibility = View.GONE
            _binding?.toolbar?.visibility = View.GONE
        }
    }

    private fun setAdapter() {
        _binding?.layoutWalletTop?.rvHomeControls?.apply {
            layoutManager=GridLayoutManager(requireContext(),3)
//            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = HomeControlsAdapter(preferenceManager, isForHomePage = false)
        }
        _binding?.layoutWalletTop?.homeAccounts?.rvAccounts?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = myAccountAdapter
        }

    }
}