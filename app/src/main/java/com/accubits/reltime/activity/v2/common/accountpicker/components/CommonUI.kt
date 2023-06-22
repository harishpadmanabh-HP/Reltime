package com.accubits.reltime.activity.v2.common.accountpicker.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.getAccountBalanceWithCoinCode
import com.accubits.reltime.utils.Extensions.getAccountId
import com.accubits.reltime.utils.Extensions.getStatistics
import com.accubits.reltime.utils.TextSizeTransformation
import com.accubits.reltime.utils.buildAnnotatedStringWithColors
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra

private val HeaderHeight = 56.dp
private val ProgressBarSize = 36.dp
private val AppPadding = 16.dp

@Composable
fun AppScaffoldV2(title: Int, backIcon:Int=R.drawable.ic_back,content: @Composable () -> Unit = {}, onBackClick: () -> Unit) {
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.ic_background),
            contentDescription = "background_image",
            contentScale = ContentScale.FillBounds
        )
        Scaffold(
            backgroundColor = Color.Transparent,   // Make the background transparent
            topBar = {
                Header(title,backIcon, onBackClick)
            }
        ) { innerPadding ->
            Box(
                Modifier.padding(innerPadding)
            ) {
                content()
            }
        }
    }
}

@Composable
fun Header(title: Int,backIcon:Int, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(HeaderHeight)
            .fillMaxWidth()
            .background(color = colorResource(R.color.darkBlack)),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(size = 48.dp)
                .padding(16.dp)
                .clickable { onBackClick.invoke() }, contentAlignment = Alignment.Center
        )
        {
            Icon(
                painter = painterResource(backIcon),
                contentDescription = "Back Button",
                modifier = Modifier
                    .fillMaxSize(),
                tint = AppBlue
            )
        }
        Text(text = stringResource(id = title), style = MaterialTheme.typography.h6)
    }
}

@Composable
fun Loader(disableState: MutableState<Boolean> = remember { mutableStateOf(false) }) {
    if (!disableState.value)
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        )
        {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(ProgressBarSize),
                color = Color.White
            )
        }
}

@Composable
fun EmptyView(message: Int = R.string.no_data_found) {
    BaseErrorView(message = stringResource(id = message))
}

@Composable
fun ErrorView(message: String?) {
    BaseErrorView(message = message ?: stringResource(id = R.string.some_thing_went_wrong))
}

@Composable
private fun BaseErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    )
    {
        Text(text = message, style = MaterialTheme.typography.subtitle2)
    }
}

@Composable
fun AccountContainer(
    accountName: Int,
    list: List<ReltimeAccount>,
    selectedAccountState: MutableState<ReltimeAccount?>? = null,
    onAccountClick: (ReltimeAccount) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = AppPadding, end = AppPadding)
    )
    {
        Text(
            text = stringResource(id = accountName),
            style = MaterialTheme.typography.body1,
            color = AppLightBlue,
            modifier = Modifier.padding(top = 20.dp),
            fontSize = 14.sp
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = colorResource(id = R.color.darkBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp)
                    .wrapContentHeight()
            ) {
                list.forEachIndexed { index, any ->
                    AccountItem(any, selectedAccountState, onAccountClick)
                    if (index != list.size - 1)
                        Divider(
                            color = White60,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                        )
                }
            }
        }
    }
}


