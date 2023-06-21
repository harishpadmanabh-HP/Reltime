package com.accubits.reltime.views.rto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.databinding.ActivityWyreWebViewBinding

const val TAG = "TAG"

class WyreWebViewActivity : AppCompatActivity() {
    private lateinit var binder: ActivityWyreWebViewBinding

    companion object {
        const val EXTRA_URL = "url"
        const val REDIRECTION_URL = "https://reltime.com/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityWyreWebViewBinding.inflate(layoutInflater)
        setContentView(binder.root)

        init()
        setListener()
    }

    private fun init() {
        binder.webView.settings.javaScriptEnabled = true
        binder.webView.settings.domStorageEnabled = true
        binder.webView.settings.allowContentAccess = true
        binder.webView.settings.allowFileAccess = true
        binder.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binder.progressBar.visibility= View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binder.progressBar.visibility= View.GONE
                if (url?.startsWith(REDIRECTION_URL) == true) {
                    val returnIntent = Intent()
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            }
        }

        intent.getStringExtra(EXTRA_URL)?.let { binder.webView.loadUrl(it) }
    }

    private fun setListener() {
        binder.ivBack.setOnClickListener { onBackPressed() }
    }

}