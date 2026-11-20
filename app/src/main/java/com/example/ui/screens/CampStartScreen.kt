package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.BoardConfig
import com.example.ui.components.CarvedWoodButton
import com.example.ui.components.CarvedWoodPanel
import com.example.ui.components.ExplorerBadgeIcon
import com.example.ui.components.FireflyField
import com.example.ui.components.JungleCampBackdrop
import com.example.ui.components.SwayingCanopyVines
import com.example.ui.theme.BambooYellowGreen
import com.example.ui.theme.CanopyDeep
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.JungleMossDark
import com.example.ui.theme.JungleMossMid
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight
import com.example.ui.theme.WoodMid

@Composable
fun CampStartScreen(
    soundEnabled: Boolean,
    showPlayerCountDialog: Boolean,
    onPlayClick: () -> Unit,
    onDismissPlayerPrompt: () -> Unit,
    onSelectPlayerCount: (Int) -> Unit,
    onToggleSound: () -> Unit,
    onOpenFieldGuide: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        JungleCampBackdrop(showCampRuins = true)
        FireflyField(count = 24)
        SwayingCanopyVines(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 128.dp
        )

        // Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.78f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onOpenFieldGuide,
                    modifier = Modifier.testTag("camp_field_guide_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MenuBook,
                        contentDescription = "Open Expedition Field Guide",
                        tint = SunbeamGold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.78f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("camp_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                        contentDescription = if (soundEnabled) "Mute jungle sounds" else "Enable jungle sounds",
                        tint = if (soundEnabled) FireflyGlow else ParchmentMuted
                    )
                }
            }
        }

        // Main Camp Emblem & Play Call-To-Action
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .align(Alignment.Center)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExpeditionCrestEmblem()

            CarvedWoodPanel(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                borderColor = SunbeamGold,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "JUNGLE QUEST",
                        style = MaterialTheme.typography.displayLarge,
                        color = FireflyGlow,
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = JungleMossMid,
                        border = BorderStroke(1.5.dp, EmeraldVibrant.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = "SNAKES & LADDERS",
                            style = MaterialTheme.typography.labelLarge,
                            color = ParchmentCream,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gather 2 to 4 explorers around one screen. Climb braided vines, dodge forest serpents, and race to Square 100!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ParchmentMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            CarvedWoodButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .widthIn(min = 240.dp, max = 320.dp)
                    .fillMaxWidth(0.75f)
                    .height(64.dp)
                    .testTag("play_button"),
                cornerRadius = 22.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = WoodDark,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "PLAY",
                        style = MaterialTheme.typography.headlineMedium,
                        color = WoodDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Party badges preview strip
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.navigationBarsPadding()
            ) {
                BoardConfig.explorers.forEach { explorer ->
                    ExplorerBadgeIcon(profile = explorer, size = 32.dp)
                }
            }
        }

        if (showPlayerCountDialog) {
            PlayerCountPromptDialog(
                onSelectCount = onSelectPlayerCount,
                onDismiss = onDismissPlayerPrompt
            )
        }
    }
}

@Composable
private fun ExpeditionCrestEmblem() {
    Canvas(modifier = Modifier.size(124.dp)) {
        val w = size.width
        val h = size.height

        // Glowing sunlit medallion
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SunbeamGold.copy(alpha = 0.45f),
                    JungleMossMid.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.56f
            ),
            radius = w * 0.56f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // Carved wooden shield backing
        drawCircle(
            brush = Brush.verticalGradient(listOf(WoodMid, WoodDark)),
            radius = w * 0.41f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
        drawCircle(
            color = SunbeamGold,
            radius = w * 0.41f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Bamboo ladder inside emblem
        drawLine(BambooYellowGreen, Offset(w * 0.34f, h * 0.24f), Offset(w * 0.34f, h * 0.74f), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(BambooYellowGreen, Offset(w * 0.49f, h * 0.24f), Offset(w * 0.49f, h * 0.74f), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
        listOf(0.34f, 0.48f, 0.62f).forEach { yFrac ->
            drawLine(ParchmentCream, Offset(w * 0.32f, h * yFrac), Offset(w * 0.51f, h * yFrac), strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
        }

        // Carved wooden die in foreground right
        drawRoundRect(
            color = WoodDark,
            topLeft = Offset(w * 0.46f, h * 0.35f),
            size = Size(w * 0.32f, h * 0.32f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(FireflyGlow, SunbeamGold)),
            topLeft = Offset(w * 0.48f, h * 0.37f),
            size = Size(w * 0.28f, h * 0.28f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawCircle(WoodDark, radius = 3.5.dp.toPx(), center = Offset(w * 0.56f, h * 0.45f))
        drawCircle(WoodDark, radius = 3.5.dp.toPx(), center = Offset(w * 0.68f, h * 0.45f))
        drawCircle(WoodDark, radius = 3.5.dp.toPx(), center = Offset(w * 0.62f, h * 0.51f))
        drawCircle(WoodDark, radius = 3.5.dp.toPx(), center = Offset(w * 0.56f, h * 0.57f))
        drawCircle(WoodDark, radius = 3.5.dp.toPx(), center = Offset(w * 0.68f, h * 0.57f))

        // Coiling emerald serpent around bottom
        val snakePath = Path().apply {
            moveTo(w * 0.24f, h * 0.70f)
            cubicTo(w * 0.36f, h * 0.58f, w * 0.54f, h * 0.80f, w * 0.72f, h * 0.72f)
            quadraticTo(w * 0.82f, h * 0.68f, w * 0.78f, h * 0.58f)
        }
        drawPath(snakePath, color = EmeraldVibrant, style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(EmeraldVibrant, radius = 6.dp.toPx(), center = Offset(w * 0.78f, h * 0.58f))
        drawCircle(CanopyNight, radius = 1.8.dp.toPx(), center = Offset(w * 0.79f, h * 0.56f))
    }
}

@Composable
fun PlayerCountPromptDialog(
    onSelectCount: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CarvedWoodPanel(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            borderColor = SunbeamGold,
            contentPadding = PaddingValues(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Groups,
                            contentDescription = null,
                            tint = SunbeamGold
                        )
                        Text(
                            text = "EXPEDITION PARTY",
                            style = MaterialTheme.typography.labelLarge,
                            color = SunbeamGold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_player_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close party selection",
                            tint = ParchmentMuted
                        )
                    }
                }

                Text(
                    text = "How many explorers are joining the expedition?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ParchmentCream,
                    textAlign = TextAlign.Center
                )

                listOf(2, 3, 4).forEach { count ->
                    PlayerCountOptionCard(
                        playerCount = count,
                        onClick = { onSelectCount(count) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerCountOptionCard(
    playerCount: Int,
    onClick: () -> Unit
) {
    val partyExplorers = BoardConfig.explorers.take(playerCount)
    val subtitle = when (playerCount) {
        2 -> "Head-to-head jungle duel"
        3 -> "Three-way canopy race"
        else -> "Full four-explorer expedition"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("select_${playerCount}_players")
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = JungleMossDark.copy(alpha = 0.92f),
        border = BorderStroke(2.dp, WoodHighlight.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "$playerCount Players",
                    style = MaterialTheme.typography.titleLarge,
                    color = FireflyGlow
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                partyExplorers.forEach { profile ->
                    ExplorerBadgeIcon(profile = profile, size = 34.dp)
                }
            }
        }
    }
}
