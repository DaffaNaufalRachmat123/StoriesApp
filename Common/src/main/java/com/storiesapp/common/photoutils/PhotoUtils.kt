package com.storiesapp.common.photoutils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.storiesapp.common.extension.toArrayList
import com.storiesapp.common.photoutils.data.MediaFolderVO
import com.storiesapp.common.photoutils.data.MediaVO
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.COLUMN_BUCKET_DISPLAY_NAME
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.COLUMN_BUCKET_ID
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.COLUMN_COUNT
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.COLUMN_DATA
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.COLUMN_URI
import com.storiesapp.common.photoutils.loader.MediaFolderLoader.load
import com.storiesapp.common.photoutils.loader.MediaLoader.load
import java.util.*


/**
 * Created on : 8/26/20.
 * Author     : Musthofa Ali Ubaed
 * Email      : panic.inc.dev@gmail.com
 */
object PhotoUtils {

    /**
     * Get all picture files
     * @param context
     * @return
     */
    fun getAllMediaPhotos(context: Context): List<MediaVO> {
        return getMediaByFolder(context, true, null)
    }

    /**
     * Get all videos
     * @param context
     * @return
     */
    fun getAllMediaVideos(context: Context): List<MediaVO> {
        return getMediaByFolder(context, true, null)
    }

    /**
     * get the most recent photos
     * @param num
     * @return
     */
    @JvmStatic
    fun getTheLastPhotos(context: Context, num: Int): ArrayList<MediaVO> {
        val list = ArrayList<MediaVO>()
        val photos = getMediaByFolder(context, true, null)
        list.addAll(photos)
        return list.take(num).toArrayList()
    }

    /**
     * Get all pictures under the corresponding path
     * @param context
     * @param isAll
     * @param folderId
     * @return
     */
    @JvmStatic
    fun getPhotosByFolder(context: Context, isAll: Boolean, folderId: String?): ArrayList<MediaVO> {
        return getMediaByFolder(context, isAll, folderId)
    }

    /**
     * Get all resources under the corresponding path
     * @param context
     * @param isAll
     * @param folderId
     * @param mediaType
     * @return
     */
    fun getMediaByFolder(context: Context, isAll: Boolean, folderId: String?): ArrayList<MediaVO> {
        val list = ArrayList<MediaVO>()
        val cursor = load(context, isAll, folderId)
        while (cursor.moveToNext()) {
            val mediaVO = MediaVO()
            val id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            mediaVO.id = id
            val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
            mediaVO.mimeType = mimeType
            mediaVO.size = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE).toLong()
            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mediaVO.uri = ContentUris.withAppendedId(contentUri, id.toLong())
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
            mediaVO.url = data
            list.add(mediaVO)
        }
        return list
    }

    /**
     * Get photo folder
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllPhotosFolder(context: Context): MutableList<MediaFolderVO> {
        return getAllMediaFolder(context)
    }

    /**
     * Get resource folder
     * @param context
     * @return
     */
    fun getAllMediaFolder(context: Context): MutableList<MediaFolderVO> {
        val list: MutableList<MediaFolderVO> = ArrayList()
        val cursor = load(context)
        while (cursor.moveToNext()) {
            val column = cursor.getString(cursor.getColumnIndex(COLUMN_URI))
            val folderVO = MediaFolderVO()
            folderVO.id = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_ID))
            folderVO.name = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME))
            folderVO.count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT))
            folderVO.coverUri = Uri.parse(column ?: "")
            folderVO.path = cursor.getString(cursor.getColumnIndex(COLUMN_DATA))
            list.add(folderVO)
        }
        cursor.close()
        return list
    }
}