package com.android.storiesapp.di

import com.storiesapp.common.di.commonModule
import com.storiesapp.core.di.coreModule
import com.storiesapp.features.auth.di.featureAuth
import com.storiesapp.features.main.di.featureMain

val appComponent = listOf(
    coreModule,
    commonModule,
    featureAuth,
    featureMain
)