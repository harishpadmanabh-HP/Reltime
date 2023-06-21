package com.accubits.reltime.activity.v2.wallet.home.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TransactionHistoryItem(
    @Expose
    @SerializedName("txn_id")
    var txn_id: String,
    @Expose
    @SerializedName("account_name")
    var account_name: String,
    @Expose
    @SerializedName("mode")
    var mode: String,
    @Expose
    @SerializedName("timestamp")
    var timestamp: Double,)

/*
"sender": {
    "id": 450,
    "email": "vishnupkaccubits@gmail.com",
    "name": "Vishnu",
    "wallet_address": "0x48c42e4560a62c0aec2b9396a2e1ab3050d1b358",
    "contact_number": "+917025549341",
    "profile_image": null,
    "amount": 1.0,
    "coinCode": "RTO",
    "symbol": "â‚¬",
    "type": "Transferred"
},
"receiver": {
    "id": 388,
    "email": "sravandth.s@gmail.com",
    "name": "Sravan",
    "wallet_address": "0xddeaa516614329de9a00c7a75e4d40fbc771b234",
    "contact_number": "+919562617447",
    "profile_image": null,
    "amount": 1.0,
    "coinCode": "RTO",
    "symbol": "â‚¬",
    "type": "Received"
},*/
