package com.accubits.reltime.activity.jointAccount.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EditJointAccountRequestModel(
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("joint_account_id")
    val jointAccount: Int? = null,
    @SerializedName("user")
    val user: List<Int>? = null
)