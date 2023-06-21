package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CloseLoanRequest(

    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("borrowing_id")
    var borrowing_id: Int
)