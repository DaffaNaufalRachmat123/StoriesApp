package com.storiesapp.common

import android.graphics.*
import android.media.ExifInterface
import android.os.Environment
import java.io.*


object CompressImageHandler {
    private const val maxHeight = 920.0f
    private const val maxWidth = 920.0f
    fun calculateSampleSize(
        options: BitmapFactory.Options,
        requireWidth: Int,
        requireHeight: Int
    ): Int {
        val actualHeight = options.outHeight
        val actualWidth = options.outWidth
        var sampleSize = 1
        if (actualHeight > requireHeight || actualWidth > requireWidth) {
            val heightRatio = Math.round(actualHeight.toFloat() / requireHeight.toFloat())
            val widthRatio = Math.round(actualWidth.toFloat() / requireWidth.toFloat())
            sampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (actualWidth * actualHeight).toFloat()
        val totalReqPixelsCap = (requireWidth * requireHeight * 2).toFloat()
        while (totalPixels / (sampleSize * sampleSize) > totalReqPixelsCap) {
            sampleSize++
        }
        return sampleSize
    }

    fun compressImage(imagePath: String?, destPath: String?): String {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeFile(imagePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize =
            calculateSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bitmap = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(
                actualWidth,
                actualHeight,
                Bitmap.Config.RGB_565
            )
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap!!,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        if (bitmap != null) {
            bitmap.recycle()
        }
        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(destPath)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return destPath ?: ""
    }

    fun createFile(): String {
        val imageFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                    + "/StoriesApp/images/"
        )
        if (!imageFile.exists()) {
            imageFile.mkdirs()
        }
        val imageName =
            "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        return imageFile.toString() + File.separator + imageName
    }

    @Throws(IOException::class)
    fun copyFile(selectedImagePath: String?, mdestinationPath: String?) {
        val inputStream: InputStream = FileInputStream(selectedImagePath)
        val outputStream: OutputStream = FileOutputStream(mdestinationPath)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
        inputStream.close()
        outputStream.close()
    }
}