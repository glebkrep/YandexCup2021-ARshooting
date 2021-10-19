package com.glebkrep.yandexcup.arshooting.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glebkrep.yandexcup.arshooting.ar.model.Player

class GameVM:ViewModel() {

    private val _players:MutableLiveData<List<Player>> = MutableLiveData(listOf())
    val player:LiveData<List<Player>> = _players



}