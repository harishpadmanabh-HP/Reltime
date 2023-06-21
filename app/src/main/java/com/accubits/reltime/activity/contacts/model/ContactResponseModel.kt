package com.accubits.reltime.activity.contacts.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ContactResponseModel(
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @Expose
    @SerializedName("message")
    val message: String?,
) {
    @Keep
    data class Result(
        @SerializedName("contact_number")
        val contact_number: String,
        @SerializedName("user_id")
        val user_id: String? = null,
        @SerializedName("is_exist")
        val is_exist: Boolean,
        @SerializedName("name")
        val name: String
    )
}