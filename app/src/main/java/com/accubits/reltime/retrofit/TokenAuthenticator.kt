/*
 * Copyright (C) 2021 Global Art Exchange, LLC ("GAE"). All Rights Reserved.
 *  * You may not use, distribute and modify this code without a license;
 *  * To obtain a license write to legal@gax.llc
 *
 */

package com.accubits.reltime.retrofit

import android.content.Context
import android.content.Intent
import com.accubits.reltime.activity.v2.welcomeScreen.WelcomeScreen
import com.accubits.reltime.helpers.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.lang.reflect.Type


class TokenAuthenticator(
    context: Context, preferenceManager: PreferenceManager
) : Authenticator {


    //private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(context.applicationContext) }
    private var contextData: Context = context
    private var preferenceManagers = preferenceManager
    var suggestionList = ArrayList<String>()

    override fun authenticate(route: Route?, response: Response): Request? {
        val list = preferenceManagers.getSuggestionList() ?: ""
        if (list != null && !list.isEmpty()) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<String>?>() {}.getType()
            suggestionList = gson.fromJson(list, type)
        }
        if (response.code == 401) {
            val intent = Intent(contextData, WelcomeScreen::class.java)//LoginActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            intent.putExtra("logout", "logout")
            contextData.startActivity(intent)

            preferenceManagers.clearPrefs(
                preferenceManagers.getPrivateKey(),
                preferenceManagers.getPublicKey(),true
            )
            preferenceManagers.setSuggestionList(suggestionList)
            return null
        }
        /* return if (response.code == 401) {
             // build retrofit client manually and call refresh token api
           *//*  val refreshTokenResponse =
                refreshtoken(preferenceHelper.getRefreshToken())*//*

            //refreshtoken(preferenceHelper.getRefreshToken())
            if (refreshTokenResponse != null) {
               *//* if (refreshTokenResponse.body() != null) {
                    if (refreshTokenResponse.body()!!.status == 200) {
                        preferenceHelper.setApiKey(refreshTokenResponse.body()!!.data.authToken)
                        preferenceHelper.setRefreshToken(refreshTokenResponse.body()!!.data.refreshToken)
                        Log.d("data!!", refreshTokenResponse.body()!!.data.refreshToken)
                    } else {
                        if(preferenceHelper.getApiKey()!="") {
                            val intent =
                                Intent(ApplicationClass.activity!!, LoginActivity::class.java)
                            ApplicationClass.activity!!.startActivity(intent)
                            ApplicationClass.activity!!.finishAffinity()
                            preferenceHelper.clearPrefs()
                        }

                    }
                } else {
                    if(preferenceHelper.getApiKey()!="") {
                        val intent =
                            Intent(ApplicationClass.activity!!, LoginActivity::class.java)
                        ApplicationClass.activity!!.startActivity(intent)
                        ApplicationClass.activity!!.finishAffinity()
                        preferenceHelper.clearPrefs()
                    }
                }
            } else {
                if(preferenceHelper.getApiKey()!="") {
                    val intent =
                        Intent(ApplicationClass.activity!!, LoginActivity::class.java)
                    ApplicationClass.activity!!.startActivity(intent)
                    ApplicationClass.activity!!.finishAffinity()
                    preferenceHelper.clearPrefs()
                }*//*
            }
            response.request.newBuilder().header("Authorization", preferenceHelper.getApiKey())
                .build()
        } else {
            response.request
        }*/

        return null
    }

}