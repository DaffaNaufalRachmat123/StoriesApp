package com.storiesapp.features.main.api

import androidx.paging.PagingData
import com.storiesapp.core.model.BaseResponse
import com.storiesapp.core.model.story.Story
import com.storiesapp.core.model.story.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MainApi {
    @Multipart
    @POST("stories")
    suspend fun createStoryWithoutCoordinate(
        @Part multipartFiles : MultipartBody.Part,
        @Part("description") description : RequestBody
    ) : BaseResponse

    @Multipart
    @POST("stories")
    suspend fun createStoryWithCoordinate(
        @Part multipartFiles : MultipartBody.Part,
        @Part("description") description : RequestBody ,
        @Part("lat") lat : RequestBody ,
        @Part("lon") lon : RequestBody
    ) : BaseResponse

    @GET("stories")
    suspend fun getStoryList(
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("location") location : Int
    ) : StoryResponse

    fun getStoryList() : Flow<PagingData<Story>>
}