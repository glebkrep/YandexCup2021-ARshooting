package com.glebkrep.yandexcup.arshooting.ui.home.pages.results

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glebkrep.yandexcup.arshooting.R

@Composable
fun ResultsPage(viewModel: ResultsPageVM) {
    val resultsText by viewModel.resultsText.observeAsState("")
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.results), Modifier.padding(16.dp))
        Text(text = resultsText, Modifier.padding(16.dp))
        Button(onClick = {
            viewModel.share(context.getActivity() ?: return@Button)
        }, Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.share))
        }
    }
}


fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}