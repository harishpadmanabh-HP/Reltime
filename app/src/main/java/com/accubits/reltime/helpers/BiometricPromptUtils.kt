package com.accubits.reltime.helpers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.accubits.reltime.R

class BiometricPromptUtils(activity: Context) {
    private var context = activity
    private var onBioSuccessListener: ((String) -> Unit)? = null
    private var onBioSuccessL: ((String) -> Unit)? = null

    fun createBiometricPrompt(activity: AppCompatActivity): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                if (errString != activity.getString(R.string.cancel)) {
                    Toast.makeText(activity, "$errString", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                //  listener.onAuthenticationSuccess()
                //  onBioSuccessListener.let { "Success" }
                onBioSuccessListener?.let { it1 -> it1("success") }
                onBioSuccessL?.let { it1 -> it1("success") }

            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onBioSuccessListener = listener
    }

    fun setOnBioSuccess(listener: (String) -> Unit) {
        onBioSuccessL = listener
    }

    fun createPromptInfo(activity: Context): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.biometric_prompt_info_title))
            setSubtitle(activity.getString(R.string.biometric_prompt_info_subtitle))
            setNegativeButtonText(activity.getString(R.string.cancel))
        }.build()

}




