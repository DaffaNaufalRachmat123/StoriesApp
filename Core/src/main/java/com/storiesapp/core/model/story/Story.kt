package com.storiesapp.core.model.story

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "stories")
@Parcelize
data class Story(
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id : String,
    @Expose
    @SerializedName("name")
    var name : String ,
    @Expose
    @SerializedName("description")
    var description : String,
    @Expose
    @SerializedName("photoUrl")
    var photoUrl : String,
    @Expose
    @SerializedName("createdAt")
    var createdAt : String,
    @Expose
    @SerializedName("lat")
    var lat : Double,
    @Expose
    @SerializedName("lon")
    var lon : Double
) : Parcelable