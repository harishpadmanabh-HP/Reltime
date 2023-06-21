package com.accubits.reltime.views.privacy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.databinding.ActivityPrivacyBinding
import com.accubits.reltime.helpers.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyActivity : AppCompatActivity() {
    private lateinit var privacyBinding: ActivityPrivacyBinding

    companion object {
        const val URL = "url"
        const val TERMS_CONDITIONS ="terms_and_conditions"//"https://reltime.com/terms"// "https://reltime.com/termsofuse"
        const val PRIVACY_POLICY = "privacy_policy"//"https://reltime.com/privacy"//"https://reltime.com/gdpr"
    }
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        privacyBinding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(privacyBinding.root)
        privacyBinding.webview.settings.javaScriptEnabled = true
        privacyBinding.webview.apply {
            this.settings.builtInZoomControls = false
            this.settings.setSupportZoom(false)
            this.settings.javaScriptCanOpenWindowsAutomatically = true
            this.settings.allowFileAccess = true
            this.settings.domStorageEnabled = true
        }
        val type = intent.getStringExtra(URL) ?: TERMS_CONDITIONS
        val url=if (type == TERMS_CONDITIONS) preferenceManager.getTermsAndConditions() else preferenceManager.getPrivacyPolicy()
        privacyBinding.webview.loadUrl(url)
        privacyBinding.textView11.text =
            resources.getString(if (type == TERMS_CONDITIONS) R.string.terms_and_conditions else R.string.privacy_policy)

        privacyBinding.ivBack.setOnClickListener { finish() }

    }
}