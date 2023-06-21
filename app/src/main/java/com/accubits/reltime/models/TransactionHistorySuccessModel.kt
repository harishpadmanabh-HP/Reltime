package com.accubits.reltime.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class TransactionHistorySuccessModel(

    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("total_transactions")
    var total_transactions: Int,
    @Expose
    @SerializedName("result")
    var result: ResultTransactionHistorySuccessModel
)

data class ResultTransactionHistorySuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
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
    @SerializedName("result")
    var result: List<ResultTransactionHistory>
)

@Parcelize
data class ResultTransactionHistory(
    @Expose
    @SerializedName("transaction_id")
    val transaction_id: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("sender")
    val sender: String?,
    @Expose
    @SerializedName("receiver")
    val receiver: String?,
    @Expose
    @SerializedName("created_time")
    val created_time: String?,
    @Expose
    @SerializedName("coin")
    val coin: String?,
    @Expose
    @SerializedName("type")
    val type: String,
    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("debited_from")
    val debited_from: String?,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("checkout_id")
    val checkoutId: String?,

) : Parcelable


