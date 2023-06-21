package com.accubits.reltime.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenCodeResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
) {
    @Keep
    data class Result(
        @SerializedName("coin_type") val coinType: List<String>
    )
}