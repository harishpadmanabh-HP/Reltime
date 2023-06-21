package com.accubits.reltime.activity.jointAccount.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PermissionRequestModel(
    @SerializedName("joint_account_id")
    val jointAccount: Int? = null,
    @SerializedName("user")
    val user: Int? = null,
    @SerializedName("permission")
    val permission: Int? = null
)