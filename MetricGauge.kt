package com.devicexpert.app.core.designsystem.components
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devicexpert.app.core.designsystem.*
@Composable
fun MetricGauge(percentage: Float, modifier: Modifier = Modifier) {
    var animationTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(percentage) { animationTarget = percentage.coerceIn(0f, 1f) }
    val animatedSweep by animateFloatAsState(targetValue = animationTarget * 360f, animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "GaugeSweep")
    val gaugeColor = when {
        percentage >= 0.90f -> StatusCriticalCrimson
        percentage >= 0.75f -> StatusWarningAmber
        else -> StatusHealthyEmerald
    }
    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val strokeWidth = 12.dp.toPx()
            drawArc(color = CardSurfaceElevated, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = strokeWidth))
            drawArc(color = gaugeColor, startAngle = -90f, sweepAngle = animatedSweep, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        }
        Text(text = "${(percentage * 100).toInt()}%", style = AppTypography.labelLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold))
    }
}