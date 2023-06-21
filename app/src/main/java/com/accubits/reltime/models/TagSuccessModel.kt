package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TagSuccessModel(
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
    var result: List<ResultTag>?
)

@Keep
data class ResultTag(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("key")
    val key: String
)