@Composable
fun AccountItem(
    account: ReltimeAccount,
    selectedAccountState: MutableState<ReltimeAccount?>? = null,
    onAccountClick: (ReltimeAccount) -> Unit,
    enableAccountSelection: Boolean = selectedAccountState != null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = {
                if (!enableAccountSelection)
                    onAccountClick(account)
                else selectedAccountState?.value = account
            })
            .padding(14.dp)
    ) {
        if (enableAccountSelection)
            IconToggleButton(
                checked = account.getAccountId() == selectedAccountState?.value?.getAccountId(),
                onCheckedChange = {
                    selectedAccountState?.value = account
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (account.getAccountId() == selectedAccountState?.value?.getAccountId())//checkedState.value)
                            R.drawable.ic_radio_active else R.drawable.ic_radio_normal
                    ),
                    contentDescription = "Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )
            }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (enableAccountSelection) 12.dp else 0.dp)
        ) {
            Text(
                text = Utils.getAccountName(account),
                style = MaterialTheme.typography.body2,
                color = Color.White
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = Utils.getAccountInfo(LocalContext.current, account),
                style = MaterialTheme.typography.body2,
                color = White60,
                fontSize = 13.sp
            )
        }
        Column() {
            Text(
                text = account.getAccountBalanceWithCoinCode(),
                style = MaterialTheme.typography.body2,
                color = White80,
                fontSize = 13.sp
            )
            val statistics = account.getStatistics()

            if (!enableAccountSelection && !statistics.isNullOrEmpty())
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                ) {
                    var color = R.color.white80
                    var icon: Int? = null
                    when {
                        statistics.toDouble() > 0.0 -> {
                            icon = R.drawable.ic_graph_green
                            color = R.color.graph_bullish_green
                        }
                        statistics.toDouble() < 0.0 -> {
                            icon = R.drawable.ic_graph_red
                            color = R.color.graph_bearish_red
                        }
                    }
                    icon?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = "Icon",
                            tint = colorResource(id = color),
                            modifier = Modifier
                                .size(14.dp)
                        )
                        Text(
                            modifier = Modifier.padding(start = 6.dp),
                            text = statistics.toDouble().toString(),
                            style = MaterialTheme.typography.body2,
                            color = colorResource(id = color),
                            fontSize = 12.sp
                        )
                    }
                }
        }

        if (!enableAccountSelection)
            Icon(
                painter = painterResource(R.drawable.ic_next),
                contentDescription = "Icon",
                tint = White40,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 12.dp)
            )
    }

}


@Composable
fun AppGradientButton(
    text: String, enableState: MutableState<Boolean> = remember { mutableStateOf(true) },
    onClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    )
    {
        val modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        Button(
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            contentPadding = PaddingValues(),
            onClick = { if (enableState.value) onClick() },
        ) {
            Box(
                modifier = Modifier
                    .alpha(if (enableState.value) 1f else .5f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                colorResource(id = R.color.linear_gradient_base),
                                colorResource(id = R.color.linear_gradient_top)
                            )
                        )
                    )
                    .then(modifier),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text, style = MaterialTheme.typography.subtitle2,
                )
            }
        }
    }
}


@Composable
fun SearchView(state: MutableState<TextFieldValue>, placeHolder: Int) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        placeholder = {
            Text(
                text = stringResource(id = placeHolder),
                style = MaterialTheme.typography.body1,
                color = White60,
            )
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        modifier = Modifier
            .padding(16.dp)
            .height(50.dp)
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body1,
        trailingIcon = {
            IconButton(
                onClick = {
                    state.value =
                        TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                }
            ) {
                Icon(
                    if (state.value != TextFieldValue("")) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp), // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = AppLightBlue,
            backgroundColor = colorResource(id = R.color.app_gray),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun SnackBar(message: String) {
    val scaffoldState = rememberScaffoldState() // this contains the `SnackbarHostState`

    LaunchedEffect(Unit) {
        scaffoldState.snackbarHostState.showSnackbar(
            message,
            duration = SnackbarDuration.Long
        )
    }

}

@Composable
fun AccountSelectionRowContent(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = name.convertReltimeToNagra().convertRTOtoEURO(),
            color = White,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Icon(
            modifier = Modifier.size(14.dp),
            tint = White,
            painter = painterResource(R.drawable.ic_dropdown_white),
            contentDescription = "from account Icon",
        )
    }
}


@Composable
fun MoveAmountBox(
    coinCode: String, balance: String,
    inputText: MutableState<TextFieldValue>,
    enableState: MutableState<Boolean>
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
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputText.value,
            enabled = enableState.value,
            onValueChange = { value ->
                inputText.value = value
                if (!value.text.contains(" ") && value.text.toDoubleOrNull() != null)
                    inputText.value = value

            },
            textStyle = TextStyle(
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                fontFamily = fonts
            ),
            visualTransformation = TextSizeTransformation(),
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = buildAnnotatedStringWithColors(stringResource(id = R.string._0_00)),
                    style = TextStyle(
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
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
        MoveBalanceBox(coinCode = coinCode, balance = balance)


    }

}

@Composable
fun MoveBalanceBox(modifier: Modifier = Modifier, coinCode: String, balance: String) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .background(
                colorResource(id = R.color.wallet_drop_down_blue),
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Text(
            text = stringResource(
                id = R.string.n_balance_n,
                coinCode,
                balance
            ).convertRTOtoEURO(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}


@Composable
fun LoaderMin() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(48.dp)
            .padding(16.dp), strokeWidth = 1.dp,
        color = White60
    )
}