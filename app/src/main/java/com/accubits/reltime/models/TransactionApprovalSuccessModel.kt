package com.accubits.reltime.models

import androidx.annotation.Keep
import com.accubits.reltime.mnemonic.Unspents
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
data class TransactionApprovalSuccessModel(
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
    var result: ResultTransactionApprovalSuccessModel?
)

@Keep
data class ResultTransactionApprovalSuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("id")
    var Id: Int,
    @Expose
    @SerializedName("data")
    var data: Data
)

@Keep
data class Data(
    @Expose
    @SerializedName("txnBuild")
    var txnBuild: TxnBuild
)

@Keep
data class TxnBuild(
    @Expose
    @SerializedName("nonce")
    val nonce: String,
    @Expose
    @SerializedName("gasPrice")
    val gasPrice: String,
    @Expose
    @SerializedName("gasLimit")
    val gasLimit: String,
    @Expose
    @SerializedName("to")
    val to: String,
    @Expose
    @SerializedName("value")
    val value: String,
    @Expose
    @SerializedName("data")
    val data: String


    ///
    ,
    @Expose
    @SerializedName("balance")
    val balance: Long?,
    @Expose
    @SerializedName("inputs")
    val inputs: List<Inputs>?,
    @Expose
    @SerializedName("outputs")
    val outputs: List<Outputs>?
)


@Keep
data class Inputs(
    @Expose
    @SerializedName("tx_hash")
    val tx_hash: String,
    @Expose
    @SerializedName("block_height")
    val block_height: Long,
    @Expose
    @SerializedName("tx_input_n")
    val tx_input_n: Long,
    @Expose
    @SerializedName("tx_output_n")
    val tx_output_n: Long,
    @Expose
    @SerializedName("value")
    val value: Long,
    @Expose
    @SerializedName("ref_balance")
    val ref_balance: Long,
    @Expose
    @SerializedName("spent")
    val spent: Boolean,
    @Expose
    @SerializedName("confirmations")
    val confirmations: Long,
    @Expose
    @SerializedName("confirmed")
    val confirmed: String,
    @Expose
    @SerializedName("double_spend")
    val double_spend: Boolean,
    @Expose
    @SerializedName("script")
    val script: String)



@Keep
data class Outputs(
    @Expose
    @SerializedName("address")
    val address: String,
    @Expose
    @SerializedName("amount")
    val amount: Long)


/*
"tx_hash":"780e58ed016fb73d0fce30991dc0d0578d9de4d83d6dbab8f41840dc6fd3de82",
"block_height":2378190,
"tx_input_n":-1,
"tx_output_n":0,
"value":6168248,
"ref_balance":6168248,
"spent":false,
"confirmations":11,
"confirmed":"2022-10-26T04:10:38Z",
"double_spend":false
*/

