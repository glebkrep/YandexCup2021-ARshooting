package com.glebkrep.yandexcup.arshooting.ui.game

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.gameCore.repository.FirestoreDatabaseGame
import com.glebkrep.yandexcup.arshooting.utils.SharePreferences

class GameVM:ViewModel() {

    private var sessionId:String = ""

    private val _otherAlivePlayers:MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val otherAlivePlayers:LiveData<List<Player>> = _otherAlivePlayers

    private val _location:MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    private val _amIAlive:MutableLiveData<Boolean> = MutableLiveData()
    val amIAlive:LiveData<Boolean> = _amIAlive

    private val _isGameOverId:MutableLiveData<String> = MutableLiveData("0")
    val isGameOverId:LiveData<String> = _isGameOverId

    fun setSessionId(sessionId: String) {
        this.sessionId = sessionId
        startTimeSending()
        startListeningForPlayersUpdates()
    }

    fun shoot(){
        //todo set timeout and think how to shot
        //if (shot)
        FirestoreDatabaseGame.shotPlayer(sessionId,shotPlayerUdid)
    }

    private fun startTimeSending(){
        //todo write your location to firebase every tic
        //and write it to _location
        FirestoreDatabaseGame.writeYourLocation(sessionId,mLocation,SharePreferences.getUdid())
    }

    private fun startListeningForPlayersUpdates(){
        //todo write to _otherAlivePlayers every tic
        //todo and check if there are anybody alive
        //todo and check whether you are alive (write to _amIAlive)
        FirestoreDatabaseGame.getPlayersDataUpdates(sessionId){ playersList->

        }
    }

    private fun nobodyIsAlive(){
        _isGameOverId.postValue(sessionId)
    }

}