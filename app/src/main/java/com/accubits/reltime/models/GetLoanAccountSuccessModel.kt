package com.accubits.reltime.models

import android.accounts.Account
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.web3j.abi.datatypes.Bool

@Keep
data class GetLoanAccountSuccessModel(
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
    var result: LoanAccountModel
) {

    @Keep
    data class LoanAccountModel(
        @Expose
        @SerializedName("wallets")
        var wallets: AccountModel,
        @Expose
        @SerializedName("jointAccounts")
        var jointAccounts: MutableList<AccountModel>

    )


    @Keep
    @Parcelize
    data class AccountModel(
        @Expose
        @SerializedName("id")
        var id: Int,
        @Expose
        @SerializedName("name")
        var name: String,
        @Expose
        @SerializedName("rto_balance")
        var rto_balance: String,
        @Expose
        @SerializedName("coinCode")
        var coinCode: String,
        @Expose
        @SerializedName("symbol")
        var symbol: String?,
        @Expose
        @SerializedName("address")
        var address: String,
        @Expose
        @SerializedName("isSelected")
        var isSelected: Boolean,
        @Expose
        @SerializedName("isWallet")
        var isWallet: Boolean

    ) : Parcelable {
    }
}