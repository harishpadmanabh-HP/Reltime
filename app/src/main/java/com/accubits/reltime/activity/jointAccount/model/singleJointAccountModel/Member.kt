package com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class Member(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_accepted")
    val isAccepted: Boolean,
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("is_rejected")
    val isRejected: Boolean,
    @SerializedName("joint_account")
    val jointAccount: String,
    @SerializedName("joint_account_id")
    val jointAccountId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("permissions")
    val permissions: Int,
    @SerializedName("permission_label")
    val permission_label: String?,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("user")
    val user: Int
): Parcelable {
}