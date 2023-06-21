package com.accubits.reltime.activity.myAccounts.model


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class Card(
    @SerializedName("id")
    val id: Int,
    @SerializedName("card_name")
    val cardName: String,
    @SerializedName("card_number")
    val cardNumber: String,
    @SerializedName("card_type")
    val cardType: String,
    @SerializedName("is_default")
    val isDefault: Boolean
):ReltimeAccount(), Serializable