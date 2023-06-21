package com.accubits.reltime.activity.jointAccount.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateJointAccountRequestModel(
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("user")
    val user: List<Int>? = null
)