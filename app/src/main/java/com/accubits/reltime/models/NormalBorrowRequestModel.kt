package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NormalBorrowRequestModel(
    @Expose
    @SerializedName("lending")
    var lending: String?

)
