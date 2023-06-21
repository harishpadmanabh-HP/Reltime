package com.accubits.reltime.activity.v2.wallet.bridge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.common.currencypicker.CurrencyPickerActivity
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.bridge.viewmodel.BridgeViewModel
import com.accubits.reltime.activity.v2.wallet.move.BoxTitle
import com.accubits.reltime.activity.v2.wallet.swap.SwapSuccessActivity
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.constants.ReltimeConstants
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.TransactionItem
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.utils.Extensions.openActivity
import com.accubits.reltime.utils.Extensions.showToast
import com.accubits.reltime.views.mpin.MpinValidateActivity
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.DecimalFormat

@AndroidEntryPoint
class BridgeActivity : ComponentActivity() {
    companion object {
        const val PAGE_ID = "bridge_activity"
    }

    private val viewModel by viewModels<BridgeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.bridge, content =  {
                    Content(viewModel = viewModel, onCurrencyClick = {
                        launchCurrencyPicker()
                    }, onBridgeClick = {
                       onBridgeClick(it)
                    }, onSuccess = {
                        openSuccessPage(it.transactionItem)
                    })
                }, onBackClick = {
                    onBackPressed()
                })
            }
        }
        viewModel.getBridgeTokenList()
    }

    private fun onBridgeClick(amount: String){
        val accountBalance =
            viewModel.selectedFromToken.value?.balance?.toDoubleOrNull()
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

    private fun launchCurrencyPicker() {
        val intent = Intent(this, CurrencyPickerActivity::class.java)
        intent.putExtra(
            CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY,
            viewModel.selectedFromToken.value
        )
        intent.putExtra(
            CurrencyPickerActivity.EXTRA_CURRENCY_LIST,
            viewModel.bridgeTokensFlow.value.data?.result as ArrayList<CryptoCurrency>
        )
        currencyPickerLauncher.launch(intent)
    }

    private var currencyPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getParcelableExtra<CryptoCurrency>(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY)
                ?.let { item ->
                    viewModel.setSelectedBridgeFromToken(item)
                }
        }
    }

    private fun openSuccessPage(transactionItem: TransactionItem) {
        openActivity(SwapSuccessActivity::class.java, true) {
            putString(SwapSuccessActivity.UI_TYPE,PAGE_ID)
            putString(
                SwapSuccessActivity.TRANSACTION_ID, transactionItem.txnId
            )
            putString(
                SwapSuccessActivity.FROM_COIN_CODE,
                Utils.coinCodeWithSymbol(
                    this@BridgeActivity,
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
                    this@BridgeActivity,
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
            if (viewModel.selectedFromToken.value != null)
                viewModel.doBridge(amount)
        } else {
            Toast.makeText(this, getString(R.string.please_check_net), Toast.LENGTH_LONG).show()
        }
    }
}


@Composable
private fun Content(
    viewModel: BridgeViewModel,
    onCurrencyClick: () -> Unit,
    onBridgeClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val response = viewModel.bridgeTokensFlow.collectAsState().value
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
                viewModel.setSelectedBridgeFromToken(accountList[0])
                SetUpBridgeFromView(
                    viewModel = viewModel,
                    onCurrencyClick,
                    onBridgeClick = onBridgeClick,
                    onSuccess = onSuccess
                )
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}

@Composable
private fun SetUpBridgeFromView(
    viewModel: BridgeViewModel,
    onCoinClick: () -> Unit,
    onBridgeClick: (amount: String) -> Unit,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val selectedFromToken = viewModel.selectedFromToken.collectAsState().value
    val inputText = remember { mutableStateOf(TextFieldValue("")) }
    val enableState = remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        selectedFromToken?.let {
            BoxTitle(id = R.string.bridge_from)
            BridgeBox(
                coinCode = it.coinCode,
                coinCodeIcon = it.icon,
                network = it.network.toString(),
                networkIcon = it.icon,
                onCoinClick = onCoinClick,
                enableState = enableState,
                isClickable = true
            )
            BoxTitle(id = R.string.bridge_to)
            BridgeBox(
                coinCode = it.coinCode,
                coinCodeIcon = it.reltime_icon.toString(),
                network = it.reltime_network.toString(),
                networkIcon = it.reltime_icon.toString(),
                onCoinClick = onCoinClick,
                enableState = enableState
            )
            BoxTitle(id = R.string.total_amount)
            Statistics(cryptoCurrency = it, enableState = enableState, inputText = inputText)
        }
    }

    if (selectedFromToken != null && inputText.value.text.toDoubleOrNull() != null)
        AppGradientButton(text = stringResource(id = R.string.bridge_tokens), enableState) {
            onBridgeClick(inputText.value.text)
        }
    ConfirmBridge(viewModel = viewModel, enableState = enableState, onSuccess = onSuccess)
    Loader(enableState)
}

