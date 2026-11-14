package com.shieldremote.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The SHIELD remote draws its own glyphs: bare triangles, a hollow circle, three bars, double
 * triangles for seek. Material's arrow-with-shaft icons look wrong next to them, so they are
 * reproduced here on a 24-unit grid.
 */
@Composable
private fun GlyphCanvas(
    size: Dp,
    tint: Color,
    modifier: Modifier = Modifier,
    draw: DrawScope.(Float) -> Unit
) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        draw(s)
    }
}

/** ◁ Back: a filled triangle with a flat right edge. */
@Composable
fun GlyphBack(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        val p = Path().apply {
            moveTo(s * 0.30f, s * 0.50f)
            lineTo(s * 0.70f, s * 0.18f)
            lineTo(s * 0.70f, s * 0.82f)
            close()
        }
        drawPath(p, tint)
    }
}

/** ◯ Home: a hollow circle. */
@Composable
fun GlyphHome(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        drawCircle(
            color = tint,
            radius = s * 0.30f,
            center = Offset(s / 2f, s / 2f),
            style = Stroke(width = s * 0.075f)
        )
    }
}

/** ☰ Menu: three equal bars. */
@Composable
fun GlyphMenu(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        val w = s * 0.075f
        val left = s * 0.22f
        val right = s * 0.78f
        listOf(0.30f, 0.50f, 0.70f).forEach { y ->
            drawLine(tint, Offset(left, s * y), Offset(right, s * y), strokeWidth = w)
        }
    }
}

/** ◀◀ Rewind. */
@Composable
fun GlyphRewind(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        listOf(0.16f, 0.50f).forEach { x ->
            val p = Path().apply {
                moveTo(s * x, s * 0.50f)
                lineTo(s * (x + 0.26f), s * 0.22f)
                lineTo(s * (x + 0.26f), s * 0.78f)
                close()
            }
            drawPath(p, tint)
        }
    }
}

/** ▶▶ Fast-forward. */
@Composable
fun GlyphForward(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        listOf(0.74f, 0.40f).forEach { x ->
            val p = Path().apply {
                moveTo(s * x, s * 0.50f)
                lineTo(s * (x - 0.26f), s * 0.22f)
                lineTo(s * (x - 0.26f), s * 0.78f)
                close()
            }
            drawPath(p, tint)
        }
    }
}

/** ▶❙❙ Play/pause: one triangle and two bars. */
@Composable
fun GlyphPlayPause(size: Dp = 24.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        val p = Path().apply {
            moveTo(s * 0.30f, s * 0.20f)
            lineTo(s * 0.62f, s * 0.50f)
            lineTo(s * 0.30f, s * 0.80f)
            close()
        }
        drawPath(p, tint)
        val barW = s * 0.075f
        drawRect(tint, topLeft = Offset(s * 0.68f, s * 0.22f), size = Size(barW, s * 0.56f))
        drawRect(tint, topLeft = Offset(s * 0.80f, s * 0.22f), size = Size(barW, s * 0.56f))
    }
}

/** ⌄ Chevron used by the device switcher and the mode control. */
@Composable
fun GlyphChevronDown(size: Dp = 16.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        val p = Path().apply {
            moveTo(s * 0.22f, s * 0.38f)
            lineTo(s * 0.50f, s * 0.64f)
            lineTo(s * 0.78f, s * 0.38f)
        }
        drawPath(p, tint, style = Stroke(width = s * 0.10f, cap = StrokeCap.Round))
    }
}

/** ▦ Apps grid: four squares. */
@Composable
fun GlyphGrid(size: Dp = 22.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        val cell = s * 0.30f
        val gap = s * 0.12f
        val start = s * 0.14f
        for (row in 0..1) for (col in 0..1) {
            drawRect(
                color = tint,
                topLeft = Offset(start + col * (cell + gap), start + row * (cell + gap)),
                size = Size(cell, cell),
                style = Stroke(width = s * 0.075f)
            )
        }
    }
}

/** ⌨ Keyboard: a rounded rectangle with key marks. */
@Composable
fun GlyphKeyboard(size: Dp = 22.dp, tint: Color = LocalContentColor.current, modifier: Modifier = Modifier) {
    GlyphCanvas(size, tint, modifier) { s ->
        drawRect(
            color = tint,
            topLeft = Offset(s * 0.10f, s * 0.26f),
            size = Size(s * 0.80f, s * 0.48f),
            style = Stroke(width = s * 0.075f)
        )
        val dot = s * 0.05f
        for (i in 0..2) {
            drawCircle(tint, dot, Offset(s * (0.24f + i * 0.13f), s * 0.40f))
            drawCircle(tint, dot, Offset(s * (0.24f + i * 0.13f), s * 0.52f))
        }
        drawRect(
            color = tint,
            topLeft = Offset(s * 0.26f, s * 0.60f),
            size = Size(s * 0.30f, s * 0.045f)
        )
    }
}

/** Thin chevron used inside the D-pad ring. */
@Composable
fun GlyphPadChevron(
    direction: Int,
    size: Dp = 26.dp,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier
) {
    GlyphCanvas(size, tint, modifier) { s ->
        val p = Path().apply {
            when (direction) {
                0 -> { moveTo(s * 0.20f, s * 0.62f); lineTo(s * 0.50f, s * 0.30f); lineTo(s * 0.80f, s * 0.62f) }
                1 -> { moveTo(s * 0.20f, s * 0.38f); lineTo(s * 0.50f, s * 0.70f); lineTo(s * 0.80f, s * 0.38f) }
                2 -> { moveTo(s * 0.62f, s * 0.20f); lineTo(s * 0.30f, s * 0.50f); lineTo(s * 0.62f, s * 0.80f) }
                else -> { moveTo(s * 0.38f, s * 0.20f); lineTo(s * 0.70f, s * 0.50f); lineTo(s * 0.38f, s * 0.80f) }
            }
        }
        drawPath(p, tint, style = Stroke(width = s * 0.13f, cap = StrokeCap.Round))
    }
}
