package com.storiesapp.features.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.storiesapp.common.base.BaseViewModel
import com.storiesapp.common.extension.errorMessage
import com.storiesapp.common.view.ViewState
import com.storiesapp.common.view.setError
import com.storiesapp.common.view.setLoading
import com.storiesapp.common.view.setSuccess
import com.storiesapp.core.AppDispatchers
import com.storiesapp.core.model.auth.LoginResponse
import com.storiesapp.core.model.auth.RegisterResponse
import com.storiesapp.features.auth.api.AuthApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthViewModel ( val api : AuthApi , val dispatchers: AppDispatchers) : BaseViewModel() {
    val registerResponse = MutableLiveData<ViewState<RegisterResponse>>()
    val loginResponse = MutableLiveData<ViewState<LoginResponse>>()

    fun registerUser(name : String , email : String , password : String , isTest : Boolean = false){
        if(isTest){
            runBlocking {
                runCatching {
                    registerResponse.setLoading()
                    api.registerUser(name , email , password)
                }.onSuccess {
                    registerResponse.setSuccess(it)
                }.onFailure {
                    registerResponse.setError(it.errorMessage())
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    registerResponse.setLoading()
                    api.registerUser(name , email , password)
                }.onSuccess {
                    registerResponse.setSuccess(it)
                }.onFailure {
                    registerResponse.setError(it.errorMessage())
                }
            }
        }
    }

    fun loginUser(email : String , password : String , isTest : Boolean = false){
        if(isTest){
            runBlocking {
                runCatching {
                    loginResponse.setLoading()
                    api.loginUser(email , password)
                }.onSuccess {
                    loginResponse.setSuccess(it)
                }.onFailure {
                    loginResponse.setError(it.errorMessage())
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    loginResponse.setLoading()
                    api.loginUser(email , password)
                }.onSuccess {
                    loginResponse.setSuccess(it)
                }.onFailure {
                    loginResponse.setError(it.errorMessage())
                }
            }
        }
    }
}