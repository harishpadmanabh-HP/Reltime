package com.accubits.reltime.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
data class NotificationSuccessModel(
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
    var result: ResultNotificationModel
)

@Keep
data class ResultNotificationModel(
    @Expose
    @SerializedName("count")
    var count: Int,
    @Expose
    @SerializedName("num_pages")
    var num_pages: Int,
    @Expose
    @SerializedName("per_page")
    var per_page: Int,
    @Expose
    @SerializedName("current_page")
    var current_page: Int,
    @Expose
    @SerializedName("row")
    var row: MutableList<RowNotificationModel>?,
)

@Keep
@Parcelize
data class RowNotificationModel(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("image")
    var image: String?,
    @Expose
    @SerializedName("message")
    var message: String,
    @Expose
    @SerializedName("type")
    var type: Int,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("created_at")
    var created_at: String,
    @Expose
    @SerializedName("updated_at")
    var updated_at: String,
    @Expose
    @SerializedName("isRead")
    var isRead: Boolean,
    @Expose
    @SerializedName("user")
    var user: Int,
    @Expose
    @SerializedName("data")
    var data: DataNotificationModel?,
    @Expose
    @SerializedName("is_accepted")
    var isAccepted: Boolean,
    @Expose
    @SerializedName("is_joint_account_removed")
    var isJointAccountRemoved: Boolean,
) : Parcelable

@Keep
@Parcelize
data class DataNotificationModel(
    @Expose
    @SerializedName("name")
    var name: String,
    @Expose
    @SerializedName("created_at")
    var created_at: String,
    @Expose
    @SerializedName("created_by")
    var created_by: String,
    @Expose
    @SerializedName("request_id")
    var request_id: Int?,
    @Expose
    @SerializedName("joint_account_id")
    var joint_account_id: Int?,
    @Expose
    @SerializedName("loan_id")
    var loan_id: Int?,
    @Expose
    @SerializedName("type")
    var type: Int?,
    @Expose
    @SerializedName("title")
    var notificationTitle: String?,
    @Expose
    @SerializedName("message")
    var notificationMessage: String?
) : Parcelable
