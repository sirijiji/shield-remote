package com.shieldremote.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Input
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shieldremote.app.protocol.RemoteKey
import com.shieldremote.app.ui.theme.Motion

private val NavHeight = 58.dp
private val VolumeHeight = 56.dp

/**
 * The control strips under the pad. Two ruled rows of equal-width cells, like a hardware panel:
 * navigation and typing on the first, volume — with a numeric readout — on the second.
 */
@Composable
fun ControlDeck(
    isMuted: Boolean,
    volumeLevel: Int,
    volumeMax: Int,
    enabled: Boolean,
    onKey: (RemoteKey) -> Unit,
    onKeyboardClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Rule()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(Modifier.weight(1f)) {
                Key(Icons.AutoMirrored.Rounded.ArrowBack, "Back", enabled) { onKey(RemoteKey.BACK) }
            }
            CellRule()
            Cell(Modifier.weight(1f)) {
                Key(Icons.Rounded.Home, "Home", enabled) { onKey(RemoteKey.HOME) }
            }
            CellRule()
            Cell(Modifier.weight(1f)) {
                PlayPauseKey(enabled) { onKey(RemoteKey.MEDIA_PLAY_PAUSE) }
            }
            CellRule()
            Cell(Modifier.weight(1f)) {
                Key(Icons.Rounded.Keyboard, "Type on TV", enabled, onClick = onKeyboardClick)
            }
            CellRule()
            Cell(Modifier.weight(1f)) {
                Key(Icons.AutoMirrored.Rounded.Input, "Input source", enabled) { onKey(RemoteKey.TV_INPUT) }
            }
            CellRule()
            Cell(Modifier.weight(1f)) {
                Key(
                    icon = Icons.Rounded.Mic,
                    description = "Voice search",
                    enabled = enabled,
                    tint = if (enabled) scheme.onSurface else scheme.onSurfaceVariant,
                    onClick = onVoiceClick
                )
            }
        }
        Rule()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(VolumeHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(Modifier.width(56.dp)) {
                Key(
                    icon = Icons.AutoMirrored.Rounded.VolumeOff,
                    description = if (isMuted) "Unmute" else "Mute",
                    enabled = enabled,
                    tint = if (isMuted) scheme.tertiary else scheme.onSurfaceVariant,
                    onClick = { onKey(RemoteKey.VOLUME_MUTE) }
                )
            }
            CellRule()
            Cell(Modifier.width(52.dp)) {
                RepeatKey(Icons.Rounded.Remove, "Volume down", enabled) { onKey(RemoteKey.VOLUME_DOWN) }
            }
            CellRule()
            VolumeReadout(
                isMuted = isMuted,
                level = volumeLevel,
                max = volumeMax,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            CellRule()
            Cell(Modifier.width(52.dp)) {
                RepeatKey(Icons.Rounded.Add, "Volume up", enabled) { onKey(RemoteKey.VOLUME_UP) }
            }
        }
    }
}

/** Play and pause share one cell: the TV toggles, so two keys would be a lie. */
@Composable
private fun PlayPauseKey(enabled: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    PressSurface(
        onClick = onClick,
        modifier = Modifier.size(46.dp),
        color = Color.Transparent,
        contentColor = scheme.onSurface,
        enabled = enabled,
        description = "Play or pause"
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.PlayArrow, null, modifier = Modifier.size(19.dp))
            Icon(Icons.Rounded.Pause, null, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun Cell(modifier: Modifier = Modifier, content: @Composable () -> Unit) {    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        content()
    }
}

/** Level as a number first, bar second — a readout, not a slider. */
@Composable
private fun VolumeReadout(
    isMuted: Boolean,
    level: Int,
    max: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val target = if (max > 0) (level.toFloat() / max).coerceIn(0f, 1f) else 0f
    val fraction by animateFloatAsState(target, Motion.spatial(), label = "volume")

    Row(
        modifier = modifier.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (isMuted) "—" else level.toString(),
            color = if (isMuted) scheme.tertiary else scheme.onSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "/$max",
            color = scheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(scheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (enabled) fraction else 0f)
                    .fillMaxHeight()
                    .background(if (isMuted) scheme.tertiary else scheme.onSurface)
            )
        }
    }
}

@Composable
private fun Key(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    IconSurface(
        icon = icon,
        description = description,
        onClick = onClick,
        size = 46.dp,
        iconSize = 19.dp,
        color = Color.Transparent,
        contentColor = tint,
        enabled = enabled
    )
}

/** Fires once on touch-down, then repeats while held. Used for volume. */
@Composable
private fun RepeatKey(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    var pressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(46.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .pressMorph(pressed, pressedScale = 0.96f)
            .background(if (pressed) scheme.surfaceContainerHigh else Color.Transparent)
            .semantics {
                contentDescription = description
                role = Role.Button
                if (enabled) onClick { haptic(HapticFeedbackType.TextHandleMove); onClick(); true }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    val job = scope.launchKeyRepeat(intervalMs = 130L, fireFirst = true) {
                        haptic(HapticFeedbackType.TextHandleMove)
                        onClick()
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
                    } finally {
                        job.cancel()
                        pressed = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = scheme.onSurface, modifier = Modifier.size(19.dp))
    }
}
