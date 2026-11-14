package com.shieldremote.app.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring

/**
 * Transitions are 180–260ms and never overshoot. The Swiss style asks for subtle, quick state
 * changes; a controlled spring reads as mechanical and keeps the panel feeling like hardware.
 */
object Motion {
    fun <T> spatial(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 1200f)
    fun <T> fastSpatial(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 1800f)
    fun <T> slowSpatial(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 700f)
    fun <T> effects(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 2600f)
    fun <T> slowEffects(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 1400f)
}
