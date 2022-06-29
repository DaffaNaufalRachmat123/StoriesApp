package com.storiesapp.features.auth.api

import com.storiesapp.core.model.auth.LoginResponse
import com.storiesapp.core.model.auth.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("name") name : String ,
        @Field("email") email : String ,
        @Field("password") password : String
    ) : RegisterResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email : String,
        @Field("password") password : String
    ) : LoginResponse
}