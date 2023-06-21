package com.accubits.reltime.activity.v2.wallet.move

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity.Companion.ACCOUNT_TYPE_MOVE_FROM
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity.Companion.ACCOUNT_TYPE_MOVE_TO
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.move.viewmodel.MoveViewModel
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.utils.Extensions.getAccountBalance
import com.accubits.reltime.utils.Extensions.getAccountName
import com.accubits.reltime.utils.Extensions.getCoinCode
import com.accubits.reltime.utils.Extensions.getSymbolWithCoinCode
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable


@AndroidEntryPoint
class MoveComposeActivity : ComponentActivity() {

    private val viewModel by viewModels<MoveViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.move_title, content = {
                    Content(viewModel = viewModel, onAccountClick = { accountType, accountResult ->
                        launchAccountPicker(
                            accountType,
                            accountResult,
                            if (accountType == ACCOUNT_TYPE_MOVE_FROM) viewModel.selectedFromAccount.value else viewModel.selectedToAccount.value
                        )
                    }, onMoveClick = {
                        onMoveClick(it)
                    }, onSuccess = {
                        val successIntent = Intent(this, MoveSuccessActivity::class.java)
                        successIntent.putExtra(
                            MoveSuccessActivity.TRANSACTION_ID,
                            it.transactionItem.txnId
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.TRANSACTION_FROM,
                            it.transactionItem.sender?.name//viewModel.selectedFromAccount.value?.getAccountName()
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.TRANSACTION_TO,
                            it.transactionItem.receiver?.name// viewModel.selectedToAccount.value?.getAccountName()
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.TIMESTAMP,
                            it.transactionItem.timestamp
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.FROM_AMOUNT,
                            it.transactionItem.sender?.amount
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.FROM_COIN_CODE,
                            Utils.coinCodeWithSymbol(
                                this@MoveComposeActivity,
                                it.transactionItem.sender?.coinCode.toString(),
                                it.transactionItem.sender?.symbol
                            )
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.TO_AMOUNT,
                            it.transactionItem.receiver?.amount
                        )
                        successIntent.putExtra(
                            MoveSuccessActivity.TO_COIN_CODE,
                            Utils.coinCodeWithSymbol(
                                this@MoveComposeActivity,
                                it.transactionItem.receiver?.coinCode.toString(),
                                it.transactionItem.receiver?.symbol
                            )
                        )
                        startActivity(successIntent)
                        finish()
                    })
                }, onBackClick = {
                    onBackPressed()
                })
            }
        }
        viewModel.getWithdrawFromAccounts()
    }

    private fun onMoveClick(amount: String) {
        val accountBalance =
            viewModel.selectedFromAccount.value?.getAccountBalance()?.toDoubleOrNull()
        val doubleAmount = amount.toDoubleOrNull()
        if (doubleAmount == null) {
            showToast(resources.getString(R.string.please_enter_valid_amount))
            return
        } else if (doubleAmount <= 0) {
            showToast(resources.getString(R.string.please_enter_amount_greater_than_0))
            return
        } else if (accountBalance != null && accountBalance < doubleAmount) {
            showToast(resources.getString(R.string.insufficient_balance))
            return
        }
        val intents = Intent(this, MpinValidateActivity::class.java)
        intents.putExtra(
            ReltimeConstants.AMOUNT, amount
        )
        intents.putExtra(ReltimeConstants.RETURN_VALUE, true)
        mpinVerificationLauncher.launch(intents)
    }

    private fun launchAccountPicker(
        pickerFor: String,
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
        intent.putExtra(AccountPickerComposeActivity.EXTRA_ACCOUNT_TYPE, pickerFor)
        accountPickerLauncher.launch(intent)
    }

    private var accountPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra(AccountPickerComposeActivity.EXTRA_ACCOUNT_TYPE)?.let {
                if (it == ACCOUNT_TYPE_MOVE_FROM) {
                    result.data?.getSerializableExtra(AccountPickerComposeActivity.EXTRA_SELECTED_ACCOUNT)
                        ?.let { any ->
                            viewModel.setSelectedFromAccount(any as ReltimeAccount)
                        }
                } else {
                    result.data?.getSerializableExtra(AccountPickerComposeActivity.EXTRA_SELECTED_ACCOUNT)
                        ?.let { any ->
                            viewModel.setSelectedToAccount(any as ReltimeAccount)
                        }
                }
            }
        }
    }

    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            postWithdraw(
                result.data?.getStringExtra(ReltimeConstants.AMOUNT)
            )
        }
    }

    private fun postWithdraw(amount: String?) {
        if (Utils.isNetworkAvailable(this)!!) {
            if (viewModel.selectedFromAccount.value != null &&
                viewModel.selectedToAccount.value != null
            )
                viewModel.doMove(amount)
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

}

@Composable
fun Content(
    viewModel: MoveViewModel,
    onAccountClick: (accountType: String, accountResult: AccountResult?) -> Unit,
    onMoveClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {

    val response = viewModel.withdrawFromFlow.collectAsState().value
    if (response.status == ApiCallStatus.LOADING) {
        Loader()
        // show loading
    } else if (response.status == ApiCallStatus.ERROR) {
        ErrorView(response.errorMessage)
        //show error
    } else if (response.status == ApiCallStatus.SUCCESS) {
        if (response.data?.success == true || response.data?.status == 200) {
            val accountList = Utils.buildAccountsList(response.data.result)
            if (accountList.isEmpty())
                EmptyView(R.string.from_account_not_found)
            else {
                //viewModel.setSelectedFromAccount(accountList[0])
                FromAccount(viewModel, onAccountClick, response.data.result, onMoveClick, onSuccess)
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}


@Composable
fun FromAccount(
    viewModel: MoveViewModel,
    onAccountClick: (accountType: String, accountResult: AccountResult?) -> Unit,
    accountResult: AccountResult?, onMoveClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val selectedFromAccount = viewModel.selectedFromAccount.collectAsState().value
    val inputText = remember { mutableStateOf(TextFieldValue("")) }
    val selectedToAccount = viewModel.selectedToAccount.collectAsState().value

    val enableState = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxTitle(id = R.string.move_from)
        AccountView(
            onAccountClick, ACCOUNT_TYPE_MOVE_FROM, accountResult,
            selectedFromAccount, enableState, stringResource(id = R.string.select_from_account)
        )

        selectedFromAccount?.let {
            MoveAmountBox(
                it.getSymbolWithCoinCode(),
                it.getAccountBalance(),
                inputText,
                enableState
            )

            val response = viewModel.withdrawToFlow.collectAsState().value

            if (response.status == ApiCallStatus.LOADING) {
                Loader()
                // show loading
            } else if (response.status == ApiCallStatus.ERROR) {
                ErrorView(response.errorMessage)
                //show error
            } else if (response.status == ApiCallStatus.SUCCESS) {
                if (response.data?.success == true || response.data?.status == 200) {
                    val accountList = Utils.buildAccountsList(response.data.result)
                    if (accountList.isEmpty())
                        EmptyView(R.string.from_account_not_found)
                    else {
//                        if (selectedToAccount == null)
//                            viewModel.setSelectedToAccount(accountList[0])
                        ToAccount(
                            viewModel = viewModel,
                            onToAccountClick = onAccountClick,
                            accountResult = response.data.result,
                            inputText = inputText,
                            selectedToAccount = selectedToAccount,
                            enableState = enableState
                        )
                    }
                } else {
                    ErrorView(response.data?.message)
                }
            }
        }
    }


    if (selectedFromAccount != null && inputText.value.text.isNotEmpty() && selectedToAccount != null)
        AppGradientButton(text = stringResource(id = R.string.move), enableState) {
            onMoveClick(inputText.value.text)
        }

    ConfirmWithdraw(viewModel = viewModel, enableState, onSuccess)

    Loader(enableState)

}

@Composable
private fun ConfirmWithdraw(
    viewModel: MoveViewModel,
    enableState: MutableState<Boolean>,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val response = viewModel.withdrawConfirmRequestFlow.collectAsState().value
    enableState.value = response.status != ApiCallStatus.LOADING
    if (response.status == ApiCallStatus.ERROR)
        response.errorMessage?.let { LocalContext.current.showToast(it) }
    else if (response.status == ApiCallStatus.SUCCESS) {
        response.data?.let { onSuccess(it) }
    }

}

@Composable
private fun ToAccount(
    viewModel: MoveViewModel,
    onToAccountClick: (accountType: String, accountResult: AccountResult?) -> Unit,
    accountResult: AccountResult?,
    inputText: MutableState<TextFieldValue>,
    selectedToAccount: ReltimeAccount?,
    enableState: MutableState<Boolean>
) {
    BoxTitle(id = R.string.to_)
    AccountView(
        onToAccountClick, ACCOUNT_TYPE_MOVE_TO, accountResult,
        selectedToAccount, enableState, stringResource(id = R.string.select_to_account)
    )
    selectedToAccount?.let {
        val inputAmount = inputText.value.text.trim()
        if (selectedToAccount.getCoinCode().isNotEmpty() && inputAmount.toDoubleOrNull() != null
            && inputAmount.toDouble() > 0
        ) {//inputText.value.text.isNotEmpty()) {
            viewModel.getPaymentStatistics(
                coinCode = selectedToAccount.getCoinCode(),
                amount = inputAmount,
                sourceCoinCode = viewModel.selectedFromAccount.value?.getCoinCode()
            )
            SetUpStatisticsBox(viewModel = viewModel, selectedToAccount = selectedToAccount)
        }
    }

}


@Composable
private fun SetUpStatisticsBox(
    viewModel: MoveViewModel,
    selectedToAccount: ReltimeAccount
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp)
            .background(
                AppCard50, shape = RoundedCornerShape(12.dp)
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatisticsAPI(viewModel, selectedToAccount)
        MoveBalanceBox(
            coinCode = selectedToAccount.getCoinCode(),
            balance = selectedToAccount.getAccountBalance()
        )
    }
}

@Composable
private fun StatisticsAPI(viewModel: MoveViewModel, selectedToAccount: ReltimeAccount) {
    val response = viewModel.paymentStatisticsResponseFlow.collectAsState().value
    if (response.status == ApiCallStatus.LOADING) {
        LoaderMin()
        // show loading
    } else if (response.status == ApiCallStatus.ERROR) {
        ErrorView(response.errorMessage)
        //show error
    } else if (response.status == ApiCallStatus.SUCCESS) {
        val responseOk = response.data?.status == 200 && response.data.success
        if (responseOk && response.data != null) {
            response.data.result?.find { cData ->
                cData.coinId == selectedToAccount.getCoinCode()
            }?.convertedRate?.let { convertedRate ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    text = convertedRate,
                    style = TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = fonts
                    ),
                    color = White60,
                )
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}


@Composable
fun BoxTitle(id: Int) {
    Text(
        text = stringResource(id = id),
        style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold,
        color = AppLightBlue,
        modifier = Modifier.padding(top = 16.dp),
        fontSize = 14.sp
    )
}

@Composable
fun AccountView(
    onClick: (accountType: String, accountResult: AccountResult?) -> Unit,
    accountType: String,
    accountResult: AccountResult?,
    selectedAccount: ReltimeAccount?,
    enableState: MutableState<Boolean>, hint: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { if (enableState.value) onClick(accountType, accountResult) },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = AppCard
    ) {
        AccountSelectionRowContent(name = selectedAccount?.getAccountName() ?: hint)
    }
}