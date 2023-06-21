package com.accubits.reltime.activity.v2.wallet.swap

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.common.currencypicker.CurrencyPickerActivity
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.swap.SwapActivity.Companion.SWAP_FROM
import com.accubits.reltime.activity.v2.wallet.swap.SwapActivity.Companion.SWAP_TO
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.activity.v2.wallet.swap.viewmodel.SwapViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.TransactionItem
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.utils.Extensions.getAccountBalance
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwapActivity : ComponentActivity() {

    companion object {
        const val PAGE_ID = "swap_activity"

        const val SWAP_FROM = "swap_from"
        const val SWAP_TO = "swap_to"
    }

    private val viewModel by viewModels<SwapViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.swap, content =  {
                    Content(viewModel = viewModel, onCurrencyClick = {
                        launchCurrencyPicker(it)
                    }, onSwapClick = {
                        onSwapClick(it)
                    }, onSuccess = {
                        openSuccessPage(it.transactionItem)
                    })
                }, onBackClick = {
                    onBackPressed()
                })
            }
        }
        viewModel.getFromCoins()
    }

    private fun onSwapClick(amount: String){
        val accountBalance =
            viewModel.selectedFromCurrency.value?.balance?.toDoubleOrNull()
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

    private fun launchCurrencyPicker(type: String) {
        val intent = Intent(this, CurrencyPickerActivity::class.java)
        val list =
            if (type == SWAP_FROM) viewModel.swapFromCurrencyFlow.value.data?.result else viewModel.swapToCurrencyFlow.value.data?.result
        intent.putExtra(CurrencyPickerActivity.EXTRA_TYPE, type)
        intent.putExtra(
            CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY,
            if (type == SWAP_FROM) viewModel.selectedFromCurrency.value else viewModel.selectedToCurrency.value
        )
        intent.putExtra(
            CurrencyPickerActivity.EXTRA_CURRENCY_LIST,
            list as ArrayList<CryptoCurrency>
        )
        currencyPickerLauncher.launch(intent)
    }

    private var currencyPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra(CurrencyPickerActivity.EXTRA_TYPE)?.let {
                if (it == SWAP_FROM) {
                    result.data?.getParcelableExtra<CryptoCurrency>(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY)
                        ?.let { item ->
                            viewModel.setSelectedFromCurrency(item)
                        }
                } else {
                    result.data?.getParcelableExtra<CryptoCurrency>(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY)
                        ?.let { item ->
                            viewModel.setSelectedToCurrency(item)
                        }
                }
            }
        }
    }


    private var mpinVerificationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSwap(
                result.data?.getStringExtra(ReltimeConstants.AMOUNT)
            )
        }
    }

    private fun onSwap(amount: String?) {
        if (Utils.isNetworkAvailable(this)!!) {
            if (viewModel.selectedFromCurrency.value != null &&
                viewModel.selectedToCurrency.value != null
            )
                viewModel.doSwap(amount)
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }

    private fun openSuccessPage(transactionItem: TransactionItem) {
        openActivity(SwapSuccessActivity::class.java,true) {
            putString(SwapSuccessActivity.UI_TYPE, PAGE_ID)
            putString(
                SwapSuccessActivity.TRANSACTION_ID, transactionItem.txnId
            )
            putString(
                SwapSuccessActivity.FROM_COIN_CODE,
                Utils.coinCodeWithSymbol(
                    this@SwapActivity,
                    transactionItem.sender?.coinCode.toString(),
                    transactionItem.sender?.symbol
                )
            )
            putString(
                SwapSuccessActivity.TRANSACTION_FROM, transactionItem.sender?.name
            )
            putString(
                SwapSuccessActivity.TO_COIN_CODE,
                Utils.coinCodeWithSymbol(
                    this@SwapActivity,
                    transactionItem.receiver?.coinCode.toString(),
                    transactionItem.receiver?.symbol
                )
            )
            putString(
                SwapSuccessActivity.TRANSACTION_TO, transactionItem.receiver?.name
            )
            putString(
                SwapSuccessActivity.FROM_AMOUNT, transactionItem.sender?.amount.toString()
            )
            putString(
                SwapSuccessActivity.TO_AMOUNT, transactionItem.receiver?.amount.toString()
            )
            putString(
                SwapSuccessActivity.TIMESTAMP, transactionItem.timestamp
            )
            putString(
                SwapSuccessActivity.TRANSACTION_TYPE, transactionItem.type
            )
        }

    }
}

