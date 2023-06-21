package com.accubits.reltime.activity.settings.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CloseRequestHome(
    @Expose
    @SerializedName("pre_order")
    var pre_order: Boolean?,
    @Expose
    @SerializedName("invite_friends")
    var invite_friends: Boolean?,
    @Expose
    @SerializedName("reltime")
    var reltime: Boolean?
)
