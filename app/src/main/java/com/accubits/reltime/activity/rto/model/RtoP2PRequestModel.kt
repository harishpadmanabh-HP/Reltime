package com.accubits.reltime.activity.rto.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RtoP2PRequestModel(
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("coinCode")
    val coinCode: String,
    @SerializedName("contact_type")
    val contactType: String,
    @SerializedName("receiver")
    val receiver: String,
) {
    override fun toString(): String {
        return "amount: $amount, coinCode: $coinCode, contactType: $contactType, receiver:$receiver(type: ${receiver::class.simpleName}) "
    }
}