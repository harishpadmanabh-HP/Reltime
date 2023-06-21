package com.accubits.reltime.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyBorrowingsSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultMyBorrowings
) :Parcelable
@Parcelize
data class ResultMyBorrowings(
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
    var row: List<RowMyBorrowings>
) : Parcelable

@Parcelize
data class RowMyBorrowings(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean,
    @Expose
    @SerializedName("pending_amount")
    var pending_amount: String?,
    @Expose
    @SerializedName("rto_amount")
    var rto_amount: String?,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("time")
    val time: String,
    @Expose
    @SerializedName("instalments")
    val instalments: String,
    @Expose
    @SerializedName("interest_rate")
    val interest_rate: String,
    @Expose
    @SerializedName("created_time")
    val created_time: String,
    @Expose
    @SerializedName("instalmentsPaid")
    val instalmentsPaid: String,
    @Expose
    @SerializedName("lending")
    var lending: Int,
    @Expose
    @SerializedName("borrower")
    var borrower: Int,
    @Expose
    @SerializedName("instalment_amount")
    var instalment_amount: String?,
    @Expose
    @SerializedName("collateral_amount")
    var collateral_amount: String?,
    @Expose
    @SerializedName("collateral")
    val collateral: String,
    @Expose
    @SerializedName("borrowed_by")
    val borrowed_by: String,
    @Expose
    @SerializedName("timestamp")
    val timestamp: String,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("symbol")
    var symbol: String?,
    @Expose
    @SerializedName("last_instalment_paid")
    val last_instalment_paid: Double?,
    @Expose
    @SerializedName("pending_installment")
    val pending_installment: String,
) : Parcelable

