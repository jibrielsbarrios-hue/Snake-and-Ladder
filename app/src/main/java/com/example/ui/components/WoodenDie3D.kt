package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight
import com.example.ui.theme.WoodMid
import com.example.ui.theme.WoodWarm

@Composable
fun WoodenDie3D(
    faceValue: Int,
    isRolling: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dieSize: Dp = 76.dp
) {
    val density = LocalDensity.current.density
    val infinite = rememberInfiniteTransition(label = "die_tumble")

    val tumbleX by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(360, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tumble_x"
    )
    val tumbleY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(440, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tumble_y"
    )
    val idleTilt by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_tilt"
    )

    val rotX by animateFloatAsState(
        targetValue = if (isRolling) tumbleX else 10f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "die_rot_x"
    )
    val rotY by animateFloatAsState(
        targetValue = if (isRolling) tumbleY else if (enabled) idleTilt else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "die_rot_y"
    )
    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.14f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "die_scale"
    )
    val liftY by animateFloatAsState(
        targetValue = if (isRolling) -18f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "die_lift"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("wooden_die_3d")
            .semantics {
                contentDescription = "Wooden 3D die showing $faceValue. Tap to roll."
            }
            .clickable(enabled = enabled && !isRolling, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(dieSize)
                .graphicsLayer {
                    cameraDistance = 14f * density
                    rotationX = rotX
                    rotationY = rotY
                    scaleX = scale
                    scaleY = scale
                    translationY = liftY
                }
        ) {
            val w = size.width
            val h = size.height
            val pad = w * 0.08f
            val faceSize = w - pad * 2f
            val radius = w * 0.2f

            // Drop shadow under wooden cube
            drawRoundRect(
                color = CanopyNight.copy(alpha = 0.55f),
                topLeft = Offset(pad + 3.dp.toPx(), pad + 6.dp.toPx()),
                size = Size(faceSize, faceSize),
                cornerRadius = CornerRadius(radius, radius)
            )

            // 3D beveled side depth (bottom & right rim)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(WoodWarm, WoodDark)
                ),
                topLeft = Offset(pad + 2.dp.toPx(), pad + 4.dp.toPx()),
                size = Size(faceSize, faceSize),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Main carved teak / amber wood face
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE7A94E),
                        Color(0xFFC57B27),
                        WoodMid
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                ),
                topLeft = Offset(pad, pad),
                size = Size(faceSize, faceSize),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Subtle horizontal wood grain streaks
            val grainColors = Color(0xFF7B4316).copy(alpha = 0.24f)
            listOf(0.28f, 0.52f, 0.74f).forEach { frac ->
                drawLine(
                    color = grainColors,
                    start = Offset(pad + 6.dp.toPx(), pad + faceSize * frac),
                    end = Offset(pad + faceSize - 6.dp.toPx(), pad + faceSize * (frac + 0.03f)),
                    strokeWidth = 1.6.dp.toPx()
                )
            }

            // Golden bevel outline
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        if (enabled) FireflyGlow.copy(alpha = 0.9f) else ParchmentCream.copy(alpha = 0.4f),
                        WoodDark
                    )
                ),
                topLeft = Offset(pad, pad),
                size = Size(faceSize, faceSize),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 2.4.dp.toPx())
            )

            // Pips (1..6) with recessed carving highlight
            val pipCoords = when (faceValue.coerceIn(1, 6)) {
                1 -> listOf(0.5f to 0.5f)
                2 -> listOf(0.28f to 0.28f, 0.72f to 0.72f)
                3 -> listOf(0.28f to 0.28f, 0.5f to 0.5f, 0.72f to 0.72f)
                4 -> listOf(0.28f to 0.28f, 0.72f to 0.28f, 0.28f to 0.72f, 0.72f to 0.72f)
                5 -> listOf(0.28f to 0.28f, 0.72f to 0.28f, 0.5f to 0.5f, 0.28f to 0.72f, 0.72f to 0.72f)
                else -> listOf(
                    0.30f to 0.24f, 0.70f to 0.24f,
                    0.30f to 0.50f, 0.70f to 0.50f,
                    0.30f to 0.76f, 0.70f to 0.76f
                )
            }

            val pipRadius = faceSize * 0.095f
            pipCoords.forEach { (px, py) ->
                val cx = pad + faceSize * px
                val cy = pad + faceSize * py
                // Warm cream highlight rim below pip for recessed carved depth
                drawCircle(
                    color = SunbeamGold.copy(alpha = 0.45f),
                    radius = pipRadius * 1.1f,
                    center = Offset(cx + 0.8.dp.toPx(), cy + 1.2.dp.toPx())
                )
                // Deep dark wood pip
                drawCircle(
                    color = Color(0xFF281205),
                    radius = pipRadius,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}
