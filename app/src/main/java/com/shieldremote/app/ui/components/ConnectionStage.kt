package com.shieldremote.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.ConnectionStatus

/**
 * Shown instead of the pad when the remote cannot drive a TV. Same visual language as the SHIELD
 * app: flat, centred, one green action.
 */
@Composable
fun ConnectionStage(
    status: ConnectionStatus,
    deviceName: String,
    hasTv: Boolean,
    isScanning: Boolean,
    onRetry: () -> Unit,
    onChooseTv: () -> Unit,
    onRePair: () -> Unit,
    onEnterCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val isError = status is ConnectionStatus.Error
    val busy = status is ConnectionStatus.Connecting || (status !is ConnectionStatus.Error && isScanning)

    val title: String
    val body: String
    when (status) {
        is ConnectionStatus.Connecting -> {
            title = "Connecting to $deviceName"
            body = "Waiting for the TV to answer on your Wi-Fi."
        }
        is ConnectionStatus.NeedsPairing -> {
            title = "Enter the code"
            body = "A 6-character code is on the TV screen. Type it here to pair this phone."
        }
        is ConnectionStatus.Error -> {
            title = "Can't reach $deviceName"
            body = "It may be off, busy with another remote, or no longer paired with this phone."
        }
        else -> if (hasTv) {
            title = "Not connected"
            body = "$deviceName is off the network right now."
        } else {
            title = "No TV selected"
            body = if (isScanning) "Looking for Android TVs on this Wi-Fi…"
            else "Put the phone and the TV on the same Wi-Fi, then choose the TV."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isError) Icons.Rounded.Tv else Icons.Rounded.Tv,
            contentDescription = null,
            tint = if (isError) scheme.error else scheme.onSurfaceVariant,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = scheme.onSurface
        )
        if (busy) {
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                color = scheme.primary,
                trackColor = scheme.outlineVariant,
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .height(2.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 300.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        when (status) {
            is ConnectionStatus.Connecting ->
                ExpressiveButton("Choose another TV", onChooseTv, tone = ButtonTone.Tonal, modifier = Modifier.fillMaxWidth())

            is ConnectionStatus.NeedsPairing -> {
                ExpressiveButton("Enter code", onEnterCode, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ExpressiveButton("Choose another TV", onChooseTv, tone = ButtonTone.Tonal, modifier = Modifier.fillMaxWidth())
            }

            is ConnectionStatus.Error -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ExpressiveButton("Try again", onRetry, icon = Icons.Rounded.Refresh, modifier = Modifier.weight(1.2f))
                    ExpressiveButton("Pair again", onRePair, tone = ButtonTone.Tonal, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                ExpressiveButton("Choose another TV", onChooseTv, tone = ButtonTone.Plain, modifier = Modifier.fillMaxWidth())
            }

            else -> if (hasTv) {
                ExpressiveButton("Connect", onRetry, icon = Icons.Rounded.Refresh, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ExpressiveButton("Choose another TV", onChooseTv, tone = ButtonTone.Plain, modifier = Modifier.fillMaxWidth())
            } else {
                ExpressiveButton("Choose TV", onChooseTv, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
