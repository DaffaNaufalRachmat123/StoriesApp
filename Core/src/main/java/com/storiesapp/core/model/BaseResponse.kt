package com.storiesapp.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @Expose
    @SerializedName("error")
    var error : Boolean ,
    @Expose
    @SerializedName("message")
    var message : String
)