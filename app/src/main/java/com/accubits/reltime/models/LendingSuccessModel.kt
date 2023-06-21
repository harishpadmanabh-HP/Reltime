package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LendingSuccessModel(

    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("result")
    var result: ResultLendingTokenSuccessModel
)

@Keep
data class ResultLendingTokenSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("data")
    var data: Data
)
