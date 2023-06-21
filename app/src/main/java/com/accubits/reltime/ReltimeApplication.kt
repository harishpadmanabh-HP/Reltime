package com.accubits.reltime

import android.app.Application
import android.util.Log
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.retrofit.RetrofitClient
import com.accubits.reltime.utils.AppLifecycleObserver
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ReltimeApplication : Application() {
    @Inject
    lateinit var preferenceHelper: PreferenceManager

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        //FirebaseApp.initializeApp(this)
         val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(this, fontRequest)
        EmojiCompat.init(config)
        registerActivityLifecycleCallbacks(AppLifecycleObserver(preferenceHelper))
    }
}