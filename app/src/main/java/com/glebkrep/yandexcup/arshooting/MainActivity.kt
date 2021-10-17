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