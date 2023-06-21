package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
data class PaymentHistorySuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultPaymentHistory
)

@Keep
data class ResultPaymentHistory(
    @Expose
    @SerializedName("links")
    var links: Links,
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
    @Expose
    @SerializedName("row")
    var row: List<MyPaymentHistory>
)

@Keep
data class MyPaymentHistory(
    @Expose
    @SerializedName("amount")
    var amount: String,
    @Expose
    @SerializedName("interest_rate")
    var interest_rate: String,
    @Expose
    @SerializedName("coinCode")
    var coinCode: String,
    @Expose
    @SerializedName("symbol")
    var symbol: String?,
    @Expose
    @SerializedName("created_date")
    var created_date: Double
)

