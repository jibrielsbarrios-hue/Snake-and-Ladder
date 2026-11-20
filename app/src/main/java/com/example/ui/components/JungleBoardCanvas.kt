package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoardConfig
import com.example.model.BoardConnector
import com.example.model.ConnectorType
import com.example.model.ExplorerEmblem
import com.example.model.PlayerState
import com.example.ui.theme.CampStartTile
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.JungleMossLight
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.StoneBorder
import com.example.ui.theme.StoneTileDark
import com.example.ui.theme.StoneTileLight
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.SunlitSandstone
import com.example.ui.theme.SummitGoldTile
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight
import com.example.ui.theme.WoodMid
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

fun squareToGridColRow(square: Int): Pair<Int, Int> {
    val zeroBased = (square.coerceIn(1, 100)) - 1
    val rowFromBottom = zeroBased / 10
    val rowFromTop = 9 - rowFromBottom
    val colInRow = zeroBased % 10
    val col = if (rowFromBottom % 2 == 0) colInRow else 9 - colInRow
    return col to rowFromTop
}

private fun squareCenterFraction(square: Int): Offset {
    val (col, rowTop) = squareToGridColRow(square)
    return Offset((col + 0.5f) / 10f, (rowTop + 0.5f) / 10f)
}

@Composable
fun JungleBoardCanvas(
    players: List<PlayerState>,
    currentPlayerIndex: Int,
    activeTrailHighlightSquare: Int?,
    animatingPlayerId: Int?,
    animatingConnector: BoardConnector?,
    animatingConnectorProgress: Float?,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "board_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(950, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "token_pulse"
    )

    // Precompute smooth animated fractional coordinates for each player token
    val groupedBySquare = remember(players) {
        players.groupBy { it.position }
    }

    val animatedFractions = players.map { player ->
        val targetFraction = if (
            animatingPlayerId == player.profile.id &&
            animatingConnector != null &&
            animatingConnectorProgress != null
        ) {
            interpolateConnectorFraction(animatingConnector, animatingConnectorProgress)
        } else {
            val base = squareCenterFraction(player.position)
            val occupants = groupedBySquare[player.position].orEmpty()
            if (occupants.size <= 1) {
                base
            } else {
                val idx = occupants.indexOfFirst { it.profile.id == player.profile.id }.coerceAtLeast(0)
                val subOffsets = listOf(
                    Offset(-0.020f, -0.018f),
                    Offset(0.020f, -0.018f),
                    Offset(-0.020f, 0.020f),
                    Offset(0.020f, 0.020f)
                )
                val delta = subOffsets[idx % subOffsets.size]
                Offset(base.x + delta.x, base.y + delta.y)
            }
        }

        val animated by animateOffsetAsState(
            targetValue = targetFraction,
            animationSpec = if (animatingPlayerId == player.profile.id && animatingConnector != null) {
                tween(durationMillis = 36)
            } else {
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            },
            label = "token_pos_${player.profile.id}"
        )
        player to animated
    }

    val activePlayerId = players.getOrNull(currentPlayerIndex)?.profile?.id

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(18.dp), ambientColor = CanopyNight, spotColor = CanopyNight)
            .semantics {
                contentDescription = "1 to 100 Jungle Snakes and Ladders expedition board"
            },
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(3.dp, WoodHighlight.copy(alpha = 0.85f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(WoodMid, WoodDark)))
                .padding(6.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val boardSize = size.minDimension
                val cell = boardSize / 10f

                // 1. Draw 100 moss-covered stone tiles
                for (square in 1..100) {
                    val (col, rowTop) = squareToGridColRow(square)
                    val topLeft = Offset(col * cell, rowTop * cell)
                    val isEvenChecker = (col + rowTop) % 2 == 0
                    val isHighlighted = activeTrailHighlightSquare == square

                    val tileColor = when {
                        square == 100 -> SummitGoldTile
                        square == 1 -> CampStartTile
                        square % 10 == 0 -> SunlitSandstone
                        isEvenChecker -> StoneTileLight
                        else -> StoneTileDark
                    }

                    // Tile base
                    drawRoundRect(
                        color = tileColor,
                        topLeft = topLeft + Offset(1f, 1f),
                        size = Size(cell - 2f, cell - 2f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Subtle moss corner accent on alternating tiles
                    if ((square * 7) % 5 == 0 && square != 100) {
                        drawCircle(
                            color = JungleMossLight.copy(alpha = 0.32f),
                            radius = cell * 0.22f,
                            center = topLeft + Offset(cell * 0.82f, cell * 0.82f)
                        )
                    }

                    // Active hop glow
                    if (isHighlighted) {
                        drawRoundRect(
                            color = FireflyGlow.copy(alpha = 0.35f),
                            topLeft = topLeft + Offset(1.5f, 1.5f),
                            size = Size(cell - 3f, cell - 3f),
                            cornerRadius = CornerRadius(5f, 5f)
                        )
                        drawRoundRect(
                            color = SunbeamGold,
                            topLeft = topLeft + Offset(1.5f, 1.5f),
                            size = Size(cell - 3f, cell - 3f),
                            cornerRadius = CornerRadius(5f, 5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    } else {
                        drawRoundRect(
                            color = StoneBorder.copy(alpha = 0.55f),
                            topLeft = topLeft + Offset(1f, 1f),
                            size = Size(cell - 2f, cell - 2f),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Stroke(width = 1f)
                        )
                    }

                    // Tile number
                    drawTileNumber(
                        textMeasurer = textMeasurer,
                        square = square,
                        topLeft = topLeft,
                        cellSize = cell
                    )

                    // Special subtle emblems for Square 1 (Camp) and Square 100 (Summit Crown)
                    if (square == 100) {
                        drawSummitCrown(topLeft + Offset(cell * 0.5f, cell * 0.62f), cell * 0.28f)
                    } else if (square == 1) {
                        drawCampFlag(topLeft + Offset(cell * 0.65f, cell * 0.68f), cell * 0.22f)
                    }
                }

                // 2. Draw Bamboo Ladders & Braided Jungle Vines
                BoardConfig.connectors
                    .filter { it.type != ConnectorType.SERPENT }
                    .forEach { connector ->
                        val startPos = squareCenterFraction(connector.start) * boardSize
                        val endPos = squareCenterFraction(connector.end) * boardSize
                        if (connector.type == ConnectorType.BAMBOO_LADDER) {
                            drawBambooLadder(startPos, endPos, cell, connector)
                        } else {
                            drawBraidedVine(startPos, endPos, cell, connector)
                        }
                    }

                // 3. Draw Jungle Serpents
                BoardConfig.connectors
                    .filter { it.type == ConnectorType.SERPENT }
                    .forEach { serpent ->
                        val startPos = squareCenterFraction(serpent.start) * boardSize
                        val endPos = squareCenterFraction(serpent.end) * boardSize
                        drawJungleSerpent(startPos, endPos, cell, serpent)
                    }

                // 4. Draw Explorer Tokens
                animatedFractions.forEach { (player, frac) ->
                    val center = Offset(frac.x * boardSize, frac.y * boardSize)
                    val isActive = player.profile.id == activePlayerId
                    val tokenRadius = cell * if (isActive) 0.34f else 0.29f
                    drawExplorerToken(
                        player = player,
                        center = center,
                        radius = tokenRadius,
                        isActive = isActive,
                        pulse = pulse,
                        textMeasurer = textMeasurer
                    )
                }
            }
        }
    }
}

private fun interpolateConnectorFraction(connector: BoardConnector, t: Float): Offset {
    val start = squareCenterFraction(connector.start)
    val end = squareCenterFraction(connector.end)
    val clamped = t.coerceIn(0f, 1f)
    return if (connector.type == ConnectorType.SERPENT) {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val len = hypot(dx, dy).coerceAtLeast(0.001f)
        val nx = -dy / len
        val ny = dx / len
        val wave = sin(clamped * PI * 3.0).toFloat() * 0.055f * sin(clamped * PI).toFloat()
        Offset(
            x = start.x + dx * clamped + nx * wave,
            y = start.y + dy * clamped + ny * wave
        )
    } else {
        Offset(
            x = start.x + (end.x - start.x) * clamped,
            y = start.y + (end.y - start.y) * clamped
        )
    }
}

private fun DrawScope.drawTileNumber(
    textMeasurer: TextMeasurer,
    square: Int,
    topLeft: Offset,
    cellSize: Float
) {
    val fontSizeSp = (cellSize * 0.28f / density).coerceIn(7.5f, 13f).sp
    val textColor = when (square) {
        100 -> WoodDark
        else -> ParchmentCream.copy(alpha = 0.88f)
    }
    val measured = textMeasurer.measure(
        text = square.toString(),
        style = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSizeSp,
            color = textColor
        )
    )
    drawText(
        textLayoutResult = measured,
        topLeft = topLeft + Offset(cellSize * 0.09f, cellSize * 0.05f)
    )
}

