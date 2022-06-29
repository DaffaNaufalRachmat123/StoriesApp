package com.storiesapp.features.main.di

import com.storiesapp.core.di.provideApiService
import com.storiesapp.features.main.MainViewModel
import com.storiesapp.features.main.api.MainApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureMain = module {
    single { provideApiService<MainApi>(get()) }
    viewModel { MainViewModel(get() , get()) }
}