package com.accubits.reltime.activity.region.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Country(
    @SerializedName("name")
    val countryName: String,
    @SerializedName("dial_code")
    val dialCode: String,
    @SerializedName("code")
    val countryISOCode: String,

    var emojiString: String
) : Serializable {
    override fun toString(): String {
        return "$countryName, $dialCode, $countryISOCode"
    }
}