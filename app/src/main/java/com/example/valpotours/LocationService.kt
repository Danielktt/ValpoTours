package com.example.valpotours

import android.annotation.SuppressLint
import android.location.Location
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class LocationService {

  @SuppressLint("MissingPermission")
  suspend  fun getUserLocation(context : Context) : Location? {
      val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
      val isUserLocationEnabled = true
      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val location =
          locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
              LocationManager.NETWORK_PROVIDER
          )
      if (!location || !isUserLocationEnabled) {
          return null
      }

      return suspendCancellableCoroutine { cont ->
          fusedLocationClient.lastLocation.apply {
              if (isSuccessful) {
                  if (isSuccessful) {
                      cont.resume(result) {

                      }

                  } else
                      cont.resume(null) {
                      }
                  return@suspendCancellableCoroutine
              }
              addOnSuccessListener {
                  cont.resume(it) {
                  }
                  addOnFailureListener {
                      cont.resume(null) {
                      }
                      addOnCanceledListener {
                          cont.cancel()
                      }
                  }
              }
          }

      }
  }
}