@Composable
private fun BridgeBox(
    coinCode: String, coinCodeIcon: String, network: String, networkIcon: String,
    onCoinClick: () -> Unit,
    enableState: MutableState<Boolean>,
    isClickable: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .background(color = AppCard, shape = RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BridgeColumn(
            modifier = Modifier
                .width(130.dp),
            stringResource(id = R.string.token),
            coinCode,
            coinCodeIcon, onCoinClick, enableState, isClickable
        )
        Divider(
            modifier = Modifier
                .height(32.dp)
                .width(0.5.dp), color = AppLightBlue
        )
        BridgeColumn(
            modifier = Modifier
                .fillMaxWidth(),
            stringResource(id = R.string.network),
            network,
            networkIcon, onCoinClick, enableState
        )
    }
}

@Composable
private fun BridgeColumn(
    modifier: Modifier,
    title: String,
    coinText: String,
    iconPath: String,
    onClick: () -> Unit, enableState: MutableState<Boolean>, isClickable: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
            .clickable {
                if (isClickable && enableState.value)
                    onClick()
            }
    ) {
        Text(
            text = title, style = MaterialTheme.typography.body2, fontWeight = FontWeight.Normal,
            color = White60,
            fontSize = 11.sp
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(iconPath),
                contentDescription = "icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
            )
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 16.dp),
                text = coinText,
                maxLines = 1,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                color = White
            )
            if (isClickable)
                Icon(
                    modifier = Modifier.size(10.dp),
                    tint = White,
                    painter = painterResource(R.drawable.ic_down_solid),
                    contentDescription = "from account Icon",
                )
        }
    }
}


@Composable
private fun Statistics(
    cryptoCurrency: CryptoCurrency, inputText: MutableState<TextFieldValue>,
    enableState: MutableState<Boolean>
) {

    val outerPadding = 16.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = AppCard50, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(
                //start = outerPadding,
                end = outerPadding,
                //  top = outerPadding,
                //    bottom = 8.dp
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = inputText.value,
                enabled = enableState.value,
                onValueChange = { value ->
                 //   inputText.value = value
                    if (!value.text.contains(" ") && value.text.toDoubleOrNull() != null)
                        inputText.value = value
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fonts
                ),
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "00.00",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = fonts
                        ),
                        color = White60,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp), // The TextFiled has rounded corners top left and right by default
                colors = TextFieldDefaults.textFieldColors(
                    textColor = White,
                    cursorColor = Color.White,
                    leadingIconColor = Color.White,
                    trailingIconColor = AppLightBlue,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )


            /*     Text(
                     modifier = Modifier
                         .background(color = AppCard, shape = RoundedCornerShape(8.dp))
                         .padding(
                             start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp
                         ),
                     text = "MAX",
                     maxLines = 1,
                     style = MaterialTheme.typography.body1,
                     fontWeight = FontWeight.SemiBold,
                     color = White,
                     fontSize = 12.sp
                 )*/
        }
        MoveBalanceBox(
            modifier = Modifier.padding(start = outerPadding),
            coinCode = Utils.coinCodeWithSymbol(
                LocalContext.current,
                cryptoCurrency.coinCode,
                cryptoCurrency.symbol
            ),
            balance = cryptoCurrency.balance
        )

        CollapsibleBox(cryptoCurrency, inputText)
    }
}

@Composable
private fun CollapsibleBox(
    cryptoCurrency: CryptoCurrency,
    inputText: MutableState<TextFieldValue>
) {
    Column(modifier = Modifier.background(AppCard, shape = RoundedCornerShape(8.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.summary),
                style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold,
                color = AppLightBlue,
                modifier = Modifier.weight(1f),
                fontSize = 12.sp
            )
            /*   Icon(
                   modifier = Modifier.size(10.dp),
                   tint = White,
                   painter = painterResource(R.drawable.ic_down_solid),
                   contentDescription = "from account Icon",
               )*/
        }
        val fee = inputText.value.text.toDoubleOrNull()?.let {
           cryptoCurrency.fee?.toDouble()?.let { it1 ->
                DecimalFormat("#0.000000").format(it.times(it1))}.toString()
        }
        TextRow(
            label = stringResource(id = R.string.fee),
            value = fee ?: stringResource(id = R.string._0_000000)
        )
        //   TextRow(label = stringResource(id = R.string.slippage))
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun TextRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 16.dp, end = 16.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold,
            color = White80,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold,
            color = White80,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ConfirmBridge(
    viewModel: BridgeViewModel,
    enableState: MutableState<Boolean>,
    onSuccess: (WalletSignInModel) -> Unit
) {
    val response = viewModel.bridgeConfirmRequestFlow.collectAsState().value
    enableState.value = response.status != ApiCallStatus.LOADING
    if (response.status == ApiCallStatus.ERROR)
        response.errorMessage?.let {
            val context=LocalContext.current
            LaunchedEffect(key1 = it) {
                context.showToast(it)
            }
        }
    else if (response.status == ApiCallStatus.SUCCESS)
        response.data?.let { onSuccess(it) }

}