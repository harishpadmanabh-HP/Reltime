package com.accubits.reltime.activity.v2.lending

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.contactPicker.ContactPickerComposeActivity
import com.accubits.reltime.activity.v2.lending.model.LendingCalculationRequest
import com.accubits.reltime.activity.v2.lending.model.ResultLendingCalculation
import com.accubits.reltime.activity.v2.lending.viewmodel.DirectLendingViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.database.Contact
import com.accubits.reltime.databinding.ActivityDirectLendingv2Binding
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.LendingRequestModel
import com.accubits.reltime.models.Row
import com.accubits.reltime.models.SignTransactionRequest
import com.accubits.reltime.models.TransactionRequestModel
import com.accubits.reltime.utils.Extensions.doSearchOnTextChange
import com.accubits.reltime.utils.Extensions.markRequiredInRed
import com.accubits.reltime.utils.Extensions.setButtonEnable
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.utils.customView.CustomSpinnerAdapter
import com.accubits.reltime.views.lend.LendSuccessActivity
import com.accubits.reltime.views.mpin.MpinValidateActivity
import com.accubits.reltime.views.privacy.PrivacyActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DirectLendingV2Activity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityDirectLendingv2Binding
    private val viewModel by viewModels<DirectLendingViewModel>()
    private var walletBalance: Double = 0.0
    private val lendingType by lazy { intent.getIntExtra(EXTRA_LENDING_TYPE, LENDING_MARKETPLACE) }

    private var selectedAmount: String = ""
    private var selectedInterest: String = ""
    private var selectedLoanTerm: String = ""
    private var selectedContactData: Contact? = null
    private var transactionId: String? = null

    private var lendCalculation: ResultLendingCalculation? = null

    companion object {
        const val EXTRA_LENDING_TYPE = "lending_type"
        const val EXTRA_LENDING_ITEM = "lending_item"
        const val LENDING_MARKETPLACE = 0
        const val LENDING_CONTACTS = 1


    }

    private val loanTenure = arrayListOf("3", "6", "9", "12", "18", "24", "36")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectLendingv2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
        observeFlow()
        viewModel.getWallet()
    }

    private fun init() {
        if (lendingType == LENDING_MARKETPLACE) {
            binding.tvToTitle.visibility = View.GONE
            binding.etTo.visibility = View.GONE
            binding.tvHead.text = resources.getString(R.string.direct_lending_to_the_marketplace)
        } else {
            binding.tvToTitle.visibility = View.VISIBLE
            binding.etTo.visibility = View.VISIBLE
            binding.tvHead.text =
                resources.getString(R.string.direct_lending_to_your_phone_contacts)
        }

        val spinnerAdapter = CustomSpinnerAdapter(
            this,
            R.layout.spinner_item,
            arrayListOf(
                resources.getString(R.string.select_number_of_installments),
                resources.getString(R.string._3_months),
                resources.getString(R.string._6_months),
                resources.getString(R.string._9_months),
                resources.getString(R.string._12_months),
                resources.getString(R.string._18_months),
                resources.getString(R.string._24_months),
                resources.getString(R.string._36_months)
            )
        )
        binding.spinnerLoanTerm.adapter = spinnerAdapter
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        setIntentData()

        binding.tvToTitle.markRequiredInRed()
        binding.tvAmountTitle.markRequiredInRed()
        binding.tvPercentageTitle.markRequiredInRed()
        binding.tvLoanTermTitle.markRequiredInRed()

    }

    private fun setIntentData() {
        intent.getSerializableExtra(EXTRA_LENDING_ITEM)?.let {
            if (it is Row) {
                transactionId = it.id.toString()
                binding.tvHead.text = resources.getString(R.string.lending_n, transactionId)
                binding.etAmount.setText(it.rto_amount)
                binding.etPercentage.setText(it.interest_rate)
                binding.swCollateral.isChecked = it.collateral == "ON"
                binding.spinnerLoanTerm.setSelection(loanTenure.indexOf(it.installments) + 1)
                binding.btnSubmit.text = resources.getString(R.string.settings_save_changes)
            }
        }
    }

    private fun setListeners() {
        binding.etAmount.doSearchOnTextChange { text, _, _, count ->
            getLoanStatistics()
        }
        binding.etPercentage.doSearchOnTextChange { text, _, _, count ->
            getLoanStatistics()
        }
        binding.etLoanTerm.setOnClickListener {
            binding.spinnerLoanTerm.performClick()
        }
        binding.spinnerLoanTerm.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        binding.etLoanTerm.text = binding.spinnerLoanTerm.selectedItem as String
                        getLoanStatistics()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.ibBack.setOnClickListener { onBackPressed() }
        binding.tvPolicy.setOnClickListener {
            val intents = Intent(this, PrivacyActivity::class.java)
            intents.putExtra(PrivacyActivity.URL, PrivacyActivity.TERMS_CONDITIONS)
            startActivity(intents)
        }

        binding.etTo.setOnClickListener {
            val intents = Intent(
                this,
                ContactPickerComposeActivity::class.java
            )//ContactPickerActivity::class.java)
            intents.putExtra(ContactPickerComposeActivity.EXTRA_SHOW_ALL_CONTACTS, true)
            contactPickerLauncher.launch(intents)
        }

        binding.btnSubmit.setOnClickListener {
            validate()
        }

        binding.ivInfo.setOnClickListener {
            lendCalculation?.let { it1 -> EarningsInfoBottomSheet(this, it1).show() }
        }
    }

    private fun observeFlow() {
        observeWallet()
        observeCalculation()
        observeApproval()
        observeSignTransaction()
        observeLendUpdate()
    }

    private fun observeWallet() {
        lifecycleScope.launch {
            viewModel.walletFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.nsv.visibility = View.GONE
                        binding.btnSubmit.visibility = View.GONE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.nsv.visibility = View.VISIBLE
                        binding.btnSubmit.visibility = View.VISIBLE
                        if (response.data!!.status == 200) {
                            walletBalance = response.data.result.wallet_balance.toDouble()
                            binding.tvBalance.text = response.data.result.wallet_balance
                        } else {
                            showToast(response.data.message)
                        }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binding.progressBar.visibility = View.GONE
                        binding.nsv.visibility = View.VISIBLE
                        binding.btnSubmit.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeCalculation() {
        lifecycleScope.launch {
            viewModel.lendingCalculationFlow.collect { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.pbCalculation.visibility = View.VISIBLE
                        binding.groupCalculations.visibility = View.INVISIBLE
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.pbCalculation.visibility = View.GONE
                        if (response.data?.status == 200 && response.data.success) {
                            lendCalculation = response.data.result
                            binding.tvLoanAmount.text =
                                Utils.getRTOAmount(response.data.result.loanAmount)
                            binding.tvMonthlyPayment.text =
                                Utils.getRTOAmount(response.data.result.monthlyPayment)
                            binding.tvTotal12Pay.text =
                                Utils.getRTOAmount(response.data.result.totalOfAll)
                            binding.tvTotalEarnings.text =
                                Utils.getRTOAmount(response.data.result.totalEarnings)
                            binding.tvTotal12PayTitle.text = resources.getString(
                                R.string.total_of_12_payments,
                                loanTenure[binding.spinnerLoanTerm.selectedItemPosition - 1]
                            )
                            binding.groupCalculations.visibility = View.VISIBLE
                        } else
                            response.data?.message?.let { showToast(it) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binding.pbCalculation.visibility = View.GONE
                        binding.groupCalculations.visibility = View.INVISIBLE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeApproval() {
        lifecycleScope.launch {
            viewModel.transactionApproveFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSubmit.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                        if (response.data != null && response.data.status == 200 && response.data.success && response.data.result != null) {
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result!!.data,
                                    preferenceManager
                                )
                            viewModel.doTransactionSignIn(
                                SignTransactionRequest(
                                    keyHash,
                                    null,
                                    null,
                                    null,
                                )
                            )
                        } else response.data?.let { showToast(it.message) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeSignTransaction() {

        lifecycleScope.launch {
            viewModel.signTransactionFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSubmit.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                        if (response.data?.status == 200 && response.data.success) {
                            if (response.data.result.details != null) {
                                Utils.validated = false
                                binding.progressBar.visibility = View.GONE
                                val mIntent =
                                    Intent(
                                        this@DirectLendingV2Activity,
                                        LendSuccessActivity::class.java
                                    )
                                response.data.result.details!!.lentTo?.let {
                                    mIntent.putExtra(
                                        LendSuccessActivity.TITLE,
                                        resources.getString(
                                            R.string.direct_lending_to_ph_number_success_title,
                                            it
                                        )
                                    )
                                }
                                mIntent.putExtra(LendSuccessActivity.IS_COME_DIRECT_LANDING, true)
                                mIntent.putExtra(
                                    LendSuccessActivity.TRANSACTION_ID,
                                    response.data.result.details!!.id.toString()
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.AMOUNT,
                                    resources.getString(
                                        R.string.n_n,
                                        response.data.result.details?.coinCode?.let {
                                            Utils.coinCodeWithSymbol(
                                                this@DirectLendingV2Activity,
                                                it, response.data.result.details?.symbol
                                            )
                                        },
                                        response.data.result.details?.rto_amount
                                    )
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.INSTALLMENT,
                                    response.data.result.details!!.installments.toString()
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.INTEREST,
                                    response.data.result.details!!.interest_rate
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.DATE,
                                    Utils.getDateCurrentTimeZone1(response.data.result.details!!.timestamp.toDouble())
                                )
                                /* intent.putExtra(
                                     LendSuccessActivity.TIME,
                                     response.data.result.details!!.time
                                 )*/
                                mIntent.putExtra(
                                    LendSuccessActivity.COLLATERAL,
                                    response.data.result.details!!.collateral
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.LEND_TO,
                                    response.data.result.details!!.lentTo
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.SHARE_OPTION,
                                    true
                                )
                                mIntent.putExtra(
                                    LendSuccessActivity.DONE_BUTTON_LABEL,
                                    intent.extras?.getString(LendSuccessActivity.DONE_BUTTON_LABEL)
                                )
                                startActivity(mIntent)
                                finish()
                            } else {
                                val lendingRequestModel = LendingRequestModel(
                                    rto_amount = selectedAmount.toDoubleOrNull(),
                                    interest_rate = selectedInterest,
                                    installments = selectedLoanTerm,
                                    collateral = getCollateralStatus(),
                                    if (lendingType == LENDING_MARKETPLACE) ReltimeConstants.LENDING else ReltimeConstants.DIRECT_LENDING,
                                    selectedContactData?.userId,
                                    transactionId
                                )
                                transactionId?.let {
                                    viewModel.doUpdateLending(
                                        lendingRequestModel
                                    )
                                } ?: viewModel.doLending(
                                    lendingRequestModel
                                )
                            }
                        } else response.data?.let { showToast(it.message) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeLendUpdate() {
        lifecycleScope.launch {
            viewModel.lendingSuccessFlow.collectLatest { response ->
                when (response.status) {
                    ApiCallStatus.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSubmit.setButtonEnable(false)
                    }
                    ApiCallStatus.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                        if (response.data?.status == 200 && response.data.success) {
                            val keyHash =
                                Utils.getKeyHasForTransaction(
                                    response.data.result.data,
                                    preferenceManager
                                )
                            val type =
                                transactionId?.let {
                                    "updateLending"
                                } ?: "createLending"
                            viewModel.doTransactionSignIn(
                                SignTransactionRequest(
                                    keyHash,
                                    type,
                                    response.data.result.id,
                                    response.data.success
                                )
                            )
                        } else response.data?.let { showToast(it.message) }
                    }
                    ApiCallStatus.ERROR -> {
                        response.errorMessage?.let { showToast(it) }
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.setButtonEnable(true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getLoanStatistics() {
        if (binding.etAmount.text.toString().isEmpty() || binding.etAmount.text.toString()
                .toDoubleOrNull() == null || binding.etAmount.text.toString().toDouble() < 1 ||
            binding.etPercentage.text.toString().isEmpty() || binding.etPercentage.text.toString()
                .toDoubleOrNull() == null ||
            binding.spinnerLoanTerm.selectedItemPosition == 0
        ) {
            binding.groupCalculations.visibility = View.INVISIBLE
            return
        }
        if (Utils.isNetworkAvailable(this)!!)
            viewModel.getLendingCalculation(
                LendingCalculationRequest(
                    amount = binding.etAmount.text.toString(),
                    instalments = loanTenure[binding.spinnerLoanTerm.selectedItemPosition - 1],
                    interest_rate = binding.etPercentage.text.toString()
                )
            )
        else showToast(getString(R.string.please_check_net))
    }

    private fun validate() {
        if (lendingType == LENDING_CONTACTS && selectedContactData == null) {
            binding.tvErrorTo.visibility = View.VISIBLE
            return
        } else binding.tvErrorTo.visibility = View.GONE
        if (binding.etAmount.text.toString().isEmpty() || binding.etAmount.text.toString()
                .toDoubleOrNull() == null
        ) {
            binding.tvErrorAmount.text = resources.getString(R.string.please_enter_amount)
            binding.tvErrorAmount.visibility = View.VISIBLE
            return
        } else binding.tvErrorAmount.visibility = View.GONE
        val doubleAmount = binding.etAmount.text.toString().toDoubleOrNull()
        if (doubleAmount != null && walletBalance < doubleAmount) {
            binding.tvErrorAmount.text = resources.getString(R.string.insufficient_balance)
            binding.tvErrorAmount.visibility = View.VISIBLE
            return
        } else binding.tvErrorAmount.visibility = View.GONE
        if (binding.etAmount.text.toString().toDouble() < 1) {
            binding.tvErrorAmount.text =
                resources.getString(R.string.please_amount_should_not_be_less_than_1)
            binding.tvErrorAmount.visibility = View.VISIBLE
            return
        } else binding.tvErrorAmount.visibility = View.GONE
        if (binding.etPercentage.text.toString().isEmpty() || binding.etPercentage.text.toString()
                .toDoubleOrNull() == null
        ) {
            binding.tvErrorPercentage.text =
                resources.getString(R.string.please_enter_the_interest_rate)
            binding.tvErrorPercentage.visibility = View.VISIBLE
            return
        } else binding.tvErrorPercentage.visibility = View.GONE
        if (binding.etPercentage.text.toString().toDouble() > 100) {
            binding.tvErrorPercentage.text =
                resources.getString(R.string.Interest_rate_should_not_be_grater_than_100)
            binding.tvErrorPercentage.visibility = View.VISIBLE
            return
        } else binding.tvErrorPercentage.visibility = View.GONE

        if (binding.spinnerLoanTerm.selectedItemPosition == 0) {
            binding.tvErrorLoanTerm.visibility = View.VISIBLE
            return
        } else binding.tvErrorLoanTerm.visibility = View.GONE
        if (!binding.cbPolicy.isChecked) {
            showToast(resources.getString(R.string.accept_terms))
            return
        }
        selectedAmount = binding.etAmount.text.toString()
        selectedInterest = binding.etPercentage.text.toString()
        selectedLoanTerm = loanTenure[binding.spinnerLoanTerm.selectedItemPosition - 1]
        validateMPIN()
    }

    private fun validateMPIN() {
        val intents = Intent(
            this@DirectLendingV2Activity,
            MpinValidateActivity::class.java
        )
        intents.putExtra(ReltimeConstants.RETURN_VALUE, true)
        mpinVerificationLauncher.launch(intents)
    }

    private fun transactionApproval() {
        if (Utils.isNetworkAvailable(this)!!)
            viewModel.transactionApproval(
                TransactionRequestModel(selectedAmount.toDoubleOrNull())
            )
        else showToast(getString(R.string.please_check_net))
    }

    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            transactionApproval()
        }
    }

    private var contactPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getParcelableExtra<Contact>(ContactPickerComposeActivity.EXTRA_SELECTED_CONTACT)
                ?.let {//ContactPickerActivity.EXTRA_SELECTED_CONTACT)?.let {
                    setSelectedContact(it)
                }

        }
    }

    private fun setSelectedContact(contactData: Contact) {
        selectedContactData = contactData
        binding.etTo.text = selectedContactData?.contactName
    }

    private fun getCollateralStatus() = if (binding.swCollateral.isChecked) "ON" else "OFF"
}

