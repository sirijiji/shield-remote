package com.shieldremote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.ConnectionStatus

/**
 * Takes the pad's place whenever the remote can't control a TV. Editorial rather than illustrative:
 * a small uppercase state label, a large light headline, body copy, a rule, then the actions.
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

    val eyebrow: String
    val title: String
    val body: String
    when (status) {
        is ConnectionStatus.Connecting -> {
            eyebrow = "Connecting"
            title = "Looking for\nthe TV"
            body = "$deviceName should answer on your Wi-Fi within a second or two."
        }
        is ConnectionStatus.NeedsPairing -> {
            eyebrow = "Pairing"
            title = "Check the TV\nfor a code"
            body = "Six characters are on the TV screen. Enter them here to pair this phone."
        }
        is ConnectionStatus.Error -> {
            eyebrow = "No connection"
            title = "Can't reach\n$deviceName"
            body = "It may be off, busy with another remote, or no longer paired with this phone."
        }
        else -> if (hasTv) {
            eyebrow = "Offline"
            title = "Not connected"
            body = "$deviceName is off the network right now."
        } else {
            eyebrow = if (isScanning) "Scanning" else "Setup"
            title = "No TV yet"
            body = if (isScanning) "Looking for Android TVs on this Wi-Fi…"
            else "Put the phone and the TV on the same Wi-Fi, then choose the TV from the list."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) scheme.error else scheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onSurface
        )
        if (busy) {
            Spacer(Modifier.height(18.dp))
            LinearProgressIndicator(
                color = scheme.onSurface,
                trackColor = scheme.outlineVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 320.dp)
        )

        Spacer(Modifier.height(28.dp))
        Rule()
        Spacer(Modifier.height(20.dp))

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
                ExpressiveButton("Choose TV", onChooseTv, icon = Icons.Rounded.Search, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
