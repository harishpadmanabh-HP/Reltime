package com.accubits.reltime.activity.v2.wallet.swap.model

import androidx.annotation.Keep

@Keep
data class CryptoConversionRequest
    (val coinCode: String?, val to_coinCode: String?, val amount: String?)