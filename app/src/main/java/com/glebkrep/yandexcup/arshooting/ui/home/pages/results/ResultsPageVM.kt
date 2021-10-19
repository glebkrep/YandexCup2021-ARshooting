package com.glebkrep.yandexcup.arshooting.ui.home.pages.results

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glebkrep.yandexcup.arshooting.gameCore.data.PlayerItem
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionItem
import com.glebkrep.yandexcup.arshooting.gameCore.repository.FirestoreDatabaseResults


class ResultsPageVM : ViewModel() {
    private val _resultsText: MutableLiveData<String> = MutableLiveData()
    val resultsText: LiveData<String> = _resultsText

    private var sessionId: String? = null
    fun setSessionId(string: String?) {
        string?.let {
            sessionId = string
            FirestoreDatabaseResults.getSessionData(it) {
                postTransformedData(it)
            }
        }
    }

    private fun postTransformedData(sessionItem: SessionItem) {
        val firstPlayer = sessionItem.players.firstOrNull { it.deadTimestamp == 0L }
        val sortedPlayers = sessionItem.players.sortedByDescending { it.deadTimestamp }
            .filter { it.deadTimestamp != 0L }
        val finalList = mutableListOf<PlayerItem>().apply {
            firstPlayer?.let {
                add(firstPlayer)
            }
            addAll(sortedPlayers)
        }
        var i = 0
        val finalString = finalList.map {
            i += 1
            "${i}) ${it.name}\n"
        }.joinToString { it }
        _resultsText.value = finalString
    }

    fun share(activity: Activity) {
        val intent = Intent(Intent.ACTION_SEND)
        val shareBody = _resultsText.value ?: ""
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        activity.startActivity(Intent.createChooser(intent, "Поделиться успехами..."))
    }
}
