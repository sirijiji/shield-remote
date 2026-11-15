package com.shieldremote.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shieldremote.app.ui.theme.Motion

// ---- Structural rules ---------------------------------------------------------------------------

/** The only structural element in the system: a 1dp rule in the theme's border colour. */
@Composable
fun Rule(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.outlineVariant) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = color)
}

/** A 1dp vertical rule, used to separate cells inside a horizontal strip. */
@Composable
fun CellRule(color: Color = MaterialTheme.colorScheme.outlineVariant) {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(color)
    )
}

// ---- Press behaviour ----------------------------------------------------------------------------

private class FractionCorner(private val fraction: Float) : CornerSize {
    override fun toPx(shapeSize: Size, density: Density): Float =
        shapeSize.minDimension * fraction.coerceIn(0f, 0.5f)
}

/**
 * Press feedback for a square system: a 3% shrink and nothing else. No corner morph, no shadow,
 * no scale bounce — the panel is hardware, not jelly.
 */
@Composable
fun Modifier.pressMorph(
    pressed: Boolean,
    restCorner: Float = 0f,
    pressedCorner: Float = 0f,
    pressedScale: Float = 0.97f
): Modifier {
    val amount = animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = Motion.fastSpatial(),
        label = "press"
    )
    return graphicsLayer {
        val p = amount.value
        val s = 1f - (1f - pressedScale) * p
        scaleX = s
        scaleY = s
        shape = RoundedCornerShape(FractionCorner(restCorner + (pressedCorner - restCorner) * p))
        clip = true
    }
}

/** Clickable rectangle with the press shrink, a ripple and a haptic tick. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PressSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    border: Color? = null,
    restCorner: Float = 0f,
    pressedCorner: Float = 0f,
    enabled: Boolean = true,
    description: String? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val haptic = rememberHaptic()

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .pressMorph(pressed, restCorner, pressedCorner)
            .background(color)
            .then(if (border != null) Modifier.border(1.dp, border) else Modifier)
            .then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier)
            .combinedClickable(
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onLongClickLabel = onLongClickLabel,
                onLongClick = onLongClick?.let {
                    {
                        haptic(HapticFeedbackType.LongPress)
                        it()
                    }
                },
                onClick = {
                    haptic(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) { content() }
    }
}

/** Square icon key. Ghost at rest: no fill, no border, a soft surface only while pressed. */
@Composable
fun IconSurface(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    iconSize: Dp = 20.dp,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    border: Color? = null,
    enabled: Boolean = true
) {
    PressSurface(
        onClick = onClick,
        modifier = modifier.size(size),
        color = color,
        contentColor = contentColor,
        border = border,
        enabled = enabled,
        description = description
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize))
    }
}

enum class ButtonTone { Primary, Tonal, Plain }

/** Text button in the same press language: square, 52dp tall, no shadow. */
@Composable
fun ExpressiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tone: ButtonTone = ButtonTone.Primary,
    enabled: Boolean = true,
    height: Dp = 52.dp
) {
    val scheme = MaterialTheme.colorScheme
    val container: Color
    val content: Color
    val border: Color?
    if (!enabled) {
        container = scheme.surfaceContainerHigh
        content = scheme.onSurfaceVariant
        border = null
    } else when (tone) {
        ButtonTone.Primary -> {
            container = scheme.primary
            content = scheme.onPrimary
            border = null
        }
        ButtonTone.Tonal -> {
            container = Color.Transparent
            content = scheme.onSurface
            border = scheme.outlineVariant
        }
        ButtonTone.Plain -> {
            container = Color.Transparent
            content = scheme.onSurfaceVariant
            border = null
        }
    }
    PressSurface(
        onClick = onClick,
        modifier = modifier.height(height),
        color = container,
        contentColor = content,
        border = border,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (tone == ButtonTone.Plain) 12.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}
