package com.glebkrep.yandexcup.arshooting.ui.home.pages


sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object CurrentSession : Screen("Current Session")
    object SessionList : Screen("Session List")
    object Results : Screen("Result/{sessionId}")

}