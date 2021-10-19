package com.glebkrep.yandexcup.arshooting.ui.game

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.gameCore.data.toPlayer
import com.glebkrep.yandexcup.arshooting.gameCore.repository.FirestoreDatabaseGame
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.glebkrep.yandexcup.arshooting.utils.SharePreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult


class GameVM(application: Application) : AndroidViewModel(application) {

    private var sessionId: String = ""

    private val _otherAlivePlayers: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val otherAlivePlayers: LiveData<List<Player>> = _otherAlivePlayers

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    private val _amIAlive: MutableLiveData<Boolean> = MutableLiveData()
    val amIAlive: LiveData<Boolean> = _amIAlive

    private val _isGameOverId: MutableLiveData<String> = MutableLiveData("")
    val isGameOverId: LiveData<String> = _isGameOverId

    fun setSessionId(sessionId: String, activity: Activity) {
        this.sessionId = sessionId
        startTimeSending(activity)
        startListeningForPlayersUpdates()
    }

    var lastTime = System.currentTimeMillis()
    fun shoot(shotPlayerUdid: String) {
        if ((System.currentTimeMillis() - lastTime) > 5000) {
            lastTime = System.currentTimeMillis()
            FirestoreDatabaseGame.shotPlayer(sessionId, shotPlayerUdid)
        }
    }

    var fusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("VisibleForTests")
    private fun startTimeSending(activity: Activity) {
        fusedLocationClient = FusedLocationProviderClient(activity)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation = locationResult.lastLocation
                _location.postValue(lastLocation)
                FirestoreDatabaseGame.writeYourLocation(
                    sessionId,
                    lastLocation,
                    SharePreferences.getUdid()
                )
            }
        }
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 10000L
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        startLocationUpdates(locationCallback, mLocationRequest)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(callback: LocationCallback, locationRequest: LocationRequest) {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(object : LocationCallback() {

        })
    }


    private fun startListeningForPlayersUpdates() {
        FirestoreDatabaseGame.getPlayersDataUpdates(sessionId) { playersList ->
            val list = playersList
            Debug.log("players: ${list.map { it.name }}")
            val me = list.firstOrNull() { it.udid == SharePreferences.getUdid() }
                ?: return@getPlayersDataUpdates
            if (me.deadTimestamp != 0L) {
                imDead()
            }
            val alivePlayers =
                list.filter { it.deadTimestamp == 0L }
            if (alivePlayers.size < 2) {
                nobodyIsAlive()
            }
            _otherAlivePlayers.postValue(alivePlayers.filter { it.udid != SharePreferences.getUdid() }
                .map { it.toPlayer() })
        }
    }

    private fun imDead() {
        stopLocationUpdates()
        _amIAlive.postValue(false)
    }

    private fun nobodyIsAlive() {
        _isGameOverId.postValue(sessionId)
        stopLocationUpdates()
    }

}