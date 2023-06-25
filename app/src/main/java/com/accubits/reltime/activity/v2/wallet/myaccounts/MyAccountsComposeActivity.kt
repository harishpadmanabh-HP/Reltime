package com.accubits.reltime.activity.v2.wallet.myaccounts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.accountpicker.components.*
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountDetailActivity
import com.accubits.reltime.activity.v2.wallet.myaccounts.model.ExternalAccountListResponse
import com.accubits.reltime.activity.v2.wallet.myaccounts.viewmodel.MyAccountsViewModel
import com.accubits.reltime.activity.withdraw.model.AccountResult
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.utils.Extensions.accountWithdrawType
import com.accubits.reltime.utils.Extensions.id
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyAccountsComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<MyAccountsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.my_wallet_accounts, content = {
                    Content(
                        viewModel
                    ) { selectedAccount ->
                        onAccountSelected(selectedAccount)
                    }
                }, onBackClick = {
                    onBackPressed()
                })
            }
        }

        viewModel.getAccountList()
        viewModel.getExternalAccountsList()
    }

    private fun onAccountSelected(selectedAccount: ReltimeAccount) {
        val intent = Intent(this, AccountDetailActivity::class.java)
        intent.putExtra("accountType", selectedAccount.accountWithdrawType())
        intent.putExtra("accountId", selectedAccount.id())
        startActivity(intent)
    }
}

@Composable
fun Content(
    viewModel: MyAccountsViewModel,
    onConfirmClick: (ReltimeAccount) -> Unit,// = hiltViewModel(),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val tabs = listOf(
            AccountTabItem.InternalWallet,
            AccountTabItem.ExternalWallet
        )
        val pageState = remember { mutableStateOf(AccountTabItem.InternalWallet.id) }

        AccountTabRow(tabs = tabs, pagerState = pageState)


        if (pageState.value == AccountTabItem.InternalWallet.id)
            InternalAccountData(viewModel = viewModel, onConfirmClick)
        else ExternalAccountData(viewModel = viewModel, onConfirmClick)

    }
}

@Composable
fun InternalAccountData(
    viewModel: MyAccountsViewModel, onConfirmClick: (ReltimeAccount) -> Unit
) {
    val response = viewModel.internalResponseFlow.collectAsState().value
    when (response.status) {
        ApiCallStatus.LOADING -> {
            Loader()
        }
        ApiCallStatus.SUCCESS -> {
            val responseOk = response.data?.status == 200 && response.data.success
            if (responseOk && response.data != null) {
                InternalAccountData(onConfirmClick, response.data.result)
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
fun ExternalAccountData(
    viewModel: MyAccountsViewModel, onConfirmClick: (ReltimeAccount) -> Unit
) {
    val response = viewModel.externalResponseFlow.collectAsState().value
    when (response.status) {
        ApiCallStatus.LOADING -> {
            Loader()
        }
        ApiCallStatus.SUCCESS -> {
            val responseOk = response.data?.status == 200 && response.data.success
            if (responseOk && response.data != null) {
                ExternalAccountData(response = response.data, onConfirmClick)
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
fun ExternalAccountData(
    response: ExternalAccountListResponse,
    onAccountClick: (ReltimeAccount) -> Unit
) {
    if (response.result.isNullOrEmpty()) {
        EmptyView(R.string.no_accounts_available)
        return
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                response.result.let {
                    if (it.isEmpty())
                        return@let
                    item {
                        AccountContainer(
                            R.string.account_name_external_digital_currencies,
                            it.sortedBy { item-> item.coin_name },
                            onAccountClick = onAccountClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InternalAccountData(
    onAccountClick: (ReltimeAccount) -> Unit,
    accountResult: AccountResult
) {
    val accountList = Utils.buildAccountsList(accountResult)
    if (accountList.isEmpty()) {
        EmptyView(R.string.no_accounts_available)
        return
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            val wallet = ArrayList<ReltimeAccount>()
            accountResult.wallets?.rTO?.let {
                wallet.add(it)
            }
            accountResult.wallets?.rTC?.let {
                wallet.add(it)

            }
            accountResult.wallets?.gbp?.let {
                wallet.add(it)

            }
            accountResult.wallets?.usd?.let {
                wallet.add(it)

            }
            if (wallet.isNotEmpty())
                item {
                    AccountContainer(
                        R.string.account_name_wallet,
                        wallet,
                        onAccountClick = onAccountClick
                    )
                }
            accountResult.jointAccounts?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(
                        R.string.account_name_joint_account,
                        it,
                        onAccountClick = onAccountClick
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
                        onAccountClick = onAccountClick
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
                        onAccountClick = onAccountClick
                    )
                }
            }
            accountResult.cards?.let {
                if (it.isEmpty())
                    return@let
                item {
                    AccountContainer(
                        R.string.account_name_cards, it, onAccountClick = onAccountClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountTabRow(tabs: List<AccountTabItem>, pagerState: MutableState<Int>) {
    Row() {
        tabs.forEach {
            AccountTabItem(
                Modifier.weight(1f),
                id = it.id,
                title = it.title,
                pagerState = pagerState
            )
        }
    }
}

@Composable
fun AccountTabItem(modifier: Modifier, id: Int, title: Int, pagerState: MutableState<Int>) {
    Box(
        modifier = modifier
            .background(color = if (pagerState.value == id) AppCard else colorResource(id = R.color.blue2))
            .padding(12.dp)
            .clickable {
                if (pagerState.value != id)
                    pagerState.value = id
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 14.sp,
            fontWeight = if (pagerState.value == id) FontWeight.SemiBold else FontWeight.Normal,
            color = if (pagerState.value == id) White else White60
        )
    }
}


sealed class AccountTabItem(val id: Int, val title: Int) {
    object InternalWallet : AccountTabItem(id = 0, R.string.my_internal_accounts)
    object ExternalWallet : AccountTabItem(id = 1, R.string.my_external_accounts)
}