package com.storiesapp.features.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.storiesapp.common.base.BaseViewModel
import com.storiesapp.common.extension.errorMessage
import com.storiesapp.common.photoutils.PhotoUtils
import com.storiesapp.common.view.ViewState
import com.storiesapp.common.view.setError
import com.storiesapp.common.view.setLoading
import com.storiesapp.common.view.setSuccess
import com.storiesapp.core.AppDispatchers
import com.storiesapp.core.model.BaseResponse
import com.storiesapp.core.model.image.Directory
import com.storiesapp.core.model.image.LoadedImage
import com.storiesapp.core.model.story.Story
import com.storiesapp.core.model.story.StoryResponse
import com.storiesapp.features.main.PagingSource.StoryPagingSource
import com.storiesapp.features.main.api.MainApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.flow.Flow

class MainViewModel ( val api : MainApi , val dispatchers: AppDispatchers) : BaseViewModel() {
    val folderListData = MutableLiveData<ViewState<List<Directory>>>().apply {
        value = ViewState.Loading
    }
    val mediaListData = MutableLiveData<ViewState<List<LoadedImage>>>().apply {
        value = ViewState.Loading
    }
    val createStoryResponse = MutableLiveData<ViewState<BaseResponse>>()
    val storyResponse = MutableLiveData<ViewState<StoryResponse>>()

    fun getStoryListMap(page : Int , pageSize : Int , location : Int = 1 , isTest : Boolean = false){
        if(isTest){
            runBlocking {
                runCatching {
                    storyResponse.setLoading()
                    api.getStoryList(page , pageSize , location)
                }.onSuccess {
                    storyResponse.setSuccess(it)
                }.onFailure {
                    storyResponse.setError(it.errorMessage())
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    storyResponse.setLoading()
                    api.getStoryList(page , pageSize , location)
                }.onSuccess {
                    storyResponse.setSuccess(it)
                }.onFailure {
                    storyResponse.setError(it.errorMessage())
                }
            }
        }
    }

    fun fetchStoryList() : Flow<PagingData<Story>> {
        return Pager(
            PagingConfig(pageSize = 3 , enablePlaceholders = true)
        ){
            StoryPagingSource(api)
        }.flow
    }

    fun fetchStory() : Flow<PagingData<Story>> {
        return fetchStoryList().cachedIn(viewModelScope)
    }

    fun getFolderList(context : Context , isTest : Boolean = false){
        if(isTest){
            runBlocking {
                runCatching {
                    val lastPhotoDef = async(Dispatchers.IO){
                        PhotoUtils.getAllPhotosFolder(context).map {
                            Directory(it.id.orEmpty() , it.name.orEmpty().trim { it1 -> it1 <= ' '} , it.path.toString() , it.count)
                        }
                    }
                    lastPhotoDef.await()
                }.onSuccess {
                    folderListData.setSuccess(it)
                }.onFailure {
                    folderListData.setError(it.message)
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    val lastPhotoDef = async(Dispatchers.IO){
                        PhotoUtils.getAllPhotosFolder(context).map {
                            Directory(it.id.orEmpty() , it.name.orEmpty().trim { it1 -> it1 <= ' '} , it.path.toString() , it.count)
                        }
                    }
                    lastPhotoDef.await()
                }.onSuccess {
                    folderListData.setSuccess(it)
                }.onFailure {
                    folderListData.setError(it.message)
                }
            }
        }
    }
    fun getPhotosByFolder(context : Context , bucketId : String , page : Int , isTest: Boolean = false) = viewModelScope.launch {
        val isAll = bucketId.toInt() == -1
        if(page == 0){
            mediaListData.setLoading()
        }
        if(isTest){
            runBlocking {
                runCatching {
                    val photos = async(Dispatchers.IO){
                        PhotoUtils.getPhotosByFolder(context , isAll , bucketId).map {
                            val loadedImage = LoadedImage(it.url , it.id , false , -1,false , 0 , "")
                            loadedImage.type = 1
                            return@map loadedImage
                        }
                    }
                    photos.await()
                }.onSuccess {
                    mediaListData.setSuccess(it)
                }.onFailure {
                    mediaListData.setError(it.message)
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    val photos = async(Dispatchers.IO){
                        PhotoUtils.getPhotosByFolder(context , isAll , bucketId).map {
                            val loadedImage = LoadedImage(it.url , it.id , false , -1,false , 0 , "")
                            loadedImage.type = 1
                            return@map loadedImage
                        }
                    }
                    photos.await()
                }.onSuccess {
                    mediaListData.setSuccess(it)
                }.onFailure {
                    mediaListData.setError(it.message)
                }
            }
        }
    }
    fun createStory(photo : MultipartBody.Part , description : RequestBody , lat : RequestBody , lon : RequestBody , isWithLocation : Boolean = false , isTest : Boolean = false){
        if(isTest){
            runBlocking {
                runCatching {
                    createStoryResponse.setLoading()
                    if(isWithLocation){
                        api.createStoryWithCoordinate(photo , description , lat , lon)
                    } else {
                        api.createStoryWithoutCoordinate(photo , description)
                    }
                }.onSuccess {
                    createStoryResponse.setSuccess(it)
                }.onFailure {
                    createStoryResponse.setError(it.errorMessage())
                }
            }
        } else {
            viewModelScope.launch(dispatchers.main){
                runCatching {
                    createStoryResponse.setLoading()
                    if(isWithLocation){
                        api.createStoryWithCoordinate(photo , description , lat , lon)
                    } else {
                        api.createStoryWithoutCoordinate(photo , description)
                    }
                }.onSuccess {
                    createStoryResponse.setSuccess(it)
                }.onFailure {
                    createStoryResponse.setError(it.errorMessage())
                }
            }
        }
    }
}