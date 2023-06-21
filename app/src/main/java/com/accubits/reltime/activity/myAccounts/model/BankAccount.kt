package com.accubits.reltime.activity.myAccounts.model


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class BankAccount(
    @SerializedName("bank_name")
    val bank_name: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("wallet")
    val wallet: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("swift_code")
    val swiftCode: String,
    @SerializedName("address")
    val address: String,
   /*
    @SerializedName("contact_number")
    val contactNumber: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("is_default")
    val isDefault: Boolean,*/
): ReltimeAccount(),Serializable