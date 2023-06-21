package com.accubits.reltime.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TransferTransactionResponseModelX(
    @SerializedName("result") val result: Page,
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean
)

@Keep
data class Page(
    @SerializedName("count") val count: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("links") val links: Links,
    @SerializedName("num_pages") val numPages: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("row") val result: List<RowData>
)


@Keep
data class RowData(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_image") val userImage: String?=null,
    @SerializedName("user_name") val userName: String
)

