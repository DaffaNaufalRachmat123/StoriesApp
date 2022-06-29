package com.storiesapp.features.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.android.common.view.statelayout.StateLayout
import com.storiesapp.common.CompressImageHandler
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.ViewState
import com.storiesapp.core.Constant
import com.storiesapp.core.model.image.LoadedImage
import com.storiesapp.features.main.databinding.ActivityAddStoryBinding
import com.storiesapp.features.main.databinding.DialogChooseImageBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ActivityAddStory : BaseActivity<MainViewModel>(R.layout.activity_add_story) {
    private val binding by binding<ActivityAddStoryBinding>()
    private val stateLayout by lazy {
        StateLayout(this)
            .wrap(binding.parentContainer)
            .showLoading()
    }
    private var latitude : Double? = null
    private var longitude : Double? = null
    private var locality : String? = null
    private var address : String? = null
    private var loadedImage : LoadedImage? = null
    override fun getViewModel() = MainViewModel::class
    override fun observerViewModel() {
        viewModel.createStoryResponse.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    stateLayout.showLoading()
                }
                is ViewState.Success -> {
                    stateLayout.showContent()
                    if(!state.data.error){
                        val intent = Intent()
                        setResult(RESULT_OK , intent)
                        supportFinishAfterTransition()
                    } else {
                        snackBar(resources.getString(R.string.add_story_failed))
                    }
                }
                is ViewState.Failed -> {
                    stateLayout.showContent()
                    state.message?.let {
                        try {
                            val objs = JSONObject(it)
                            snackBar(objs.getString("message"))
                        } catch ( e : JSONException ){
                            snackBar(resources.getString(R.string.add_story_failed))
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.btnAddImage.setOnClickListener {
            checkPermission()
        }
        binding.icBack.click {
            onBackPressed()
        }
        binding.btnLocation.click {
            checkLocationPermission(it)
        }
        binding.btnCreate.setOnClickListener {
            if(loadedImage != null && binding.descriptionText.text.toString() != null &&
                    binding.descriptionText.text.toString() != ""){
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                val multipartFiles: MultipartBody.Part = MultipartBody.Part.createFormData("photo" , File(loadedImage!!.bitmap).name , RequestBody.create("image/*".toMediaTypeOrNull() , File(loadedImage!!.bitmap)))
                val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull() , binding.descriptionText.text.toString())
                val latBody = RequestBody.create("text/plain".toMediaTypeOrNull() , latitude.toString() ?: "")
                val lonBody = RequestBody.create("text/plain".toMediaTypeOrNull() , longitude.toString() ?: "0")
                viewModel.createStory(multipartFiles , descriptionBody , latBody , lonBody ,
                    latitude != null && longitude != null
                )
            } else {
                snackBar(resources.getString(R.string.add_story_warning))
            }
        }
    }

    private fun checkLocationPermission(it : View){
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this , arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) , 300)
        } else {
            launchChooseLocation(it)
        }
    }

    private fun launchChooseLocation(it : View){
        val activityOptionsCompat : ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
        if(latitude != null && longitude != null && locality != null && address != null){
            startActivityForResult(Intent(this , ActivityChooseLocation::class.java).apply {
                putExtra(Constant.LATITUDE , latitude)
                putExtra(Constant.LONGITUDE , longitude)
                putExtra(Constant.LOCALITY , locality)
                putExtra(Constant.ADDRESS , address)
            } , 400 , activityOptionsCompat.toBundle())
        } else {
            startActivityForResult(Intent(this , ActivityChooseLocation::class.java) , 400 , activityOptionsCompat.toBundle())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            100 -> {
                if(resultCode == RESULT_OK){
                    loadedImage = data?.getParcelableExtra(Constant.LOADED_IMAGE)
                    val newFile = CompressImageHandler.createFile()
                    runOnUiThread {
                        run {
                            try {
                                CompressImageHandler.copyFile(loadedImage?.bitmap , newFile)
                            } catch (e : IOException){
                                e.printStackTrace()
                            }
                        }
                    }
                    ImageCompression().execute(loadedImage?.bitmap , newFile)
                }
            }
            400 -> {
                if(resultCode == RESULT_OK){
                    latitude = data?.getDoubleExtra(Constant.LATITUDE , 0.0)
                    longitude = data?.getDoubleExtra(Constant.LONGITUDE , 0.0)
                    locality = data?.getStringExtra(Constant.LOCALITY)
                    address = data?.getStringExtra(Constant.ADDRESS)
                    binding.latitudeText.text = "${resources.getString(R.string.add_story_latitude_text)} : $latitude"
                    binding.longitudeText.text = "${resources.getString(R.string.add_story_longitude_text)} : $longitude"
                    binding.localityText.text = "${resources.getString(R.string.add_story_region_text)} : $locality"
                    binding.addressText.text = "${resources.getString(R.string.add_story_address_text)} : $address"
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    inner class ImageCompression : AsyncTask<String , Void, String>(){
        override fun doInBackground(vararg p0: String?): String? {
            if(p0.isEmpty() || p0[0] == null)
                return null
            return CompressImageHandler.compressImage(p0[0] , p0[1])
        }
        override fun onPostExecute(result: String?) {
            loadedImage?.bitmap = File(result).absolutePath
            binding.cautionText.hide()
            binding.imgStory.show()
            binding.btnAddImage.setImageResource(R.drawable.ic_edit_white)
            binding.imgStory.loadImageRoundedFile(loadedImage?.bitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            100 -> {
                if(grantResults.size > 0){
                    launchImageChooser()
                }
            }
            300 -> {
                if(grantResults.size > 0){
                    launchChooseLocation(binding.btnLocation)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun checkPermission(){
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this , arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) , 100)
        } else {
            launchImageChooser()
        }
    }
    private fun launchImageChooser(){
        val activityOptionsCompat : ActivityOptionsCompat =
            ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
        startActivityForResult(Intent(this , ActivityFolder::class.java) , 100 , activityOptionsCompat.toBundle())
    }
}