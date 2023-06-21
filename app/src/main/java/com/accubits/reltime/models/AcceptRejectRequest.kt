package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AcceptRejectRequest(
    @Expose
    @SerializedName("is_accepted")
    var isAccepted: Boolean?,
    @Expose
    @SerializedName("is_rejected")
    var isRejected: Boolean?,
    @Expose
    @SerializedName("request_id")
    var requestId: Int?,
    @Expose
    @SerializedName("joint_account_id")
    val jointAccountId: Int?,
)