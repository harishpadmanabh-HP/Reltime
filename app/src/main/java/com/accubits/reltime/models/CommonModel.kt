package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CommonModel(

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
    var result: ResultC?,

    var error: Boolean = false,

    )

data class ResultC(
    @Expose
    @SerializedName("email")
    val email: String?
)
