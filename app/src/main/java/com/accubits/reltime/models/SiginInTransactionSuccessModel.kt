package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class SiginInTransactionSuccessModel(
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
    var result: ResultSiginTranscationRequest
)

@Keep
data class ResultSiginTranscationRequest(
    @Expose
    @SerializedName("txnHash")
    val txnHash: String,
    @Expose
    @SerializedName("details")
    var details: Details?



)

@Keep
data class Details(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("loan_id")
    var loan_id: Int,
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: String,
    @Expose
    @SerializedName("installments")
    var installments: Int,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("collateral")
    val collateral: String,
    @Expose
    @SerializedName("collateral_amount")
    val collateral_amount: String,
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
    @SerializedName("lendingTypes")
    val lendingTypes: String,
    @Expose
    @SerializedName("lentTo")
    val lentTo: String?,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("is_blocked")
    var is_blocked: Boolean,
    @Expose
    @SerializedName("is_removed")
    var is_removed: Boolean,



    @Expose
    @SerializedName("installment_amount")
    val installment_amount: String,

    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("symbol")
    var symbol: String?,

    @Expose
    @SerializedName("timestamp")
    var timestamp: Double,

)
