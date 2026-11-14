package com.shieldremote.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.ConnectionStatus
import com.shieldremote.app.protocol.KeyDirection
import com.shieldremote.app.ui.theme.Motion
import com.shieldremote.app.ui.theme.RemoteTheme

/**
 * SHIELD's top bar: device switcher on the left, then mic, volume and power on the right. The
 * connected state is the only place green appears.
 */
@Composable
fun TopBar(
    status: ConnectionStatus,
    deviceName: String,
    hasTv: Boolean,
    isMuted: Boolean,
    onDevicePickerClick: () -> Unit,
    onPowerKey: (KeyDirection) -> Unit,
    onMuteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val connected = status is ConnectionStatus.Connected

    val dotColor by animateColorAsState(
        targetValue = when (status) {
            is ConnectionStatus.Connected -> RemoteTheme.status.success
            is ConnectionStatus.Connecting, is ConnectionStatus.NeedsPairing -> scheme.onSurfaceVariant
            is ConnectionStatus.Error -> scheme.error
            is ConnectionStatus.Disconnected -> scheme.outline
        },
        animationSpec = Motion.effects(),
        label = "dotColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(scheme.surfaceContainer)
            .padding(start = 16.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onDevicePickerClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlyphChevronDown(size = 14.dp, tint = scheme.onSurface)
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, MaterialTheme.shapes.extraSmall)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (hasTv) deviceName else "Select TV",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        BarKey(
            description = "Voice search",
            enabled = connected,
            tint = scheme.onSurface,
            onClick = onVoiceClick
        ) { tint ->
            Icon(Icons.Rounded.Mic, null, tint = tint, modifier = Modifier.size(20.dp))
        }
        BarKey(
            description = if (isMuted) "Unmute" else "Mute",
            enabled = connected,
            tint = if (isMuted) scheme.tertiary else scheme.onSurface,
            onClick = onMuteClick
        ) { tint ->
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(21.dp)
            )
        }
        IconSurface(
            icon = Icons.Rounded.MoreVert,
            description = "Settings",
            onClick = onSettingsClick,
            size = 44.dp,
            iconSize = 20.dp,
            color = Color.Transparent,
            contentColor = scheme.onSurfaceVariant
        )
        HoldableKey(
            icon = Icons.Rounded.PowerSettingsNew,
            description = "Power",
            longPressLabel = "Power menu",
            onKey = onPowerKey,
            size = 44.dp,
            iconSize = 20.dp,
            color = Color.Transparent,
            contentColor = if (connected) scheme.onSurface else scheme.outline,
            enabled = connected
        )
    }
}

/** Square-ish ghost key sized for a 56dp bar. */
@Composable
private fun BarKey(
    description: String,
    enabled: Boolean,
    tint: Color,
    onClick: () -> Unit,
    content: @Composable (Color) -> Unit
) {
    PressSurface(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        color = Color.Transparent,
        contentColor = tint,
        enabled = enabled,
        description = description
    ) {
        content(tint)
    }
}
