package com.storiesapp.features.main

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.common.view.statelayout.StateLayout
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.binding
import com.storiesapp.common.extension.click
import com.storiesapp.common.view.ViewState
import com.storiesapp.core.Constant
import com.storiesapp.features.main.adapter.MediaAdapter
import com.storiesapp.features.main.databinding.ActivityMediaBinding

class ActivityMedia : BaseActivity<MainViewModel>(R.layout.activity_media) {
    private var bucketId : String? = null
    private var albumName : String? = null
    private val binding by binding<ActivityMediaBinding>()
    private val stateLayout by lazy {
        StateLayout(this)
            .wrap(binding.recyclerViewMedia)
            .showLoading()
    }
    private val mAdapter by lazy { MediaAdapter{
        val intent = Intent()
        intent.putExtra(Constant.LOADED_IMAGE , it)
        setResult(RESULT_OK , intent)
        finish()
    } }
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {
        viewModel.mediaListData.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    stateLayout.showLoading()
                }
                is ViewState.Success -> {
                    if(state.data.size > 0){
                        stateLayout.showContent()
                        mAdapter.setNewData(state.data)
                    } else {
                        stateLayout.showEmpty(noDataText = resources.getString(R.string.no_photos_found))
                    }
                }
                is ViewState.Failed -> {
                    stateLayout.showError()
                    stateLayout.onRetry {
                        bucketId?.let {
                            viewModel.getPhotosByFolder(applicationContext , it , 1)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        bucketId = intent.getStringExtra(Constant.BUCKET_ID)
        albumName = intent.getStringExtra(Constant.ALBUM_NAME)
        binding.titleText.text = albumName
        binding.recyclerViewMedia.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = GridLayoutManager(applicationContext , 2 , RecyclerView.VERTICAL , false)
            adapter = mAdapter
        }
        binding.icBack.click {
            onBackPressed()
        }
        bucketId?.let {
            viewModel.getPhotosByFolder(applicationContext , it , 1)
        }
    }
}