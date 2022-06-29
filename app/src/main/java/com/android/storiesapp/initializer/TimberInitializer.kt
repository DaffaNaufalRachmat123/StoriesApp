package com.android.storiesapp.initializer

import android.content.Context
import androidx.startup.Initializer
import com.android.storiesapp.log.DebugTree
import com.github.ajalt.timberkt.Timber
import com.storiesapp.core.BuildConfig

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            //Timber.plant(FirebaseCrashlyticsTree())
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}