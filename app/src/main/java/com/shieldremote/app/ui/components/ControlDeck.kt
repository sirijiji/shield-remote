package com.shieldremote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shieldremote.app.protocol.RemoteKey

private val NavRowHeight = 64.dp
private val MediaRowHeight = 56.dp

/**
 * The two key rows from the SHIELD app: navigation (back / home / menu) above a rule, then
 * transport (rewind / play-pause / forward).
 */
@Composable
fun ControlDeck(
    enabled: Boolean,
    onKey: (RemoteKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Rule()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavRowHeight)
                .background(scheme.surfaceContainerLow),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeckKey("Back", enabled, { onKey(RemoteKey.BACK) }) { tint -> GlyphBack(size = 26.dp, tint = tint) }
            DeckKey("Home", enabled, { onKey(RemoteKey.HOME) }) { tint -> GlyphHome(size = 24.dp, tint = tint) }
            DeckKey("Menu", enabled, { onKey(RemoteKey.MENU) }) { tint -> GlyphMenu(size = 24.dp, tint = tint) }
        }
        Rule()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MediaRowHeight)
                .background(scheme.surfaceContainerLow),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeckKey("Rewind", enabled, { onKey(RemoteKey.MEDIA_REWIND) }) { tint ->
                GlyphRewind(size = 24.dp, tint = tint)
            }
            DeckKey("Play or pause", enabled, { onKey(RemoteKey.MEDIA_PLAY_PAUSE) }) { tint ->
                GlyphPlayPause(size = 24.dp, tint = tint)
            }
            DeckKey("Fast forward", enabled, { onKey(RemoteKey.MEDIA_FAST_FORWARD) }) { tint ->
                GlyphForward(size = 24.dp, tint = tint)
            }
        }
    }
}

/**
 * A key in the SHIELD deck: transparent, no border, icon only. [content] receives the tint so the
 * glyph can be drawn directly instead of going through an icon font.
 */
@Composable
private fun DeckKey(
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable (Color) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    PressSurface(
        onClick = onClick,
        modifier = Modifier
            .width(72.dp)
            .height(56.dp),
        color = Color.Transparent,
        contentColor = scheme.onSurface,
        enabled = enabled,
        description = description
    ) {
        content(scheme.onSurface)
    }
}
