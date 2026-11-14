package com.shieldremote.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * NVIDIA SHIELD remote palette, sampled from the official app's screens: a stack of neutral
 * near-black greys (#141414 → #2B2B2B), white line icons, hairline dividers, and NVIDIA green
 * (#76B900) used only as the "live" signal — connected state and the active control mode.
 */

val BrandDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF76B900),
    onPrimary = Color(0xFF0C1600),
    primaryContainer = Color(0xFF243300),
    onPrimaryContainer = Color(0xFFC8F08A),
    inversePrimary = Color(0xFF4F7A00),
    secondary = Color(0xFFE5E5E5),
    onSecondary = Color(0xFF141414),
    secondaryContainer = Color(0xFF2B2B2B),
    onSecondaryContainer = Color(0xFFEDEDED),
    tertiary = Color(0xFF76B900),
    onTertiary = Color(0xFF0C1600),
    tertiaryContainer = Color(0xFF243300),
    onTertiaryContainer = Color(0xFFC8F08A),
    error = Color(0xFFE5484D),
    onError = Color(0xFF1A0505),
    errorContainer = Color(0xFF3A1214),
    onErrorContainer = Color(0xFFFFC9CA),
    background = Color(0xFF141414),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF2B2B2B),
    onSurfaceVariant = Color(0xFF9E9E9E),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2E2E2E),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF141414),
    surfaceDim = Color(0xFF0F0F0F),
    surfaceBright = Color(0xFF333333),
    surfaceContainerLowest = Color(0xFF0F0F0F),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF242424),
    surfaceContainerHighest = Color(0xFF2B2B2B)
)

val BrandLightScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF4F7A00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE4F3C8),
    onPrimaryContainer = Color(0xFF1B2A00),
    inversePrimary = Color(0xFF76B900),
    secondary = Color(0xFF3A3A3A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDEDED),
    onSecondaryContainer = Color(0xFF141414),
    tertiary = Color(0xFF4F7A00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE4F3C8),
    onTertiaryContainer = Color(0xFF1B2A00),
    error = Color(0xFFC62828),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = Color(0xFF5F1310),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF121212),
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF121212),
    surfaceVariant = Color(0xFFE7E7E7),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFFC2C2C2),
    outlineVariant = Color(0xFFDCDCDC),
    inverseSurface = Color(0xFF262626),
    inverseOnSurface = Color(0xFFF5F5F5),
    surfaceDim = Color(0xFFE0E0E0),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFEAEAEA),
    surfaceContainerHighest = Color(0xFFE2E2E2)
)

/** The SHIELD app is black, never "pure black with grey chrome" — this keeps the grey stack. */
fun ColorScheme.asPureBlack(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceDim = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF101010),
    surfaceContainerHigh = Color(0xFF181818),
    surfaceContainerHighest = Color(0xFF202020),
    outline = Color(0xFF2C2C2C),
    outlineVariant = Color(0xFF222222)
)

/** Status colours Material doesn't define. */
data class StatusColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color
)

val DarkStatusColors = StatusColors(
    success = Color(0xFF76B900),
    onSuccess = Color(0xFF0C1600),
    warning = Color(0xFFE5E5E5)
)

val LightStatusColors = StatusColors(
    success = Color(0xFF4F7A00),
    onSuccess = Color(0xFFFFFFFF),
    warning = Color(0xFF616161)
)

val LocalStatusColors = staticCompositionLocalOf { DarkStatusColors }
