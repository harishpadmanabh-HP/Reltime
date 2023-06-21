package com.accubits.reltime.activity.biometricLogin.model

import androidx.annotation.Keep
import com.accubits.reltime.constants.ReltimeConstants.ANDROID
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class SmartLoginRequest(
    @Expose
    @SerializedName("emailOrPhone")
    var emailOrPhone: String?,
/*
    @Expose
    @SerializedName("user_id")
    var userId: String?,*/
    @Expose
    @SerializedName("refresh_token")
    var refreshToken: String,
    @Expose
    @SerializedName("loginType")
    var loginType: Int,
    @Expose
    @SerializedName("password")
    var password: String? = null,
    @Expose
    @SerializedName("fcmToken")
    var fcmToken: String? = null,
    @Expose
    @SerializedName("type")
    var type: String? = ANDROID
)
