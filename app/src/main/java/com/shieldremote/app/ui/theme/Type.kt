package com.shieldremote.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
 * The SHIELD app uses the platform face (Roboto) at calm sizes with almost no weight variation:
 * regular for labels and body, medium only for the device name. Tracking is slight and positive.
 */

private val Base = Typography()

val RemoteTypography = Typography(
    displaySmall = Base.displaySmall.copy(fontWeight = FontWeight.Light, letterSpacing = (-0.5).sp),
    headlineLarge = Base.headlineLarge.copy(fontWeight = FontWeight.Normal, letterSpacing = (-0.25).sp),
    headlineMedium = Base.headlineMedium.copy(fontWeight = FontWeight.Normal, letterSpacing = (-0.25).sp),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Normal, letterSpacing = (-0.1).sp),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Normal, letterSpacing = 0.1.sp),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.1.sp),
    bodyLarge = Base.bodyLarge.copy(letterSpacing = 0.15.sp),
    bodyMedium = Base.bodyMedium.copy(letterSpacing = 0.15.sp),
    bodySmall = Base.bodySmall.copy(letterSpacing = 0.2.sp),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp),
    labelMedium = Base.labelMedium.copy(fontWeight = FontWeight.Normal, letterSpacing = 0.4.sp),
    labelSmall = Base.labelSmall.copy(fontWeight = FontWeight.Normal, letterSpacing = 0.5.sp)
)
