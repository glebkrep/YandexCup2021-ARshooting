package com.glebkrep.yandexcup.arshooting.ui.home.pages.sessionList

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.glebkrep.yandexcup.arshooting.ui.home.HomeActivityVM

@Composable
fun SessionListPage(
    homeActivityVM: HomeActivityVM,
    onSessionSelected: (sessionUid: String) -> (Unit)
) {
    val sessions by homeActivityVM.sessions.observeAsState(listOf())
    LazyColumn(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = stringResource(R.string.get_session_id),
                Modifier.padding(16.dp)
            )
            Text(text = stringResource(R.string.session_list), Modifier.padding(16.dp))
        }
        items(sessions) {
            SessionItemView(it) {
                onSessionSelected.invoke(it)
            }
        }
    }
}

@Composable
fun SessionItemView(sessionUid: String, onCLick: (String) -> (Unit)) {
    Row(
        Modifier
            .border(2.dp, Color.Black)
            .padding(16.dp)
            .clickable {
                onCLick.invoke(sessionUid)
            }) {
        Text(text = sessionUid)
    }
}