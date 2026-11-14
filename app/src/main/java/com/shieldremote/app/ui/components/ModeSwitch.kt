package com.shieldremote.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.shieldremote.app.ui.ControlMode
import com.shieldremote.app.ui.theme.Motion

data class SegmentOption<T>(val value: T, val label: String, val icon: ImageVector? = null)

/**
 * A tab bar, not a segmented pill. Equal-width cells, full-cell 2dp underline on the active one,
 * and a rule underneath. Text is uppercase with tracking; the inactive label is muted.
 */
@Composable
fun <T> SegmentedPills(
    options: List<SegmentOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    trackColor: Color = Color.Transparent
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = rememberHaptic()
    val index = options.indexOfFirst { it.value == selected }.coerceAtLeast(0)

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val segment = maxWidth / options.size
            val x by animateDpAsState(segment * index, Motion.spatial(), label = "tabX")

            Row(modifier = Modifier.fillMaxSize().selectableGroup()) {
                options.forEach { option ->
                    val isSelected = option.value == selected
                    val tint by animateColorAsState(
                        targetValue = if (isSelected) scheme.onSurface else scheme.onSurfaceVariant,
                        animationSpec = Motion.effects(),
                        label = "tabTint"
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(
                                selected = isSelected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.RadioButton
                            ) {
                                if (!isSelected) {
                                    haptic(HapticFeedbackType.TextHandleMove)
                                    onSelect(option.value)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        if (option.icon != null) {
                            Icon(
                                option.icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(14.dp)
                            )
                            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
                        }
                        Text(
                            text = option.label.uppercase(),
                            color = tint,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(x.roundToPx(), 0) }
                    .fillMaxWidth(1f / options.size)
                    .height(2.dp)
                    .background(scheme.onSurface)
            )
        }
        Rule()
    }
}

private val ModeOptions = listOf(
    SegmentOption(ControlMode.SWIPE, "Swipe"),
    SegmentOption(ControlMode.DPAD, "D-pad")
)

/** Swipe | D-pad. */
@Composable
fun ModeSwitch(
    mode: ControlMode,
    onModeChange: (ControlMode) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.Transparent
) {
    SegmentedPills(
        options = ModeOptions,
        selected = mode,
        onSelect = onModeChange,
        modifier = modifier,
        height = 48.dp
    )
}
