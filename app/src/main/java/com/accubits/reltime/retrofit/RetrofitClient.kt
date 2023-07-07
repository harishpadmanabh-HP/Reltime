/*
 * Copyright (C) 2021 Global Art Exchange, LLC ("GAE"). All Rights Reserved.
 *  * You may not use, distribute and modify this code without a license;
 *  * To obtain a license write to legal@gax.llc
 *
 */
package com.accubits.reltime.retrofit

import android.app.Application
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.utils.AppConfig
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    val retrofitClient: Retrofit.Builder by lazy {

        val levelType: HttpLoggingInterceptor.Level
        levelType = if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
            HttpLoggingInterceptor.Level.BODY else Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)
        val okhttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().also {
                it.addHeader("App", "Nagra")
            }.build())
        }.also { client ->
            client.addInterceptor(logging)
        }.build()

        // val authenticator = TokenAuthenticator(application)

        Retrofit.Builder()
            .baseUrl(AppConfig.getBaseUrl())
            .client(okhttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

  /*  private fun getRetrofitClient(authenticator: Authenticator? = null): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                }.build())
            }.also { client ->
                authenticator?.let { client.authenticator(it) }
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }*/

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }


}



