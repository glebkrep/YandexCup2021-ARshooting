package com.glebkrep.yandexcup.arshooting.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionState
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionStateFromInt
import com.glebkrep.yandexcup.arshooting.gameCore.data.toPlayer
import com.glebkrep.yandexcup.arshooting.gameCore.repository.FirestoreDatabaseHome
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.glebkrep.yandexcup.arshooting.utils.SharePreferences

//todo divide in two vms
class HomeActivityVM : ViewModel() {
    private val _sessionUID: MutableLiveData<String> = MutableLiveData()
    val sessionUID: LiveData<String> = _sessionUID

    private val _isCreator: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCreator: LiveData<Boolean> = _isCreator

    private val _connectedPlayers: MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val connectedPlayers: LiveData<List<Player>> = _connectedPlayers

    private val _myUdid: MutableLiveData<String> = MutableLiveData("")
    val myUdid: LiveData<String> = _myUdid

    private val _sessionState: MutableLiveData<SessionState> = MutableLiveData()
    val sessionState: LiveData<SessionState> = _sessionState

    private val _sessions: MutableLiveData<List<String>> = MutableLiveData()
    val sessions: LiveData<List<String>> = _sessions

    private var _myName: String = ""
    fun setMyName(name: String) {
        _myName = name
        val udid = SharePreferences.getUdid()
        _myUdid.postValue(udid)
    }

    fun createSession() {
        _isCreator.postValue(true)
        val sesionUid = SharePreferences.getUdid() + System.currentTimeMillis()
        _sessionUID.value = sesionUid
        FirestoreDatabaseHome.createSessionWithYourself(sesionUid,_myName,SharePreferences.getUdid())
        startListeningForPlayersAndSessionState(sesionUid)
    }

    fun goToSessionList() {
        _isCreator.postValue(false)
        startListeningForSessions()
    }


    fun connectToSession(sessionUid: String) {
        _sessionUID.postValue(sessionUid)
        startListeningForPlayersAndSessionState(sessionUid)
        addYourselfToSession(sessionUid)
    }

    private fun addYourselfToSession(sessionUid: String){
        FirestoreDatabaseHome.addYourselfToSession(sessionUid,_myName,SharePreferences.getUdid())
    }

    private fun startListeningForSessions(){
        FirestoreDatabaseHome.startListeningForSessions {
            val sessions = it.filter { it.value.state==SessionState.IN_LOBBY.int }.map { it.key }
            _sessions.postValue(sessions)
        }
    }

    private fun startListeningForPlayersAndSessionState(sessionUid: String) {
        FirestoreDatabaseHome.startListeningForPlayersAndSessionState(sessionUid){ sessionItem ->
            val players = sessionItem.players.map { it.toPlayer() }
            Debug.log("sessionUid: ${sessionUid}")
            Debug.log("players: ${players.map { it.name }}")
            _connectedPlayers.postValue(players)
            _sessionState.postValue(SessionStateFromInt(sessionItem.state))
        }
    }

    fun startGame() {
        FirestoreDatabaseHome.setSessionState(_sessionUID.value!!,SessionState.PLAYING)
    }

    fun stopSession() {
        FirestoreDatabaseHome.setSessionState(_sessionUID.value!!,SessionState.GAME_FINISHED)
    }

    override fun onCleared() {
        super.onCleared()
        if (_sessionState.value==SessionState.IN_LOBBY && _isCreator.value==true){
            FirestoreDatabaseHome.setSessionState(_sessionUID.value!!, SessionState.GAME_FINISHED)
        }
    }
}