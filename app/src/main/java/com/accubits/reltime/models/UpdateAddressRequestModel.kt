package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateAddressRequestModel(

    @Expose
    @SerializedName("address1")
    val address1: String,
    @Expose
    @SerializedName("address2")
    val address2: String,
    @Expose
    @SerializedName("city")
    val city: String,
    @Expose
    @SerializedName("state")
    val state: String
)
