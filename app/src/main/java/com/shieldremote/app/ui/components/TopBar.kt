package com.shieldremote.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
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
 * Header: a status square, the device name, and two keys, closed by a 1dp rule. No card, no
 * rounded container, no elevation.
 */
@Composable
fun TopBar(
    status: ConnectionStatus,
    deviceName: String,
    hasTv: Boolean,
    onDevicePickerClick: () -> Unit,
    onPowerKey: (KeyDirection) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val connected = status is ConnectionStatus.Connected

    val statusLabel = when (status) {
        is ConnectionStatus.Connected -> "Connected"
        is ConnectionStatus.Connecting -> "Connecting"
        is ConnectionStatus.NeedsPairing -> "Pairing"
        is ConnectionStatus.Error -> "No connection"
        is ConnectionStatus.Disconnected -> if (hasTv) "Offline" else "No TV"
    }

    val squareColor by animateColorAsState(
        targetValue = when (status) {
            is ConnectionStatus.Connected -> RemoteTheme.status.success
            is ConnectionStatus.Connecting, is ConnectionStatus.NeedsPairing -> RemoteTheme.status.warning
            is ConnectionStatus.Error -> scheme.error
            is ConnectionStatus.Disconnected -> scheme.outline
        },
        animationSpec = Motion.effects(),
        label = "squareColor"
    )
    val labelColor by animateColorAsState(
        targetValue = when (status) {
            is ConnectionStatus.Error -> scheme.error
            else -> scheme.onSurfaceVariant
        },
        animationSpec = Motion.effects(),
        label = "labelColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(role = Role.Button, onClick = onDevicePickerClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(squareColor)
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (hasTv) deviceName else "No TV selected",
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = statusLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                        maxLines = 1
                    )
                }
            }

            HoldableKey(
                icon = Icons.Rounded.PowerSettingsNew,
                description = "Power",
                longPressLabel = "Power menu",
                onKey = onPowerKey,
                size = 44.dp,
                iconSize = 19.dp,
                color = Color.Transparent,
                contentColor = if (connected) scheme.tertiary else scheme.outline,
                enabled = connected
            )
            IconSurface(
                icon = Icons.Rounded.Settings,
                description = "Settings",
                onClick = onSettingsClick,
                size = 44.dp,
                iconSize = 19.dp,
                contentColor = scheme.onSurfaceVariant
            )
        }
        Rule()
    }
}
