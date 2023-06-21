/*
 * Copyright (C) 2021 Global Art Exchange, LLC ("GAE"). All Rights Reserved.
 *  * You may not use, distribute and modify this code without a license;
 *  * To obtain a license write to legal@gax.llc
 *
 */

package com.accubits.reltime.helpers

interface IPreferenceHelper {
    fun setFCMToken(fcmToken: String)
    fun getFCMToken(): String
    fun setApiKey(apiKey: String)
    fun getApiKey(): String
    fun setUserId(userId: Int)
    fun getUserId(): Int
    fun setKycCreated(created: Boolean)
    fun isKycCreated(): Boolean
    fun setKycApproved(approved: Boolean)
    fun isKycApproved(): Boolean
    fun clearPrefs(token: String, key: String,restoreLastUser:Boolean)
    fun setEmail(apiKey: String)
    fun setPhone(apiKey: String)
    fun getEmail(): String
    fun getPhone(): String
    fun setRefreshToken(id: String)
    fun getRefreshToken(): String
    fun setFormToken(token: String)
    fun getFormToken(): String
    fun getName(): String
    fun setName(name: String)
    fun getLastLogin(): String
    fun setLastLogin(name: String)
    fun setOnBoardingCompleted(name: String)
    fun getOnBoardingCompleted(): String
    fun setBiometric(name: Boolean)
    fun getBiometric(): Boolean
    fun setMpin(name: Boolean)
    fun getMpin(): Boolean
    fun setMpinEnabled(mpin: Boolean)
    fun getMpinEnabled(): Boolean
    fun setMemonic(mpin: Boolean)
    fun getMomic(): Boolean
    fun setPublickKey(key: String)
    fun getPublicKey(): String
    fun setPrivateKey(key: String)
    fun getPrivateKey(): String
    fun setPublicKeyFromLogin(key: String)
    fun getPublicKeyFromLogin(): String
    fun setBTCPublickKey(key: String)
    fun getBTCPublicKey(): String
    fun setBTCPrivateKey(key: String)
    fun getBTCPrivateKey(): String
    fun setReferalCodeFromLogin(key: String)
    fun getReferalCodeFromLogin(): String
    fun setMnemonicCreated(created: Boolean)
    fun getMnemonicCreated(): Boolean
    fun setSuggestionList(loginUserList: ArrayList<String>)
    fun getSuggestionList(): String?

    fun setLoginBiometricEnabled(status: Boolean)
    fun getLoginBiometricEnabled(): Boolean
    fun setLoginPINEnabled(status: Boolean)
    fun getLoginPINEnabled(): Boolean
    fun setLoginPINSetStatus(status: Boolean)
    fun getLoginPINSetStatus(): Boolean

    fun setLoggedIn(status: Boolean)
    fun getLoggedIn(): Boolean

    fun isBiometricCompleted(): Boolean
    fun setBiometricCompleted(status: Boolean)

    fun setPrivacyPolicy(link: String)
    fun getPrivacyPolicy(): String
    fun setTermsAndConditions(link: String)
    fun getTermsAndConditions(): String
}