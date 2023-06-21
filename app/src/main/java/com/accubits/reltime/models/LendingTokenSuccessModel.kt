package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LendingTokenSuccessModel(
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
    var result: List<ResultLendingList>?
)

data class ResultLendingList(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: String,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("installments")
    val installments: String,
    @Expose
    @SerializedName("collateral")
    val collateral: String,
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("time")
    val time: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("is_blocked")
    var is_blocked: Boolean,
    @Expose
    @SerializedName("is_removed")
    var is_removed: Boolean
)



