package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.accubits.reltime.models.Data
import com.google.gson.annotations.SerializedName

@Keep
data class CreateJointAccountTxnResponse(
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
        @SerializedName("data")
        val data: Data,
        @SerializedName("error")
        val error: String? = null,
        @SerializedName("id")
        val jointAccountId: Int,
        @SerializedName("success")
        val success: Boolean
    )


}


