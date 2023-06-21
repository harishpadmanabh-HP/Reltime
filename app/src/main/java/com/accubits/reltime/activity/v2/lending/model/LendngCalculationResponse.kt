package com.accubits.reltime.activity.v2.lending.model

import androidx.annotation.Keep
import com.accubits.reltime.models.Earnings
import com.accubits.reltime.models.ResultTransactionHistory
import com.accubits.reltime.models.ResultWallet
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LendngCalculationResponse(
    @Expose
    @SerializedName("success")
    val success: Boolean,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("status")
    val status: Int,
    @Expose
    @SerializedName("result")
    var result: ResultLendingCalculation
    )

@Keep
data class ResultLendingCalculation(
    @Expose
    @SerializedName("loanAmount")
    var loanAmount: String,
    @Expose
    @SerializedName("monthlyPayment")
    var monthlyPayment: String,
    @Expose
    @SerializedName("totalOfAll")
    var totalOfAll: String,
    @Expose
    @SerializedName("totalEarnings")
    var totalEarnings: String,



    @Expose
    @SerializedName("totalAdminShare")
    var totalAdminShare: String,
    @Expose
    @SerializedName("totalPercentage")
    var totalPercentage: String,
    @Expose
    @SerializedName("earningPercentage")
    var earningPercentage: String,
    @Expose
    @SerializedName("reltimePercentage")
    var reltimePercentage: String,


    @Expose
    @SerializedName("totalInterestAmount")
    var totalInterestAmount: String,
)

