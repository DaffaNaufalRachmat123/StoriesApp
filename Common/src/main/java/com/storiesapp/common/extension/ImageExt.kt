package com.storiesapp.common.extension

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.storiesapp.common.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.ajalt.timberkt.e
import java.io.File

fun Drawable.copy() = constantState?.newDrawable()?.mutate()

@SuppressLint("CheckResult")
fun getGlideOptions(
    target: ImageView,
    isCenterCrop: Boolean = false,
    radius: Int = 0,
    width: Int = 0,
    height: Int = 0,
    errorDrawable: Int? = null,
    placeholder: Drawable? = ShimmerDrawable()
): RequestOptions {

    val isLowRamDevice = target.context.isLowRamDevice()

    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .downsample(DownsampleStrategy.CENTER_INSIDE)
        .priority(Priority.HIGH)
        .dontAnimate()
        .apply(RequestOptions.bitmapTransform(RoundedCorners(radius)))
        .let { request ->
            if (target.drawable != null) {
                request.placeholder(target.drawable.copy())
            } else {
                request
            }
        }

    if (width > 0 || height > 0) {
        options.override(width, height)
    }else{
        options.override(target.width, target.height)
    }

    if (errorDrawable != null) {
        options.error(errorDrawable)
    }

    if (placeholder != null) {
        options.placeholder(placeholder)
    }

    return options
}

fun ImageView.usingRounded(
    url: String?, radius: Int = 8.dp, placeholder: Drawable? = ShimmerDrawable()
) {
    try {
        Glide.with(context)
            .load(url)
            .apply(
                getGlideOptions(
                    this,
                    radius = radius,
                    errorDrawable = R.drawable.ic_broke_image,
                    placeholder = placeholder
                )
            )
            .dontAnimate()
            .into(this)
            .waitForLayout()
            .clearOnDetach()
    } catch (ex: Exception) {
        e { "Error loadImageRounded : ${ex.message}" }
    }

    applyOverlay()
}

fun ImageView.usingRoundedBrokenImage(
    url: Int, radius: Int = 8.dp, placeholder: Drawable? = ShimmerDrawable()
) {
    try {
        Glide.with(context)
            .load(url)
            .apply(
                getGlideOptions(
                    this,
                    radius = radius,
                    errorDrawable = R.drawable.not_found_image,
                    placeholder = placeholder
                )
            )
            .dontAnimate()
            .into(this)
            .waitForLayout()
            .clearOnDetach()
    } catch (ex: Exception) {
        e { "Error loadImageRounded : ${ex.message}" }
    }

    applyOverlay()
}

fun ImageView.usingRoundedFile(
    url: String?, radius: Int = 8.dp, placeholder: Drawable? = ShimmerDrawable()
) {
    try {
        Glide.with(context)
            .load(File(url))
            .apply(
                getGlideOptions(
                    this,
                    radius = radius,
                    errorDrawable = R.drawable.ic_broke_image,
                    placeholder = placeholder
                )
            )
            .dontAnimate()
            .into(this)
            .waitForLayout()
            .clearOnDetach()
    } catch (ex: Exception) {
        e { "Error loadImageRounded : ${ex.message}" }
    }

    applyOverlay()
}

fun ImageView.applyOverlay() {
    setColorFilter(ContextCompat.getColor(
        context,
        R.color.colorOverlayImage),
        PorterDuff.Mode.SRC_OVER
    )
}

fun ImageView.loadImageRounded(url: String?, radius: Int = 8.dpToPx) {
    if(url != null){
        usingRounded(url, radius)
    } else {
        usingRoundedBrokenImage(R.drawable.not_found_image)
    }
}

fun ImageView.loadImageRoundedFile(url: String?, radius: Int = 8.dpToPx) {
    if(url != null){
        usingRoundedFile(url, radius)
    } else {
        usingRoundedBrokenImage(R.drawable.not_found_image)
    }
}