private fun DrawScope.drawSummitCrown(center: Offset, radius: Float) {
    val path = Path().apply {
        moveTo(center.x - radius, center.y + radius * 0.45f)
        lineTo(center.x - radius * 1.05f, center.y - radius * 0.45f)
        lineTo(center.x - radius * 0.4f, center.y + radius * 0.05f)
        lineTo(center.x, center.y - radius * 0.75f)
        lineTo(center.x + radius * 0.4f, center.y + radius * 0.05f)
        lineTo(center.x + radius * 1.05f, center.y - radius * 0.45f)
        lineTo(center.x + radius, center.y + radius * 0.45f)
        close()
    }
    drawPath(path, color = WoodDark.copy(alpha = 0.85f))
}

private fun DrawScope.drawCampFlag(base: Offset, size: Float) {
    drawLine(
        color = ParchmentCream,
        start = base + Offset(-size * 0.4f, size * 0.45f),
        end = base + Offset(-size * 0.4f, -size * 0.65f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )
    val flag = Path().apply {
        moveTo(base.x - size * 0.4f, base.y - size * 0.65f)
        lineTo(base.x + size * 0.55f, base.y - size * 0.3f)
        lineTo(base.x - size * 0.4f, base.y + size * 0.05f)
        close()
    }
    drawPath(flag, color = SunbeamGold)
}

private fun DrawScope.drawBambooLadder(
    start: Offset,
    end: Offset,
    cell: Float,
    connector: BoardConnector
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = hypot(dx, dy).coerceAtLeast(1f)
    val nx = -dy / len
    val ny = dx / len
    val halfWidth = cell * 0.19f
    val railThickness = cell * 0.11f

    val rail1Start = Offset(start.x + nx * halfWidth, start.y + ny * halfWidth)
    val rail1End = Offset(end.x + nx * halfWidth, end.y + ny * halfWidth)
    val rail2Start = Offset(start.x - nx * halfWidth, start.y - ny * halfWidth)
    val rail2End = Offset(end.x - nx * halfWidth, end.y - ny * halfWidth)

    // Soft shadow
    val shadowShift = Offset(2.dp.toPx(), 3.dp.toPx())
    drawLine(CanopyNight.copy(alpha = 0.4f), rail1Start + shadowShift, rail1End + shadowShift, strokeWidth = railThickness, cap = StrokeCap.Round)
    drawLine(CanopyNight.copy(alpha = 0.4f), rail2Start + shadowShift, rail2End + shadowShift, strokeWidth = railThickness, cap = StrokeCap.Round)

    // Rungs
    val rungCount = (len / (cell * 0.52f)).toInt().coerceIn(3, 12)
    for (i in 1 until rungCount) {
        val t = i.toFloat() / rungCount.toFloat()
        val p1 = Offset(rail1Start.x + (rail1End.x - rail1Start.x) * t, rail1Start.y + (rail1End.y - rail1Start.y) * t)
        val p2 = Offset(rail2Start.x + (rail2End.x - rail2Start.x) * t, rail2Start.y + (rail2End.y - rail2Start.y) * t)
        drawLine(
            color = Color(0xFF6D4C2F),
            start = p1,
            end = p2,
            strokeWidth = railThickness * 0.9f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFF0D48A),
            start = p1,
            end = p2,
            strokeWidth = railThickness * 0.45f,
            cap = StrokeCap.Round
        )
    }

    // Main bamboo rails
    val bambooColor = Color(connector.primaryColorHex)
    val jointColor = Color(connector.secondaryColorHex)
    drawLine(jointColor, rail1Start, rail1End, strokeWidth = railThickness * 1.25f, cap = StrokeCap.Round)
    drawLine(bambooColor, rail1Start, rail1End, strokeWidth = railThickness * 0.78f, cap = StrokeCap.Round)
    drawLine(jointColor, rail2Start, rail2End, strokeWidth = railThickness * 1.25f, cap = StrokeCap.Round)
    drawLine(bambooColor, rail2Start, rail2End, strokeWidth = railThickness * 0.78f, cap = StrokeCap.Round)
}

