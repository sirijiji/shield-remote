package com.shieldremote.app.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.shieldremote.app.ui.ControlMode

/**
 * SHIELD's control bar: apps on the left, the current control labelled in the middle with a
 * dropdown chevron, the keyboard on the right.
 */
@Composable
fun ModeBar(
    mode: ControlMode,
    onModeChange: (ControlMode) -> Unit,
    appsOpen: Boolean,
    onAppsClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    var menu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(scheme.surfaceContainer)
    ) {
        BarGlyphKey(
            description = if (appsOpen) "Hide apps" else "Show apps",
            enabled = enabled,
            tint = if (appsOpen) scheme.onSurface else scheme.onSurfaceVariant,
            onClick = onAppsClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) { GlyphGrid(size = 20.dp, tint = it) }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(role = Role.Button, enabled = enabled) { menu = true }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (mode == ControlMode.SWIPE) "Trackpad" else "D-pad",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface
            )
            Spacer(Modifier.width(8.dp))
            GlyphChevronDown(size = 13.dp, tint = scheme.onSurfaceVariant)
        }

        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            ModeItem("Trackpad", mode == ControlMode.SWIPE) {
                menu = false
                onModeChange(ControlMode.SWIPE)
            }
            ModeItem("D-pad", mode == ControlMode.DPAD) {
                menu = false
                onModeChange(ControlMode.DPAD)
            }
        }

        BarGlyphKey(
            description = "Type on TV",
            enabled = enabled,
            tint = scheme.onSurfaceVariant,
            onClick = onKeyboardClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) { GlyphKeyboard(size = 20.dp, tint = it) }
    }
}

/** 44dp ghost key for a bar, drawing a glyph instead of a Material icon. */
@Composable
private fun BarGlyphKey(
    description: String,
    enabled: Boolean,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Color) -> Unit
) {
    PressSurface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        color = Color.Transparent,
        contentColor = tint,
        enabled = enabled,
        description = description
    ) {
        content(tint)
    }
}

@Composable
private fun ModeItem(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = onClick
    )
}