@Composable
fun Content(
    viewModel: SwapViewModel,
    onCurrencyClick: (type: String) -> Unit,
    onSwapClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val response = viewModel.swapFromCurrencyFlow.collectAsState().value
    if (response.status == ApiCallStatus.LOADING) {
        Loader()
        // show loading
    } else if (response.status == ApiCallStatus.ERROR) {
        ErrorView(response.errorMessage)
        //show error
    } else if (response.status == ApiCallStatus.SUCCESS) {
        if (response.data?.success == true && response.data.status == 200 && response.data.result != null) {
            val accountList = response.data.result
            if (accountList.isEmpty())
                EmptyView(R.string.crypto_currencies_not_found)
            else {
              //  viewModel.setSelectedFromCurrency(accountList[0])
                FromCurrency(
                    viewModel,
                    onCurrencyClick,
                    onSwapClick,
                    onSuccess
                )
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}


@Composable
fun FromCurrency(
    viewModel: SwapViewModel,
    onCurrencyClick: (type: String) -> Unit,
    onSwapClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val selectedFromCurrency = viewModel.selectedFromCurrency.collectAsState().value
    val inputText = remember { mutableStateOf(TextFieldValue("")) }
    val selectedToCurrency = viewModel.selectedToCurrency.collectAsState().value
    val enableState = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AccountView(
            onCurrencyClick, SWAP_FROM, selectedFromCurrency, enableState, stringResource(id = R.string.select_from_coin)
        )
        selectedFromCurrency?.let {
            MoveAmountBox(Utils.coinCodeWithSymbol(LocalContext.current,it.coinCode,it.symbol), it.balance, inputText, enableState)
            LoadToCurrency(
                viewModel = viewModel,
                onToCurrencyClick = onCurrencyClick,
                inputText = inputText,
                selectedToCurrency = selectedToCurrency,
                enableState = enableState
            )

        }
    }

    if (selectedFromCurrency != null && inputText.value.text.toDoubleOrNull() != null && selectedToCurrency != null)
        AppGradientButton(text = stringResource(id = R.string.swap), enableState) {
            onSwapClick(inputText.value.text)
        }

    ConfirmSwap(viewModel = viewModel, enableState, onSuccess)

    Loader(enableState)

}

@Composable
fun LoadToCurrency(
    viewModel: SwapViewModel,
    onToCurrencyClick: (type: String) -> Unit,
    inputText: MutableState<TextFieldValue>,
    selectedToCurrency: CryptoCurrency?,
    enableState: MutableState<Boolean>
) {
    val response = viewModel.swapToCurrencyFlow.collectAsState().value

    if (response.status == ApiCallStatus.LOADING) {
        Loader()
        // show loading
    } else if (response.status == ApiCallStatus.ERROR) {
        ErrorView(response.errorMessage)
        //show error
    } else if (response.status == ApiCallStatus.SUCCESS) {
        if (response.data?.success == true && response.data.status == 200 && response.data.result != null) {
            val accountList = response.data.result
            if (accountList.isEmpty())
                EmptyView(R.string.from_account_not_found)
            else {
              /*  if (selectedToCurrency == null)
                    viewModel.setSelectedToCurrency(accountList[0])*/
                ToCurrency(
                    viewModel = viewModel,
                    onToCurrencyClick = onToCurrencyClick,
                    inputText = inputText,
                    selectedToCurrency = selectedToCurrency,
                    enableState = enableState
                )
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}

@Composable
fun AccountView(
    onClick: (type: String) -> Unit,
    type: String,
    selectedCurrency: CryptoCurrency?,
    enableState: MutableState<Boolean>,hint:String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { if (enableState.value) onClick(type) },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = AppCard
    ) {
        AccountSelectionRowContent(name = selectedCurrency?.coin_name?:hint)

    }
}


@Composable
private fun ToCurrency(
    viewModel: SwapViewModel,
    onToCurrencyClick: (type: String) -> Unit,
    inputText: MutableState<TextFieldValue>,
    selectedToCurrency: CryptoCurrency?,
    enableState: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .wrapContentWidth()
            .background(color = AppBlue, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
            text = stringResource(id = R.string.swap),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Icon(
            modifier = Modifier.size(10.dp),
            tint = White,
            painter = painterResource(R.drawable.ic_swap_two_way),
            contentDescription = "swap two way",
        )
        Spacer(modifier = Modifier.width(10.dp))
    }
    Spacer(modifier = Modifier.height(8.dp))
    AccountView(
        onToCurrencyClick, SWAP_TO,
        selectedToCurrency, enableState, stringResource(id = R.string.select_to_coin)
    )
    selectedToCurrency?.let {
        val inputAmount = inputText.value.text.trim()
        if (inputAmount.toDoubleOrNull() != null && inputAmount.toDouble() > 0)
            viewModel.getCryptoConversionStatistics(
                coinCode = selectedToCurrency.coinCode,
                amount = inputAmount,
                sourceCoinCode = viewModel.selectedFromCurrency.value?.coinCode
            )
        SetUpStatisticsBox(
            viewModel = viewModel,
            inputText = inputText.value.text,
            selectedToCurrency = it
        )
    }

}


@Composable
private fun SetUpStatisticsBox(
    viewModel: SwapViewModel, inputText: String,
    selectedToCurrency: CryptoCurrency
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
        if (inputText.toDoubleOrNull() != null && inputText.toDouble() > 0)
            StatisticsAPI(viewModel)
        else Spacer(modifier = Modifier.height(16.dp))
        MoveBalanceBox(coinCode = selectedToCurrency.coinCode, balance = selectedToCurrency.balance)
    }
}


@Composable
private fun StatisticsAPI(viewModel: SwapViewModel) {
    val response = viewModel.currencyStatisticsResponseFlow.collectAsState().value
    if (response.status == ApiCallStatus.LOADING) {
        LoaderMin()
        // show loading
    } else if (response.status == ApiCallStatus.ERROR) {
        ErrorView(response.errorMessage)
        //show error
    } else if (response.status == ApiCallStatus.SUCCESS) {
        val responseOk = response.data?.status == 200 && response.data.success
        if (responseOk && response.data != null && response.data.result != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = response.data.result.converted_amount,
                style = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = fonts
                ),
                color = White60,
            )
        } else {
            ErrorView(response.data?.message)
        }
    }
}

@Composable
private fun ConfirmSwap(
    viewModel: SwapViewModel,
    enableState: MutableState<Boolean>,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val response = viewModel.swapConfirmRequestFlow.collectAsState().value
    enableState.value = response.status != ApiCallStatus.LOADING
    if (response.status == ApiCallStatus.ERROR)
        response.errorMessage?.let {
            val context=LocalContext.current
            LaunchedEffect(key1 = it) {
                context.showToast(it)
            }
        }
    else if (response.status == ApiCallStatus.SUCCESS) {
        response.data?.let { onSuccess(it) }
    }

}
