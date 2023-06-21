package com.accubits.reltime.retrofit

import com.accubits.reltime.activity.biometricLogin.model.SmartLoginRequest
import com.accubits.reltime.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*

interface ApiInterface {
    @POST("auth/smart_login/")
    suspend fun smartLogin(
        @Body request: SmartLoginRequest,
        @Header("Time-Zone") timeZone:String= TimeZone.getDefault().id
    ): LoginSuccessModel
}