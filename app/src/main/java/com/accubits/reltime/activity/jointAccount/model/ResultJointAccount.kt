package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep

data class ResultJointAccount(
    @SerializedName("address")
    val address: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("members")
    val members: List<Member>,
    @SerializedName("name")
    val name: String,
    @SerializedName("rto_balance")
    val rtoBalance: String,
    @SerializedName("status")
    val status: Boolean
)