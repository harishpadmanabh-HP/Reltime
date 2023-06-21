package com.accubits.reltime.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CheckoutSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int?,
    @Expose
    @SerializedName("message")
    val message: String?,
    @Expose
    @SerializedName("result")
    var result: CheckoutData?
):Parcelable

@Parcelize
data class CheckoutData(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("amount")
    val amount: String,
    @Expose
    @SerializedName("converted_amount")
    val convertedAmount: String,
    @Expose
    @SerializedName("display_value")
    val displayValue: String,
    @Expose
    @SerializedName("payment_instructions")
    val paymentInstructions: String,
    @Expose
    @SerializedName("created_at")
    val createdAt: String,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String,
    @Expose
    @SerializedName("source_currency_address")
    val sourceCurrencyAddress: String,
    @Expose
    @SerializedName("dest_currency_address")
    val destCurrencyAddress: String,
    @Expose
    @SerializedName("is_success")
    var isSuccess: Boolean,
    @Expose
    @SerializedName("exchange_rate")
    var exchangeRate: Double,
    @Expose
    @SerializedName("dest_currency")
    var destCurrency:@RawValue Any,
    @Expose
    @SerializedName("source_currency")
    var sourceCurrency:@RawValue Any,

    @Expose
    @SerializedName("txn_status")
    var txnStatus:@RawValue Int?,

):Parcelable
