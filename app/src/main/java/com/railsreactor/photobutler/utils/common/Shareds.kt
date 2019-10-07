package com.railsreactor.photobutler.utils.common

import android.content.Context
import android.content.SharedPreferences

class Shareds private constructor(context: Context) : Storage {
    private lateinit var preferences: SharedPreferences


    override fun isLockTaskMode() = preferences.getBoolean(LOCK_TASK_MODE, false)

    override fun setLockTaskMode(yes: Boolean) {
        preferences.edit().putBoolean(LOCK_TASK_MODE, yes).apply()
    }

    companion object {
        const val STORAGE_NAME = "com.railsreactor.photobutler.utils.common.PREFERENCE_STORAGE"
        const val LOCK_TASK_MODE = "LOCK_TASK_MODE"

        @Volatile
        private var INSTANCE: Shareds? = null

        fun getInstance(context: Context): Shareds =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: init(context).also { INSTANCE = it }
            }
        private fun init(context: Context) = Shareds(context).apply {
            getPrefs(context)
        }
    }
    private fun getPrefs(context: Context) {
        preferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
    }
}