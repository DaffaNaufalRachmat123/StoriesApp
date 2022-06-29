package com.storiesapp.common.di

import com.storiesapp.common.locationhelper.GpsTracker
import com.storiesapp.common.locationhelper.LocationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    single { GpsTracker(androidContext()) }
    single { LocationHelper(androidContext()) }
}