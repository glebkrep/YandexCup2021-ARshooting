package com.glebkrep.yandexcup.arshooting.ui


sealed class Screen(val route: String) {
    object Home : Screen("Home")

}