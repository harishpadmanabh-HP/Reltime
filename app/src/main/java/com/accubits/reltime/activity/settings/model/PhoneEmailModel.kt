package com.accubits.reltime.activity.settings.model

import androidx.annotation.Keep
import com.accubits.reltime.constants.ReltimeConstants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class PhoneEmailModel(
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("new_phone")
    var newPhoneNumber: String? = null,
    @SerializedName("new_email")
    var newEmailId: String? = null,


    @Expose
    @SerializedName("token")
    val token: String? = null,
    @Expose
    @SerializedName("type")
    var type: String? = ReltimeConstants.ANDROID
)
