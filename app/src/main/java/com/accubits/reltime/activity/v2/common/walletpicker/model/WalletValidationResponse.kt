package com.accubits.reltime.activity.v2.common.walletpicker.model

import androidx.annotation.Keep
import com.accubits.reltime.activity.v2.lending.model.ResultLendingCalculation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WalletValidationResponse(
    @Expose
    @SerializedName("success")
    val success: Boolean,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("status")
    val status: Int
)
