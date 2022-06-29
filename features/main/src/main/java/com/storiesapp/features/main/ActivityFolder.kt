package com.storiesapp.features.main

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.common.view.statelayout.StateLayout
import com.github.ajalt.timberkt.d
import com.storiesapp.common.CompressImageHandler
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.ViewState
import com.storiesapp.core.Constant
import com.storiesapp.core.model.image.Directory
import com.storiesapp.core.model.image.LoadedImage
import com.storiesapp.features.main.adapter.DirectoryAdapter
import com.storiesapp.features.main.databinding.ActivityFolderBinding
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ActivityFolder : BaseActivity<MainViewModel>(R.layout.activity_folder) {
    private val binding by binding<ActivityFolderBinding>()
    private val stateLayout by lazy {
        StateLayout(this)
            .wrap(binding.recyclerViewFolder)
            .showLoading()
    }
    private var universalPath : String? = null
    private val mAdapter by lazy { DirectoryAdapter { view , it ->
        val activityOptionsCompat : ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
        startActivityForResult(Intent(applicationContext , ActivityMedia::class.java).apply {
            putExtra(Constant.BUCKET_ID , it.albumId)
            putExtra(Constant.ALBUM_NAME , it.albumName)
        } , 100 , activityOptionsCompat.toBundle())
    } }
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {
        viewModel.folderListData.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    stateLayout.showLoading()
                }
                is ViewState.Success -> {
                    if(state.data.size > 0){
                        stateLayout.showContent()
                        mAdapter.setNewData(state.data)
                    } else {
                        stateLayout.showEmpty(noDataText = resources.getString(R.string.no_folder_found))
                    }
                }
                is ViewState.Failed -> {
                    stateLayout.showError()
                    stateLayout.onRetry {
                        viewModel.getFolderList(applicationContext)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            100 -> {
                if(resultCode == RESULT_OK && data?.getParcelableExtra<LoadedImage>(Constant.LOADED_IMAGE) != null){
                    val intent = Intent()
                    intent.putExtra(Constant.LOADED_IMAGE , data!!.getParcelableExtra<LoadedImage>(Constant.LOADED_IMAGE)!!)
                    setResult(RESULT_OK , intent)
                    supportFinishAfterTransition()
                }
            }
            500 -> {
                if(resultCode == RESULT_OK){
                    val loadedImage = LoadedImage(universalPath , "",type = 1 , photoCaption = "")
                    loadedImage?.bitmap = universalPath
                    val intent = Intent()
                    intent.putExtra(Constant.LOADED_IMAGE , loadedImage)
                    setResult(RESULT_OK , intent)
                    supportFinishAfterTransition()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.recyclerViewFolder.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = GridLayoutManager(applicationContext , 2 , RecyclerView.VERTICAL , false)
            adapter = mAdapter
        }
        binding.icBack.click {
            onBackPressed()
        }
        binding.icCamera.click {
            var imageUri : File? = null
            try {
                imageUri = pictureFile()
            } catch (e: IOException){
                e.printStackTrace()
            }
            val uriImage = FileProvider.getUriForFile(this , "com.android.storiesapp" , imageUri!!)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT , uriImage)
            val activityOptionsCompat : ActivityOptionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
            startActivityForResult(intent , 500 , activityOptionsCompat.toBundle())
        }
        viewModel.getFolderList(this)
    }
    fun pictureFile() : File? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd").format(Date())
            val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val tempFile = File.createTempFile(timestamp , ".jpg" , directory)
            universalPath = tempFile.absolutePath
            tempFile
        } catch (e: Exception){
            null
        }
    }
}