package com.accubits.reltime.activity.v2.wallet.deposit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.deposit.viewmodel.DepositViewModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.getAccountBalance
import com.accubits.reltime.utils.Extensions.getAccountName
import com.accubits.reltime.utils.Extensions.getSymbolWithCoinCode
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class DepositActivity : ComponentActivity() {

    private val viewModel by viewModels<DepositViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.deposit,content =  {
                    Content(viewModel = viewModel, onAccountClick = {
                        launchAccountPicker()
                    })
                }, onBackClick = {
                    onBackPressed()
                })
            }
        }
        viewModel.getDepositAccounts()
    }

    private fun launchAccountPicker() {
        val selectedAccount=viewModel.selectedFromAccount.value
        val accountResult=viewModel.depositFromFromFlow.value.data?.result
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
    private var accountPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getSerializableExtra(AccountPickerComposeActivity.EXTRA_SELECTED_ACCOUNT)
                ?.let { any ->
                    viewModel.setSelectedDepositFrom(any as ReltimeAccount)
                }
        }
    }
}

@Composable
private fun Content(viewModel: DepositViewModel, onAccountClick: () -> Unit) {
    val response = viewModel.depositFromFromFlow.collectAsState().value
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
                viewModel.setSelectedDepositFrom(accountList[0])
                FromAccount(viewModel, onAccountClick)
            }
        } else {
            ErrorView(response.data?.message)
        }
    }
}

@Composable
private fun FromAccount(viewModel: DepositViewModel, onAccountClick: () -> Unit) {
    val selectedFromAccount = viewModel.selectedFromAccount.collectAsState().value
    val inputText = remember { mutableStateOf(TextFieldValue("")) }
    val enableState = remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.deposit_into),
            style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold,
            color = White,
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 14.sp
        )
        selectedFromAccount?.let {
            AccountView(accountName=selectedFromAccount.getAccountName(),
                onClick = onAccountClick,
                enableState = enableState
            )
            MoveAmountBox(it.getSymbolWithCoinCode(), it.getAccountBalance(), inputText, enableState)
            Warning()
        }
    }
}


@Composable
private fun AccountView(
    accountName:String,
    onClick: () -> Unit,
    enableState: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { if (enableState.value) onClick() },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = AppCard
    ) {
        AccountSelectionRowContent(name = accountName)
    }

}

@Composable
private fun Warning(){
    Row(modifier = Modifier.padding(16.dp)) {
        Icon(
            modifier = Modifier.size(24.dp).padding(top = 4.dp),
            tint = White,
            painter = painterResource(R.drawable.ic_info),
            contentDescription = "swap two way",
        )
        Text(
            text = stringResource(id = R.string.deposit_warning),
            style = MaterialTheme.typography.body1, fontWeight = FontWeight.Normal,
            color = White80,
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 14.sp
        )
    }
}
