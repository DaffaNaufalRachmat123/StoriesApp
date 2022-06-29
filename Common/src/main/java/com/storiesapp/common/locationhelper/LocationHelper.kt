package com.storiesapp.common.locationhelper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException


class LocationHelper(private val context: Context){
    private val fusedLocation: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }
    private val settings: SettingsClient by lazy { LocationServices.getSettingsClient(context) }
    private val location: LocationManager? by lazy { context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager }

    @Throws(LocationException::class, LocationDisabledException::class, SecurityException::class, TimeoutException::class, ServicesAvailabilityException::class)
    suspend fun actualLocation(@Accuracy accuracy: Int = Accuracy.BALANCED,
                               @IntRange(from = 0) timeout: Long = 5_000L): Location {
        val coroutine = CompletableDeferred<Location>()
        if (location == null) {
            coroutine.completeExceptionally(LocationException("Location manager not found"))
        } else if (!isGooglePlayServicesAvailable(context)) {
            coroutine.completeExceptionally(ServicesAvailabilityException())
        } else if (!isLocationEnabled(location)) {
            coroutine.completeExceptionally(LocationDisabledException())
        } else if (!checkPermission(context, false)) {
            coroutine.completeExceptionally(SecurityException("Permissions for GPS was not given"))
        } else {
            val listener = CGPSCallback(coroutine, null)

            requestLocationUpdates(listener, accuracy, timeout, timeout, 1)

            try {
                withTimeout(timeout) {
                    coroutine.await()
                }
            } catch (e: TimeoutCancellationException) {
                if (coroutine.isActive) {
                    coroutine.completeExceptionally(TimeoutException("Location timeout on $timeout ms"))
                }
            } finally {
                fusedLocation.removeLocationUpdates(listener)
            }
        }

        return coroutine.await()
    }
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(listener: LocationCallback,
                                       @Accuracy accuracy: Int,
                                       interval: Long,
                                       timeout: Long,
                                       updates: Int? = null) {
        val request = createRequest(accuracy, interval, timeout, updates)

        fusedLocation.requestLocationUpdates(request, listener, Looper.getMainLooper())
    }

    private fun createRequest(@Accuracy accuracy: Int,
                              interval: Long,
                              timeout: Long,
                              updates: Int? = null): LocationRequest {
        return LocationRequest().apply {
            updates?.let { count -> this.numUpdates = count }
            this.interval = interval
            this.maxWaitTime = timeout
            this.priority = accuracy
        }
    }

    private class CGPSCallback(private val coroutine: CompletableDeferred<Location>?,
                               private val listener: Channel<Result<Location>>?) : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.getOrNull(0)?.let {
                coroutine?.complete(it)
                listener?.offer(Result.success(it))
            } ?: handleError()
        }

        override fun onLocationAvailability(locationStatus: LocationAvailability?) {
            if (locationStatus?.isLocationAvailable == false) {
                coroutine?.completeExceptionally(LocationException("Location are unavailable with those settings"))
                listener?.offer(Result.failure(LocationException("Location are unavailable with those settings")))
            }
        }

        fun handleError() {
            coroutine?.completeExceptionally(LocationException("Location not found"))
            listener?.offer(Result.failure(LocationException("Location not found")))
        }
    }

    @IntDef(
        Accuracy.HIGH,
        Accuracy.BALANCED,
        Accuracy.LOW,
        Accuracy.NO
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Accuracy {
        companion object {
            const val HIGH = LocationRequest.PRIORITY_HIGH_ACCURACY
            const val BALANCED = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            const val LOW = LocationRequest.PRIORITY_LOW_POWER
            const val NO = LocationRequest.PRIORITY_NO_POWER
        }
    }
}

fun isGooglePlayServicesAvailable(context: Context): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
}
internal fun checkPermission(context: Context, isCoarse: Boolean) = if (isCoarse) {
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
} else {
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

internal fun isLocationEnabled(manager: LocationManager?) = manager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
        || manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false

class LocationException(message: String) : Exception(message)

class LocationDisabledException : Exception("Location adapter turned off on device")

class ServicesAvailabilityException : Exception("Google services is not available on this device")
