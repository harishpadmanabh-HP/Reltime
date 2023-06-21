package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BorrowRequest(
    @Expose
    @SerializedName("borrow_id")
    var borrow_id: Int,
    @Expose
    @SerializedName("is_approved")
    var is_approved: Boolean?,
    @Expose
    @SerializedName("is_rejected")
    var is_rejected: Boolean?,
)
