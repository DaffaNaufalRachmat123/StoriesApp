package com.android.storiesapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import com.storiesapp.common.base.BaseApp
import java.lang.reflect.Method

open class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        showDebugDBAddressLogToast(this)
    }
    open fun showDebugDBAddressLogToast(context: Context?) {
        if (BuildConfig.DEBUG) {
            try {
                val debugDB = Class.forName("com.amitshekhar.DebugDB")
                val getAddressLog: Method = debugDB.getMethod("getAddressLog")
                val value: Any? = getAddressLog.invoke(null)
                i { "Debug_Database $value" }
            } catch (ignore: Exception) {
                e { "${ignore.message}" }
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}