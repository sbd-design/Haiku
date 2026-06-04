package com.haiku.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = PaperWhite,
    onPrimary = InkBlack,
    primaryContainer = SurfaceDark,
    onPrimaryContainer = PaperWhite,
    background = InkBlack,
    onBackground = PaperWhite,
    surface = SurfaceDark,
    onSurface = PaperWhite,
    surfaceVariant = SurfaceDarker,
    onSurfaceVariant = BrushGray,
    secondary = AccentRed,
    onSecondary = PaperWhite
)

private val LightColors = lightColorScheme(
    primary = InkBlack,
    onPrimary = PaperWhite,
    primaryContainer = PaperCream,
    onPrimaryContainer = InkBlack,
    background = PaperWhite,
    onBackground = InkBlack,
    surface = PaperCream,
    onSurface = InkBlack,
    surfaceVariant = PaperCream,
    onSurfaceVariant = FadedInk,
    secondary = AccentRed,
    onSecondary = PaperWhite
)

@Composable
fun HaikuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HaikuTypography,
        content = content
    )
}
