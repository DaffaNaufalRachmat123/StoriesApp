package com.storiesapp.common.locationhelper

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.github.ajalt.timberkt.d
import com.storiesapp.core.model.location.LocationSearch

import java.io.IOException;
import java.util.Locale;

class GpsTracker(val mContext: Context) : Service(), LocationListener {

    // flag for GPS Status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    // flag for GPS Tracking is enabled
    var isGPSTrackingEnabled = false
    var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    // How many Geocoder should return our GPSTracker
    var geocoderMaxResults = 1

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private var provider_info: String? = null

    /**
     * Try to get my current location by GPS or Network Provider
     */
    @SuppressLint("MissingPermission")
    fun getLocation() {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            //getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            //getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                isGPSTrackingEnabled = true
                Log.d(TAG, "Application use GPS Service")

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */provider_info = LocationManager.GPS_PROVIDER
            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                isGPSTrackingEnabled = true
                Log.d(TAG, "Application use Network State to get GPS coordinates")

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */provider_info = LocationManager.NETWORK_PROVIDER
            }

            // Application can use GPS or Network Provider
            if (!provider_info!!.isEmpty()) {
                locationManager?.requestLocationUpdates(
                    provider_info!!,
                    MIN_TIME_BW_UPDATES.toLong(),
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(provider_info!!)
                    updateGPSCoordinates()
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
    }
    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    @SuppressLint("MissingPermission")
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager?.removeUpdates(this@GpsTracker)
        }
    }
    /**
     * Function to show settings alert dialog
     */
    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
    </Address> */
    fun getGeocoderAddress(context: Context?): MutableList<Address>? {
        if (location != null) {
            val geocoder = Geocoder(context, Locale.ENGLISH)
            try {
                return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
            } catch (e: IOException) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e)
            }
        }
        return null
    }

    fun getGeocoderAddressByCoordinate(
        context: Context?,
        latitude: Double,
        longitude: Double
    ): MutableList<Address>? {
        val geocoder = Geocoder(context, Locale.ENGLISH)
        try {
            return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    fun getAddressLineByCoordinate(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(mContext, Locale.ENGLISH)
        try {
            val addressList: MutableList<Address>? = geocoder.getFromLocation(
                latitude, longitude,
                geocoderMaxResults
            )
            return if (addressList != null && addressList.size > 0) {
                addressList[0].getAddressLine(0)
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    fun getLocality(context: Context?): String? {
        val addresses: MutableList<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address: Address = addresses[0]
            address.getLocality()
        } else {
            null
        }
    }

    fun getLocalityByCoordinate(latitude: Double, longitude: Double): String {
        val addressList: MutableList<Address>? =
            getGeocoderAddressByCoordinate(mContext, latitude, longitude)
        d { "Address List : $addressList" }
        d { "Latitude : $latitude Longitude : $longitude" }
        return if (addressList != null && addressList.size > 0) {
            if(addressList[0] != null){
                val address: Address = addressList[0]
                if(address != null)
                    address.locality ?: ""
                else ""
            } else ""
        } else {
            ""
        }
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        // Get Class Name
        private val TAG = GpsTracker::class.java.name

        // The minimum distance to change updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
                ).toLong()
    }

    init {
        getLocation()
    }
}