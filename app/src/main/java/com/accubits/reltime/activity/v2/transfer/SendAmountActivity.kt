package com.accubits.reltime.activity.v2.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity
import com.accubits.reltime.activity.v2.transfer.viewmodel.TransferViewModel
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.databinding.ActivitySendAmountBinding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.TransferApprovalRequest
import com.accubits.reltime.models.TransferRequest
import com.accubits.reltime.utils.Extensions.accountWithdrawType
import com.accubits.reltime.utils.Extensions.getAccountBalance
import com.accubits.reltime.utils.Extensions.getCoinCode
import com.accubits.reltime.utils.Extensions.getSymbolWithCoinCode
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class SendAmountActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendAmountBinding

    private val viewModel by viewModels<TransferViewModel>()

    @Inject
    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.contactType = intent.getStringExtra(TransferObject.CONTACT_TYPE).toString()
        viewModel.receiver = intent.getStringExtra(TransferObject.RECEIVER).toString()
        viewModel.profileImage = intent.getStringExtra(TransferObject.PROFILE_IMAGE).toString()

        actions()
        setData()


        if (Utils.isNetworkAvailable(this)!!) {
            viewModel.getAccountList()
        } else showToast(getString(R.string.please_check_net))

        observer()

    }

    private fun setData() {
        val name = intent.getStringExtra(TransferObject.NAME)

        when (viewModel.profileImage) {
            "" -> {
                binding.ivuserprofileimage.setFirstLetter(name.toString())
            }
            else -> {
                binding.ivuserprofileimage.setFirstLetter(name.toString())

            }
        }

        name?.let { name ->
            binding.tvName.text = getString(R.string.transfer_to) + " " + name

        }
        intent.getStringExtra(TransferObject.RECEIVER)?.let { reciever ->
            binding.tvNumber.text = reciever

        }
    }

    private fun actions() {

        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.clMyRtoAccount.setOnClickListener {
            launchAccountPicker(
                viewModel.listAccountsResponseFlow.value.data?.result,
                viewModel.selectedFromAccount.value
            )
        }

        binding.btnNext.setOnClickListener {
            val accountBalance =
                viewModel.selectedFromAccount.value?.getAccountBalance()?.toDoubleOrNull()
            val doubleAmount = binding.etAmount.text.toString().toDoubleOrNull()

            if (doubleAmount == null) {
                showToast(resources.getString(R.string.please_enter_valid_amount))
                return@setOnClickListener
            } else if (viewModel.selectedFromAccount.value == null) {
                showToast(resources.getString(R.string.please_choose_account))
                return@setOnClickListener
            } else if (doubleAmount <= 0) {
                showToast(resources.getString(R.string.please_enter_amount_greater_than_0))
                return@setOnClickListener
            } else if (accountBalance != null && accountBalance < doubleAmount) {
                showToast(resources.getString(R.string.insufficient_balance))
                return@setOnClickListener
            }

            viewModel.amount = doubleAmount.toString()
            viewModel.description = binding.etDescription.text.toString()

            val intents = Intent(this, MpinValidateActivity::class.java)
            intents.putExtra(ReltimeConstants.RETURN_VALUE, true)
            mpinVerificationLauncher.launch(intents)
        }
    }


    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (viewModel.method == "jointAccounts") {
                viewModel.performTransferApproval(
                    TransferApprovalRequest(
                        amount = viewModel.amount.toDouble(),
                        coinCode = viewModel.selectedFromAccount.value?.getCoinCode(),
                        joint_account = viewModel.address
                    )
                )
            } else {
                viewModel.performTransfer(
                    TransferRequest(
                        address = viewModel.address,
                        amount = viewModel.amount.toDouble(),
                        coinCode = viewModel.selectedFromAccount.value?.getCoinCode(),
                        contactType = viewModel.contactType,
                        method = viewModel.method,
                        receiver = viewModel.receiver,
                        description = viewModel.description
                    )
                )
            }
        }
    }

    private fun observer() {

        lifecycleScope.launch {
            viewModel.transferApprovalSiginFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        disableTouch()
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    ApiCallStatus.SUCCESS -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                        if (response.data!!.status == 200) {
                            viewModel.intialWalletSigIn(
                                data = response.data.result!!.data
                            )
                        } else {
                            showToast(response.data.message)

                        }
                    }
                    ApiCallStatus.ERROR -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }

        }

        lifecycleScope.launch {
            viewModel.intialwalletSiginFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        disableTouch()
                    }
                    ApiCallStatus.SUCCESS -> {
                        enableTouch()

                        binding.progressBar.visibility = View.GONE

                        if (response.data!!.status == 200) {
                            viewModel.performTransfer(
                                TransferRequest(
                                    address = viewModel.address,
                                    amount = viewModel.amount.toDouble(),
                                    coinCode = viewModel.selectedFromAccount.value?.getCoinCode(),
                                    contactType = viewModel.contactType,
                                    method = viewModel.method,
                                    receiver = viewModel.receiver,
                                    description = viewModel.description
                                )
                            )
                        } else showToast(response.data.message)
                    }
                    ApiCallStatus.ERROR -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }

        }


        lifecycleScope.launch {
            viewModel.transferResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        disableTouch()
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    ApiCallStatus.SUCCESS -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                        if (response.data!!.status == 200) {
                            viewModel.walletSigin(
                                id = response.data.result.id,
                                data = response.data.result
                            )

                        } else {
                            showToast(response.data.message)

                        }
                    }
                    ApiCallStatus.ERROR -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }

        }

        lifecycleScope.launch {
            viewModel.walletSiginFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        disableTouch()
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE
                        if (response.data!!.status == 200) {
                            openActivity(TransferSuccessActivity::class.java, shouldFinish = true) {
                                putString(
                                    TransferObject.Amount,
                                    response.data.transactionItem.receiver?.amount.toString()
                                )
                                putString(
                                    TransferObject.NAME,
                                    response.data.transactionItem.receiver?.name
                                )
                                putString(
                                    TransferObject.SENDER_NUMBER,
                                    response.data.transactionItem.sender?.contactNumber
                                )
                                putString(
                                    TransferObject.COIN_CODE,
                                    Utils.coinCodeWithSymbol(
                                        this@SendAmountActivity,
                                        response.data.transactionItem.receiver?.coinCode.toString(),
                                        response.data.transactionItem.receiver?.symbol
                                    )
                                )
                                putString(
                                    TransferObject.PROFILE_IMAGE,
                                    response.data.transactionItem.receiver?.profileImage.toString()
                                )

                                putString(
                                    TransferObject.TIMESTAMP,
                                    response.data.transactionItem.timestamp
                                )

                                putString(
                                    TransferObject.TRANSACTION_ID,
                                    response.data.transactionItem.txnId
                                )
                                putString(
                                    TransferObject.ACCOUNT,
                                    response.data.transactionItem.accountName
                                )
                                putString(
                                    TransferObject.RECEIVER_NAME,
                                    response.data.transactionItem.receiver?.name
                                )
                                putString(
                                    TransferObject.SENDER_NAME,
                                    response.data.transactionItem.sender?.name
                                )

                                putString(
                                    TransferObject.SENDER_EMAIL,
                                    response.data.transactionItem.sender?.email
                                )
                                putString(
                                    TransferObject.SENDER_WALLET_ADDRESS,
                                    response.data.transactionItem.sender?.wallet_address
                                )
                                putString(
                                    TransferObject.MODE,
                                    response.data.transactionItem.mode
                                )
                                putString(
                                    TransferObject.RECEIVER_WALLET_ADDRESS,
                                    response.data.transactionItem.receiver?.wallet_address
                                )
                                putString(
                                    TransferObject.RECEIVER_EMAIL,
                                    response.data.transactionItem.receiver?.email
                                )
                                putString(
                                    TransferObject.RECEIVER_NUMBER,
                                    response.data.transactionItem.receiver?.contactNumber
                                )
                            }
                        } else showToast(response.data.toString())

                    }
                    ApiCallStatus.ERROR -> {
                        enableTouch()
                        binding.progressBar.visibility = View.GONE

                    }
                    else -> {}
                }
            }

        }


        lifecycleScope.launch {
            viewModel.listAccountsResponseFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.clMyRtoAccount.visibility = View.GONE
                        binding.btnNext.visibility = View.GONE
                        binding.constraintLayout31.visibility = View.GONE
                        binding.etDescription.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        if (response.data?.success == true && response.data.status == 200) {
                            binding.clMyRtoAccount.visibility = View.VISIBLE
                            binding.btnNext.visibility = View.VISIBLE
                            binding.constraintLayout31.visibility = View.VISIBLE
                            binding.etDescription.visibility = View.VISIBLE
                                 val accounts = Utils.buildAccountsList(response.data.result)
                                 if (accounts.isEmpty())
                                     showToast(resources.getString(R.string.no_accounts_available))//selectFromAccount(accounts.first())
                        } else {
                            binding.progressBar.visibility = View.GONE
                            binding.clMyRtoAccount.visibility = View.GONE
                            binding.btnNext.visibility = View.GONE
                            binding.constraintLayout31.visibility = View.GONE
                            binding.etDescription.visibility = View.GONE
                            showToast(response.data?.message)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        binding.clMyRtoAccount.visibility = View.GONE
                        binding.btnNext.visibility = View.GONE
                        binding.constraintLayout31.visibility = View.GONE
                        binding.etDescription.visibility = View.GONE
                        showToast(response.errorMessage)
                    }
                    else -> {}
                }
            }
        }
    }


    private var accountPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getSerializableExtra(AccountPickerComposeActivity.EXTRA_SELECTED_ACCOUNT)
                ?.let { any ->
                    selectFromAccount(any as ReltimeAccount)
                }
        }
    }

    private fun selectFromAccount(selectedAccount: ReltimeAccount) {
        selectedAccount.let {
            viewModel.setSelectedFromAccount(it)
            binding.tvMyRtoAccount.text = Utils.getAccountName(it)
            viewModel.address = Utils.getAccountAddress(it)
            viewModel.method = it.accountWithdrawType()
            binding.tvBalance.text =
                Utils.setAmountWithCoin(
                    resources.getString(
                        R.string.n_balance,
                        it.getSymbolWithCoinCode()
                    ), it.getAccountBalance(), sizeArray = arrayOf(0.8f, 1f, 0.8f)
                )
        }
    }

    private fun launchAccountPicker(
        accountResult: AccountResult?,
        selectedAccount: ReltimeAccount?
    ) {
        val intent = Intent(
            this,
            AccountPickerComposeActivity::class.java
        )
        selectedAccount?.let {
            intent.putExtra(
                AccountPickerComposeActivity.EXTRA_SELECTED_ACCOUNT,
                selectedAccount as Serializable
            )
        }
        accountResult?.let {
            intent.putExtra(AccountPickerComposeActivity.EXTRA_ACCOUNT_LIST, accountResult)
        }
        accountPickerLauncher.launch(intent)
    }

    private fun disableTouch() {
        binding.etAmount.isEnabled = false
        binding.etDescription.isEnabled = false
        binding.clMyRtoAccount.isClickable = false
        binding.btnNext.setButtonEnable(false)
    }

    private fun enableTouch() {
        binding.etAmount.isEnabled = true
        binding.etDescription.isEnabled = true
        binding.clMyRtoAccount.isClickable = true
        binding.btnNext.setButtonEnable(true)
    }

}