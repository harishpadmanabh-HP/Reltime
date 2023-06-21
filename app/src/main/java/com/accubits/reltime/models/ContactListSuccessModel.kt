package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContactListSuccessModel(

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
    var result: List<ResultContact>
)

data class ResultContact(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("name")
    val name: String
)
