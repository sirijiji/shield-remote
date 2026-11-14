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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Segmented choice for the settings sheet: a recessed track with a white selected cell, in the
 * spirit of the SHIELD app's flat grey controls.
 */
@Composable
fun <T> SegmentedPills(
    options: List<SegmentOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = rememberHaptic()
    val index = options.indexOfFirst { it.value == selected }.coerceAtLeast(0)

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(4.dp))
                .background(trackColor)
                .padding(2.dp)
        ) {
            val segment = maxWidth / options.size
            val x by animateDpAsState(segment * index, Motion.spatial(), label = "segmentX")

            Box(
                modifier = Modifier
                    .offset { IntOffset(x.roundToPx(), 0) }
                    .fillMaxHeight()
                    .fillMaxWidth(1f / options.size)
                    .clip(RoundedCornerShape(3.dp))
                    .background(scheme.onSurface)
            )

            Row(modifier = Modifier.fillMaxSize().selectableGroup()) {
                options.forEach { option ->
                    val isSelected = option.value == selected
                    val tint by animateColorAsState(
                        targetValue = if (isSelected) scheme.surface else scheme.onSurfaceVariant,
                        animationSpec = Motion.effects(),
                        label = "segmentTint"
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
                        Text(
                            text = option.label,
                            color = tint,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private val ModeOptions = listOf(
    SegmentOption(ControlMode.SWIPE, "Trackpad"),
    SegmentOption(ControlMode.DPAD, "D-pad")
)

/** Trackpad | D-pad, for the settings sheet. */
@Composable
fun ModeSwitch(
    mode: ControlMode,
    onModeChange: (ControlMode) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    SegmentedPills(
        options = ModeOptions,
        selected = mode,
        onSelect = onModeChange,
        modifier = modifier,
        height = 40.dp,
        trackColor = trackColor
    )
}
