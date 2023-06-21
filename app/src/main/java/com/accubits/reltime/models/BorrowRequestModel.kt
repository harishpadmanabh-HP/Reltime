package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BorrowRequestModel(
    @Expose
    @SerializedName("lending")
    val lending: Int
)
