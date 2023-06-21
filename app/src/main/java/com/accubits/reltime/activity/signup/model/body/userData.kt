package com.accubits.reltime.activity.signup.model.body

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class userData(
    @Expose
    @SerializedName("emailOrPhone")
    val emailOrPhone: String? = null,
    @Expose
    @SerializedName("name")
    val userName: String,
    @Expose
    @SerializedName("password")
    val userPassword: String,
    @Expose
    @SerializedName("password2")
    val userConfirmPassword: String,
    @Expose
    @SerializedName("location")
    val userLocation: String,
    @Expose
    @SerializedName("referral_code")
    val referral_code: String?,

    )
