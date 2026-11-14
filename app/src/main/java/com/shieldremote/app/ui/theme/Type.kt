package com.shieldremote.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shieldremote.app.R

/**
 * Inter, one family, weight and tracking do all the work (ui-ux-pro-max "Minimal Swiss" pairing).
 * Large type is Light; structure is small Medium caps with 1.4–1.6sp of tracking.
 */
val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

private val Base = Typography()

val RemoteTypography = Typography(
    displaySmall = Base.displaySmall.copy(
        fontFamily = Inter, fontWeight = FontWeight.Light, letterSpacing = (-1.4).sp
    ),
    headlineLarge = Base.headlineLarge.copy(
        fontFamily = Inter, fontWeight = FontWeight.Light, letterSpacing = (-1.0).sp
    ),
    headlineMedium = Base.headlineMedium.copy(
        fontFamily = Inter, fontWeight = FontWeight.Light, letterSpacing = (-0.8).sp
    ),
    headlineSmall = Base.headlineSmall.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = (-0.4).sp
    ),
    titleLarge = Base.titleLarge.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = (-0.2).sp
    ),
    titleMedium = Base.titleMedium.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = (-0.1).sp
    ),
    titleSmall = Base.titleSmall.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = 0.sp
    ),
    bodyLarge = Base.bodyLarge.copy(fontFamily = Inter, letterSpacing = 0.1.sp),
    bodyMedium = Base.bodyMedium.copy(fontFamily = Inter, letterSpacing = 0.15.sp),
    bodySmall = Base.bodySmall.copy(fontFamily = Inter, letterSpacing = 0.15.sp),
    labelLarge = Base.labelLarge.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp
    ),
    labelMedium = Base.labelMedium.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = 1.2.sp
    ),
    labelSmall = Base.labelSmall.copy(
        fontFamily = Inter, fontWeight = FontWeight.Medium, letterSpacing = 1.6.sp
    )
)
