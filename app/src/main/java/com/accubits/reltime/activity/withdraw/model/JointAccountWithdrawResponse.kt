package com.accubits.reltime.activity.withdraw.model

import androidx.annotation.Keep
import com.accubits.reltime.models.Data
import com.google.gson.annotations.SerializedName

@Keep
data class JointAccountWithdrawResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
) {
    @Keep
    data class Result(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("error")
        val error: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("data")
        val data: Data?,
    )



}
