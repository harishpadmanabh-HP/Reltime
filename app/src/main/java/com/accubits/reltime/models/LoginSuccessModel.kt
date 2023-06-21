package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LoginSuccessModel(

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
    var result: ResultLogin
)

@Keep
data class ResultLogin(
    @Expose
    @SerializedName("token_type")
    val tokenType: String,
    @Expose
    @SerializedName("expiresIn")
    val expiresIn: String,
    @Expose
    @SerializedName("refresh")
    val refresh: String,
    @Expose
    @SerializedName("access")
    val access: String,
    @Expose
    @SerializedName("user_details")
    var userDetails: UserDetails
)

@Keep
data class UserDetails(
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("contact_number")
    val contactNumber: String?,
    @Expose
    @SerializedName("biometrics_enabled")
    val biometricsEnabled: Boolean?,
    @Expose
    @SerializedName("biometric_login")
    val biometricLogin: Boolean,
    @Expose
    @SerializedName("mpin_enabled")
    val mpinEnabled: Boolean?,
    @Expose
    @SerializedName("referral_code")
    val referralCode: String?,
    @Expose
    @SerializedName("mpin_created")
    val mpinCreated: Boolean,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("mnemonic_created")
    val mnemonicCreated: Boolean,
    @Expose
    @SerializedName("joined_date")
    val joinedDate: String,
    @Expose
    @SerializedName("public_key")
    val publicKey: String?,
    @Expose
    @SerializedName("role")
    val role: String,
    @Expose
    @SerializedName("location")
    val location: String,
    @Expose
    @SerializedName("last_login")
    val last_login: String,
    @Expose
    @SerializedName("kyc_created")
    val kycCreated: Boolean,
    @Expose
    @SerializedName("kyc_approved")
    val kycApproved: Boolean,
    @Expose
    @SerializedName("new_last_login")
    val new_last_login: Double
)
