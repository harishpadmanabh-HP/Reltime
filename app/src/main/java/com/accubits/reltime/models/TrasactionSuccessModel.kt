package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TrasactionSuccessModel(

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
    var result: List<ResultTrasactionSuccessModel>
)

@Keep
data class ResultTrasactionSuccessModel(
    @Expose
    @SerializedName("transaction_id")
    val transaction_id: String,
    @Expose
    @SerializedName("purchased")
    var purchased: Int,
    @Expose
    @SerializedName("debited_from")
    val debited_from: String,
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("time")
    val time: String
)
