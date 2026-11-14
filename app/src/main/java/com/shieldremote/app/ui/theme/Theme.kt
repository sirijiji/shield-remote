package com.shieldremote.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shieldremote.app.ui.ThemeMode

/**
 * SHIELD geometry: the trackpad carries a small 6dp radius, buttons stay squarish, and the D-pad is
 * the only true circle. Nothing is a pill.
 */
private val RemoteShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(12.dp)
)

/** True when wallpaper-based colours are available on this phone. */
val supportsDynamicColor: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun isDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun TVRemoteTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    pureBlack: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val dark = isDarkTheme(themeMode)
    val context = LocalContext.current

    // The grey stack and the single green signal are the identity; wallpaper colours would
    // replace both, so the toggle only applies when the user explicitly asks for it.
    var scheme = if (dark) BrandDarkScheme else BrandLightScheme
    if (dynamicColor && supportsDynamicColor) {
        scheme = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    if (dark && pureBlack) scheme = scheme.asPureBlack()

    CompositionLocalProvider(LocalStatusColors provides if (dark) DarkStatusColors else LightStatusColors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = RemoteTypography,
            shapes = RemoteShapes,
            content = content
        )
    }
}

object RemoteTheme {
    val status: StatusColors
        @Composable
        @ReadOnlyComposable
        get() = LocalStatusColors.current
}
