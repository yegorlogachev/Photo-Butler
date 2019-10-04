package com.railsreactor.photobutler.utils

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.railsreactor.photobutler.R

class MainActivity : AppCompatActivity() {

    private val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    private lateinit var mAdminComponentName: ComponentName
    private lateinit var mDevicePolicyManager: DevicePolicyManager
    private var isNeedRemoveHomeLaunch = false

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
        blockAllUiControllers()
        bindDoubleTap()
    }

    private fun bindDoubleTap() {
        var doubleBackToExit = false
        findViewById<TextView>(R.id.backBtn).setOnClickListener {
            if (doubleBackToExit) {
                stopLockTask()
                Toast.makeText(this, "stopLockTask", Toast.LENGTH_SHORT).show()
                isNeedRemoveHomeLaunch = true
            }
            doubleBackToExit = true
            Handler().postDelayed({ doubleBackToExit = false }, 2000)
        }
    }

    private fun blockAllUiControllers() {
        mDevicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminComponentName = UserDeviceAdminReceiver.getComponentName(applicationContext)
        window.decorView.systemUiVisibility = flags
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// block screen on battery charge

        Log.d(
            "mLog",
            "CompNam =" + UserDeviceAdminReceiver.getComponentName(applicationContext)
        ) // add to: adb shell
        //for this application: adb shell dpm set-device-owner com.photobutler.kiosk/com.railsreactor.photobutler.utils.UserDeviceAdminReceiver
        //for this application: adb shell dpm remove-active-admin  com.photobutler.kiosk/com.railsreactor.photobutler.utils.UserDeviceAdminReceiver
        mDevicePolicyManager
            .setKeyguardDisabled(mAdminComponentName, true) // start app without block screen

        val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        mDevicePolicyManager.addPersistentPreferredActivity(
            mAdminComponentName,
            intentFilter,
            ComponentName(packageName, MainActivity::class.java.name)
        ) // launch from start app

        mDevicePolicyManager.setGlobalSetting(
            mAdminComponentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            (BatteryManager.BATTERY_PLUGGED_AC
                    or BatteryManager.BATTERY_PLUGGED_USB
                    or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
        )// do not go out on battery charge

        mDevicePolicyManager.setLockTaskPackages(
            mAdminComponentName,
            arrayOf(packageName)
        ) // add on block mode

        if (mDevicePolicyManager.isDeviceOwnerApp(packageName)) Log.d("mLog", "admin")
        else Log.d("mLog", "NoT admin")
    }

    private fun startAlertDialog() = android.app.AlertDialog.Builder(this)
        .setTitle(R.string.start_dialog_title)
        .setMessage(
            String.format(this.resources.getString(R.string.strt_dlg_desc), CURRENT_URL, getUdId())
        )
        .setPositiveButton(R.string.start_dialog_btn_yes) { d, _ ->
            d.dismiss()
            showBody(findViewById(R.id.webView))
            Toast.makeText(this, "startLockTask", Toast.LENGTH_SHORT).show()
            isNeedRemoveHomeLaunch = false
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

    override fun onBackPressed() {
        super.onBackPressed()
        if(isNeedRemoveHomeLaunch)
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, packageName)
    }

}
