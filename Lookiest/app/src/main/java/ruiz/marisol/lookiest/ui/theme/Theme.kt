package ruiz.marisol.lookiest.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Azul50,
    secondary = Rosa,
    tertiary = Amarillo50,
    background = NegroFondo,
    surface = GrisSuperficie,
    onPrimary = Blanco,
    onSecondary = GrisElevado,
    onBackground = Blanco,
    onSurface = BlancoSuave,
    onTertiaryContainer = Negro
)

private val LightColorScheme = lightColorScheme(
    primary = Azul,
    secondary = Rosa,
    tertiary = Amarillo,
    background = BlancoFondo,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onBackground = Negro,
    onSurface = Negro,
    onTertiaryContainer = Negro
)

@Composable
fun LookiestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}