package com.storiesapp.features.main

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.android.common.view.statelayout.StateLayout
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.*
import com.storiesapp.core.local.LanguageHelper
import com.storiesapp.core.local.UserSession
import com.storiesapp.features.main.adapter.PagingLoadStateAdapter
import com.storiesapp.features.main.adapter.StoryAdapter
import com.storiesapp.features.main.databinding.DialogBottomSheetLanguageBinding
import com.storiesapp.features.main.databinding.MainActivityBinding
import com.storiesapp.navigation.Activities
import com.storiesapp.navigation.startFeature
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainViewModel>(R.layout.main_activity) {
    private val binding by binding<MainActivityBinding>()
    private val mAdapter by lazy { StoryAdapter {
        val activityOptionsCompat : ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
        startActivity(Intent(this , ActivityDetailStory::class.java).apply {
            putExtra("Story" , it)
        } , activityOptionsCompat.toBundle())
    } }
    private val stateLayout by lazy {
        StateLayout(this)
            .wrap(binding.recyclerView)
            .showLoading()
    }
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {

    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        UserSession.pageNumber = 1
        binding.fab.setOnClickListener {
            val activityOptionsCompat : ActivityOptionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
            startActivityForResult(Intent(this , ActivityAddStory::class.java) , 100 , activityOptionsCompat.toBundle())
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = LinearLayoutManager(this@MainActivity , RecyclerView.VERTICAL , false)
        }
        binding.recyclerView.adapter = mAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(mAdapter),
            footer = PagingLoadStateAdapter(mAdapter)
        )
        lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            UserSession.pageNumber = 1
            mAdapter.refresh()
        }
        binding.btnLogout.click {
            UserSession.logout()
            startFeature(Activities.LoginActivity){
                clearTask()
                newTask()
            }
            finish()
        }
        binding.bgLocale.click {
            val customDialog = MaterialDialog(this , BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                customView(R.layout.dialog_bottom_sheet_language , noVerticalPadding = true)
                cornerRadius(16f)
            }
            val itemBinding = DialogBottomSheetLanguageBinding.bind(customDialog.getCustomView())
            itemBinding.indonesiaSection.click {
                LanguageHelper.changeLocale(this@MainActivity , true)
                changeLanguage()
                customDialog.dismiss()
            }
            itemBinding.usaSection.click {
                LanguageHelper.changeLocale(this@MainActivity , false)
                changeLanguage()
                customDialog.dismiss()
            }
        }
        binding.icMaps.click {
            val activityOptionsCompat : ActivityOptionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
            startActivity(Intent(this , ActivityMapStory::class.java) , activityOptionsCompat.toBundle())
        }
        changeLanguageIcon()
        binding.localeText.text = LanguageHelper.getLanguage(this)
        fetchStoryList()
    }

    private fun fetchStoryList(){
        lifecycleScope.launch {
            viewModel.fetchStory().collectLatest { pagingData ->
                mAdapter.submitData(pagingData)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            100 -> {
                if(resultCode == RESULT_OK){
                    UserSession.pageNumber = 1
                    mAdapter.refresh()
                    binding.recyclerView.smoothScrollToPosition(0)
                    snackBar(resources.getString(R.string.add_story_succeed))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun changeLanguage(){
        binding.titleText.text = resources.getString(R.string.home_list_story_title)
        binding.btnLogout.text = resources.getString(R.string.home_list_story_logout)
        changeLanguageIcon()
    }
    private fun changeLanguageIcon(){
        binding.localeText.text = if(UserSession.isIndonesia) "ID" else "EN"
        binding.icLanguage.setImageResource(if(UserSession.isIndonesia) R.drawable.ic_indonesia else R.drawable.ic_usa)
    }
}