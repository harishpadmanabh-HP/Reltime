package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BorrowSuccesModel(
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
    var result: ResultBorrow
)

data class ResultBorrow(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("pending_amount")
    var pending_amount: Double,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("lending")
    var lending: Int,
    @Expose
    @SerializedName("borrower")
    var borrower: Int
)
