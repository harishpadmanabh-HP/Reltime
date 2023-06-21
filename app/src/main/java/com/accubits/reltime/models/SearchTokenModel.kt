package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class SearchTokenModel(


    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultSearchTokenModel
)

@Keep
data class ResultSearchTokenModel(
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
    var row: List<Row>?
)

@Keep
data class Row(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: String,
    @Expose
    @SerializedName("installments")
    val installments: String,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("collateral")
    val collateral: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("time")
    val time: String,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("is_blocked")
    var is_blocked: Boolean,
    @Expose
    @SerializedName("expected_return")
    var expectedReturn: Double,
    @Expose
    @SerializedName("coinCode")
    var coinCode: String,
    @Expose
    @SerializedName("symbol")
    var symbol: String?,
    @Expose
    @SerializedName("is_removed")
    var is_removed: Boolean,
    @Expose
    @SerializedName("lendingTypes")
    var lendingTypes: String,
    @Expose
    @SerializedName("collateral_amount")
    val collateral_amount: String,
):Serializable



