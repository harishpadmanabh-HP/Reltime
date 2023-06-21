package com.accubits.reltime.activity.v2.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InitDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: AppDetails?,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

@Keep
data class AppDetails(
    @SerializedName("privacy_url")
    val privacy_url: String,
    @SerializedName("terms_url")
    val terms_url: String,
    @SerializedName("android_version")
    val android_version: AndroidVersion
)

@Keep
data class AndroidVersion(
    @SerializedName("immediate")
    val immediate: String,
    @SerializedName("flexible")
    val flexible: String,

    /** appPackage is for migrating the users to the new application. pass the new application package name at that time*/
    @SerializedName("app_package")
    val app_package: String?,
    /** to change update button label*/
    @SerializedName("update_action")
    val update_action: String?,
    @SerializedName("immediate_title")
    val immediate_title: String?,
    @SerializedName("immediate_description")
    val immediate_description: String?,

    )
