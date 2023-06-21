package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardSuccessModel(
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
    var result: List<ResultCardSuccessModel>?
)

data class ResultCardSuccessModel(
    @Expose
    @SerializedName("card_type")
    val card_type: String,
    @Expose
    @SerializedName("card_number")
    val card_number: String,
    @Expose
    @SerializedName("card_name")
    val card_name: String,
    @Expose
    @SerializedName("is_default")
    val is_default: Boolean,
)