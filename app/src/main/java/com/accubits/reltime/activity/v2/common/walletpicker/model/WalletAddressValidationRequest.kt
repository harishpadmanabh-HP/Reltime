package com.accubits.reltime.activity.v2.common.walletpicker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WalletAddressValidationRequest(
    @Expose
    @SerializedName("coin_type")
    val coinType: String? = null,
    @Expose
    @SerializedName("address")
    val address: String? = null
)



