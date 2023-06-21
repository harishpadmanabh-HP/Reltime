package com.accubits.reltime.activity.withdraw.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class JointAccountWithdrawRequestNew(
    @Expose
    @SerializedName("withdraw_from")
    val withdraw_from: String? = null,
    @Expose
    @SerializedName("withdraw_from_type")
    val withdraw_from_type: String? = null,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String? = null,
    @Expose
    @SerializedName("amount")
    val amount: String? = null,
    @Expose
    @SerializedName("withdraw_to")
    val withdraw_to: String? = null,
    @Expose
    @SerializedName("withdraw_to_type")
    val withdraw_to_type: String? = null,
    @Expose
    @SerializedName("withdraw_to_coinCode")
    val withdraw_to_coinCode: String? = null

)

