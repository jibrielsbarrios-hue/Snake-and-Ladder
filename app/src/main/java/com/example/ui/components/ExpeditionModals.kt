package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ExpeditionLogEntry
import com.example.model.LogCategory
import com.example.model.PlayerState
import com.example.ui.theme.BambooYellowGreen
import com.example.ui.theme.CanopyDeep
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.JungleMossDark
import com.example.ui.theme.JungleMossMid
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.SerpentCrimson
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight
import com.example.ui.theme.WoodMid

@Composable
fun FieldGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CarvedWoodPanel(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp),
            contentPadding = PaddingValues(18.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = SunbeamGold
                        )
                        Text(
                            text = "Expedition Field Guide",
                            style = MaterialTheme.typography.titleLarge,
                            color = ParchmentCream
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_field_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close field guide",
                            tint = ParchmentMuted
                        )
                    }
                }

                GuideRuleCard(
                    title = "Bamboo Ladders & Braided Vines",
                    description = "Land on the base of a ladder or hanging vine to climb ahead up the rainforest canopy.",
                    accent = EmeraldVibrant,
                    icon = {
                        Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = EmeraldVibrant)
                    }
                )
                GuideRuleCard(
                    title = "Jungle Serpents",
                    description = "Watch your step! Landing on a serpent's head sends your explorer sliding down to its tail.",
                    accent = SerpentCrimson,
                    icon = {
                        Icon(Icons.Rounded.TrendingDown, contentDescription = null, tint = SerpentCrimson)
                    }
                )
                GuideRuleCard(
                    title = "Lucky 6 Bonus Roll",
                    description = "Rolling a 6 on the carved wooden die grants an immediate bonus turn!",
                    accent = SunbeamGold,
                    icon = {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = SunbeamGold)
                    }
                )
                GuideRuleCard(
                    title = "Summit 100 Bounce Rule",
                    description = "Reach Square 100 exactly to claim the Golden Temple. Overshooting bounces your explorer back the extra steps!",
                    accent = BambooYellowGreen,
                    icon = {
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = BambooYellowGreen)
                    }
                )

                CarvedWoodButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "GOT IT",
                        style = MaterialTheme.typography.labelLarge,
                        color = WoodDark
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideRuleCard(
    title: String,
    description: String,
    accent: Color,
    icon: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CanopyDeep.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentCream
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeditionLogSheet(
    entries: List<ExpeditionLogEntry>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CanopyDeep,
        contentColor = ParchmentCream
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Rounded.HistoryEdu,
                        contentDescription = null,
                        tint = SunbeamGold
                    )
                    Text(
                        text = "Expedition Journal",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentCream
                    )
                }
                Text(
                    text = "${entries.size} entries",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentMuted
                )
            }

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Roll the wooden die to record your party's first steps!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ParchmentMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        ExpeditionLogRow(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpeditionLogRow(entry: ExpeditionLogEntry) {
    val accent = when (entry.category) {
        LogCategory.LADDER, LogCategory.VINE -> EmeraldVibrant
        LogCategory.SNAKE -> SerpentCrimson
        LogCategory.BONUS, LogCategory.VICTORY -> SunbeamGold
        LogCategory.BOUNCE -> Color(0xFFFF9E40)
        LogCategory.MOVE, LogCategory.SYSTEM -> ParchmentMuted
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = JungleMossDark.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(entry.playerColorHex))
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.playerName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(entry.playerColorHex),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Turn ${entry.turnNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentMuted.copy(alpha = 0.75f)
                    )
                }
                Text(
                    text = entry.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentCream
                )
            }
        }
    }
}

