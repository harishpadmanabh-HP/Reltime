/*
 * Copyright (C) 2021 Global Art Exchange, LLC ("GAE"). All Rights Reserved.
 *  * You may not use, distribute and modify this code without a license;
 *  * To obtain a license write to legal@gax.llc
 *
 */

package com.accubits.reltime.helpers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson


open class PreferenceManager constructor(context: Context) : IPreferenceHelper {
    private val PREFS_NAME = "SharedPreferenceReltime"
    private var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun setFCMToken(fcmToken: String) {
        preferences[FCM_TOKEN] = fcmToken
    }

    override fun getFCMToken(): String {
        return preferences[FCM_TOKEN] ?: ""
    }

    override fun setApiKey(apiKey: String) {
        preferences[API_KEY] = "Bearer $apiKey"
    }

    override fun getApiKey(): String {
        return preferences[API_KEY] ?: ""
    }


    override fun setUserId(userId: Int) {
        preferences[USER_ID] = userId
    }

    override fun getUserId(): Int {
        return preferences[USER_ID] ?: 0
    }

    override fun setKycCreated(created: Boolean) {
        preferences[KYC_CREATED] = created
    }

    override fun isKycCreated(): Boolean {
        return preferences[KYC_CREATED] ?: false
    }

    override fun setKycApproved(approved: Boolean) {
        preferences[KYC_APPROVED] = approved
    }

    override fun isKycApproved(): Boolean {
        return preferences[KYC_APPROVED] ?: false
    }

    override fun clearPrefs(token: String, key: String,restoreLastUser:Boolean) {
        val userId = getUserId()
        val name = getName()
        val refreshToken = getRefreshToken()
        val loginBiometricEnabled = getLoginBiometricEnabled()
        val loginPINEnabled = getLoginPINEnabled()
        val loginPINSetStatus = getLoginPINSetStatus()
        val onBoarding = getOnBoardingCompleted()
        val isLoggedIn = getLoggedIn()
        val phone=getPhone()

        val publicKey =getPublicKey()
        val publicKeyFromLogin =getPublicKeyFromLogin()
        val privateKey =getPrivateKey()
        val mnemonic =getMomic()

        val privateKeyBTC =getBTCPrivateKey()
        val publicKeyBTC =getBTCPublicKey()


        preferences.edit().clear().apply()
        preferences[PRIVATEKEY] = token
        preferences[KEY] = key
       // if (restoreLastUser){
            setOnBoardingCompleted(onBoarding)
            setUserId(userId)
            setName(name)
            setRefreshToken(refreshToken)
            setLoginBiometricEnabled(loginBiometricEnabled)
            setLoginPINEnabled(loginPINEnabled)
            setLoginPINSetStatus(loginPINSetStatus)
            setLoggedIn(isLoggedIn)
            setPhone(phone)

        setPublickKey(publicKey)
        setPublicKeyFromLogin(publicKeyFromLogin)
        setPrivateKey(privateKey)
        setMemonic(mnemonic)

        setBTCPrivateKey(privateKeyBTC)
        setBTCPublickKey(publicKeyBTC)
       // }
    }

    override fun setEmail(apiKey: String) {
        preferences[EMAIL] = apiKey
    }

    override fun setPhone(phone: String) {
        preferences[PHONE] = phone
    }


    override fun getEmail(): String {
        return preferences[EMAIL] ?: ""
    }

    override fun getPhone(): String {
        return preferences[PHONE] ?: ""
    }


    override fun setRefreshToken(id: String) {
        preferences[REFRESHTOKEN] = id

    }

    override fun setFormToken(token: String) {
        preferences[FORMTOKEN] = token
    }

    override fun getFormToken(): String {
        return preferences[FORMTOKEN] ?: ""
    }

    override fun getName(): String {
        return preferences[NAME] ?: ""
    }

    override fun setName(name: String) {
        preferences[NAME] = name
    }

    override fun getLastLogin(): String {
        return preferences[LASTLOGIN] ?: ""
    }

    override fun setLastLogin(name: String) {
        preferences[LASTLOGIN] = name
    }

    override fun setOnBoardingCompleted(completed: String) {
        preferences[ON_BOARDING_COMPLETED] = completed
    }

    override fun getOnBoardingCompleted(): String {
        return preferences[ON_BOARDING_COMPLETED] ?: ""
    }

    override fun setBiometric(bio: Boolean) {
        preferences[BIOMETRIC] = bio
    }

    override fun getBiometric(): Boolean {
        return preferences[BIOMETRIC] ?: false
    }

    override fun setMpin(Mpin: Boolean) {
        preferences[MPIN] = Mpin
    }

    override fun getMpin(): Boolean {
        return preferences[MPIN] ?: false
    }

    override fun setMpinEnabled(mpin: Boolean) {
        preferences[MPINENABLED] = mpin
    }

    override fun getMpinEnabled(): Boolean {
        return preferences[MPINENABLED] ?: false
    }

    override fun setMemonic(mpin: Boolean) {
        preferences[MEMONIC] = mpin
    }

    override fun getMomic(): Boolean {
        return preferences[MEMONIC] ?: false
    }

    override fun setPublickKey(key: String) {
        preferences[KEY] = key
    }

    override fun getPublicKey(): String {
        return preferences[KEY] ?: ""
    }

    override fun setPrivateKey(key: String) {
        preferences[PRIVATEKEY] = key

    }

