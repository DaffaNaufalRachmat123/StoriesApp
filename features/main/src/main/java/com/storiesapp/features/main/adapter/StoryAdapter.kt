package com.storiesapp.features.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.storiesapp.common.extension.click
import com.storiesapp.common.extension.loadImageRounded
import com.storiesapp.core.model.story.Story
import com.storiesapp.features.main.databinding.ItemStoryBinding

class StoryAdapter ( val onItemClicked : (Story) -> Unit ) : PagingDataAdapter<Story, StoryAdapter.EpisodeViewHolder>(EpisodeComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EpisodeViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class EpisodeViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Story) {
            binding.nameText.text = item.name
            binding.descriptionText.text = item.description
            binding.imageStory.loadImageRounded(item.photoUrl)
            binding.parentContainer.click {
                onItemClicked.invoke(item)
            }
        }
    }

    object EpisodeComparator : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Story, newItem: Story) =
            oldItem == newItem
    }
}