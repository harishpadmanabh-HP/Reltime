package com.accubits.reltime.activity.jointAccount.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep

data class Member(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_accepted")
    val isAccepted: Boolean,
    @SerializedName("is_rejected")
    val isRejected: Boolean,
    @SerializedName("joint_account")
    val jointAccount: String,
    @SerializedName("joint_account_id")
    val jointAccountId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("permissions")
    val permissions: Int,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("user")
    val user: Int
)