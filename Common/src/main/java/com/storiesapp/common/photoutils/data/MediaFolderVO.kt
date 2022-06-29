package com.storiesapp.common.photoutils.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep


/**
 * Created on : 8/26/20.
 * Author     : Musthofa Ali Ubaed
 * Email      : panic.inc.dev@gmail.com
 */
@Keep
data class MediaFolderVO(var id: String? = "",
                         var name: String? = "",
                         var count: Int = 0,
                         var coverUri: Uri? = null,
                         var path: String? = "") : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(path)
        dest.writeString(name)
        dest.writeInt(count)
        dest.writeString(id)
        dest.writeParcelable(coverUri, flags)
    }

    companion object CREATOR : Parcelable.Creator<MediaFolderVO> {
        override fun createFromParcel(parcel: Parcel): MediaFolderVO {
            return MediaFolderVO(parcel)
        }

        override fun newArray(size: Int): Array<MediaFolderVO?> {
            return arrayOfNulls(size)
        }
    }

}