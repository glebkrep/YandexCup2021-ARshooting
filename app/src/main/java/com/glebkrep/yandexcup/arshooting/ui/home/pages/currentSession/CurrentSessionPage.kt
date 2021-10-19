package com.glebkrep.yandexcup.arshooting.ui.home.pages.currentSession

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glebkrep.yandexcup.arshooting.R
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
            Text(stringResource(R.string.host_closed), Modifier.padding(16.dp))
        } else {
            Text(text = stringResource(id = R.string.rules), Modifier.padding(16.dp))
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
        Text(text = stringResource(R.string.you_are_host), Modifier.padding(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                onSessionStop.invoke()
            }, Modifier.padding(8.dp)) {
                Text(text = stringResource(R.string.kill_session))
            }
            Button(onClick = { onGameStart.invoke() }, Modifier.padding(8.dp)) {
                Text(text = stringResource(R.string.start_game))
            }
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState())
    ) {
        Text(text = stringResource(R.string.player_list), Modifier.padding(8.dp))
        for (item in players) {
            PlayerItem(item, myUdid)
        }
    }

}

@Composable
fun PlayerItem(player: Player, myUdid: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                if (myUdid == player.udid) {
                    Color.Green
                } else {
                    Color.Black
                }
            )
    ) {
        Text(text = player.name, Modifier.padding(8.dp))
        Text(text = player.udid, Modifier.padding(8.dp))
    }
}