private fun DrawScope.drawBraidedVine(
    start: Offset,
    end: Offset,
    cell: Float,
    connector: BoardConnector
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = hypot(dx, dy).coerceAtLeast(1f)
    val nx = -dy / len
    val ny = dx / len
    val amp = cell * 0.14f

    val pathA = Path()
    val pathB = Path()
    val steps = 28
    for (i in 0..steps) {
        val t = i.toFloat() / steps.toFloat()
        val wave = sin(t * PI * 4.0).toFloat() * amp
        val pxA = start.x + dx * t + nx * wave
        val pyA = start.y + dy * t + ny * wave
        val pxB = start.x + dx * t - nx * wave
        val pyB = start.y + dy * t - ny * wave
        if (i == 0) {
            pathA.moveTo(pxA, pyA)
            pathB.moveTo(pxB, pyB)
        } else {
            pathA.lineTo(pxA, pyA)
            pathB.lineTo(pxB, pyB)
        }
    }

    val vineStroke = cell * 0.11f
    drawPath(pathA, color = CanopyNight.copy(alpha = 0.35f), style = Stroke(width = vineStroke * 1.2f, cap = StrokeCap.Round))
    drawPath(pathB, color = Color(connector.secondaryColorHex), style = Stroke(width = vineStroke, cap = StrokeCap.Round))
    drawPath(pathA, color = Color(connector.primaryColorHex), style = Stroke(width = vineStroke, cap = StrokeCap.Round))

    // Little bright vine leaves along the braid
    listOf(0.22f, 0.50f, 0.78f).forEachIndexed { idx, t ->
        val side = if (idx % 2 == 0) 1f else -1f
        val base = Offset(start.x + dx * t, start.y + dy * t)
        val leafTip = Offset(base.x + nx * cell * 0.26f * side, base.y + ny * cell * 0.26f * side)
        drawLine(
            color = EmeraldVibrant,
            start = base,
            end = leafTip,
            strokeWidth = cell * 0.11f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawJungleSerpent(
    start: Offset,
    end: Offset,
    cell: Float,
    serpent: BoardConnector
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = hypot(dx, dy).coerceAtLeast(1f)
    val nx = -dy / len
    val ny = dx / len
    val waveAmp = cell * 0.42f

    val bodyPath = Path()
    val steps = 36
    for (i in 0..steps) {
        val t = i.toFloat() / steps.toFloat()
        val envelope = sin(t * PI).toFloat().coerceAtLeast(0.18f)
        val wave = sin(t * PI * 3.0).toFloat() * waveAmp * envelope
        val px = start.x + dx * t + nx * wave
        val py = start.y + dy * t + ny * wave
        if (i == 0) bodyPath.moveTo(px, py) else bodyPath.lineTo(px, py)
    }

    val bodyWidth = cell * 0.24f
    // Shadow
    drawPath(
        path = bodyPath,
        color = CanopyNight.copy(alpha = 0.42f),
        style = Stroke(width = bodyWidth * 1.22f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Primary vibrant serpent skin
    drawPath(
        path = bodyPath,
        color = Color(serpent.primaryColorHex),
        style = Stroke(width = bodyWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Patterned scale stripes along back
    drawPath(
        path = bodyPath,
        color = Color(serpent.secondaryColorHex).copy(alpha = 0.88f),
        style = Stroke(
            width = bodyWidth * 0.48f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(cell * 0.16f, cell * 0.18f), 0f)
        )
    )

    // Serpent head at `start` (top square)
    val headRadius = cell * 0.22f
    val angle = atan2(dy, dx)
    val tongueDirX = -cos(angle)
    val tongueDirY = -sin(angle)

    // Forked crimson tongue
    val tongueBase = Offset(start.x + tongueDirX * headRadius * 0.85f, start.y + tongueDirY * headRadius * 0.85f)
    val tongueMid = Offset(start.x + tongueDirX * headRadius * 1.55f, start.y + tongueDirY * headRadius * 1.55f)
    val perpX = -tongueDirY
    val perpY = tongueDirX
    drawLine(Color(0xFFFF1744), tongueBase, tongueMid, strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    drawLine(
        Color(0xFFFF1744),
        tongueMid,
        Offset(tongueMid.x + (tongueDirX + perpX * 0.6f) * headRadius * 0.45f, tongueMid.y + (tongueDirY + perpY * 0.6f) * headRadius * 0.45f),
        strokeWidth = 1.3.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        Color(0xFFFF1744),
        tongueMid,
        Offset(tongueMid.x + (tongueDirX - perpX * 0.6f) * headRadius * 0.45f, tongueMid.y + (tongueDirY - perpY * 0.6f) * headRadius * 0.45f),
        strokeWidth = 1.3.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Head circle
    drawCircle(
        color = Color(serpent.primaryColorHex),
        radius = headRadius,
        center = start
    )
    drawCircle(
        color = CanopyNight.copy(alpha = 0.65f),
        radius = headRadius,
        center = start,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Expressive eyes
    val eyeOffsetFwd = headRadius * 0.25f
    val eyeOffsetSide = headRadius * 0.48f
    val leftEye = Offset(start.x + tongueDirX * eyeOffsetFwd + perpX * eyeOffsetSide, start.y + tongueDirY * eyeOffsetFwd + perpY * eyeOffsetSide)
    val rightEye = Offset(start.x + tongueDirX * eyeOffsetFwd - perpX * eyeOffsetSide, start.y + tongueDirY * eyeOffsetFwd - perpY * eyeOffsetSide)
    listOf(leftEye, rightEye).forEach { eye ->
        drawCircle(color = ParchmentCream, radius = headRadius * 0.34f, center = eye)
        drawCircle(color = CanopyNight, radius = headRadius * 0.18f, center = eye)
    }
}

private fun DrawScope.drawExplorerToken(
    player: PlayerState,
    center: Offset,
    radius: Float,
    isActive: Boolean,
    pulse: Float,
    textMeasurer: TextMeasurer
) {
    val main = Color(player.profile.colorHex)
    val dark = Color(player.profile.darkColorHex)

    if (isActive) {
        drawCircle(
            color = FireflyGlow.copy(alpha = 0.42f * pulse),
            radius = radius * (1.35f + 0.16f * pulse),
            center = center
        )
    }

    // Token drop shadow
    drawCircle(
        color = CanopyNight.copy(alpha = 0.65f),
        radius = radius,
        center = center + Offset(1.5.dp.toPx(), 2.5.dp.toPx())
    )

    // Token body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(main, dark),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius * 1.25f
        ),
        radius = radius,
        center = center
    )

    // Crisp rim
    drawCircle(
        color = if (isActive) FireflyGlow else ParchmentCream.copy(alpha = 0.92f),
        radius = radius,
        center = center,
        style = Stroke(width = if (isActive) 2.2.dp.toPx() else 1.4.dp.toPx())
    )

    // Mini emblem glyph or player number
    val label = "P${player.profile.id}"
    val fontSizeSp = (radius * 0.82f / density).coerceIn(7f, 12f).sp
    val measured = textMeasurer.measure(
        text = label,
        style = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSizeSp,
            color = if (player.profile.emblem == ExplorerEmblem.LANTERN) WoodDark else ParchmentCream
        )
    )
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(
            center.x - measured.size.width / 2f,
            center.y - measured.size.height / 2f
        )
    )
}
