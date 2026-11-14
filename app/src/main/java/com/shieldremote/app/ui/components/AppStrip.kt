package com.shieldremote.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shieldremote.app.apps.AppShortcut
import com.shieldremote.app.apps.BrandIcons

/** First letters used inside a tile: "YouTube" -> "Y", "Prime Video" -> "PV". */
fun appInitials(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(1).uppercase()
        else -> words.take(2).joinToString("") { it.take(1).uppercase() }
    }
}

/**
 * A monochrome app tile: 1dp border, brand mark in the foreground ink. Brand colour is dropped on
 * purpose — the system allows one accent and it is reserved for power and failure.
 */
@Composable
fun AppTileIcon(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = TileSize,
    presetId: String? = null
) {
    val scheme = MaterialTheme.colorScheme
    val pathData = presetId?.let { BrandIcons.paths[it] }
    val glyph = remember(pathData) {
        pathData?.let { PathParser().parsePathString(it).toPath() }
    }

    Box(
        modifier = modifier
            .size(size)
            .border(1.dp, scheme.outlineVariant)
            .background(scheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        if (glyph != null) {
            Canvas(modifier = Modifier.size(size * 0.42f)) {
                val scale = this.size.minDimension / 24f
                withTransform({ scale(scale, scale, pivot = Offset.Zero) }) {
                    drawPath(glyph, scheme.onSurface)
                }
            }
        } else {
            Text(
                text = appInitials(name),
                color = scheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * The app dock: a row of bordered squares. Tap launches the app on the TV; press and hold opens
 * Edit / Move / Remove.
 */
@Composable
fun AppStrip(
    shortcuts: List<AppShortcut>,
    onLaunch: (AppShortcut) -> Unit,
    onAdd: () -> Unit,
    onEdit: (AppShortcut) -> Unit,
    onMove: (AppShortcut, Int) -> Unit,
    onRemove: (AppShortcut) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(shortcuts, key = { it.id }) { shortcut ->
                val index = shortcuts.indexOf(shortcut)
                AppButton(
                    shortcut = shortcut,
                    enabled = enabled,
                    canMoveLeft = index > 0,
                    canMoveRight = index < shortcuts.lastIndex,
                    onLaunch = { onLaunch(shortcut) },
                    onEdit = { onEdit(shortcut) },
                    onMove = { delta -> onMove(shortcut, delta) },
                    onRemove = { onRemove(shortcut) },
                    modifier = Modifier.animateItem()
                )
            }
            if (shortcuts.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "Add your favourite apps",
                        color = scheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier.padding(start = 8.dp)) {
            PressSurface(
                onClick = onAdd,
                modifier = Modifier.size(TileSize),
                color = Color.Transparent,
                border = scheme.outlineVariant,
                description = "Add app"
            ) {
                androidx.compose.material3.Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private val TileSize = 56.dp

@Composable
private fun AppButton(
    shortcut: AppShortcut,
    enabled: Boolean,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onLaunch: () -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    var menu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        PressSurface(
            onClick = onLaunch,
            onLongClick = { menu = true },
            onLongClickLabel = "Edit ${shortcut.name}",
            description = "Open ${shortcut.name} on TV",
            color = Color.Transparent,
            enabled = enabled,
            modifier = Modifier.size(TileSize)
        ) {
            AppTileIcon(shortcut.name, Color(shortcut.colorArgb), size = TileSize, presetId = shortcut.presetId)
        }

        DropdownMenu(
            expanded = menu,
            onDismissRequest = { menu = false },
            containerColor = scheme.surfaceContainerHigh
        ) {
            Text(
                text = shortcut.name.uppercase(),
                color = scheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            MenuItem("Edit") { menu = false; onEdit() }
            if (canMoveLeft) MenuItem("Move left") { menu = false; onMove(-1) }
            if (canMoveRight) MenuItem("Move right") { menu = false; onMove(1) }
            MenuItem("Remove", color = scheme.error) { menu = false; onRemove() }
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text, color = color, style = MaterialTheme.typography.bodyMedium) },
        onClick = onClick,
        modifier = Modifier.height(44.dp)
    )
}
