package com.shieldremote.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.shieldremote.app.ui.VoiceUi
import com.shieldremote.app.ui.theme.Motion

/**
 * Shown in place of the control deck while voice search runs. A square meter brackets your voice;
 * the TV shows what it heard.
 */
@Composable
fun VoicePanel(
    state: VoiceUi,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val listening = state is VoiceUi.Listening
    val level = (state as? VoiceUi.Listening)?.level ?: 0f
    val swell by animateFloatAsState(if (listening) level else 0f, Motion.fastSpatial(), label = "voiceLevel")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(scheme.surfaceContainerLow)
            .border(1.dp, scheme.outlineVariant)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
            when {
                listening -> {
                    val ink = scheme.tertiary
                    Canvas(modifier = Modifier.size(56.dp)) {
                        val amount = swell.coerceIn(0f, 1f)
                        val inset = size.minDimension * (0.34f - 0.22f * amount)
                        drawRect(
                            color = ink.copy(alpha = 0.18f + 0.22f * amount),
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - inset * 2, size.height - inset * 2),
                            style = Stroke(1.dp.toPx())
                        )
                    }
                    Icon(Icons.Rounded.Mic, null, tint = scheme.tertiary, modifier = Modifier.size(20.dp))
                }
                state is VoiceUi.WaitingForTv -> {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, scheme.outlineVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Mic, null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                }
                else -> CircularProgressIndicator(
                    color = scheme.onSurface,
                    trackColor = scheme.outlineVariant,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            val (title, body) = when (state) {
                is VoiceUi.Listening -> "Listening" to "Speak now. Results show on your TV."
                VoiceUi.WaitingForTv -> "Press a mic on the TV" to "No voice search here, but apps like YouTube can hear you."
                else -> "Starting voice search" to "Waiting for your TV"
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
        }

        IconSurface(
            icon = Icons.Rounded.Stop,
            description = "Stop listening",
            onClick = onStop,
            size = 44.dp,
            iconSize = 18.dp,
            contentColor = scheme.onSurface
        )
    }
}
