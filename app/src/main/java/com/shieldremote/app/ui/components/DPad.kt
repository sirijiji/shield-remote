package com.shieldremote.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.KeyDirection
import com.shieldremote.app.protocol.RemoteKey
import com.shieldremote.app.ui.theme.Motion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * SHIELD's D-pad: a lit circle, an inner ring for OK, four chevrons at the cardinals and hairline
 * separators on the diagonals.
 */
@Composable
fun DPad(
    onKey: (RemoteKey, KeyDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme

    var pressed by remember { mutableStateOf<RemoteKey?>(null) }
    var okLong by remember { mutableStateOf(false) }
    val okDown = pressed == RemoteKey.DPAD_CENTER

    val okScale = animateFloatAsState(
        targetValue = when {
            okLong -> 0.90f
            okDown -> 0.94f
            else -> 1f
        },
        animationSpec = Motion.fastSpatial(),
        label = "okScale"
    )

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val diameter = minOf(maxWidth, maxHeight)
        val density = LocalDensity.current
        val okRadiusPx = with(density) { (diameter * 0.21f).toPx() }

        Box(
            modifier = Modifier
                .size(diameter)
                .background(scheme.surfaceContainerHighest, CircleShape)
                .semantics { contentDescription = "Directional pad" }
                .drawBehind {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val r = size.minDimension / 2f
                    val inner = r * 0.42f
                    val hair = scheme.outline

                    // Inner ring: the OK target.
                    drawCircle(
                        color = hair,
                        radius = inner,
                        center = c,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx())
                    )

                    // Four separators on the diagonals, from the inner ring to the edge.
                    listOf(45f, 135f, 225f, 315f).forEach { deg ->
                        val a = deg * PI.toFloat() / 180f
                        drawLine(
                            color = hair,
                            start = Offset(c.x + cos(a) * inner, c.y + sin(a) * inner),
                            end = Offset(c.x + cos(a) * r, c.y + sin(a) * r),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // OK press: the inner ring fills with ink.
                    if (okDown || okLong) {
                        drawCircle(
                            color = if (okLong) scheme.tertiary.copy(alpha = 0.35f) else scheme.onSurface.copy(alpha = 0.10f),
                            radius = inner * okScale.value,
                            center = c
                        )
                    }
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
                }
        ) {
            PadChevron(0, pressed == RemoteKey.DPAD_UP, Modifier.align(Alignment.TopCenter))
            PadChevron(1, pressed == RemoteKey.DPAD_DOWN, Modifier.align(Alignment.BottomCenter))
            PadChevron(2, pressed == RemoteKey.DPAD_LEFT, Modifier.align(Alignment.CenterStart))
            PadChevron(3, pressed == RemoteKey.DPAD_RIGHT, Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
private fun PadChevron(direction: Int, active: Boolean, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val tint by animateColorAsState(
        targetValue = if (active) scheme.tertiary else scheme.onSurface,
        animationSpec = Motion.effects(),
        label = "padChevron"
    )
    GlyphPadChevron(
        direction = direction,
        size = 26.dp,
        tint = tint,
        modifier = modifier
            .padding(28.dp)
            .graphicsLayer {
                val s = if (active) 1.12f else 1f
                scaleX = s
                scaleY = s
            }
    )
}
