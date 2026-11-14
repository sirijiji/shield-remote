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
 * Swiss style: no corner radius anywhere. Panels, keys, sheets and cells are all rectangles, and
 * structure comes from 1dp rules rather than rounding.
 */
private val RemoteShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
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

    // A single-accent palette cannot survive wallpaper sampling, so dynamic colour is ignored
    // when it would introduce a second hue.
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
