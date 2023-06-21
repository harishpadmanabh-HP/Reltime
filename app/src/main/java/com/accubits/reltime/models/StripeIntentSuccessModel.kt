package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StripeIntentSuccessModel(
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
    var result: ResultStripeIntentSuccessModel
)

data class ResultStripeIntentSuccessModel(
    @Expose
    @SerializedName("client_secret")
    val client_secret: String?,
    @Expose
    @SerializedName("intent_id")
    val intent_id: String?
)