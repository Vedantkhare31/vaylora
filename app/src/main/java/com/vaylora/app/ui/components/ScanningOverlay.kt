package com.vaylora.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun VayloraScanningReticle(
    isScanning: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val boxWidth = width * 0.75f * if (isScanning) scaleAnim else 1f
        val boxHeight = width * 0.75f * if (isScanning) scaleAnim else 1f
        val left = (width - boxWidth) / 2
        val top = (height - boxHeight) / 2.3f
        val cornerLength = 48.dp.toPx()
        val strokeWidth = 5.dp.toPx()
        val cornerRadius = 16.dp.toPx()

        val limeGreen = Color(0xFF8BC34A).copy(alpha = if (isScanning) alphaAnim else 0.8f)

        // Top-Left corner bracket
        val tlPath = Path().apply {
            moveTo(left, top + cornerLength)
            lineTo(left, top + cornerRadius)
            quadraticBezierTo(left, top, left + cornerRadius, top)
            lineTo(left + cornerLength, top)
        }
        drawPath(tlPath, limeGreen, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Top-Right corner bracket
        val trPath = Path().apply {
            moveTo(left + boxWidth - cornerLength, top)
            lineTo(left + boxWidth - cornerRadius, top)
            quadraticBezierTo(left + boxWidth, top, left + boxWidth, top + cornerRadius)
            lineTo(left + boxWidth, top + cornerLength)
        }
        drawPath(trPath, limeGreen, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Bottom-Left corner bracket
        val blPath = Path().apply {
            moveTo(left, top + boxHeight - cornerLength)
            lineTo(left, top + boxHeight - cornerRadius)
            quadraticBezierTo(left, top + boxHeight, left + cornerRadius, top + boxHeight)
            lineTo(left + cornerLength, top + boxHeight)
        }
        drawPath(blPath, limeGreen, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Bottom-Right corner bracket
        val brPath = Path().apply {
            moveTo(left + boxWidth - cornerLength, top + boxHeight)
            lineTo(left + boxWidth - cornerRadius, top + boxHeight)
            quadraticBezierTo(left + boxWidth, top + boxHeight, left + boxWidth, top + boxHeight - cornerRadius)
            lineTo(left + boxWidth, top + boxHeight - cornerLength)
        }
        drawPath(brPath, limeGreen, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}
