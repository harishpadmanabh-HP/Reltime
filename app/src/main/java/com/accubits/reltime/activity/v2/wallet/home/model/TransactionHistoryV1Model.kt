package com.accubits.reltime.activity.v2.wallet.home.model

import androidx.annotation.Keep
import com.accubits.reltime.models.ResultTransactionHistorySuccessModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class TransactionHistoryV1Model(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("message")
    var message: String,
    @Expose
    @SerializedName("result")
    var result: ResultTransactionHistorySuccessModel
)


private data class TransactionHistoryResultV1Model(
    @Expose
    @SerializedName("count")
    var count: Int,
    @Expose
    @SerializedName("num_pages")
    var num_pages: Int,
    @Expose
    @SerializedName("per_page")
    var per_page: Int,
    @Expose
    @SerializedName("current_page")
    var current_page: Int,

)
