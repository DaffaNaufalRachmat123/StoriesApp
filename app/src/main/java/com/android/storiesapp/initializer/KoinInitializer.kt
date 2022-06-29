package com.android.storiesapp.initializer

import android.content.Context
import androidx.startup.Initializer
import com.github.ajalt.timberkt.Timber
import com.storiesapp.core.BuildConfig
import com.android.storiesapp.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

fun Context.startKoinIfNeeded(): Koin {
    return startKoin {
        logger(KoinInitializer.KoinLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE))
        androidContext(applicationContext)
        modules(appComponent)
    }.koin
}

class KoinInitializer : Initializer<Koin> {

    override fun create(context: Context): Koin {
        return context
            .startKoinIfNeeded()
            .also {
                Timber.tag("KoinInitializer").d("Koin initialized")
            }
    }

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(TimberInitializer::class.java)

    internal class KoinLogger(
        logLevel: Level
    ) : Logger(logLevel) {
        override fun log(level: Level, msg: MESSAGE) {
            when (level) {
                Level.DEBUG -> Timber.tag(KOIN_TAG).d(msg)
                Level.INFO -> Timber.tag(KOIN_TAG).i(msg)
                Level.ERROR -> Timber.tag(KOIN_TAG).e(msg)
                Level.NONE -> Timber.tag(KOIN_TAG).v(msg)
            }
        }
    }

}