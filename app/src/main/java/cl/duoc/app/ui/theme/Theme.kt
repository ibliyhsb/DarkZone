package cl.duoc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Red,
    background = Black,
    surface = Black,
    onPrimary = Black,
    onBackground = Red,
    onSurface = Red,
    outline = DarkRed
)

@Composable
fun DarkZoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
