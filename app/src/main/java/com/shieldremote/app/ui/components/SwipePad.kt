package com.shieldremote.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * SHIELD's trackpad: one large slab, a 1dp divider near the bottom and a caret in the click strip.
 * Swipe to move (~76dp per step), tap to select, hold for a long press.
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
    val ink = scheme.onSurface

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .background(scheme.surfaceContainerHighest)
                .semantics { contentDescription = "Trackpad. Swipe to navigate, tap to select, hold to long press." }
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val stripHeight = 44.dp.toPx()

                    // Click strip: a hairline and a caret, exactly as the SHIELD app draws it.
                    val stripY = h - stripHeight
                    drawLine(
                        color = scheme.outline,
                        start = Offset(14.dp.toPx(), stripY),
                        end = Offset(w - 14.dp.toPx(), stripY),
                        strokeWidth = 1.dp.toPx()
                    )
                    val caretH = 14.dp.toPx()
                    drawLine(
                        color = ink.copy(alpha = 0.55f),
                        start = Offset(w / 2f, stripY + (stripHeight - caretH) / 2f),
                        end = Offset(w / 2f, stripY + (stripHeight + caretH) / 2f),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    val p = presence.value.coerceIn(0f, 1f)
                    if (p > 0.01f) {
                        // Position, shown as a hairline crosshair rather than a glowing dot.
                        drawLine(
                            color = ink.copy(alpha = 0.10f * p),
                            start = Offset(touchPos.x, 0f),
                            end = Offset(touchPos.x, stripY),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = ink.copy(alpha = 0.10f * p),
                            start = Offset(0f, touchPos.y),
                            end = Offset(w, touchPos.y),
                            strokeWidth = 1.dp.toPx()
                        )
                        // The edge you are pushing against lights up while you swipe.
                        val f = flash.value
                        val dir = flashDir
                        if (f > 0.01f && dir != null) {
                            val len = 84.dp.toPx()
                            val inset = 10.dp.toPx()
                            val stroke = 2.dp.toPx()
                            val alpha = 0.55f * f
                            when (dir) {
                                RemoteKey.DPAD_UP -> drawLine(
                                    ink.copy(alpha = alpha),
                                    Offset(w / 2f - len / 2f, inset),
                                    Offset(w / 2f + len / 2f, inset),
                                    stroke
                                )
                                RemoteKey.DPAD_DOWN -> drawLine(
                                    ink.copy(alpha = alpha),
                                    Offset(w / 2f - len / 2f, stripY - inset),
                                    Offset(w / 2f + len / 2f, stripY - inset),
                                    stroke
                                )
                                RemoteKey.DPAD_LEFT -> drawLine(
                                    ink.copy(alpha = alpha),
                                    Offset(inset, h / 2f - len / 2f),
                                    Offset(inset, h / 2f + len / 2f),
                                    stroke
                                )
                                else -> drawLine(
                                    ink.copy(alpha = alpha),
                                    Offset(w - inset, h / 2f - len / 2f),
                                    Offset(w - inset, h / 2f + len / 2f),
                                    stroke
                                )
                            }
                        }
                    }

                    val pulse = tapPulse.value
                    if (pulse < 1f) {
                        val r = 10.dp.toPx() + 34.dp.toPx() * pulse
                        drawCircle(
                            color = ink.copy(alpha = (1f - pulse) * 0.35f),
                            radius = r,
                            center = tapPos,
                            style = Stroke(1.dp.toPx())
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
                                flash.animateTo(0f, tween(320))
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
                                    tapPulse.animateTo(1f, tween(420))
                                }
                            }
                        } finally {
                            longJob.cancel()
                            repeatJob?.cancel()
                            if (longActive) onKey(RemoteKey.DPAD_CENTER, KeyDirection.END_LONG)
                            longPressed = false
                            touching = false
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(visible = !hasInteracted, enter = fadeIn(), exit = fadeOut()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Swipe to navigate",
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap to select",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
