package com.storiesapp.features.main

import android.os.Bundle
import com.android.common.view.statelayout.StateLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.changeBackground
import com.storiesapp.common.extension.click
import com.storiesapp.common.view.ViewState
import com.storiesapp.core.model.story.Story
import kotlinx.android.synthetic.main.activity_map_story.*

class ActivityMapStory : BaseActivity<MainViewModel>(R.layout.activity_map_story) ,
    OnMapReadyCallback , GoogleMap.OnMarkerClickListener {
    private var page : Int = 1
    private var maxPage : Int = 1
    private var isEnd = false
    private val stateLayout by lazy {
        StateLayout(this)
            .wrap(mapArea)
            .showLoading()
    }
    private var mMap : GoogleMap? = null
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {
        viewModel.storyResponse.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    stateLayout.showLoading()
                }
                is ViewState.Success -> {
                    stateLayout.showContent()
                    if(state.data.listStory.size > 0){
                        updateGoogleMapsMarker(state.data.listStory)
                    } else {
                        isEnd = true
                        maxPage = page
                        btnNext.isEnabled = false
                        btnNext.changeBackground(applicationContext , R.drawable.btn_navigation_disabled)
                    }
                }
                is ViewState.Failed -> {
                    stateLayout.showError()
                    stateLayout.onRetry {
                        viewModel.getStoryListMap(page , 10)
                    }
                }
            }
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mChooseMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        pageNumberText.text = "${resources.getString(R.string.page_number_text)} $page"
        icBack.click {
            onBackPressed()
        }
        btnPrev.setOnClickListener {
            if(page > 1)
                page -= 1
            if(page == 1){
                btnPrev.changeBackground(applicationContext , R.drawable.btn_navigation_disabled)
                btnPrev.isEnabled = false
            }
            pageNumberText.text = "${resources.getString(R.string.page_number_text)} $page"
            btnNext.changeBackground(applicationContext , R.drawable.btn_navigation)
            btnNext.isEnabled = true
            viewModel.getStoryListMap(page , 10)
        }
        btnNext.setOnClickListener {
            page += 1
            btnPrev.changeBackground(applicationContext , R.drawable.btn_navigation)
            btnPrev.isEnabled = true
            pageNumberText.text = "${resources.getString(R.string.page_number_text)} $page"
            viewModel.getStoryListMap(page , 10)
        }
        viewModel.getStoryListMap(page , 10)
    }
    private fun updateGoogleMapsMarker(data : MutableList<Story>){
        mMap?.clear()
        for(story in data){
            mMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mark_location)).position(LatLng(story.lat , story.lon)).title(story.name))
        }
        if(data.size > 0){
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data[0].lat , data[0].lon),14f))
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }
}