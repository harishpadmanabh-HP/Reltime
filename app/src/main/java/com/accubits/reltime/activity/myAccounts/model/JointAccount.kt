package com.accubits.reltime.activity.myAccounts.model


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class JointAccount(
    @SerializedName("address")
    val address: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("members")
    val members: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rto_balance")
    val rtoBalance: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("statistics")
    val statistics: String?
) : ReltimeAccount(), Serializable