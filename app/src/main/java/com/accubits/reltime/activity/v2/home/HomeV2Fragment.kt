package com.accubits.reltime.activity.v2.home

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.accubits.reltime.R
import com.accubits.reltime.activity.contacts.ContactActivity
import com.accubits.reltime.activity.kyc.KYCDetailsActivity
import com.accubits.reltime.activity.notification.NotificationActivity
import com.accubits.reltime.activity.settings.MyAccountStartActivity
import com.accubits.reltime.activity.settings.SettingsProfileActivity
import com.accubits.reltime.activity.settings.model.CloseRequestHome
import com.accubits.reltime.activity.v2.home.adapter.HomeControlsAdapter
import com.accubits.reltime.activity.v2.home.adapter.HomeRecentTransferAdapter
import com.accubits.reltime.activity.v2.home.viewmodel.HomeV2ViewModel
import com.accubits.reltime.activity.v2.transfer.SendAmountActivity
import com.accubits.reltime.activity.v2.transfer.TransferObject
import com.accubits.reltime.activity.v2.transfer.adapter.RecentTransactionAccountsAdapter
import com.accubits.reltime.activity.v2.wallet.MyAccountsV2Adapter
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountDetailActivity
import com.accubits.reltime.activity.v2.wallet.myaccounts.MyAccountsComposeActivity
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.FragmentHomeV2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.mnemonic.ImportMemonicActivity
import com.accubits.reltime.mnemonic.MnemonicActivity
import com.accubits.reltime.models.RowData
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.views.home.ContainerActivity
import com.accubits.reltime.views.home.ui.dashboard.DashboardViewModel
import com.accubits.reltime.views.rto.BuyRTOActivity
import com.accubits.reltime.views.video.FullscreenVideoActivity
import com.universalvideoview.UniversalMediaController
import com.universalvideoview.UniversalVideoView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeV2Fragment : Fragment()  {

    private val homeViewModel by viewModels<HomeV2ViewModel>()
    private var _binding: FragmentHomeV2Binding? = null

    companion object {
        const val VIDEO_TYPE = "video_type"
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    // private val dashboardViewModel by viewModels<WalletViewModel>()
    private val viewModel by viewModels<DashboardViewModel>()
    private val binding get() = _binding!!
    private var rtoCount: String = "0"
    var isDialogOpened = false
    var dialog: Dialog? = null

    private val myAccountAdapter: MyAccountsV2Adapter by lazy {
        MyAccountsV2Adapter {
            val intent = Intent(activity, AccountDetailActivity::class.java)
            intent.putExtra("accountType", it.type?.convertRTOtoEURO())
            intent.putExtra("accountId", it.id)
            startActivity(intent)
        }
    }

    private val recentTransferAdapter: HomeRecentTransferAdapter by lazy {
        HomeRecentTransferAdapter(
            object : RecentTransactionAccountsAdapter.OnItemClickListener {
                override fun OnRecentItemClickListener(rowData: RowData) {
                    requireActivity().openActivity(SendAmountActivity::class.java) {
                        putString(TransferObject.RECEIVER, rowData.mobile)
                        putString(TransferObject.NAME, rowData.userName)
                        putString(TransferObject.USERID, rowData.userId.toString())
                        putString(TransferObject.PROFILE_IMAGE, rowData.userImage)
                        putString(
                            TransferObject.CONTACT_TYPE,
                            Utils.TransferContactType.PHONE.contactType
                        )

                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeV2Binding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setAction()
        observeAPIs()

    }

    private fun init() {
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(_binding?.rvHomeControls)

        activity?.intent?.getBooleanExtra(ContainerActivity.EXTRA_IS_FIRST_LOGIN, false)?.let {
            if (it)
                binding.textView20.visibility = View.INVISIBLE
        }
        _binding?.rvHomeControls?.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
//            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = HomeControlsAdapter(preferenceManager)
        }
        _binding?.homeAccounts?.rvAccounts?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = myAccountAdapter
        }
        binding.tvNameData.text = preferenceManager.getName()
        if (preferenceManager.getName().length > 1) {
            binding.tvNameTwo.text = preferenceManager.getName().substring(0, 2).uppercase()
        } else {
            binding.tvNameTwo.text = preferenceManager.getName().substring(0).uppercase()
        }

        binding.textView20.text =
            resources.getString(
                R.string.last_login,
                Utils.getDateCurrentTimeZone1(
                    preferenceManager.getLastLogin().toDouble()
                )
            )

        _binding?.rvRecentPayments?.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = recentTransferAdapter
        }
    }

    private fun setAction() {
        binding.constraintLayout2.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivVideo.setMediaController(binding.mediaController);
        binding.btCreatWallet.setOnClickListener {
            startActivity(Intent(requireActivity(), MnemonicActivity::class.java))
        }

        binding.ivVideo.setOnCompletionListener {
            binding.ivPlay.visibility = View.VISIBLE
            binding.ivPlayView.visibility = View.VISIBLE
            binding.mediaController.visibility = View.GONE
        }

        binding.ivVideo.setVideoViewCallback(object : UniversalVideoView.VideoViewCallback {
            override fun onScaleChange(isFullscreen: Boolean) {

            }

            override fun onPause(mediaPlayer: MediaPlayer?) {

            }

            override fun onStart(mediaPlayer: MediaPlayer?) {
                binding.ivPlay.visibility = View.GONE
                //  binding.ivPlayView.visibility = View.GONE
                mediaPlayer?.setVolume(0f, 0f)
            }

            override fun onBufferingStart(mediaPlayer: MediaPlayer?) {

            }

            override fun onBufferingEnd(mediaPlayer: MediaPlayer?) {

            }

        })
        binding.shapeableImageView.setOnClickListener {
            val intent = Intent(activity, SettingsProfileActivity::class.java)
            activity?.startActivity(intent)
        }
        binding.ivClosePre.setOnClickListener {
            if (Utils.isNetworkAvailable(requireContext())!!) {
                if (preferenceManager.getApiKey() != "") {
                    viewModel.doCloseHome(
                        preferenceManager.getApiKey(),
                        CloseRequestHome(false, null, null)
                    )
                    updateUiClose()
                }
            } else {
                context?.showToast(getString(R.string.please_check_net))
            }
        }
        binding.ivCloseInvite.setOnClickListener {
            if (Utils.isNetworkAvailable(requireContext())!!) {
                if (preferenceManager.getApiKey() != "") {
                    viewModel.doCloseHome(
                        preferenceManager.getApiKey(),
                        CloseRequestHome(null, false, null)
                    )
                    updateUiClose()
                }
            } else {
                context?.showToast(getString(R.string.please_check_net))
            }
        }
        binding.ivCloseReltime.setOnClickListener {
            if (Utils.isNetworkAvailable(requireContext())!!) {
                if (preferenceManager.getApiKey() != "") {
                    binding.ivVideo.stopPlayback()

                    viewModel.doCloseHome(
                        preferenceManager.getApiKey(),
                        CloseRequestHome(null, null, false)
                    )
                    updateUiClose()
                }
            } else {
                context?.showToast(getString(R.string.please_check_net))
            }
        }
        binding.btMarket1.setOnClickListener {
            context?.showToast("This feature will be available soon")
        }
        binding.ivPreVideo.setOnClickListener {
            displayPopupWindow()
        }

        binding.invite.setOnClickListener {
            if (preferenceManager.isKycApproved()) {
                val intent = Intent(activity, ContactActivity::class.java)
                activity?.startActivity(intent)
            } else {
                context?.showToast("Invite feature will be enabled only after KYC verification.")
            }
        }
        binding.ivPlayView.setOnClickListener {
            val intent = Intent(activity, FullscreenVideoActivity::class.java)
            intent.putExtra(VIDEO_TYPE, 2)
            startActivity(intent)
        }
        binding.imgClose.setOnClickListener {
            binding.clKycSatatus.visibility = View.GONE
        }
        binding.imgKycStatus1.setOnClickListener {
            this@HomeV2Fragment.context?.openActivity(KYCDetailsActivity::class.java)
        }
        binding.txtRestoreWallet.setOnClickListener {
            this@HomeV2Fragment.context?.openActivity(ImportMemonicActivity::class.java)
        }
        binding.homeAccounts.imgAddAccount.setOnClickListener {
            val intent = Intent(activity, MyAccountStartActivity::class.java)
            startActivity(intent)
        }

        binding.homeAccounts.tvAddAccount.setOnClickListener {
            val intent = Intent(activity, MyAccountStartActivity::class.java)
            startActivity(intent)
        }

        binding.homeAccounts.tvSeeAllAccount.setOnClickListener {
            startActivity(Intent(requireContext(), MyAccountsComposeActivity::class.java))
        }
        binding.ivAddRtc.setOnClickListener {
            openDeposit()
        }
        binding.tvRto.setOnClickListener {
            openDeposit()
        }
    }

    private fun setUpKYCnWalletViews() {
        if (preferenceManager.getMomic()) {
            binding.clCreateRestoreWallet.visibility = View.GONE
            binding.constraintLayout51.visibility = View.VISIBLE
            binding.constraintLayout13.visibility = View.VISIBLE
            binding.clKycSatatus.visibility = View.GONE
            binding.clKycSatatus1.visibility = View.GONE

            binding.clReltimeBalance.visibility = View.VISIBLE
            binding.llRTOBalance.visibility = View.VISIBLE
            binding.rvHomeControls.visibility = View.VISIBLE
            binding.tvRecentPaymentTitle.visibility = View.VISIBLE
            binding.rvRecentPayments.visibility = View.VISIBLE
            binding.homeAccounts.clHomeAccounts.visibility = View.VISIBLE
            callHomeAPIs()
        } else {
            binding.clCreateRestoreWallet.visibility = View.VISIBLE
            binding.constraintLayout51.visibility = View.GONE
            binding.constraintLayout13.visibility = View.GONE
            binding.clKycSatatus.visibility = View.GONE
            binding.clKycSatatus1.visibility = View.GONE

            binding.clReltimeBalance.visibility = View.GONE
            binding.llRTOBalance.visibility = View.GONE
            binding.rvHomeControls.visibility = View.GONE
            binding.tvRecentPaymentTitle.visibility = View.GONE
            binding.rvRecentPayments.visibility = View.GONE
            binding.homeAccounts.clHomeAccounts.visibility = View.GONE

        }
    }


    private fun updateUiClose() {
        lifecycleScope.launch {
            viewModel.closeSuccesModel.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            response.data.message?.let { context?.showToast(response.data.message) }
                            /* dashboardViewModel.getWalletDetails()
                             updateUiWallet()*/
                            callHomeAPIs()
                        } else {
                            Toast.makeText(
                                activity,
                                response.data.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpKYCnWalletViews()
        binding.ivVideo.setVideoURI(Uri.parse("android.resource://" + requireActivity().packageName.toString() + "/" + R.raw.nagra))
        binding.ivVideo.seekTo(0)
        binding.ivVideo.start()
        callNotificationStatusAPI()
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
            displayPopupWindow()
        }
    }

    private fun callHomeAPIs() {
        if (activity?.let { Utils.isNetworkAvailable(it) }!!) {
            homeViewModel.getWalletHomeV2Details()
            homeViewModel.getWalletDetails()
        } else requireActivity().showToast(getString(R.string.please_check_net))
    }

    private fun callNotificationStatusAPI() {
        if (activity?.let { Utils.isNetworkAvailable(it) }!!)
            homeViewModel.getWalletDetails()
    }

    private fun displayPopupWindow() {
        dialog = Dialog(requireActivity())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.popup_pre_order)

        val vvPreOrder = dialog?.findViewById<UniversalVideoView>(R.id.vvPreOrder)
        val ivPlay = dialog?.findViewById<ImageView>(R.id.iv_play)
        val ivClose = dialog?.findViewById<ImageView>(R.id.iv_close)
        val mMediaController =
            dialog?.findViewById<UniversalMediaController>(R.id.media_controller);
        vvPreOrder?.setMediaController(mMediaController);
        vvPreOrder?.setVideoURI(Uri.parse("android.resource://" + requireActivity().packageName.toString() + "/" + R.raw.nagra))
        vvPreOrder?.seekTo(1)

        ivPlay?.setOnClickListener {
            isDialogOpened = true
            val intent = Intent(activity, FullscreenVideoActivity::class.java)
            intent.putExtra(VIDEO_TYPE, 1)
            startActivity(intent)
        }
        ivClose?.setOnClickListener {
            dialog?.dismiss()
        }
        dialog?.show()
        dialog?.setOnDismissListener {
            isDialogOpened = false
        }
    }

    private fun observeAPIs() {
        lifecycleScope.launch {
            homeViewModel.walletDetailsFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        _binding?.progressBar?.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        _binding?.progressBar?.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (response.data != null && responseOk) {
                            //   binding.ivVideo.start()
                            response.data.result?.earnings?.let {
                                val earnings = it.convertRTOtoEURO()
                                _binding?.tvEarnings?.text =
                                    Utils.buildStyledAmount(context = context, earnings )
                            }

                            response.data.result?.accounts?.let {
                                myAccountAdapter.setItems(it)
                                val rtoAccount = it.find { account -> account.coinCode == "RTO" }

                                rtoAccount?.balance?.let { balance ->
                                    rtoCount = balance
                                    _binding?.tvRto?.text = balance
                                }
                            }
                            response.data.result?.allAccounts?.let {
                                if (it > 3) {
                                    _binding?.homeAccounts?.lineDivider?.visibility =
                                        View.VISIBLE
                                    _binding?.homeAccounts?.tvSeeAllAccount?.visibility =
                                        View.VISIBLE
                                    _binding?.homeAccounts?.tvSeeAllAccount?.text =
                                        resources.getString(R.string.see_all_n_accounts, it)
                                } else {
                                    _binding?.homeAccounts?.lineDivider?.visibility = View.GONE
                                    _binding?.homeAccounts?.tvSeeAllAccount?.visibility =
                                        View.GONE
                                }
                            }
                        } else requireActivity().showToast(response.data?.message)
                    }
                    ApiCallStatus.ERROR -> {
                        _binding?.progressBar?.visibility = View.GONE
                        requireActivity().showToast(response.errorMessage)
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.homeTransferHistoryFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        _binding?.rvRecentPayments?.visibility = View.GONE
                        _binding?.tvRecentPaymentTitle?.visibility = View.GONE
                        _binding?.progressBar?.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        _binding?.progressBar?.visibility = View.GONE
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (response.data != null && responseOk) {
                            val list = response.data.result.result
                            recentTransferAdapter.setItems(list)
                            if (list.isEmpty()) {
                                _binding?.rvRecentPayments?.visibility = View.GONE
                                _binding?.tvRecentPaymentTitle?.visibility = View.GONE
                            } else {
                                _binding?.rvRecentPayments?.visibility = View.VISIBLE
                                _binding?.tvRecentPaymentTitle?.visibility = View.VISIBLE
                            }
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        _binding?.progressBar?.visibility = View.GONE
                        requireActivity().showToast(response.errorMessage)
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.walletSuccessModel.collectLatest { response ->
                if (response.status == ApiCallStatus.SUCCESS) {
                    binding.progressBar.visibility = View.GONE
                    if (response.data!!.status == 200) {
                        /* if (response.data.result.reltime) {
                             binding.ivVideo.start()
                             binding.constraintLayout10.visibility = View.VISIBLE
                         } else {
                             binding.constraintLayout10.visibility = View.GONE
                         }*/
                        if (!preferenceManager.isKycApproved()) {
                            if (response.data.result.kycStatus == "In-Progress") {
                                binding.clKycSatatus1.visibility = View.GONE
                                binding.clKycSatatus.visibility = View.VISIBLE
                                binding.txtKycStatus.text =
                                    resources.getText(R.string.your_ekyc_verificaion_is_in_progress_this_will_take_between_1_2_business_days)
                                binding.imgKycStatus.visibility = View.GONE
                            } else if (response.data.result.kycStatus == "N/A") {
                                binding.clKycSatatus1.visibility = View.VISIBLE
                                binding.clKycSatatus.visibility = View.GONE
                            }
                        }
                        if (response.data.result.isUnreadNotificationAvailable) {
                            binding.imageView7.visibility = View.VISIBLE
                        } else {
                            binding.imageView7.visibility = View.GONE
                        }
                    }
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
            //requireActivity().openActivity(DepositActivity::class.java)
        }
    }
}