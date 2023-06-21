package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class UserSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int?,
    @Expose
    @SerializedName("result")
    var result: ResultUser?
)

@Keep
data class ResultUser(
    @Expose
    @SerializedName("id")
    var id: Int?,
    @Expose
    @SerializedName("user")
    var user: Int?,
    @Expose
    @SerializedName("full_name")
    val full_name: String?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("image")
    val image: String?,
    @Expose
    @SerializedName("gender")
    val gender: String?,
    @Expose
    @SerializedName("date_of_birth")
    val date_of_birth: String?,
    @Expose
    @SerializedName("contact_number")
    val contact_number: String?,
    @Expose
    @SerializedName("location")
    val location: String?,
    @Expose
    @SerializedName("address1")
    val address1: String?,
    @Expose
    @SerializedName("address2")
    val address2: String?,
    @Expose
    @SerializedName("city")
    val city: String?,
    @Expose
    @SerializedName("state")
    val state: String?,
    @Expose
    @SerializedName("rtc_balance")
    var rtc_balance: Double?,
    @Expose
    @SerializedName("rto_balance")
    var rto_balance: Double?,
    @Expose
    @SerializedName("kyc_status")
    val kyc_status: String?,
    @Expose
    @SerializedName("kyc_created")
    val kyc_created: Boolean,
    @Expose
    @SerializedName("kyc_approved")
    val kyc_approved: Boolean,
    @Expose
    @SerializedName("document")
    val document: String?,
    @Expose
    @SerializedName("biometrics_enabled")
    val biometrics_enabled: Boolean?,
    @Expose
    @SerializedName("mpin_enabled")
    val mpin_enabled: Boolean?,
    @Expose
    @SerializedName("status")
    var status: Boolean?,
    @Expose
    @SerializedName("created_at")
    val created_at: String?
)