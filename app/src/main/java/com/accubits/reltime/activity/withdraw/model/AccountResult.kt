package com.accubits.reltime.activity.withdraw.model


import androidx.annotation.Keep
import com.accubits.reltime.activity.myAccounts.model.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AccountResult(
    @SerializedName("bankAccounts")
    val bankAccounts: List<BankAccount>?,
    @SerializedName("cards")
    val cards: List<Card>?,
    @SerializedName("jointAccounts")
    val jointAccounts: List<JointAccount>?,

    @SerializedName("wallets")
    val wallets: Wallets?,

    @SerializedName("cryptoWallet")
    val cryptoWallet: List<CryptoWallet>?

) : Serializable

@Keep
data class Wallets(
    @SerializedName("RTC")
    val rTC: RTO?,
    @SerializedName("RTO")
    val rTO: RTO?,
    @SerializedName("GBP")
    val gbp: RTO?,
    @SerializedName("USD")
    val usd: RTO?
) : Serializable