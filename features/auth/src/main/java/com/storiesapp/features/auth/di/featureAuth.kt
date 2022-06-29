package com.storiesapp.features.auth.di

import com.storiesapp.core.di.provideApiService
import com.storiesapp.features.auth.AuthViewModel
import com.storiesapp.features.auth.api.AuthApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureAuth = module {
    single { provideApiService<AuthApi>(get()) }
    viewModel { AuthViewModel(get() , get()) }
}