package com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep

data class Result(
    @SerializedName("address")
    val address: String,
    @SerializedName("created_at")
    val createdAt: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("members")
    val members: ArrayList<Member>,
    @SerializedName("name")
    val name: String,
    @SerializedName("rto_balance")
    val rtoBalance: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("coinCode")
    val coinCode: String? = null,
    @SerializedName("symbol")
    val symbol: String? = null
)