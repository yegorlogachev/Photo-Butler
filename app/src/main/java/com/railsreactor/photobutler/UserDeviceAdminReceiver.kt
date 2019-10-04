package com.railsreactor.photobutler

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context

class UserDeviceAdminReceiver : DeviceAdminReceiver() {
    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, UserDeviceAdminReceiver::class.java)
        }
    }
}