@Composable
fun LeaveExpeditionDialog(
    onConfirmLeave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CarvedWoodPanel(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp),
            contentPadding = PaddingValues(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Return to Base Camp?",
                    style = MaterialTheme.typography.titleLarge,
                    color = ParchmentCream,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your current trek toward Square 100 will end so you can pick a new party size.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CarvedWoodButton(
                        onClick = onDismiss,
                        isGoldAccent = false,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "STAY",
                            style = MaterialTheme.typography.labelLarge,
                            color = ParchmentCream
                        )
                    }
                    CarvedWoodButton(
                        onClick = onConfirmLeave,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_leave_button")
                    ) {
                        Text(
                            text = "BASE CAMP",
                            style = MaterialTheme.typography.labelLarge,
                            color = WoodDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VictoryCelebrationOverlay(
    winner: PlayerState,
    players: List<PlayerState>,
    onPlayAgain: () -> Unit,
    onChangeParty: () -> Unit
) {
    val ranked = players.sortedByDescending { it.position }
    val infinite = rememberInfiniteTransition(label = "victory_rays")
    val shimmer by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CanopyNight.copy(alpha = 0.82f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            FireflyField(count = 28)

            CarvedWoodPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp),
                borderColor = SunbeamGold,
                contentPadding = PaddingValues(20.dp)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Summit Temple Illustration Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(126.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1D4F2D), Color(0xFF0E2B19))
                                )
                            )
                            // Golden temple sunrays
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        FireflyGlow.copy(alpha = 0.65f),
                                        SunbeamGold.copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    center = Offset(w * 0.5f, h * 0.52f),
                                    radius = w * 0.55f
                                ),
                                center = Offset(w * 0.5f, h * 0.52f),
                                radius = w * 0.55f
                            )
                            // Stepped Golden Temple silhouette
                            val temple = Path().apply {
                                moveTo(w * 0.22f, h)
                                lineTo(w * 0.30f, h * 0.68f)
                                lineTo(w * 0.37f, h * 0.68f)
                                lineTo(w * 0.42f, h * 0.44f)
                                lineTo(w * 0.58f, h * 0.44f)
                                lineTo(w * 0.63f, h * 0.68f)
                                lineTo(w * 0.70f, h * 0.68f)
                                lineTo(w * 0.78f, h)
                                close()
                            }
                            drawPath(
                                path = temple,
                                brush = Brush.verticalGradient(listOf(SunbeamGold, Color(0xFF9C6615)))
                            )
                            // Drifting celebratory petals
                            for (i in 0..11) {
                                val px = ((i * 83 + shimmer * 120f) % w)
                                val py = ((i * 47 + shimmer * h) % h)
                                drawCircle(
                                    color = if (i % 2 == 0) FireflyGlow else Color(0xFFFF6E40),
                                    radius = 4.dp.toPx(),
                                    center = Offset(px, py)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ExplorerBadgeIcon(profile = winner.profile, size = 54.dp)
                        }
                    }

                    Text(
                        text = "SUMMIT CONQUERED!",
                        style = MaterialTheme.typography.labelLarge,
                        color = SunbeamGold
                    )
                    Text(
                        text = "${winner.profile.name} Wins!",
                        style = MaterialTheme.typography.displaySmall,
                        color = ParchmentCream,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${winner.profile.title} reached Square 100 in ${winner.rolls} rolls (${winner.climbs} climbs, ${winner.slides} slides).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentMuted,
                        textAlign = TextAlign.Center
                    )

                    // Final Expedition Standings
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CanopyDeep.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, WoodHighlight.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Final Expedition Standings",
                                style = MaterialTheme.typography.labelSmall,
                                color = SunbeamGold
                            )
                            ranked.forEachIndexed { idx, p ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "#${idx + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (idx == 0) SunbeamGold else ParchmentMuted
                                        )
                                        ExplorerBadgeIcon(profile = p.profile, size = 26.dp)
                                        Text(
                                            text = "${p.profile.name} • ${p.profile.title}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ParchmentCream
                                        )
                                    }
                                    Text(
                                        text = "Sq ${p.position}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(p.profile.colorHex)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CarvedWoodButton(
                            onClick = onChangeParty,
                            isGoldAccent = false,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("change_party_button")
                        ) {
                            Text(
                                text = "PARTY SIZE",
                                style = MaterialTheme.typography.labelLarge,
                                color = ParchmentCream
                            )
                        }
                        CarvedWoodButton(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("play_again_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    tint = WoodDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "PLAY AGAIN",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WoodDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
