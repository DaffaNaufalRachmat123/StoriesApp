package com.storiesapp.common.base

import android.app.Application
import android.content.Context
import com.storiesapp.common.BuildConfig
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.w
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

abstract class BaseApp : Application() {

    fun context(): Context = applicationContext

    companion object {
        lateinit var instance: BaseApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        addRxJavaExceptionHandler()
    }

    private fun addRxJavaExceptionHandler() {
        RxJavaPlugins.setErrorHandler { e ->

            if (BuildConfig.DEBUG) {
                e { "handle error ${e.message}" }
                e.printStackTrace()
            }

            var error = e
            if (error is UndeliverableException) {
                error = e.cause
            }
            if (error is IOException || error is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (error is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (error is NullPointerException || error is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
            if (error is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
            w { "Undeliverable exception received, not sure what to do ${error.message}" }
        }
    }
}