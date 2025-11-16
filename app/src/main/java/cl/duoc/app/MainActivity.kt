package cl.duoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cl.duoc.app.navigation.NavBar
import cl.duoc.app.ui.theme.DarkZoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            DarkZoneTheme {
                NavBar()
            }
        }
    }
}