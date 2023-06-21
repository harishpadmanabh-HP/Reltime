/*
 * Copyright (C) 2021 Global Art Exchange, LLC ("GAE"). All Rights Reserved.
 *  * You may not use, distribute and modify this code without a license;
 *  * To obtain a license write to legal@gax.llc
 *
 */

package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequestModel(
    @Expose
    @SerializedName("emailOrPhone")
    var emailOrPhone: String,
    @Expose
    @SerializedName("password")
    var password: String,
    @Expose
    @SerializedName("fcmToken")
    var fcmToken: String? = null,
    @Expose
    @SerializedName("type")
    var type: String? = null,
)