package com.glebkrep.yandexcup.arshooting.ui.home.pages.sessionList

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.glebkrep.yandexcup.arshooting.ui.home.HomeActivityVM

@Composable
fun SessionListPage(homeActivityVM: HomeActivityVM, onSessionSelected: (sessionUid: String) -> (Unit)) {
    val sessions by homeActivityVM.sessions.observeAsState(listOf())
    LazyColumn(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item{
            Text(text = "Узнай у своего товарища название сессии и нажми чтобы подключиться",Modifier.padding(16.dp))
            Text(text = "Список сессий:",Modifier.padding(16.dp))
        }
        items(sessions){
            SessionItemView(it){
                onSessionSelected.invoke(it)
            }
        }
    }
}

@Composable
fun SessionItemView(sessionUid:String,onCLick:(String)->(Unit)){
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