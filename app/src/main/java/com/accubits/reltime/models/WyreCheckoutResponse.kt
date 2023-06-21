package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WyreCheckoutResponse(
    @Expose
    @SerializedName("success")
    val success: Boolean,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("status")
    val status: Int,
    @Expose
    @SerializedName("result")
    val result: WyreCheckout
)

@Keep
data class WyreCheckout(
    @Expose
    @SerializedName("url")
    val url: String,
    @Expose
    @SerializedName("reservation")
    val reservation: String,
    @Expose
    @SerializedName("id")
    val id: Int,
)
