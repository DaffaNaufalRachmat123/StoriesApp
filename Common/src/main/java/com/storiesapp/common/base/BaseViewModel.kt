package com.storiesapp.common.base

import androidx.lifecycle.*
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.w
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

abstract class BaseViewModel : ViewModel(), KoinComponent {
    var errorMessage = SingleLiveEvent<String>()
    override fun onCleared() {
        super.onCleared()
        i { "ViewModel Cleared" }
    }
    inline fun <T> launchPagingAsync(
        crossinline execute: suspend () -> Flow<T>,
        crossinline onSuccess: (Flow<T>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = execute()
                onSuccess(result)
            } catch (ex: Exception) {
                errorMessage.value = ex.message
            }
        }
    }
}