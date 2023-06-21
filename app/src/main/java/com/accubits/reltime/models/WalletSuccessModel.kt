package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WalletSuccessModel(


    /*   @Expose
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
       var result: ResultWallet
   )

   data class ResultWallet(
       @Expose
       @SerializedName("wallet")
       var wallet: Int,
       @Expose
       @SerializedName("wallet_balance")
       var wallet_balance: Int,
       @Expose
       @SerializedName("total_lendings")
       var total_lendings: Int,
       @Expose
       @SerializedName("total_amount_lent")
       var total_amount_lent: Int*/
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
    var result: ResultWallet
)

@Keep
data class ResultWallet(
    @Expose
    @SerializedName("wallet")
    var wallet: Int,
    @Expose
    @SerializedName("wallet_balance")
    var wallet_balance: String,
    @Expose
    @SerializedName("total_lendings")
    var total_lendings: Double,
    @Expose
    @SerializedName("total_amount_lent")
    var total_amount_lent: Double,
    @Expose
    @SerializedName("pre_order")
    var pre_order: Boolean,
    @Expose
    @SerializedName("invite_friends")
    var invite_friends: Boolean,
    @Expose
    @SerializedName("reltime")
    var reltime: Boolean,
    @Expose
    @SerializedName("last_deposit")
    var last_deposit: String,
    @Expose
    @SerializedName("earnings")
    var earnings: Earnings,
    @Expose
    @SerializedName("recent_transactions")
    var recent_transactions: List<ResultTransactionHistory>?,//ResultTransactionHistory>,
    @Expose
    @SerializedName("my_listed_token")
    var my_listed_token: Double,
    @Expose
    @SerializedName("my_borrowings")
    var my_borrowings: Int,
    @Expose
    @SerializedName("borrow_requests")
    var borrow_requests: Int,
    @Expose
    @SerializedName("isUnreadNotificationAvailable")
    var isUnreadNotificationAvailable: Boolean,
    @Expose
    @SerializedName("kyc_status")
    var kycStatus: String
)

@Keep
data class Earnings(
    @Expose
    @SerializedName("all_time")
    var all_time: String,
    @Expose
    @SerializedName("last_week")
    var last_week: String,
    @Expose
    @SerializedName("last_month")
    var last_month: String,
    @Expose
    @SerializedName("last_year")
    var last_year: String
)
