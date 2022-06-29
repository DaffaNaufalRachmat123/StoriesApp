package com.storiesapp.features.main.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.storiesapp.common.extension.ShimmerDrawable
import com.storiesapp.common.extension.dp
import com.storiesapp.core.model.image.LoadedImage
import com.storiesapp.features.main.R
import com.storiesapp.features.main.databinding.ItemDirectoryImageBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

class MediaAdapter ( val onItemClicked : (LoadedImage) -> Unit ) : BaseQuickAdapter<LoadedImage, BaseViewHolder>(R.layout.item_directory_image) {
    override fun convert(helper: BaseViewHolder, item: LoadedImage?) {
        val itemBinding = ItemDirectoryImageBinding.bind(helper.itemView)
        item?.let { model ->
            Glide.with(mContext)
                .load(File(model.bitmap.toString()))
                .dontAnimate()
                .fitCenter()
                .thumbnail(0.5f)
                .placeholder(ShimmerDrawable())
                .transform(MultiTransformation(CenterCrop(), RoundedCornersTransformation(8.dp, 0)))
                .error(R.drawable.ic_broken_image)
                .override(itemBinding.imageView.width, itemBinding.imageView.height)
                .into(itemBinding.imageView)
                .waitForLayout()
                .clearOnDetach()
            itemBinding.parentContainer.setOnClickListener {
                onItemClicked.invoke(model)
            }
        }
    }
}