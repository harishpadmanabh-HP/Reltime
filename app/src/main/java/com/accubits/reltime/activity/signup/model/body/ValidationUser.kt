package com.accubits.reltime.activity.signup.model.body

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ValidationUser(
    @Expose
    @SerializedName("emailOrPhone")
    val email: String? = null,
)
