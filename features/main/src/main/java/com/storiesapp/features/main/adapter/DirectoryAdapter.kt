package com.storiesapp.features.main.adapter

import android.util.Log.d
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.ajalt.timberkt.d
import com.storiesapp.core.model.image.Directory
import com.storiesapp.features.main.R
import com.storiesapp.features.main.databinding.ItemDirectoryBinding

class DirectoryAdapter ( val onFolderClicked : ( View , Directory ) -> Unit ) : BaseQuickAdapter<Directory, BaseViewHolder>(R.layout.item_directory) {
    override fun convert(helper: BaseViewHolder, item: Directory?) {
        val itemBinding = ItemDirectoryBinding.bind(helper.itemView)
        item?.let { model ->
            Glide.with(mContext)
                .load("${model.firstImage}")
                .into(itemBinding.imageFirst)
            itemBinding.dirNameText.text = if(model.albumName.isNullOrEmpty() || model.albumName.isNullOrBlank()) mContext.resources.getString(R.string.unknown_folder) else model.albumName
            itemBinding.dirCountText.text = model.imageCount.toString()
            itemBinding.parentContainer.setOnClickListener {
                onFolderClicked.invoke(it , model)
            }
        }
    }
}