package com.accubits.reltime.activity.v2.wallet.myaccounts.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.accubits.reltime.activity.myAccounts.model.CryptoWallet
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Keep
data class ExternalAccountListResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<CryptoWallet>?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)
