package com.storiesapp.features.main

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.github.ajalt.timberkt.d
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.binding
import com.storiesapp.common.extension.changeBackground
import com.storiesapp.common.extension.hide
import com.storiesapp.common.extension.show
import com.storiesapp.common.locationhelper.GpsTracker
import com.storiesapp.common.locationhelper.LocationHelper
import com.storiesapp.common.view.*
import com.storiesapp.common.view.model.LottieDialogType
import com.storiesapp.core.Constant
import com.storiesapp.core.model.location.LocationSearch
import com.storiesapp.features.main.databinding.ActivityChooseLocationBinding
import kotlinx.android.synthetic.main.activity_choose_location.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.experimental.property.inject

class ActivityChooseLocation : BaseActivity<MainViewModel>(R.layout.activity_choose_location),
    OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener , GoogleMap.OnCameraMoveListener ,
    GoogleMap.OnCameraIdleListener , GoogleMap.OnCameraMoveStartedListener ,
    GoogleMap.OnCameraMoveCanceledListener , GoogleMap.OnMyLocationChangeListener ,
    GoogleMap.OnMarkerClickListener{
    private var isMapReady = false
    private var currentLatitude : Double? = null
    private var currentLongitude : Double? = null
    private var locality : String? = null
    private var address : String? = null
    private var mMap : GoogleMap? = null
    private var locationList : MutableList<LocationSearch> = arrayListOf()
    private val locationHelper by inject<LocationHelper>()
    private val gpsTracker by inject<GpsTracker>()
    private val progressDialog by lazy {
        lottieLoadingDialog {
            type = LottieDialogType.BOTTOM_SHEET
            withAnimation {
                fileRes = R.raw.place_loading
                animationSpeed = 1f
            }

            withTitle {
                text = getString(R.string.choose_location_find_location)

            }
            withCancelOption {
                onBackPressed = false
                onTouchOutside = false
            }

            onTimeout {

            }
        }
    }
    override fun getViewModel() = MainViewModel::class
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap?.let {
            it.apply {
                setOnCameraIdleListener(this@ActivityChooseLocation)
                setOnCameraMoveListener(this@ActivityChooseLocation)
                setOnCameraMoveStartedListener(this@ActivityChooseLocation)
                setOnCameraMoveCanceledListener(this@ActivityChooseLocation)
                setOnInfoWindowClickListener(this@ActivityChooseLocation)
                setOnMyLocationChangeListener(this@ActivityChooseLocation)
                isMapReady = true
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude!! , currentLongitude!!) , 17f))
            }
        }
    }
    override fun observerViewModel() {

    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        intent?.let {
            currentLatitude = it.getDoubleExtra(Constant.LATITUDE , 0.0)
            currentLongitude = it.getDoubleExtra(Constant.LONGITUDE , 0.0)
            locality = it.getStringExtra(Constant.LOCALITY)
            address = it.getStringExtra(Constant.ADDRESS)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mChooseMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        icGps.setOnClickListener {
            startLocationRefresh()
        }
        btnPickLocation.setOnClickListener {
            if(currentLatitude != null && currentLongitude != null){
                val intent = Intent()
                intent.apply {
                    putExtra(Constant.LATITUDE , currentLatitude)
                    putExtra(Constant.LONGITUDE , currentLongitude)
                    putExtra(Constant.LOCALITY , gpsTracker.getLocalityByCoordinate(currentLatitude!! , currentLongitude!!))
                    putExtra(Constant.ADDRESS , gpsTracker.getAddressLineByCoordinate(currentLatitude!! , currentLongitude!!))
                }
                setResult(RESULT_OK , intent)
                finish()
            }
        }
        if((currentLatitude != null && currentLatitude != 0.0) && (currentLongitude != null && currentLongitude != 0.0) && locality != null && address != null){
            d { "Latitude : $currentLatitude" }
            d { "Longitude : $currentLongitude" }
            d { "Locality : $locality" }
            d { "Address : $address" }
            localityText.text = locality
            addressText.text = address
            btnPickLocation.changeBackground(this@ActivityChooseLocation , R.drawable.bg_enabled_blue)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude!! , currentLongitude!!) , 17f))
        } else {
            startLocationRefresh()
        }
    }

    private fun startLocationRefresh(){
        onLoading(true)
        lifecycleScope.launch {
            runCatching {
                locationHelper.actualLocation()
            }.onSuccess {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                address = gpsTracker.getAddressLineByCoordinate(currentLatitude!! , currentLongitude!!)
                locality = gpsTracker.getLocalityByCoordinate(currentLatitude!! , currentLongitude!!)
                localityText.text = locality
                addressText.text = address
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude!! , currentLongitude!!) , 17f))
                btnPickLocation.isEnabled = true
                btnPickLocation.changeBackground(this@ActivityChooseLocation , R.drawable.bg_enabled_blue)
            }.onFailure {
                btnPickLocation.isEnabled = false
                btnPickLocation.changeBackground(this@ActivityChooseLocation , R.drawable.bg_disabled_gray)
                onLoading(false)
            }
        }
    }

    private fun onLoading(loading : Boolean){
        if (loading )
            showLottieDialog(progressDialog)
        else
            dismissLottieDialog()
    }

    override fun onInfoWindowClick(p0: Marker?) {

    }

    override fun onCameraMove() {
        progressBarLocation.show()
    }

    override fun onCameraIdle() {
        progressBarLocation.hide()
        mMap?.let { googleMap ->
            currentLatitude = googleMap.cameraPosition.target.latitude
            currentLongitude = googleMap.cameraPosition.target.longitude
        }
        if(currentLatitude != null && currentLongitude != null){
            address = gpsTracker.getAddressLineByCoordinate(currentLatitude!! , currentLongitude!!)
            locality = gpsTracker.getLocalityByCoordinate(currentLatitude!! , currentLongitude!!)
        }
        localityText.text = locality
        addressText.text = address
    }

    override fun onCameraMoveStarted(p0: Int) {

    }

    override fun onCameraMoveCanceled() {

    }

    override fun onMyLocationChange(p0: Location?) {

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.let {
            for((_ , model) in locationList.withIndex()){
                if(it.position == LatLng(model.latitude , model.longitude)){
                    localityText.text = model.locality
                    addressText.text = model.address
                }
            }
        }
        return true
    }
}