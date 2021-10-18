package com.glebkrep.yandexcup.arshooting

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glebkrep.yandexcup.arshooting.ui.Screen
import com.glebkrep.yandexcup.arshooting.ui.pages.home.HomePage
import com.glebkrep.yandexcup.arshooting.ui.theme.ArshootingTheme
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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArshootingTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val mainNavController = rememberNavController()
                    NavHost(
                        navController = mainNavController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) { HomePage(mainNavController){
                            startActivity(Intent(this@MainActivity,GameActivity::class.java))
                        } }

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArshootingTheme {
        Greeting("Android")
    }
}