package com.railsreactor.photobutler

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

    companion object {
        const val CURRENT_URL = "http://www.google.com"
        const val TYPE = "text/html; charset=utf-8"
        const val ENCODING = "UTF-8"
        const val DEFAULT_URL_SCHEMA_HTTP = "http"
        const val DEFAULT_URL_SCHEMA_HTTPS = "https"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startAlertDialog()
    }

    private fun startAlertDialog() = android.app.AlertDialog.Builder(this)
        .setTitle(R.string.start_dialog_title)
        .setMessage(
            String.format(
                this.resources.getString(R.string.start_dialog_descr),
                CURRENT_URL,
                getUdId()
            )
        )
        .setPositiveButton(R.string.start_dialog_btn_yes) { d, _ ->
            d.dismiss()
            showBody(findViewById(R.id.webView))
            startLockTask()
        }.create()
        .show()

    private fun getUdId() =
        Settings.System.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

    @SuppressLint("SetJavaScriptEnabled")
    private fun showBody(wv: WebView) {
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = WebViewClient()
        wv.loadUrl(CURRENT_URL)
    }
}
