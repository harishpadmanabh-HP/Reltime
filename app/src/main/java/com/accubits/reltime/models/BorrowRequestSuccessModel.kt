package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BorrowRequestSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultBorrowRequestSuccessModel
)

data class ResultBorrowRequestSuccessModel(
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
    var row: List<RowBorrowRequestSuccessModel>
)

data class RowBorrowRequestSuccessModel(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("lending_id")
    var lending_id: Int,
    @Expose
    @SerializedName("lender")
    val lender: String,
    @Expose
    @SerializedName("rto_amount")
    val rto_amount: String,
    @Expose
    @SerializedName("borrower")
    val borrower: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("is_rejected")
    var is_rejected: Boolean
)

