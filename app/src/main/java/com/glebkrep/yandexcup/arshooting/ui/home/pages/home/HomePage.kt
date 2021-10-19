package com.glebkrep.yandexcup.arshooting.ui.home.pages.home

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glebkrep.yandexcup.arshooting.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePage(
    gameId: String?,
    createSession: (String) -> (Unit),
    sessionList: (String) -> (Unit),
    toResults: (String) -> (Unit)
) {
    if (gameId != null) {
        toResults.invoke(gameId)
        return
    }
    val cameraAndLocationPermissionState =
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

    val withPadding = Modifier.padding(16.dp)

    var name by remember {
        mutableStateOf("")
    }
    var nameError by remember {
        mutableStateOf("")
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.ar_voinuwka), withPadding)
        PermissionsRequired(
            multiplePermissionsState = cameraAndLocationPermissionState,
            permissionsNotGrantedContent = {
                Text(
                    stringResource(R.string.permission_reasoning),
                    withPadding,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { cameraAndLocationPermissionState.launchMultiplePermissionRequest() },
                    withPadding
                ) {
                    Text(stringResource(R.string.provide_permission))
                }
            },
            permissionsNotAvailableContent = {
                Text(
                    stringResource(R.string.no_permission),
                    withPadding, textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
        ) {
            TextField(value = name, onValueChange = {
                name = it
                nameError = ""
            }, label = {
                Text(text = stringResource(R.string.your_name))
            }, modifier = withPadding)
            if (nameError != "") {
                Text(text = nameError, color = Color.Red, modifier = withPadding)
            }
            Button(onClick = {
                checkName(name = name, nameOk = {
                    createSession.invoke(it)
                },
                    error = {
                        nameError = it
                    })
            }, withPadding) {
                Text(text = stringResource(R.string.create_session))
            }
            Button(onClick = {
                checkName(name = name, nameOk = {
                    sessionList.invoke(it)
                },
                    error = {
                        nameError = it
                    })
            }, withPadding) {
                Text(text = stringResource(id = R.string.session_list))
            }
        }

    }


}

fun checkName(name: String, nameOk: (String) -> (Unit), error: (String) -> (Unit)) {
    if (name == "") {
        error.invoke("Имя не должно быть пустым")
    } else
        nameOk.invoke(name)
}