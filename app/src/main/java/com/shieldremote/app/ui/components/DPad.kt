package com.shieldremote.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shieldremote.app.protocol.KeyDirection
import com.shieldremote.app.protocol.RemoteKey
import com.shieldremote.app.ui.theme.Motion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val OkSize = 96.dp

/**
 * D-pad mode: a square panel divided by a 1dp cross, with a solid ink square for OK. Tap an arrow to
 * step, hold it to repeat. Tap OK to select, hold OK to long-press.
 */
@Composable
fun DPad(
    onKey: (RemoteKey, KeyDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val okRadiusPx = with(LocalDensity.current) { (OkSize / 2).toPx() }

    var pressed by remember { mutableStateOf<RemoteKey?>(null) }
    var okLong by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme
    val okDown = pressed == RemoteKey.DPAD_CENTER

    val okScale = animateFloatAsState(
        targetValue = when {
            okLong -> 0.94f
            okDown -> 0.97f
            else -> 1f
        },
        animationSpec = Motion.fastSpatial(),
        label = "okScale"
    )

    Box(
        modifier = modifier
            .background(scheme.surfaceContainerLow)
            .border(1.dp, scheme.outlineVariant)
            .semantics { contentDescription = "Directional pad" }
            .drawBehind {
                val c = Offset(size.width / 2f, size.height / 2f)
                val stroke = 1.dp.toPx()
                drawLine(scheme.outlineVariant, Offset(0f, c.y), Offset(size.width, c.y), stroke)
                drawLine(scheme.outlineVariant, Offset(c.x, 0f), Offset(c.x, size.height), stroke)
            }
            .pointerInput(okRadiusPx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val dx = down.position.x - size.width / 2f
                    val dy = down.position.y - size.height / 2f
                    val zone = SwipeMath.dpadZone(dx, dy, okRadiusPx)
                    pressed = zone

                    var repeatJob: Job? = null
                    var longJob: Job? = null
                    var longActive = false

                    if (zone == RemoteKey.DPAD_CENTER) {
                        longJob = scope.launch {
                            delay(400L)
                            longActive = true
                            okLong = true
                            haptic(HapticFeedbackType.LongPress)
                            onKey(RemoteKey.DPAD_CENTER, KeyDirection.START_LONG)
                        }
                    } else {
                        repeatJob = scope.launchKeyRepeat(fireFirst = true) {
                            haptic(HapticFeedbackType.TextHandleMove)
                            onKey(zone, KeyDirection.SHORT)
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
                        }
                        longJob?.cancel()
                        if (zone == RemoteKey.DPAD_CENTER) {
                            if (longActive) {
                                longActive = false
                                onKey(RemoteKey.DPAD_CENTER, KeyDirection.END_LONG)
                            } else {
                                haptic(HapticFeedbackType.LongPress)
                                onKey(RemoteKey.DPAD_CENTER, KeyDirection.SHORT)
                            }
                        }
                    } finally {
                        longJob?.cancel()
                        repeatJob?.cancel()
                        if (longActive) onKey(RemoteKey.DPAD_CENTER, KeyDirection.END_LONG)
                        okLong = false
                        pressed = null
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Chevron(Icons.Rounded.KeyboardArrowUp, "Up", pressed == RemoteKey.DPAD_UP, Modifier.align(Alignment.TopCenter))
        Chevron(Icons.Rounded.KeyboardArrowDown, "Down", pressed == RemoteKey.DPAD_DOWN, Modifier.align(Alignment.BottomCenter))
        Chevron(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, "Left", pressed == RemoteKey.DPAD_LEFT, Modifier.align(Alignment.CenterStart))
        Chevron(Icons.AutoMirrored.Rounded.KeyboardArrowRight, "Right", pressed == RemoteKey.DPAD_RIGHT, Modifier.align(Alignment.CenterEnd))

        Box(
            modifier = Modifier
                .size(OkSize)
                .graphicsLayer {
                    scaleX = okScale.value
                    scaleY = okScale.value
                }
                .background(if (okLong) scheme.tertiary else scheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                color = if (okLong) scheme.onTertiary else scheme.onPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun Chevron(icon: ImageVector, description: String, active: Boolean, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val tint by animateColorAsState(
        targetValue = if (active) scheme.onSurface else scheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = Motion.effects(),
        label = "chevronTint"
    )
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = tint,
        modifier = modifier
            .padding(20.dp)
            .size(28.dp)
    )
}
