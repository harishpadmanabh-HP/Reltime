package com.accubits.reltime.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SendWyreSuccessModel(
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
    var result: @RawValue Any
):Parcelable
