package com.accubits.reltime.utils

import com.accubits.reltime.BuildConfig

object AppConfig {


    @JvmStatic
    private external fun developmentBaseUrl(): String

    @JvmStatic
    private external fun preProductionBaseUrl(): String

    @JvmStatic
    private external fun stagingBaseUrl(): String

    @JvmStatic
    private external fun releaseBaseUrl(): String

    @JvmStatic
    external fun getSiteKey(): String

    init {
        System.loadLibrary("native-lib")
    }

    fun getBaseUrl(): String = when (BuildConfig.FLAVOR) {
        "production" -> releaseBaseUrl()
        "preproduction" -> preProductionBaseUrl()
        "development" -> developmentBaseUrl()
        else -> stagingBaseUrl()
    }

}