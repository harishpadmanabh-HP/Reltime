package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("result")
    val result: Result,
    @Expose
    @SerializedName("status")
    val status: Int,
    @Expose
    @SerializedName("success")
    val success: Boolean
) {
    data class Result(
        @Expose
        @SerializedName("contact_number")
        val contact_number: String,
        @Expose
        @SerializedName("is_exist")
        val is_exist: Boolean,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("profile_image")
        val profile_image: String,
        @Expose
        @SerializedName("user_id")
        val user_id: String
    )
}