    override fun getPrivateKey(): String {
        return preferences[PRIVATEKEY] ?: ""
    }

    override fun setPublicKeyFromLogin(key: String) {
        preferences[PUBLICKEYLOGIN] = key
    }

    override fun getPublicKeyFromLogin(): String {
        return preferences[PUBLICKEYLOGIN] ?: ""
    }

    override fun setBTCPublickKey(key: String) {
        preferences[BTC_KEY] = key
    }

    override fun getBTCPublicKey(): String {
        return preferences[BTC_KEY] ?: ""
    }

    override fun setBTCPrivateKey(key: String) {
        preferences[BTC_PRIVATEKEY] = key
    }

    override fun getBTCPrivateKey(): String {
        return preferences[BTC_PRIVATEKEY] ?: ""
    }

    override fun setReferalCodeFromLogin(key: String) {
        preferences[REFERALCODE] = key
    }

    override fun getReferalCodeFromLogin(): String {
        return preferences[REFERALCODE] ?: ""
    }

    override fun setMnemonicCreated(created: Boolean) {
        preferences[MNEMONIC_CREATED] = created
    }

    override fun getMnemonicCreated(): Boolean {
        return preferences[MNEMONIC_CREATED] ?: false
    }

    override fun getRefreshToken(): String {
        return preferences[REFRESHTOKEN] ?: ""
    }

    override fun setSuggestionList(loginUserList: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(loginUserList)
        preferences[SUGGESTION_LIST] = json
    }

    override fun getSuggestionList(): String? {
        return preferences[SUGGESTION_LIST] ?: ""
    }

    override fun setLoginBiometricEnabled(status: Boolean) {
        preferences[LOGIN_BIOMETRIC_STATUS] = status
    }

    override fun getLoginBiometricEnabled(): Boolean {
        return preferences[LOGIN_BIOMETRIC_STATUS] ?: false
    }

    override fun setLoginPINEnabled(status: Boolean) {
        preferences[LOGIN_PIN_STATUS] = status
    }

    override fun getLoginPINEnabled(): Boolean {
        return preferences[LOGIN_PIN_STATUS] ?: false
    }

    override fun setLoginPINSetStatus(status: Boolean) {
        preferences[LOGIN_PIN_SET_STATUS] = status
    }

    override fun getLoginPINSetStatus(): Boolean {
        return preferences[LOGIN_PIN_SET_STATUS] ?: false
    }

    override fun setLoggedIn(status: Boolean) {
        preferences[IS_LOGGED_IN] = status
    }

    override fun getLoggedIn(): Boolean {
        return preferences[IS_LOGGED_IN] ?: false
    }

    override fun isBiometricCompleted(): Boolean {
        return preferences[BIOMETRIC_COMPLETED] ?: false
    }

    override fun setBiometricCompleted(status: Boolean) {
        preferences[BIOMETRIC_COMPLETED] = status
    }

    override fun setPrivacyPolicy(link: String) {
        preferences[PRIVACY_POLICY] = link
    }

    override fun getPrivacyPolicy(): String {
        return preferences[PRIVACY_POLICY] ?: ""
    }

    override fun setTermsAndConditions(link: String) {
        preferences[TERMS_AND_CONDITIONS] = link
    }

    override fun getTermsAndConditions(): String {
        return preferences[TERMS_AND_CONDITIONS] ?: ""
    }

    companion object {
        const val API_KEY = "api_key"
        const val USER_ID = "userId"
        const val EMAIL = "email_user"
        const val PHONE = "phone"
        const val REFRESHTOKEN = "refreshToken"
        const val FORMTOKEN = "formToken"
        const val NAME = "name"
        const val LASTLOGIN = "lastLogin"
        const val ON_BOARDING_COMPLETED = "on_boarding_completed"
        const val BIOMETRIC = "biometric"
        const val KYC_CREATED = "kycCreated"
        const val KYC_APPROVED = "kycApproved"
        const val MNEMONIC_CREATED = "mnemonicCreated"
        const val MPIN = "mpin"
        const val MPINENABLED = "mpinenabled"
        const val MEMONIC = "memonic"
        const val KEY = "key"
        const val BTC_KEY = "btc_key"
        const val PRIVATEKEY = "privatekey"
        const val BTC_PRIVATEKEY = "btc_privatekey"
        const val REFERALCODE = "referalcode"
        const val PUBLICKEYLOGIN = "publicKeyLogin"
        const val FCM_TOKEN = "fcmToken"
        const val SUGGESTION_LIST = "suggestionList"
        const val FORGOT_MPIN = "forgotMpin"


        const val LOGIN_BIOMETRIC_STATUS = "login_biometric_status"
        const val LOGIN_PIN_STATUS = "login_pin_status"
        const val LOGIN_PIN_SET_STATUS = "login_pin_set_status"
        //   const val LOGIN_USER_NAME = "login_user_name"
        const val IS_LOGGED_IN = "is_logged_in"
        const val BIOMETRIC_COMPLETED = "biometric_completed"
        const val PRIVACY_POLICY = "privacy_policy"
        const val TERMS_AND_CONDITIONS = "terms_and_conditions"

    }
}

/**
 * SharedPreferences extension function, to listen the edit() and apply() fun calls
 * on every SharedPreferences operation.
 */
private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
private operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
private inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}