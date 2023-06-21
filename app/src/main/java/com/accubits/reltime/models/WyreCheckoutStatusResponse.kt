package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class WyreCheckoutStatusResponse(
    @Expose
    @SerializedName("success")
    val success: Boolean,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("status")
    val status: Int,

)

