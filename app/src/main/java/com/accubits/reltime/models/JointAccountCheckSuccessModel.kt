package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class JointAccountCheckSuccessModel(

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
    var result: JointAccountCheckModel
)

@Keep
data class JointAccountCheckModel(
    @Expose
    @SerializedName("isJointWalletAddress")
    var isJointWalletAddress: Boolean
)
