package com.accubits.reltime.activity.settings.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginBiometricStatusRequest(
    @Expose
    @SerializedName("biometric_login")
    var biometric_login: Boolean
)