package com.railsreactor.photobutler.utils.common

interface Storage {
    fun isLockTaskMode(): Boolean

    fun setLockTaskMode(yes: Boolean)
}