package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyBorrowSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultMyBorrowSuccessModel
)

data class ResultMyBorrowSuccessModel(
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
    var row: List<RowMyBorrowSuccessModel>
)

data class RowMyBorrowSuccessModel(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("instalments")
    val instalments: String,
    @Expose
    @SerializedName("instalmentsPaid")
    val instalmentsPaid: String,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("created_time")
    val created_time: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("pending_amount")
    val pending_amount: Double,

    @Expose
    @SerializedName("collateral")
    val collateral: String,

    @Expose
    @SerializedName("instalment_amount")
    val instalment_amount: String,

    @Expose
    @SerializedName("lender")
    val lender: String,
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: String,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("is_rejected")
    var is_rejected: Boolean,
    @Expose
    @SerializedName("pending_installment")
    val pending_installment: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("lending")
    var lending: Int,
    @Expose
    @SerializedName("borrower")
    var borrower: Int
)

