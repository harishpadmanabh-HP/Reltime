package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LoanCloseSuccessModel(


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
    var result: List<ResultLoanCloseSuccessModel>
)

data class ResultLoanCloseSuccessModel(
    @Expose
    @SerializedName("lendingId")
    val lendingId: String?,
    @Expose
    @SerializedName("paidAmount")
    val paidAmount: String?,
    @Expose
    @SerializedName("amountListed")
    val amountListed: String?,
    @Expose
    @SerializedName("interestRate")
    val interestRate: String?,
    @Expose
    @SerializedName("installments")
    val installments: String?,
    @Expose
    @SerializedName("closeDate")
    val closeDate: String?,
    @Expose
    @SerializedName("time")
    val time: String?,
    @Expose
    @SerializedName("transaction_id")
    val transaction_id: String?,
    @Expose
    @SerializedName("purchased")
    var purchased: Int?,
    @Expose
    @SerializedName("debited_from")
    val debited_from: String?,
    @Expose
    @SerializedName("date")
    val date: String?
)
