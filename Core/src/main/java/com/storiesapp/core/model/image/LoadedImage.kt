package com.storiesapp.core.model.image

import android.database.sqlite.SQLiteDatabase
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoadedImage(
    var bitmap : String?,
    var imageId : String?,
    var isRotated : Boolean = false,
    var rotate : Int = 0,
    var isSelected : Boolean = false,
    var type : Int ,
    var photoCaption : String ,
) : Parcelable