package com.storiesapp.core.model.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @Expose
    @SerializedName("error")
    var error : Boolean ,
    @Expose
    @SerializedName("message")
    var message : String
)