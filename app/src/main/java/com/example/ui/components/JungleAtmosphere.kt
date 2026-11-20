package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ExplorerEmblem
import com.example.model.ExplorerProfile
import com.example.ui.theme.CanopyDeep
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.JungleMossDark
import com.example.ui.theme.JungleMossLight
import com.example.ui.theme.JungleMossMid
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.VineLime
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight
import com.example.ui.theme.WoodMid
import com.example.ui.theme.WoodWarm
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JungleCampBackdrop(
    modifier: Modifier = Modifier,
    showCampRuins: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "sunbeam_sway")
    val beamShift by transition.animateFloat(
        initialValue = -0.04f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam_shift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep layered canopy sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CanopyNight,
                    CanopyDeep,
                    JungleMossDark,
                    CanopyNight
                )
            )
        )

        // Distant glowing canopy horizon
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SunbeamGold.copy(alpha = 0.18f),
                    JungleMossLight.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.28f),
                radius = w * 0.78f
            ),
            center = Offset(w * 0.5f, h * 0.28f),
            radius = w * 0.78f
        )

        // Volumetric sunbeams piercing the canopy
        val sunOrigin = Offset(w * (0.28f + beamShift), -h * 0.06f)
        val beamWidths = listOf(0.18f to 0.07f, 0.38f to 0.055f, 0.62f to 0.08f)
        beamWidths.forEach { (targetXFrac, alpha) ->
            val path = Path().apply {
                moveTo(sunOrigin.x, sunOrigin.y)
                lineTo(w * (targetXFrac - 0.16f + beamShift), h * 0.92f)
                lineTo(w * (targetXFrac + 0.12f + beamShift), h * 0.92f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FireflyGlow.copy(alpha = alpha * 1.4f),
                        SunbeamGold.copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    )
                )
            )
        }

        // Distant jungle tree-line ridge
        val farRidge = Path().apply {
            moveTo(0f, h * 0.68f)
            cubicTo(w * 0.22f, h * 0.58f, w * 0.48f, h * 0.64f, w * 0.72f, h * 0.57f)
            cubicTo(w * 0.88f, h * 0.53f, w * 0.96f, h * 0.60f, w, h * 0.62f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(farRidge, color = JungleMossMid.copy(alpha = 0.45f))

        if (showCampRuins) {
            // Ancient mossy stone temple pillars on left and right
            drawRoundRect(
                color = Color(0xFF1A3324).copy(alpha = 0.75f),
                topLeft = Offset(w * 0.04f, h * 0.44f),
                size = Size(w * 0.14f, h * 0.45f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = Color(0xFF1A3324).copy(alpha = 0.75f),
                topLeft = Offset(w * 0.82f, h * 0.46f),
                size = Size(w * 0.14f, h * 0.44f),
                cornerRadius = CornerRadius(14f, 14f)
            )

            // Warm expedition camp lanterns hanging near ruins
            val leftLantern = Offset(w * 0.16f, h * 0.54f)
            val rightLantern = Offset(w * 0.84f, h * 0.56f)
            listOf(leftLantern, rightLantern).forEach { center ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SunbeamGold.copy(alpha = 0.42f),
                            Color(0xFFFF8F00).copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.18f
                    ),
                    center = center,
                    radius = w * 0.18f
                )
                drawCircle(
                    color = FireflyGlow.copy(alpha = 0.9f),
                    radius = 7f,
                    center = center
                )
            }
        }

        // Foreground lush fern & jungle floor silhouette
        val floorRidge = Path().apply {
            moveTo(0f, h * 0.83f)
            cubicTo(w * 0.25f, h * 0.76f, w * 0.55f, h * 0.84f, w, h * 0.77f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(floorRidge, color = CanopyNight.copy(alpha = 0.88f))

        // Corner monstera leaf silhouettes
        drawLeafSilhouette(
            anchor = Offset(-w * 0.04f, h * 0.86f),
            tip = Offset(w * 0.24f, h * 0.72f),
            width = w * 0.13f,
            color = JungleMossDark.copy(alpha = 0.9f)
        )
        drawLeafSilhouette(
            anchor = Offset(w * 1.04f, h * 0.88f),
            tip = Offset(w * 0.75f, h * 0.73f),
            width = w * 0.14f,
            color = JungleMossDark.copy(alpha = 0.9f)
        )
    }
}

private fun DrawScope.drawLeafSilhouette(
    anchor: Offset,
    tip: Offset,
    width: Float,
    color: Color
) {
    val midX = (anchor.x + tip.x) / 2f
    val midY = (anchor.y + tip.y) / 2f
    val dx = tip.x - anchor.x
    val dy = tip.y - anchor.y
    val len = kotlin.math.hypot(dx, dy).coerceAtLeast(1f)
    val nx = -dy / len * width
    val ny = dx / len * width

    val path = Path().apply {
        moveTo(anchor.x, anchor.y)
        quadraticTo(midX + nx, midY + ny, tip.x, tip.y)
        quadraticTo(midX - nx, midY - ny, anchor.x, anchor.y)
        close()
    }
    drawPath(path, color = color)
}

private data class FireflySeed(
    val xFrac: Float,
    val yFrac: Float,
    val radiusDp: Float,
    val speed: Float,
    val phase: Float,
    val driftAmp: Float
)

@Composable
fun FireflyField(
    modifier: Modifier = Modifier,
    count: Int = 22
) {
    val seeds = remember(count) {
        List(count) { idx ->
            val f = idx.toFloat()
            FireflySeed(
                xFrac = ((idx * 37 + 13) % 100) / 100f,
                yFrac = ((idx * 53 + 7) % 100) / 100f,
                radiusDp = 2.2f + (idx % 4) * 1.1f,
                speed = 0.7f + (idx % 5) * 0.22f,
                phase = f * 1.37f,
                driftAmp = 0.025f + (idx % 3) * 0.015f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "fireflies")
    val clock by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "firefly_clock"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        seeds.forEach { seed ->
            val angle = clock * seed.speed + seed.phase
            val cx = ((seed.xFrac + sin(angle) * seed.driftAmp) * w).coerceIn(0f, w)
            val cy = (((seed.yFrac - (clock * 0.08f * seed.speed) / (2f * PI.toFloat())) % 1f + 1f) % 1f) * h
            val pulse = 0.35f + 0.65f * ((sin(angle * 2.1f) + 1f) / 2f)
            val rPx = seed.radiusDp.dp.toPx()

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        FireflyGlow.copy(alpha = 0.75f * pulse),
                        SunbeamGold.copy(alpha = 0.25f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = rPx * 5.5f
                ),
                center = Offset(cx, cy),
                radius = rPx * 5.5f
            )
            drawCircle(
                color = ParchmentCream.copy(alpha = 0.92f * pulse),
                radius = rPx,
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
fun SwayingCanopyVines(
    modifier: Modifier = Modifier,
    height: Dp = 110.dp
) {
    val transition = rememberInfiniteTransition(label = "vine_sway")
    val sway by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway_angle"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height

        val vineConfigs = listOf(
            Triple(0.08f, 0.92f, 1.0f),
            Triple(0.19f, 0.64f, -0.8f),
            Triple(0.81f, 0.68f, 0.9f),
            Triple(0.92f, 0.95f, -1.0f)
        )

        vineConfigs.forEach { (xFrac, lenFrac, dir) ->
            val anchor = Offset(w * xFrac, -8f)
            val tipX = anchor.x + sway * dir * 2.2f
            val tipY = h * lenFrac
            val ctrlX = anchor.x + sway * dir * 1.1f
            val ctrlY = tipY * 0.55f

            val vinePath = Path().apply {
                moveTo(anchor.x, anchor.y)
                quadraticTo(ctrlX, ctrlY, tipX, tipY)
            }
            drawPath(
                path = vinePath,
                color = VineLime.copy(alpha = 0.85f),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sprouting tropical leaves along the vine
            listOf(0.35f, 0.62f, 0.88f).forEachIndexed { i, t ->
                val lx = (1 - t) * (1 - t) * anchor.x + 2 * (1 - t) * t * ctrlX + t * t * tipX
                val ly = (1 - t) * (1 - t) * anchor.y + 2 * (1 - t) * t * ctrlY + t * t * tipY
                val side = if (i % 2 == 0) 1f else -1f
                drawLeafSilhouette(
                    anchor = Offset(lx, ly),
                    tip = Offset(lx + side * 18.dp.toPx(), ly + 12.dp.toPx()),
                    width = 7.dp.toPx(),
                    color = if (i % 2 == 0) EmeraldVibrant.copy(alpha = 0.8f) else JungleMossLight
                )
            }
        }
    }
}

@Composable
fun CarvedWoodPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    borderColor: Color = WoodHighlight,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Surface(
        modifier = modifier
            .shadow(12.dp, shape, ambientColor = CanopyNight, spotColor = CanopyNight),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(2.5.dp, borderColor.copy(alpha = 0.85f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(WoodMid, WoodDark)
                    )
                )
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun CarvedWoodButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isGoldAccent: Boolean = true,
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val bgBrush = if (isGoldAccent) {
        Brush.verticalGradient(
            colors = if (enabled) {
                listOf(Color(0xFFFFD868), SunbeamGold, Color(0xFFD18B1E))
            } else {
                listOf(Color(0xFF7A6A46), Color(0xFF5D5035))
            }
        )
    } else {
        Brush.verticalGradient(
            colors = if (enabled) {
                listOf(WoodWarm, WoodMid, WoodDark)
            } else {
                listOf(WoodMid.copy(alpha = 0.5f), WoodDark.copy(alpha = 0.5f))
            }
        )
    }

    val borderBrush = if (isGoldAccent) {
        Brush.verticalGradient(listOf(ParchmentCream.copy(alpha = 0.9f), WoodDark))
    } else {
        Brush.verticalGradient(listOf(WoodHighlight, WoodDark))
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .shadow(if (enabled) 10.dp else 2.dp, shape)
            .clip(shape)
            .background(bgBrush)
            .border(2.dp, borderBrush, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun ExplorerBadgeIcon(
    profile: ExplorerProfile,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val mainColor = Color(profile.colorHex)
    val darkColor = Color(profile.darkColorHex)

    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        val r = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(mainColor, darkColor),
                center = Offset(center.x - r * 0.25f, center.y - r * 0.25f),
                radius = r * 1.2f
            ),
            radius = r,
            center = center
        )
        drawCircle(
            color = ParchmentCream.copy(alpha = 0.92f),
            radius = r - 1.5.dp.toPx(),
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        val iconR = r * 0.48f
        when (profile.emblem) {
            ExplorerEmblem.COMPASS -> {
                val star = Path().apply {
                    moveTo(center.x, center.y - iconR)
                    lineTo(center.x + iconR * 0.35f, center.y)
                    lineTo(center.x, center.y + iconR)
                    lineTo(center.x - iconR * 0.35f, center.y)
                    close()
                }
                rotate(degrees = 25f, pivot = center) {
                    drawPath(star, color = ParchmentCream)
                }
            }
            ExplorerEmblem.FEATHER -> {
                val feather = Path().apply {
                    moveTo(center.x - iconR * 0.6f, center.y + iconR * 0.7f)
                    quadraticTo(center.x - iconR * 0.4f, center.y - iconR * 0.6f, center.x + iconR * 0.7f, center.y - iconR * 0.7f)
                    quadraticTo(center.x + iconR * 0.5f, center.y + iconR * 0.4f, center.x - iconR * 0.6f, center.y + iconR * 0.7f)
                    close()
                }
                drawPath(feather, color = ParchmentCream)
            }
            ExplorerEmblem.LANTERN -> {
                drawRoundRect(
                    color = ParchmentCream,
                    topLeft = Offset(center.x - iconR * 0.45f, center.y - iconR * 0.55f),
                    size = Size(iconR * 0.9f, iconR * 1.1f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawCircle(
                    color = darkColor,
                    radius = iconR * 0.22f,
                    center = center
                )
            }
            ExplorerEmblem.LEAF -> {
                drawLeafSilhouette(
                    anchor = Offset(center.x - iconR * 0.65f, center.y + iconR * 0.55f),
                    tip = Offset(center.x + iconR * 0.65f, center.y - iconR * 0.65f),
                    width = iconR * 0.55f,
                    color = ParchmentCream
                )
            }
        }
    }
}
