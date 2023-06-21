package com.accubits.reltime.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.accubits.reltime.helpers.PreferenceManager


class AppLifecycleObserver(val preferenceHelper: PreferenceManager) :
    Application.ActivityLifecycleCallbacks {

    private var numStarted = 0


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        if (numStarted == 0) {
            // app went to foreground
            /** Omitting OTP activities from auto populating biometric login*/
            /*    if (p0 !is OtpVerifyActivity && p0 !is LoginActivity && p0 !is SettingsOTPActivity && p0 !is SignUpOtpActivity
                    && p0 !is LoginOTPVerifyActivity && p0 !is KYCDetailsActivity
                    && p0 !is SplashActivity
                    && p0 !is PermissionActivity
                    && p0 !is WelcomeScreen
                    && p0 !is SignUpPhoneNumberActivity
                    && p0 !is OTPVerificationActivity
                    && p0 !is SetUpProfileActivity
                    && p0 !is SetUpProfileConfirmActivity
                    && p0 !is BiometricLoginV2Activity
                    && p0 !is ConfirmRecoveryKeyActivity
                    && p0 !is MemonicActivity
                    && p0 !is ImportMemonicActivity
                    && p0 !is ContactPickerActivity
                ) {
                    if (preferenceHelper.getUserId() != 0 && preferenceHelper.getRefreshToken()
                            .isNotEmpty() && preferenceHelper.getName()
                            .isNotEmpty() && preferenceHelper.getLoggedIn() && preferenceHelper.isBiometricCompleted()
                    ) {
                        val intent = Intent(p0, WelcomeScreen::class.java)
                        p0.startActivity(intent)
                        p0.finishAffinity()
                    }
                }*/
        }
        numStarted++
    }

    override fun onActivityResumed(p0: Activity) {
        android.util.Log.e("CurrentActivity", p0.localClassName)
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        numStarted--
        if (numStarted == 0) {
            // app went to background
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}