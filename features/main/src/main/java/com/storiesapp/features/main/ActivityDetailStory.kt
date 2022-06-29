package com.storiesapp.features.main

import android.os.Bundle
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.binding
import com.storiesapp.common.extension.click
import com.storiesapp.common.extension.loadImageRounded
import com.storiesapp.core.model.story.Story
import com.storiesapp.features.main.databinding.ActivityDetailStoryBinding

class ActivityDetailStory : BaseActivity<MainViewModel>(R.layout.activity_detail_story) {
    private var story : Story? = null
    private val binding by binding<ActivityDetailStoryBinding>()
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {

    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        story = intent?.getParcelableExtra("Story")
        story?.let {
            binding.imageStory.loadImageRounded(it.photoUrl)
            binding.nameText.text = it.name
            binding.descriptionText.text = it.description
        }
        binding.icBack.click {
            onBackPressed()
        }
    }
}