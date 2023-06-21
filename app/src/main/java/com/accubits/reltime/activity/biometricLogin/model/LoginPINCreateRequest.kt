package com.accubits.reltime.activity.biometricLogin.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginPINCreateRequest(
    @Expose
    @SerializedName("pin")
    var pin: String
)
