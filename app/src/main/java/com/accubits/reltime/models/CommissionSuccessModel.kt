package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CommissionSuccessModel(

    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("result")
    var result: ResultCommission
)

data class ResultCommission(
    @Expose
    @SerializedName("loanCommissionRate")
    var loanCommissionRate: Int,
    @Expose
    @SerializedName("transactionCommissionRate")
    var transactionCommissionRate: Int
)
