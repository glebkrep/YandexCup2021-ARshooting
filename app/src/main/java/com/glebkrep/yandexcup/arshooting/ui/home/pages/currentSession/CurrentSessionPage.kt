package com.glebkrep.yandexcup.arshooting.ui.home.pages.currentSession

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionState
import com.glebkrep.yandexcup.arshooting.ui.home.HomeActivityVM

@Composable
fun CurrentSessionPage(homeActivityVM: HomeActivityVM, onGameStart: (String) -> (Unit)) {
    val isCreator by homeActivityVM.isCreator.observeAsState(false)
    val sessionUID by homeActivityVM.sessionUID.observeAsState("")
    val playersList by homeActivityVM.connectedPlayers.observeAsState(listOf())
    val sessionState by homeActivityVM.sessionState.observeAsState(SessionState.IN_LOBBY)
    val myUdid by homeActivityVM.myUdid.observeAsState("")

    Column(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (sessionState == SessionState.PLAYING) {
            onGameStart.invoke(sessionUID)
        }
        if (sessionState == SessionState.GAME_FINISHED) {
            Text("Извините, хост закрыл сессию", Modifier.padding(16.dp))
        } else {
            SessionScreen(
                isCreator = isCreator,
                sessionUid = sessionUID,
                players = playersList,
                myUdid = myUdid,
                onSessionStop = {
                    homeActivityVM.stopSession()
                },
                onGameStart = {
                    homeActivityVM.startGame()
                })
        }

    }
}

@Composable
fun SessionScreen(
    isCreator: Boolean,
    sessionUid: String,
    players: List<Player>,
    myUdid: String,
    onSessionStop: () -> (Unit),
    onGameStart: () -> Unit
) {
    Text(text = "Id сессии: ${sessionUid}", Modifier.padding(16.dp))
    if (isCreator) {
        Text(text = "Вы хост", Modifier.padding(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                onSessionStop.invoke()
            }) {
                Text(text = "Закрыть сессию")
            }
            Button(onClick = { onGameStart.invoke() }) {
                Text(text = "Начать игру")
            }
        }
    }
    for (item in players) {
        PlayerItem(item, myUdid)
    }
}

@Composable
fun PlayerItem(player: Player, myUdid: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .padding(16.dp)
            .border(
                1.dp,
                if (myUdid == player.udid) {
                    Color.Green
                } else {
                    Color.Black
                }
            )
    ) {
        Text(text = player.name)
        Text(text = player.udid)
    }
}