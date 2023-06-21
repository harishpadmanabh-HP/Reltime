package com.accubits.reltime.activity.v2.common.accountpicker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.common.accountpicker.viewmodel.AccountPicketViewModel
import com.accubits.reltime.activity.v2.ui.theme.ReltimeTheme
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.helpers.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class AccountPickerComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<AccountPicketViewModel>()

    companion object {
        const val EXTRA_SELECTED_ACCOUNT = "selected_account"
        const val EXTRA_ACCOUNT_TYPE = "account_type"
        const val EXTRA_ACCOUNT_LIST = "account_list"

        const val ACCOUNT_TYPE_MOVE_FROM = "move_from"
        const val ACCOUNT_TYPE_MOVE_TO = "move_to"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.my_accounts, content = {
                    Content(
                        viewModel,
                        intent.getSerializableExtra(EXTRA_ACCOUNT_LIST) as AccountResult?,
                        intent.getSerializableExtra(EXTRA_SELECTED_ACCOUNT) as ReltimeAccount?
                    ) { selectedAccount ->
                        setOnNextButtonClick(selectedAccount)
                    }
                }, onBackClick =  {
                    onBackPressed()
                })
            }
        }
    }

    private fun setOnNextButtonClick(selectedAccount: Any) {
        if (selectedAccount !is Serializable) return
        val returnIntent = Intent()
        returnIntent.putExtra(
            EXTRA_SELECTED_ACCOUNT,
            selectedAccount
        )
        returnIntent.putExtra(
            EXTRA_ACCOUNT_TYPE,
            intent.getStringExtra(EXTRA_ACCOUNT_TYPE)
        )
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}

@Composable
fun Content(
    viewModel: AccountPicketViewModel,
    accountResult: AccountResult?,
    selectedAccount: ReltimeAccount?,
    onConfirmClick: (ReltimeAccount) -> Unit,// = hiltViewModel(),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            text = stringResource(id = R.string.select_account),
            style = MaterialTheme.typography.body1
        )
        AccountData(viewModel = viewModel, onConfirmClick, accountResult, selectedAccount)
    }
}

@Composable
fun AccountData(
    viewModel: AccountPicketViewModel, onConfirmClick: (ReltimeAccount) -> Unit,
    accountResult: AccountResult?, selectedAccount: ReltimeAccount?
) {
    if (accountResult != null) {
        AccountData(onConfirmClick, accountResult, selectedAccount)
        return
    }
    val response = viewModel.listResponseFlow.collectAsState().value
    when (response.status) {
        ApiCallStatus.LOADING -> {
            Loader()
        }
        ApiCallStatus.SUCCESS -> {
            val responseOk = response.data?.status == 200 && response.data.success
            if (responseOk && response.data != null) {
                AccountData(onConfirmClick, response.data.result, selectedAccount)
            }
        }
        ApiCallStatus.ERROR -> {
            ErrorView(response.errorMessage)
        }
        else -> {

        }
    }
}

@Composable
fun AccountData(
    onConfirmClick: (ReltimeAccount) -> Unit,
    accountResult: AccountResult, selectedAccount: ReltimeAccount?
) {
    val accountList = Utils.buildAccountsList(accountResult)
    if (accountList.isEmpty()) {
        EmptyView(R.string.no_accounts_available)
        return
    }
    val selectedState = remember { mutableStateOf(selectedAccount) }// ?: accountList[0]) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            val wallet = ArrayList<ReltimeAccount>()
            accountResult.wallets?.rTO?.let {
                wallet.add(it)
            }
            accountResult.wallets?.rTC?.let {
                wallet.add(it)
            }
            if (wallet.isNotEmpty())
                item {
                    AccountContainer(
                        R.string.account_name_wallet,
                        wallet,
                        selectedState
                    )
                }
            accountResult.jointAccounts?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(
                        R.string.account_name_joint_account,
                        it,
                        selectedState
                    )
                }
            }
            accountResult.cryptoWallet?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(
                        R.string.account_name_reltim_digital_currencies,
                        it,
                        selectedState
                    )
                }
            }
            accountResult.bankAccounts?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(
                        R.string.account_name_bank_account,
                        it,
                        selectedState
                    )
                }
            }
            accountResult.cards?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(R.string.account_name_cards, it, selectedState)
                }
            }
            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
        selectedState.value?.let {
            AppGradientButton(text = stringResource(id = R.string.next)) {
                onConfirmClick(it)
            }
        }
    }
}