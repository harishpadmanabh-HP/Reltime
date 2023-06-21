package com.accubits.reltime.repository

import com.accubits.reltime.activity.biometricLogin.model.SmartLoginRequest
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.retrofit.RetrofitClient
import hu.akarnokd.rxjava3.retrofit.Result.response
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException


private const val TAG = "BiometricLoginRepository"

class BiometricLoginRepository {

    suspend fun smartLogin( smartLoginRequest: SmartLoginRequest) =
        try {
            val response =  RetrofitClient.apiInterface.smartLogin(smartLoginRequest)
            ApiMapper(ApiCallStatus.SUCCESS, response, null)
        } catch (ex: HttpException) {
            var message: String? = null
            try {
                message= ex.response()?.errorBody()?.string()?.let {
                  JSONObject(it).getString("message")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            ApiMapper(ApiCallStatus.ERROR, null,message?: ex.message)

        }

}
