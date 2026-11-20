package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.BoardEventBanner
import com.example.model.GameUiState
import com.example.model.LogCategory
import com.example.model.PlayerState
import com.example.ui.components.CarvedWoodButton
import com.example.ui.components.CarvedWoodPanel
import com.example.ui.components.ExpeditionLogRow
import com.example.ui.components.ExplorerBadgeIcon
import com.example.ui.components.FireflyField
import com.example.ui.components.JungleBoardCanvas
import com.example.ui.components.JungleCampBackdrop
import com.example.ui.components.SwayingCanopyVines
import com.example.ui.components.WoodenDie3D
import com.example.ui.theme.CanopyDeep
import com.example.ui.theme.CanopyNight
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FireflyGlow
import com.example.ui.theme.JungleMossDark
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.SerpentCrimson
import com.example.ui.theme.SunbeamGold
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodHighlight

@Composable
fun JungleBoardScreen(
    uiState: GameUiState,
    onRollDice: () -> Unit,
    onRequestBackToCamp: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenFieldGuide: () -> Unit,
    onOpenExpeditionLog: () -> Unit
) {
    BackHandler(onBack = onRequestBackToCamp)

    Box(modifier = Modifier.fillMaxSize()) {
        JungleCampBackdrop(showCampRuins = false)
        FireflyField(count = 14)
        SwayingCanopyVines(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 76.dp
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            val isWideLayout = maxWidth >= 680.dp

            if (isWideLayout) {
                // Tablet / Landscape Canonical Split-Pane Layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        JungleBoardCanvas(
                            players = uiState.players,
                            currentPlayerIndex = uiState.currentPlayerIndex,
                            activeTrailHighlightSquare = uiState.activeTrailHighlightSquare,
                            animatingPlayerId = uiState.animatingPlayerId,
                            animatingConnector = uiState.animatingConnector,
                            animatingConnectorProgress = uiState.animatingConnectorProgress,
                            modifier = Modifier.fillMaxHeight(0.96f)
                        )
                        FloatingEventBanner(
                            banner = uiState.eventBanner,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BoardTopBar(
                            turnNumber = uiState.turnNumber,
                            soundEnabled = uiState.soundEnabled,
                            onBackClick = onRequestBackToCamp,
                            onFieldGuideClick = onOpenFieldGuide,
                            onJournalClick = onOpenExpeditionLog,
                            onToggleSound = onToggleSound
                        )

                        ExplorerPartyStrip(
                            players = uiState.players,
                            currentPlayerIndex = uiState.currentPlayerIndex
                        )

                        DiceControlDeck(
                            uiState = uiState,
                            onRollDice = onRollDice
                        )

                        // Embedded Live Expedition Journal feed on wide screens
                        CarvedWoodPanel(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cornerRadius = 18.dp,
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LIVE EXPEDITION JOURNAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SunbeamGold
                                    )
                                    Text(
                                        text = "${uiState.expeditionLog.size} logs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ParchmentMuted
                                    )
                                }
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(uiState.expeditionLog, key = { it.id }) { entry ->
                                        ExpeditionLogRow(entry = entry)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Compact Portrait Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoardTopBar(
                        turnNumber = uiState.turnNumber,
                        soundEnabled = uiState.soundEnabled,
                        onBackClick = onRequestBackToCamp,
                        onFieldGuideClick = onOpenFieldGuide,
                        onJournalClick = onOpenExpeditionLog,
                        onToggleSound = onToggleSound
                    )

                    ExplorerPartyStrip(
                        players = uiState.players,
                        currentPlayerIndex = uiState.currentPlayerIndex
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        JungleBoardCanvas(
                            players = uiState.players,
                            currentPlayerIndex = uiState.currentPlayerIndex,
                            activeTrailHighlightSquare = uiState.activeTrailHighlightSquare,
                            animatingPlayerId = uiState.animatingPlayerId,
                            animatingConnector = uiState.animatingConnector,
                            animatingConnectorProgress = uiState.animatingConnectorProgress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        FloatingEventBanner(
                            banner = uiState.eventBanner,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                        )
                    }

                    DiceControlDeck(
                        uiState = uiState,
                        onRollDice = onRollDice
                    )

                    LiveJournalTicker(
                        uiState = uiState,
                        onOpenJournal = onOpenExpeditionLog
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardTopBar(
    turnNumber: Int,
    soundEnabled: Boolean,
    onBackClick: () -> Unit,
    onFieldGuideClick: () -> Unit,
    onJournalClick: () -> Unit,
    onToggleSound: () -> Unit
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
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("back_to_camp_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Return to Base Camp",
                        tint = ParchmentCream
                    )
                }
            }

            Column {
                Text(
                    text = "JUNGLE QUEST",
                    style = MaterialTheme.typography.titleMedium,
                    color = FireflyGlow
                )
                Text(
                    text = "Turn $turnNumber • Race to 100",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentMuted
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onJournalClick,
                    modifier = Modifier.testTag("open_journal_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.HistoryEdu,
                        contentDescription = "Open Expedition Journal",
                        tint = SunbeamGold
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onFieldGuideClick,
                    modifier = Modifier.testTag("board_field_guide_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MenuBook,
                        contentDescription = "Open Field Guide",
                        tint = ParchmentCream
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = WoodDark.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, WoodHighlight.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("board_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                        contentDescription = if (soundEnabled) "Mute sound effects" else "Enable sound effects",
                        tint = if (soundEnabled) FireflyGlow else ParchmentMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplorerPartyStrip(
    players: List<PlayerState>,
    currentPlayerIndex: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        players.forEachIndexed { idx, player ->
            val isTurn = idx == currentPlayerIndex
            val accent = Color(player.profile.colorHex)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = if (isTurn) JungleMossDark else CanopyDeep.copy(alpha = 0.82f),
                border = BorderStroke(
                    width = if (isTurn) 2.dp else 1.dp,
                    color = if (isTurn) FireflyGlow else accent.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExplorerBadgeIcon(profile = player.profile, size = 26.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "P${player.profile.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isTurn) FireflyGlow else ParchmentMuted,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "Sq ${player.position}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ParchmentCream,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceControlDeck(
    uiState: GameUiState,
    onRollDice: () -> Unit
) {
    val active = uiState.currentPlayer
    val canRoll = !uiState.isAnimatingTurn && uiState.winner == null && active != null
    val activeColor = active?.let { Color(it.profile.colorHex) } ?: SunbeamGold

    CarvedWoodPanel(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        borderColor = if (canRoll) activeColor.copy(alpha = 0.9f) else WoodHighlight,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WoodenDie3D(
                faceValue = uiState.lastRoll,
                isRolling = uiState.isDiceRolling,
                enabled = canRoll,
                onClick = onRollDice,
                dieSize = 68.dp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (active != null) "${active.profile.name}'s Turn" else "Expedition Ready",
                    style = MaterialTheme.typography.titleMedium,
                    color = activeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when {
                        uiState.isDiceRolling -> "Rolling carved die..."
                        uiState.animatingConnector != null -> uiState.animatingConnector.name
                        uiState.isAnimatingTurn -> "Trekking along the trail..."
                        active != null -> "${active.profile.title} • Square ${active.position}/100"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            CarvedWoodButton(
                onClick = onRollDice,
                enabled = canRoll,
                cornerRadius = 16.dp,
                modifier = Modifier.testTag("roll_dice_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Casino,
                        contentDescription = null,
                        tint = WoodDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (uiState.isAnimatingTurn) "..." else "ROLL",
                        style = MaterialTheme.typography.labelLarge,
                        color = WoodDark
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveJournalTicker(
    uiState: GameUiState,
    onOpenJournal: () -> Unit
) {
    val latest = uiState.expeditionLog.firstOrNull()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenJournal)
            .testTag("journal_ticker_button"),
        shape = RoundedCornerShape(14.dp),
        color = CanopyDeep.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, WoodHighlight.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.HistoryEdu,
                contentDescription = null,
                tint = SunbeamGold,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = latest?.let { "${it.playerName.substringBefore(" (")}: ${it.message}" }
                    ?: "Tap ROLL to begin your rainforest journey!",
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentCream,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "LOG",
                style = MaterialTheme.typography.labelSmall,
                color = SunbeamGold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FloatingEventBanner(
    banner: BoardEventBanner?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = banner != null,
        enter = fadeIn() + slideInVertically { -it / 2 },
        exit = fadeOut() + slideOutVertically { -it / 2 },
        modifier = modifier
    ) {
        if (banner != null) {
            val accent = when (banner.category) {
                LogCategory.LADDER, LogCategory.VINE -> EmeraldVibrant
                LogCategory.SNAKE -> SerpentCrimson
                LogCategory.BONUS, LogCategory.VICTORY -> SunbeamGold
                else -> FireflyGlow
            }
            val icon = when (banner.category) {
                LogCategory.LADDER, LogCategory.VINE -> Icons.Rounded.TrendingUp
                LogCategory.SNAKE -> Icons.Rounded.TrendingDown
                else -> Icons.Rounded.AutoAwesome
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = CanopyNight.copy(alpha = 0.92f),
                border = BorderStroke(2.dp, accent),
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = accent
                        )
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = ParchmentCream
                        )
                    }
                }
            }
        }
    }
}
