package com.accubits.reltime.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class TransactionHistorySuccessModelV2(
    val success: Boolean? = null,
    val status: Long? = null,
    val message: String? = null,
    val result: TransactionHistoryV2ResultResult? = null
)

//data class TransactionHistoryV2Result(
//    val success: Boolean? = null,
//    val status: Long? = null,
//    val result: TransactionHistoryV2ResultResult? = null
//)

@Keep
data class TransactionHistoryV2ResultResult(
    val links: TransactionHistoryV2Links? = null,
    val count: Int? = null,

    @Expose
    @SerializedName("num_pages")
    val numPages: Int? = null,

    @Expose
    @SerializedName("per_page")
    val perPage: Int? = null,

    @Expose
    @SerializedName("current_page")
    val currentPage: Int? = null,

    @Expose
    @SerializedName("row")
    val row: List<TransactionItem>
)

@Keep
data class TransactionHistoryV2Links(
    val next: String? = null,
    val previous: String? = null
)
/*

@Parcelize
data class RowTransactionHistoryV2(
    @Expose
    @SerializedName("txn_id")
    val txnID: String? = null,

    @Expose
    @SerializedName("type")
    val type: String? = null,

    val sender: SenderTransactionHistoryV2? = null,
    val receiver: SenderTransactionHistoryV2? = null,
    val timestamp: Double? = null,

    @Expose
    @SerializedName("account_name")
    val accountName: String? = null,

    @Expose
    @SerializedName("checkoutId")
    val checkoutId: String? = null,

    val mode: String? = null
) : Parcelable

@Parcelize
data class SenderTransactionHistoryV2(
    val id: Int? = null,
    val email: String? = null,
    val name: String? = null,

    @Expose
    @SerializedName("wallet_address")
    val walletAddress: String? = null,

    @Expose
    @SerializedName("contact_number")
    val contactNumber: String? = null,

    @Expose
    @SerializedName("profile_image")
    val profileImage: String? = null,

    val amount: Double? = null,
    val coinCode: String? = null,
    val symbol: String? = null,
    val type: String? = null
) : Parcelable
*/
