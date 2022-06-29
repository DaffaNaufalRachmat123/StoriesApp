package com.storiesapp.core.model.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @Expose
    @SerializedName("error")
    var error : Boolean ,
    @Expose
    @SerializedName("message")
    var message : String,
    @Expose
    @SerializedName("loginResult")
    var loginResult : LoginResult
)

data class LoginResult(
    @Expose
    @SerializedName("userId")
    var userId : String ,
    @Expose
    @SerializedName("name")
    var name : String ,
    @Expose
    @SerializedName("token")
    var token : String
)