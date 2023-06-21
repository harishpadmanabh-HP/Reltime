package com.accubits.reltime.models


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
data class WalletSignInModel(
    @SerializedName("message") val message: String,
    @SerializedName("result") val transactionItem: TransactionItem,
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean
)

@Keep
@Parcelize
data class TransactionItem(
    @SerializedName("mode") val mode: String?,
    @SerializedName("account_name") val accountName: String,
    @SerializedName("receiver") val `receiver`: Receiver?,
    @SerializedName("sender") val sender: Receiver?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("txn_id") val txnId: String?,
    @SerializedName("type") val type: String,
    @SerializedName("checkoutId") val checkoutId: String? = null,
    @SerializedName("status") val status: String? = null,
): Parcelable {
    @Keep
    @Parcelize
    data class Receiver(
        @SerializedName("wallet_address") val wallet_address: String,
        @SerializedName("name") val name: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("coinCode") val coinCode: String,
        @SerializedName("symbol") val symbol: String?,
        @SerializedName("contact_number") val contactNumber: String,
        @SerializedName("email") val email: String?,
        @SerializedName("id") val id: Int,
        @SerializedName("profile_image") val profileImage: String?,
        @SerializedName("type") val type: String
    ): Parcelable
}