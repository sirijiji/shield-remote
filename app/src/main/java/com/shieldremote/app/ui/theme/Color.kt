package com.shieldremote.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * Swiss / kiosk palette (Digital Signage row of the ui-ux-pro-max colour catalogue).
 *
 * Near-black slate canvas, one grey for structure, one white for the "ink" that fills the keys you
 * press, and exactly one accent — red — reserved for power and for failure. There is no second hue
 * anywhere: if a surface is not white, grey or slate, it is a signal.
 */

val BrandDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFF8FAFC),
    onPrimary = Color(0xFF020617),
    primaryContainer = Color(0xFF1A1E2F),
    onPrimaryContainer = Color(0xFFF8FAFC),
    inversePrimary = Color(0xFF334155),
    secondary = Color(0xFF334155),
    onSecondary = Color(0xFFF8FAFC),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFFEF4444),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF3B1114),
    onTertiaryContainer = Color(0xFFFECACA),
    error = Color(0xFFEF4444),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF3B1114),
    onErrorContainer = Color(0xFFFECACA),
    background = Color(0xFF020617),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF020617),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1A1E2F),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF020617),
    surfaceDim = Color(0xFF020617),
    surfaceBright = Color(0xFF1E293B),
    surfaceContainerLowest = Color(0xFF01030A),
    surfaceContainerLow = Color(0xFF0A0E1C),
    surfaceContainer = Color(0xFF0E1223),
    surfaceContainerHigh = Color(0xFF141A2D),
    surfaceContainerHighest = Color(0xFF1A1E2F)
)

val BrandLightScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF020617),
    onPrimary = Color(0xFFF8FAFC),
    primaryContainer = Color(0xFFE8ECF1),
    onPrimaryContainer = Color(0xFF020617),
    inversePrimary = Color(0xFFCBD5E1),
    secondary = Color(0xFF475569),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8ECF1),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFFDC2626),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFEE2E2),
    onTertiaryContainer = Color(0xFF7F1D1D),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF020617),
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF020617),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFE2E8F0),
    inverseSurface = Color(0xFF020617),
    inverseOnSurface = Color(0xFFF8FAFC),
    surfaceDim = Color(0xFFE2E8F0),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F5F9),
    surfaceContainer = Color(0xFFE8ECF1),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1)
)

/** Swaps the dark surfaces for true black, keeping the white ink and red accent. */
fun ColorScheme.asPureBlack(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceDim = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF06070D),
    surfaceContainer = Color(0xFF0A0C16),
    surfaceContainerHigh = Color(0xFF10131F),
    surfaceContainerHighest = Color(0xFF161A28),
    outlineVariant = Color(0xFF232838)
)

/** Status colours Material doesn't define. "Success" is simply full-strength ink. */
data class StatusColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color
)

val DarkStatusColors = StatusColors(
    success = Color(0xFFF8FAFC),
    onSuccess = Color(0xFF020617),
    warning = Color(0xFF94A3B8)
)

val LightStatusColors = StatusColors(
    success = Color(0xFF020617),
    onSuccess = Color(0xFFF8FAFC),
    warning = Color(0xFF475569)
)

val LocalStatusColors = staticCompositionLocalOf { DarkStatusColors }
