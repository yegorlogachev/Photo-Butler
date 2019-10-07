package com.railsreactor.photobutler.utils

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.railsreactor.photobutler.R
import com.railsreactor.photobutler.utils.common.Shareds
import com.railsreactor.photobutler.utils.common.utils.ModeUtIls

class MainActivity : AppCompatActivity() {

    private lateinit var shareds: Shareds
    private lateinit var modeUtIls: ModeUtIls

    companion object {
        const val SOME_URL = "https://www.google.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shareds = Shareds.getInstance(applicationContext)
        modeUtIls = ModeUtIls.getInstance(applicationContext)
        modeUtIls.setupLockMode(this)
        bindDoubleTap()
        if (shareds.isLockTaskMode())
            modeUtIls.startLockMode(this)
        else
            startAlertDialog()
    }

    private fun bindDoubleTap() {
        var doubleBackToExit = false
        findViewById<TextView>(R.id.backBtn).setOnClickListener {
            if (doubleBackToExit) {
                modeUtIls.stopLockMode(this)
                shareds.setLockTaskMode(false)
            }
            doubleBackToExit = true
            Handler().postDelayed({
                doubleBackToExit = false
            }, 2000)
        }
    }

    private fun startAlertDialog() =
        AlertDialog
            .Builder(this)
            .setTitle(R.string.start_dialog_title)
            .setMessage(
                String.format(this.resources.getString(R.string.strt_dlg_desc), SOME_URL, getUdId())
            )
            .setPositiveButton(R.string.start_dialog_btn_yes) { d, _ ->
                d.dismiss()
                showBody(findViewById(R.id.webView))
                modeUtIls.startLockMode(this)
                shareds.setLockTaskMode(true)
            }.create()
            .show()

    private fun getUdId() =
        Settings.System.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

    @SuppressLint("SetJavaScriptEnabled")
    private fun showBody(wv: WebView) {
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = WebViewClient()
        wv.loadUrl(SOME_URL)
    }


}
