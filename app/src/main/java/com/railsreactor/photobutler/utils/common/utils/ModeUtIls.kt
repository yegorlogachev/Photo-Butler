package com.railsreactor.photobutler.utils.common.utils

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.railsreactor.photobutler.utils.MainActivity
import com.railsreactor.photobutler.utils.UserDeviceAdminReceiver

class ModeUtIls private constructor(val appContext: Context) {

    companion object {
        private var instance: ModeUtIls? = null
        private lateinit var mAdminComponentName: ComponentName
        private lateinit var mDevicePolicyManager: DevicePolicyManager

        fun getInstance(appContext: Context) = ModeUtIls(appContext).apply {

        }

        val LockTaskFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    fun setupLockMode(activity: Activity) {
        mDevicePolicyManager =
            activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (mDevicePolicyManager.isDeviceOwnerApp(appContext.packageName)) {
            mAdminComponentName = UserDeviceAdminReceiver.getComponentName(appContext)
            activity.window.decorView.systemUiVisibility = LockTaskFlags
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// block screen on battery charge
            Log.d("mLog", "${UserDeviceAdminReceiver.getComponentName(appContext)}")
            // add to: adb shell
            //for this application: adb shell dpm set-device-owner com.photobutler.kiosk/com.railsreactor.photobutler.utils.UserDeviceAdminReceiver
            //for this application: adb shell dpm remove-active-admin  com.photobutler.kiosk/com.railsreactor.photobutler.utils.UserDeviceAdminReceiver
            mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, true)
            // start app without block screen

            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            } // make as launcher
            mDevicePolicyManager.addPersistentPreferredActivity(
                mAdminComponentName,
                intentFilter,
                ComponentName(activity.packageName, MainActivity::class.java.name)
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
                arrayOf(activity.packageName)
            ) // add on block mode
        }
    }

    fun startLockMode(activity: Activity) {
        activity.startLockTask()
    }

    fun stopLockMode(activity: Activity) {
        activity.stopLockTask()
        mDevicePolicyManager.clearPackagePersistentPreferredActivities(
            mAdminComponentName, activity.packageName
        )
    }
}
