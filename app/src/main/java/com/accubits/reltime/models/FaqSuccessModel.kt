package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class FaqSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int?,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("result")
    var result: List<ResultFaqSuccessModel>?
)

@Keep
data class ResultFaqSuccessModel(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("question")
    val question: String,
    @Expose
    @SerializedName("answer")
    val answer: String,
    @Expose
    @SerializedName("is_top_question")
    var is_top_question: Boolean,
    @Expose
    @SerializedName("key")
    var key: Int
)