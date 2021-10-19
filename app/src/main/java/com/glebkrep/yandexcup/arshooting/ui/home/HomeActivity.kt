package com.glebkrep.yandexcup.arshooting.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glebkrep.yandexcup.arshooting.ui.game.GameActivity
import com.glebkrep.yandexcup.arshooting.ui.home.pages.Screen
import com.glebkrep.yandexcup.arshooting.ui.home.pages.currentSession.CurrentSessionPage
import com.glebkrep.yandexcup.arshooting.ui.home.pages.home.HomePage
import com.glebkrep.yandexcup.arshooting.ui.home.pages.results.ResultsPage
import com.glebkrep.yandexcup.arshooting.ui.home.pages.results.ResultsPageVM
import com.glebkrep.yandexcup.arshooting.ui.home.pages.sessionList.SessionListPage
import com.glebkrep.yandexcup.arshooting.ui.theme.ArshootingTheme
import com.glebkrep.yandexcup.arshooting.utils.Debug

//В детстве все любили играть в "войнушку",
//главной проблемой которой было выяснить, кто в кого первый попал.
//Благодаря современным технологиям "войнушку" можно модифицировать.
//Вместо игрушечных пистолетов в нее можно играть телефонами:
//увидел товарища, направил на него телефон, ткнул в экран — кто успел первым,
//тот и молодец. Можно вести счет, устраивать чемпионаты и даже делиться результатами
//в соцсетях, чтобы все точно знали, кто выиграл.
//
//Вам предстоит написать приложение, позволяющее играть в "войнушку" "по сети".
//
//Приложение должно:
//- вести «сессию» на участников игры в войнушку.
// Приложение знает других участников, их координаты и другие параметры,
//- уметь «стрелять». «Выстрел» — это касание экрана в направлении одного из играющих.
// Приложение оценивает параметры и регистрирует попадание (можно с некоторым эпсилон),
//- вести статистику «живых» игроков, регистрировать победителя,
//- показывать на экране поток с камеры (AR). Для полного балла по задаче
// при попадании в поле зрения экрана надо показывать метку игрока (любую на усмотрение участника).
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var gameId = intent.extras?.getString("game_id")
        setContent {
            ArshootingTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val mainNavController = rememberNavController()
                    val viewModel: HomeActivityVM = viewModel()
                    NavHost(
                        navController = mainNavController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomePage(gameId = gameId,
                                createSession = {
                                    viewModel.setMyName(it)
                                    viewModel.createSession()
                                    mainNavController.navigate(Screen.CurrentSession.route)
                                },
                                sessionList = {
                                    viewModel.setMyName(it)
                                    viewModel.goToSessionList()
                                    mainNavController.navigate(Screen.SessionList.route)
                                },
                                toResults = {
                                    gameId = null
                                    mainNavController.navigate(
                                        Screen.Results.route.replace(
                                            "{sessionId}",
                                            it
                                        )
                                    )
                                }
                            )
                        }
                        composable(Screen.CurrentSession.route) {
                            CurrentSessionPage(viewModel) {
                                Debug.log("session id: ${it}")
                                this@HomeActivity.startActivity(
                                    Intent(
                                        this@HomeActivity,
                                        GameActivity::class.java
                                    ).apply {
                                        putExtra("SESSION_ID", it)
                                    }
                                )
                                finish()
                            }
                        }
                        composable(Screen.SessionList.route) {
                            SessionListPage(viewModel) {
                                viewModel.connectToSession(it)
                                mainNavController.navigate(Screen.CurrentSession.route)
                            }
                        }
                        composable(Screen.Results.route) {
                            val vm: ResultsPageVM = androidx.lifecycle.viewmodel.compose.viewModel()
                            val sessionId = it.arguments?.getString("sessionId")
                            Debug.log("sessionId: $sessionId")
                            vm.setSessionId(sessionId)
                            ResultsPage(vm)
                        }
                    }
                }
            }
        }
    }
}