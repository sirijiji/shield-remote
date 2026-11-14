package com.shieldremote.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.KeyDirection
import com.shieldremote.app.protocol.RemoteKey
import com.shieldremote.app.ui.theme.Motion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Swipe mode: one square surface. Swipe to move (one step per ~76dp of travel, hold still to
 * repeat slowly), tap to press OK, press and hold to long-press OK.
 *
 * Feedback is structural: a hairline crosshair, a bracketed contact square, and a rule that expands
 * from the tap point. Nothing decorative.
 */
@Composable
fun SwipePad(
    onKey: (RemoteKey, KeyDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val density = androidx.compose.ui.platform.LocalDensity.current
    val stepPx = with(density) { 76.dp.toPx() }

    var touchPos by remember { mutableStateOf(Offset.Zero) }
    var touching by remember { mutableStateOf(false) }
    var longPressed by remember { mutableStateOf(false) }
    var hasInteracted by rememberSaveable { mutableStateOf(false) }

    var flashDir by remember { mutableStateOf<RemoteKey?>(null) }
    val flash = remember { Animatable(0f) }
    var tapPos by remember { mutableStateOf(Offset.Zero) }
    val tapPulse = remember { Animatable(1f) }

    val presence = animateFloatAsState(
        targetValue = if (touching) 1f else 0f,
        animationSpec = if (touching) Motion.fastSpatial() else Motion.slowEffects(),
        label = "presence"
    )
    val hold = animateFloatAsState(if (longPressed) 1f else 0f, Motion.spatial(), label = "hold")

    val scheme = MaterialTheme.colorScheme
    val hair = scheme.onSurface
    val accent = scheme.tertiary

    Box(
        modifier = modifier
            .background(scheme.surfaceContainerLow)
            .border(1.dp, scheme.outlineVariant)
            .semantics { contentDescription = "Swipe pad. Swipe to navigate, tap to select, hold to long press." }
            .drawBehind {
                val p = presence.value.coerceIn(0f, 1f)
                val h = hold.value

                // Resting reticle: two ticks marking the centre of the panel.
                if (hasInteracted) {
                    val middle = Offset(size.width / 2f, size.height / 2f)
                    val tick = 8.dp.toPx()
                    val rest = hair.copy(alpha = 0.22f)
                    drawLine(rest, Offset(middle.x - tick, middle.y), Offset(middle.x + tick, middle.y), 1.dp.toPx())
                    drawLine(rest, Offset(middle.x, middle.y - tick), Offset(middle.x, middle.y + tick), 1.dp.toPx())
                }

                if (p > 0.01f) {
                    // Full-height and full-width crosshair.
                    drawLine(
                        color = hair.copy(alpha = 0.14f * p),
                        start = Offset(touchPos.x, 0f),
                        end = Offset(touchPos.x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = hair.copy(alpha = 0.14f * p),
                        start = Offset(0f, touchPos.y),
                        end = Offset(size.width, touchPos.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    // Contact square: corners only, opening as a long-press arms.
                    val half = (14.dp.toPx()) * (1f + 0.4f * h)
                    val arm = half * 0.42f
                    val stroke = 1.dp.toPx()
                    val tone = lerp(hair.copy(alpha = 0.55f), accent, h)
                    val l = touchPos.x - half
                    val r = touchPos.x + half
                    val t = touchPos.y - half
                    val b = touchPos.y + half
                    // Top-left
                    drawLine(tone, Offset(l, t), Offset(l + arm, t), stroke)
                    drawLine(tone, Offset(l, t), Offset(l, t + arm), stroke)
                    // Top-right
                    drawLine(tone, Offset(r - arm, t), Offset(r, t), stroke)
                    drawLine(tone, Offset(r, t), Offset(r, t + arm), stroke)
                    // Bottom-left
                    drawLine(tone, Offset(l, b - arm), Offset(l, b), stroke)
                    drawLine(tone, Offset(l, b), Offset(l + arm, b), stroke)
                    // Bottom-right
                    drawLine(tone, Offset(r - arm, b), Offset(r, b), stroke)
                    drawLine(tone, Offset(r, b - arm), Offset(r, b), stroke)
                }

                // A single expanding square acknowledges a tap.
                val pulse = tapPulse.value
                if (pulse < 1f) {
                    val grow = 10.dp.toPx() + 52.dp.toPx() * pulse
                    drawRect(
                        color = hair.copy(alpha = (1f - pulse) * 0.45f),
                        topLeft = Offset(tapPos.x - grow, tapPos.y - grow),
                        size = Size(grow * 2, grow * 2),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
            .pointerInput(stepPx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    hasInteracted = true
                    touchPos = down.position
                    touching = true
                    var anchor = down.position
                    var moved = false
                    var repeatJob: Job? = null
                    var lastDir: RemoteKey? = null
                    var longActive = false
                    val slop = viewConfiguration.touchSlop

                    fun step(dir: RemoteKey) {
                        haptic(HapticFeedbackType.TextHandleMove)
                        onKey(dir, KeyDirection.SHORT)
                        flashDir = dir
                        scope.launch {
                            flash.snapTo(1f)
                            flash.animateTo(0f, tween(420))
                        }
                    }

                    val longJob = scope.launch {
                        delay(400L)
                        if (!moved) {
                            longActive = true
                            longPressed = true
                            haptic(HapticFeedbackType.LongPress)
                            onKey(RemoteKey.DPAD_CENTER, KeyDirection.START_LONG)
                        }
                    }

                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                change.consume()
                                break
                            }
                            touchPos = change.position
                            val dx = change.position.x - anchor.x
                            val dy = change.position.y - anchor.y

                            if (!moved && !longActive && SwipeMath.exceeds(dx, dy, slop)) {
                                moved = true
                                longJob.cancel()
                            }
                            if (!moved) continue
                            change.consume()

                            // Finger drifting a different way than the held repeat: stop repeating.
                            if (repeatJob != null &&
                                SwipeMath.exceeds(dx, dy, slop * 2) &&
                                SwipeMath.direction(dx, dy) != lastDir
                            ) {
                                repeatJob.cancel()
                                repeatJob = null
                            }

                            if (SwipeMath.exceeds(dx, dy, stepPx)) {
                                val dir = SwipeMath.direction(dx, dy)
                                lastDir = dir
                                anchor = change.position
                                step(dir)
                                // Holding still after a swipe keeps stepping in that direction.
                                repeatJob?.cancel()
                                repeatJob = scope.launchKeyRepeat(initialDelayMs = 550L, intervalMs = 300L) {
                                    step(dir)
                                }
                            }
                        }

                        longJob.cancel()
                        if (longActive) {
                            longActive = false
                            onKey(RemoteKey.DPAD_CENTER, KeyDirection.END_LONG)
                        } else if (!moved) {
                            haptic(HapticFeedbackType.LongPress)
                            onKey(RemoteKey.DPAD_CENTER, KeyDirection.SHORT)
                            tapPos = touchPos
                            scope.launch {
                                tapPulse.snapTo(0f)
                                tapPulse.animateTo(1f, tween(520))
                            }
                        }
                    } finally {
                        longJob.cancel()
                        repeatJob?.cancel()
                        if (longActive) {
                            // Gesture cancelled mid long-press: still release the TV key.
                            onKey(RemoteKey.DPAD_CENTER, KeyDirection.END_LONG)
                        }
                        longPressed = false
                        touching = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        EdgeChevron(Icons.Rounded.KeyboardArrowUp, RemoteKey.DPAD_UP, flashDir, { flash.value }, Modifier.align(Alignment.TopCenter))
        EdgeChevron(Icons.Rounded.KeyboardArrowDown, RemoteKey.DPAD_DOWN, flashDir, { flash.value }, Modifier.align(Alignment.BottomCenter))
        EdgeChevron(Icons.Rounded.KeyboardArrowLeft, RemoteKey.DPAD_LEFT, flashDir, { flash.value }, Modifier.align(Alignment.CenterStart))
        EdgeChevron(Icons.Rounded.KeyboardArrowRight, RemoteKey.DPAD_RIGHT, flashDir, { flash.value }, Modifier.align(Alignment.CenterEnd))

        AnimatedVisibility(visible = !hasInteracted, enter = fadeIn(), exit = fadeOut()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "SWIPE TO NAVIGATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    text = "TAP TO SELECT",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/** Faint arrow on each edge; the one matching the last step goes to full ink. */
@Composable
private fun EdgeChevron(
    icon: ImageVector,
    key: RemoteKey,
    flashDir: RemoteKey?,
    flash: () -> Float,
    modifier: Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val active = flashDir == key
    val rest = scheme.onSurfaceVariant.copy(alpha = 0.45f)
    val kick = with(androidx.compose.ui.platform.LocalDensity.current) { 5.dp.toPx() }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (active) lerp(rest, scheme.onSurface, flash().coerceIn(0f, 1f)) else rest,
        modifier = modifier
            .padding(12.dp)
            .size(24.dp)
            .graphicsLayer {
                if (active) {
                    val f = flash()
                    when (key) {
                        RemoteKey.DPAD_UP -> translationY = -kick * f
                        RemoteKey.DPAD_DOWN -> translationY = kick * f
                        RemoteKey.DPAD_LEFT -> translationX = -kick * f
                        else -> translationX = kick * f
                    }
                }
            }
    )
}
