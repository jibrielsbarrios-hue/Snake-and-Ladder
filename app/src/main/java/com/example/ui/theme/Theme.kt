package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val JungleDarkColorScheme = darkColorScheme(
    primary = SunbeamGold,
    onPrimary = WoodDark,
    primaryContainer = WoodMid,
    onPrimaryContainer = ParchmentCream,
    secondary = EmeraldVibrant,
    onSecondary = CanopyNight,
    secondaryContainer = JungleMossMid,
    onSecondaryContainer = ParchmentCream,
    tertiary = ExplorerCoral,
    onTertiary = CanopyNight,
    background = CanopyNight,
    onBackground = ParchmentCream,
    surface = CanopyDeep,
    onSurface = ParchmentCream,
    surfaceVariant = JungleMossDark,
    onSurfaceVariant = ParchmentMuted,
    outline = WoodWarm,
    error = SerpentCrimson,
    onError = ParchmentCream
)

val JungleShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun JungleQuestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JungleDarkColorScheme,
        typography = JungleTypography,
        shapes = JungleShapes,
        content = content
    )
}
