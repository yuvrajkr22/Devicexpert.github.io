package com.devicexpert.app.core.designsystem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan, secondary = TechIndigo, background = DeepCarbon, surface = CardSurface,
    surfaceVariant = CardSurfaceElevated, onPrimary = DeepCarbon, onSecondary = TextPrimary,
    onBackground = TextPrimary, onSurface = TextPrimary, onSurfaceVariant = TextSecondary,
    outline = BorderStroke, error = StatusCriticalCrimson
)
@Composable
fun DeviceXpertTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = AppTypography, content = content)
}