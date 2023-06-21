package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.web3j.abi.datatypes.Bool

@Keep
data class PermissionResponseModel(
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("result")
    var result: ArrayList<PermissionList>
)

@Keep
data class PermissionList(
    @Expose
    @SerializedName("permission_id")
    var permissionId: Int,
    @Expose
    @SerializedName("label")
    var label: String,
    @Expose
    @SerializedName("status")
    var status: Boolean
)


