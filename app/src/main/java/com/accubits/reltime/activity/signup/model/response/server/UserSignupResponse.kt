package com.accubits.reltime.activity.signup.model.response.server

import com.accubits.reltime.models.ErrorResponse
import com.google.gson.annotations.SerializedName

data class userSignupResponse(
    @SerializedName("success")
    val responseSuccess: Boolean = false,
    @SerializedName("message")
    val responseMessage: String?,
    @SerializedName("status")
    val responseStatus: Int?,
    @SerializedName("result")
    var responseResult: Result?,

    val error: ErrorResponse? = null

) {
    data class Result(
        @SerializedName("emailOrPhone")
        val resultEmail: String
    )

}