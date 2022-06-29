package com.storiesapp.core.model.story

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @Expose
    @SerializedName("error")
    var error : Boolean ,
    @Expose
    @SerializedName("message")
    var message : String,
    @Expose
    @SerializedName("listStory")
    var listStory : MutableList<Story>
)