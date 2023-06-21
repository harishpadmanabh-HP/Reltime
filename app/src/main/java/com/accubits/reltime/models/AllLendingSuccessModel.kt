package com.accubits.reltime.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class AllLendingSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultAllLending
)

@Keep
data class ResultAllLending(
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
    var row: List<RowAllLending>
)

@Keep
data class RowAllLending(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("published_by")
    val published_by: String,
    @Expose
    @SerializedName("contact_number")
    val contact_number: String,
    @Expose
    @SerializedName("location")
    val location: String,
    @Expose
    @SerializedName("time")
    val time: String,
    @Expose
    @SerializedName("email")
    val email: String,
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
    @SerializedName("date_published")
    val date_published: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("lendingType")
    val lendingType: String,
    @Expose
    @SerializedName("customer_id")
    var customer_id: Int,
    @Expose
    @SerializedName("total_amount")
    var total_amount: Double,
    @Expose
    @SerializedName("total_earning")
    val total_earning: Double,
    @Expose
    @SerializedName("reltime_commision")
    var reltime_commision: Int,
    @Expose
    @SerializedName("payback")
    var payback: Int,
    @Expose
    @SerializedName("coinCode")
    var coinCode: String,
    @Expose
    @SerializedName("symbol")
    var symbol: String?,
    @Expose
    @SerializedName("repayment_date")
    val repayment_date: String,
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
    @SerializedName("is_rejected")
    var is_rejected: Boolean,
    @Expose
    @SerializedName("created_date")
    var created_date: String,
    @Expose
    @SerializedName("amount")
    var amount: String,
    @Expose
    @SerializedName("lendingTypes")
    var lendingTypes: String,
    @Expose
    @SerializedName("collateral_amount")
    var collateral_amount: String
):Serializable

@Keep
@Parcelize
data class Links(
    @Expose
    @SerializedName("next")
    val next: String
) :Parcelable
