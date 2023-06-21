package com.accubits.reltime.activity.signup.model.response.server

import androidx.annotation.Keep
import com.accubits.reltime.models.ErrorResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ValidationResponse(
    @Expose
    @SerializedName("success")
    val success: Boolean?,
    @Expose
    @SerializedName("is_verified")
    val is_verified: Boolean?,
    @Expose
    @SerializedName("already_exists")
    val already_exits: Boolean?,
    @Expose
    @SerializedName("status")
    val status: Int?,
    @Expose
    @SerializedName("message")
    val message: String?,

    val error: ErrorResponse?
)