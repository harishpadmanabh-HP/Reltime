package com.accubits.reltime.activity.signup.model.body

import androidx.annotation.Keep
import com.accubits.reltime.constants.ReltimeConstants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class OtpData(
    @Expose
    @SerializedName("emailOrPhone")
    val emailOrPhone: String? = null,
    @Expose
    @SerializedName("otp")
    val otp: String? = null,
    @Expose
    @SerializedName("token")
    val token: String? = null,
    @Expose
    @SerializedName("type")
    var type: String? = ReltimeConstants.ANDROID
)
