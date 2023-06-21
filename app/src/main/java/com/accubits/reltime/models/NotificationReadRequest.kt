package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationReadRequest(
    @Expose
    @SerializedName("isRead")
    var isRead: Boolean?,
    @Expose
    @SerializedName("notification_id")
    var notificationId: Int?
)
