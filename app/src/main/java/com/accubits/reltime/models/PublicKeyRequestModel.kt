package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class PublicKeyRequestModel(

    @Expose
    @SerializedName("RTC")
    val RTC: String,
    @Expose
    @SerializedName("RTO")
    val RTO: String,
    @Expose
    @SerializedName("BTC")
    val BTC: String,
    @Expose
    @SerializedName("GBP")
    val GBP: String,
    @Expose
    @SerializedName("USD")
    val USD: String,

)