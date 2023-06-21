package com.accubits.reltime.activity.v2.signUp.model

import androidx.annotation.Keep
import com.accubits.reltime.constants.ReltimeConstants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
data class SignUpRequestModel(
    @Expose
    @SerializedName("email")
    val email: String? = null,
    @Expose
    @SerializedName("phone")
    val phone: String?,
    @Expose
    @SerializedName("firstName")
    val firstName: String?,
    @Expose
    @SerializedName("lastName")
    val lastName: String?,
    @Expose
    @SerializedName("password")
    val password: String,
    @Expose
    @SerializedName("password2")
    val password2: String,
    @Expose
    @SerializedName("location")
    val location: String?,

    @Expose
    @SerializedName("fcmToken")
    var fcmToken: String? = null,
    @Expose
    @SerializedName("biometric_login")
    val biometricLogin: Boolean,
    @Expose
    @SerializedName("type")
    var type: String? = ReltimeConstants.ANDROID
    )
