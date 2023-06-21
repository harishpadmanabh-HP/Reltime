package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendWyreApiRequest(
    @Expose
    @SerializedName("checkout_id")
    val checkoutId: Int,
)
