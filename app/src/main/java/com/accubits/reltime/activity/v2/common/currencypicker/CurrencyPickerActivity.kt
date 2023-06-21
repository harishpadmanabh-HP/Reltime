package com.accubits.reltime.activity.v2.common.currencypicker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.common.accountpicker.components.AppScaffoldV2
import com.accubits.reltime.activity.v2.common.accountpicker.components.ErrorView
import com.accubits.reltime.activity.v2.ui.theme.*
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.convertReltimeToNagra
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class CurrencyPickerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SELECTED_CURRENCY = "selected_currency"
        const val EXTRA_TYPE = "type"
        const val EXTRA_CURRENCY_LIST = "currency_list"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReltimeTheme {
                AppScaffoldV2(R.string.cryptocurrency,content =   {
                    Content(
                        list = intent.getParcelableArrayListExtra(EXTRA_CURRENCY_LIST)
                    ) { selectedCurrency ->
                        setOnNextButtonClick(selectedCurrency)
                    }
                }, onBackClick =  {
                    onBackPressed()
                })
            }


        }

    }

    private fun setOnNextButtonClick(selectedAccount: CryptoCurrency) {
        val returnIntent = Intent()
        returnIntent.putExtra(
            EXTRA_SELECTED_CURRENCY,
            selectedAccount
        )
        returnIntent.putExtra(
            EXTRA_TYPE,
            intent.getStringExtra(EXTRA_TYPE)
        )
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}

@Composable
fun Content(
    list: ArrayList<CryptoCurrency>?,
    onConfirmClick: (CryptoCurrency) -> Unit,// = hiltViewModel(),
) {
    if (list.isNullOrEmpty())
        ErrorView(message = stringResource(id = R.string.crypto_currencies_not_found))
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            text = stringResource(id = R.string.choose_cryptocurrency),
            style = MaterialTheme.typography.body1
        )
        LazyColumn {
            list?.forEach {
                item { CryptoCurrencyRow(currency = it, onConfirmClick) }
            }
        }
    }
}

@Composable
private fun CryptoCurrencyRow(currency: CryptoCurrency, onConfirmClick: (CryptoCurrency) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.clickable { onConfirmClick(currency) }
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            ) {
            Image(
                painter = rememberAsyncImagePainter(currency.icon),
                contentDescription = "icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 12.dp)) {
                Text(
                    modifier = Modifier,
                    text = currency.coin_name.convertReltimeToNagra().convertRTOtoEURO(),
                    style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold
                )
                Text(
                    modifier = Modifier,
                    color = White60,
                    text = currency.coinCode.convertRTOtoEURO(),
                    style = MaterialTheme.typography.body1
                )
            }
            Icon(
                painter = painterResource( R.drawable.ic_next),
                contentDescription = "Icon",
                tint = White,
                modifier = Modifier.size(20.dp).padding(start = 12.dp)
            )

        }
        Divider(
            color = White20,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
    }
}
 