package com.storiesapp.common.photoutils.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal

object MediaLoader {
    private const val COLUMN_DATA = "_data"
    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.SIZE,
        COLUMN_DATA
    )
    private const val SELECTION_ALL = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val SELECTION_ALL_ARGS = arrayOf<String?>(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

    private const val SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String?> {
        return arrayOf(mediaType.toString())
    }
    private const val SELECTION_ALBUM = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionAlbumArgs(albumId: String?): Array<String?> {
        return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
            albumId
        )
    }
    private const val SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionAlbumArgsForSingleMediaType(mediaType: Int, albumId: String?): Array<String?> {
        return arrayOf(mediaType.toString(), albumId)
    }
    private fun getSelection(isAll: Boolean): String {
        return if (isAll) {
            SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
        } else {
            SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
        }
    }

    private fun getSelectionArgs(isAll: Boolean, folderId: String?): Array<String?> {
        return if (isAll) {
            getSelectionArgsForSingleMediaType(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        } else {
            getSelectionAlbumArgsForSingleMediaType(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                folderId)
        }
    }

    private const val ORDER_BY = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    const val ITEM_ID_CAPTURE: Long = -1
    const val ITEM_DISPLAY_NAME_CAPTURE = "Capture"
    @JvmStatic
    fun load(context: Context, isAll: Boolean, folderId: String?): Cursor {
        val result: Cursor = ContentResolverCompat.query(context.contentResolver,
            QUERY_URI, PROJECTION, getSelection(isAll), getSelectionArgs(isAll, folderId), ORDER_BY,
            CancellationSignal())
        val dummy = MatrixCursor(PROJECTION)
        return MergeCursor(arrayOf(dummy, result))
    }

}