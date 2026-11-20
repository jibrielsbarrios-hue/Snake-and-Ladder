package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.JungleSoundEngine
import com.example.model.BoardConfig
import com.example.model.BoardEventBanner
import com.example.model.ConnectorType
import com.example.model.ExpeditionLogEntry
import com.example.model.ExpeditionScreen
import com.example.model.GameUiState
import com.example.model.LogCategory
import com.example.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class JungleGameViewModel : ViewModel() {
    private val soundEngine = JungleSoundEngine()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var turnJob: Job? = null
    private var bannerClearJob: Job? = null
    private var nextLogId: Long = 1L

    fun openPlayerCountPrompt() {
        _uiState.update { it.copy(showPlayerCountDialog = true) }
    }

    fun dismissPlayerCountPrompt() {
        _uiState.update { it.copy(showPlayerCountDialog = false) }
    }

    fun openFieldGuide() {
        _uiState.update { it.copy(showFieldGuideDialog = true) }
    }

    fun closeFieldGuide() {
        _uiState.update { it.copy(showFieldGuideDialog = false) }
    }

    fun openExpeditionLog() {
        _uiState.update { it.copy(showExpeditionLogSheet = true) }
    }

    fun closeExpeditionLog() {
        _uiState.update { it.copy(showExpeditionLogSheet = false) }
    }

    fun requestReturnToCamp() {
        val state = _uiState.value
        if (state.winner != null || state.turnNumber <= 1) {
            confirmReturnToCamp()
        } else {
            _uiState.update { it.copy(showLeaveConfirmDialog = true) }
        }
    }

    fun dismissLeaveConfirm() {
        _uiState.update { it.copy(showLeaveConfirmDialog = false) }
    }

    fun confirmReturnToCamp() {
        turnJob?.cancel()
        bannerClearJob?.cancel()
        _uiState.update {
            it.copy(
                currentScreen = ExpeditionScreen.CampStart,
                showPlayerCountDialog = false,
                showLeaveConfirmDialog = false,
                showExpeditionLogSheet = false,
                isAnimatingTurn = false,
                isDiceRolling = false,
                animatingConnector = null,
                animatingConnectorProgress = null,
                animatingPlayerId = null,
                activeTrailHighlightSquare = null,
                eventBanner = null,
                winner = null
            )
        }
    }

    fun changePartyFromVictory() {
        turnJob?.cancel()
        bannerClearJob?.cancel()
        _uiState.update {
            it.copy(
                currentScreen = ExpeditionScreen.CampStart,
                showPlayerCountDialog = true,
                winner = null,
                eventBanner = null,
                isAnimatingTurn = false,
                isDiceRolling = false
            )
        }
    }

    fun toggleSound() {
        _uiState.update { state ->
            val next = !state.soundEnabled
            soundEngine.isEnabled = next
            state.copy(soundEnabled = next)
        }
    }

    fun startExpedition(playerCount: Int) {
        turnJob?.cancel()
        bannerClearJob?.cancel()
        val count = playerCount.coerceIn(2, 4)
        val party = BoardConfig.explorers.take(count).map { PlayerState(profile = it, position = 1) }
        val startLog = ExpeditionLogEntry(
            id = nextLogId++,
            playerName = "Expedition Camp",
            playerColorHex = 0xFFF6C244,
            message = "Party of $count explorers set out from Base Camp toward Square 100!",
            category = LogCategory.SYSTEM,
            turnNumber = 1
        )
        _uiState.update {
            it.copy(
                currentScreen = ExpeditionScreen.JungleBoard,
                showPlayerCountDialog = false,
                showLeaveConfirmDialog = false,
                showExpeditionLogSheet = false,
                players = party,
                currentPlayerIndex = 0,
                lastRoll = 5,
                isDiceRolling = false,
                isAnimatingTurn = false,
                activeTrailHighlightSquare = 1,
                animatingPlayerId = null,
                animatingConnector = null,
                animatingConnectorProgress = null,
                eventBanner = null,
                expeditionLog = listOf(startLog),
                turnNumber = 1,
                winner = null
            )
        }
    }

    fun playAgainSameParty() {
        val count = _uiState.value.players.size.coerceIn(2, 4)
        startExpedition(count)
    }

    fun rollDice() {
        val snapshot = _uiState.value
        if (snapshot.isAnimatingTurn || snapshot.winner != null || snapshot.players.isEmpty()) return

        bannerClearJob?.cancel()
        val activeIndex = snapshot.currentPlayerIndex
        val activePlayer = snapshot.players[activeIndex]
        val turn = snapshot.turnNumber

        turnJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnimatingTurn = true,
                    isDiceRolling = true,
                    eventBanner = null
                )
            }

            soundEngine.playDiceRoll()

            // Tumble the 3D die through intermediate faces before landing
            repeat(6) {
                delay(72L)
                _uiState.update { state ->
                    state.copy(lastRoll = Random.nextInt(1, 7))
                }
            }

            val rolled = Random.nextInt(1, 7)
            soundEngine.playDiceLand()
            _uiState.update { state ->
                val updatedPlayers = state.players.mapIndexed { idx, p ->
                    if (idx == activeIndex) p.copy(rolls = p.rolls + 1) else p
                }
                state.copy(
                    lastRoll = rolled,
                    isDiceRolling = false,
                    players = updatedPlayers
                )
            }

            delay(200L)

            val startPos = activePlayer.position
            val rawTarget = startPos + rolled
            val bounced = rawTarget > 100
            val hopPath = buildList {
                if (!bounced) {
                    for (sq in (startPos + 1)..rawTarget) add(sq)
                } else {
                    for (sq in (startPos + 1)..100) add(sq)
                    val finalBounce = 200 - rawTarget
                    for (sq in 99 downTo finalBounce) add(sq)
                }
            }

            hopPath.forEachIndexed { stepIdx, sq ->
                soundEngine.playFootstep(stepIdx)
                _uiState.update { state ->
                    val updatedPlayers = state.players.mapIndexed { idx, p ->
                        if (idx == activeIndex) p.copy(position = sq) else p
                    }
                    state.copy(
                        players = updatedPlayers,
                        activeTrailHighlightSquare = sq
                    )
                }
                delay(150L)
            }

            val landedSquare = hopPath.lastOrNull() ?: startPos
            if (bounced) {
                appendLog(
                    player = activePlayer,
                    message = "Rolled $rolled, reached Square 100 and bounced back to Square $landedSquare.",
                    category = LogCategory.BOUNCE,
                    turnNumber = turn
                )
            } else {
                appendLog(
                    player = activePlayer,
                    message = "Rolled $rolled and trekked from Square $startPos to Square $landedSquare.",
                    category = LogCategory.MOVE,
                    turnNumber = turn
                )
            }

            var finalSquare = landedSquare
            val connector = BoardConfig.connectorsByStart[landedSquare]
            if (connector != null) {
                delay(160L)
                val delta = kotlin.math.abs(connector.end - connector.start)
                val banner = when (connector.type) {
                    ConnectorType.BAMBOO_LADDER -> BoardEventBanner(
                        title = "${connector.name}! +$delta",
                        subtitle = "${activePlayer.profile.name} climbs ${connector.start} → ${connector.end}",
                        category = LogCategory.LADDER
                    )
                    ConnectorType.BRAIDED_VINE -> BoardEventBanner(
                        title = "${connector.name}! +$delta",
                        subtitle = "${activePlayer.profile.name} swings ${connector.start} → ${connector.end}",
                        category = LogCategory.VINE
                    )
                    ConnectorType.SERPENT -> BoardEventBanner(
                        title = "${connector.name}! -$delta",
                        subtitle = "${activePlayer.profile.name} slides ${connector.start} → ${connector.end}",
                        category = LogCategory.SNAKE
                    )
                }

                if (connector.type == ConnectorType.SERPENT) {
                    soundEngine.playSnakeSlide()
                } else {
                    soundEngine.playLadderClimb()
                }

                _uiState.update {
                    it.copy(
                        eventBanner = banner,
                        animatingPlayerId = activePlayer.profile.id,
                        animatingConnector = connector,
                        animatingConnectorProgress = 0f
                    )
                }

                val frames = 24
                for (frame in 1..frames) {
                    val progress = frame.toFloat() / frames.toFloat()
                    _uiState.update { it.copy(animatingConnectorProgress = progress) }
                    delay(24L)
                }

                finalSquare = connector.end
                _uiState.update { state ->
                    val updatedPlayers = state.players.mapIndexed { idx, p ->
                        if (idx == activeIndex) {
                            p.copy(
                                position = finalSquare,
                                climbs = p.climbs + if (connector.type != ConnectorType.SERPENT) 1 else 0,
                                slides = p.slides + if (connector.type == ConnectorType.SERPENT) 1 else 0
                            )
                        } else p
                    }
                    state.copy(
                        players = updatedPlayers,
                        activeTrailHighlightSquare = finalSquare,
                        animatingConnector = null,
                        animatingConnectorProgress = null,
                        animatingPlayerId = null
                    )
                }

                val logCat = when (connector.type) {
                    ConnectorType.BAMBOO_LADDER -> LogCategory.LADDER
                    ConnectorType.BRAIDED_VINE -> LogCategory.VINE
                    ConnectorType.SERPENT -> LogCategory.SNAKE
                }
                val actionVerb = if (connector.type == ConnectorType.SERPENT) "slid down" else "climbed"
                appendLog(
                    player = activePlayer,
                    message = "${connector.name}: $actionVerb from Square ${connector.start} to Square ${connector.end}!",
                    category = logCat,
                    turnNumber = turn
                )
            }

            if (finalSquare == 100) {
                soundEngine.playVictoryDrums()
                val winnerState = _uiState.value.players[activeIndex]
                appendLog(
                    player = winnerState,
                    message = "Reached the Golden Temple at Square 100 and won the expedition!",
                    category = LogCategory.VICTORY,
                    turnNumber = turn
                )
                _uiState.update {
                    it.copy(
                        isAnimatingTurn = false,
                        winner = winnerState,
                        eventBanner = null,
                        activeTrailHighlightSquare = 100
                    )
                }
            } else if (rolled == 6) {
                soundEngine.playBonusRoll()
                val bonusBanner = BoardEventBanner(
                    title = "Lucky 6! Bonus Roll",
                    subtitle = "${activePlayer.profile.name} rolls again!",
                    category = LogCategory.BONUS
                )
                appendLog(
                    player = activePlayer,
                    message = "Rolled a Lucky 6 and earned an extra roll!",
                    category = LogCategory.BONUS,
                    turnNumber = turn
                )
                _uiState.update {
                    it.copy(
                        isAnimatingTurn = false,
                        eventBanner = bonusBanner,
                        turnNumber = it.turnNumber + 1
                    )
                }
                scheduleBannerDismiss()
            } else {
                _uiState.update { state ->
                    val nextIdx = (activeIndex + 1) % state.players.size
                    state.copy(
                        isAnimatingTurn = false,
                        currentPlayerIndex = nextIdx,
                        turnNumber = state.turnNumber + 1
                    )
                }
                scheduleBannerDismiss()
            }
        }
    }

    private fun scheduleBannerDismiss() {
        bannerClearJob?.cancel()
        bannerClearJob = viewModelScope.launch {
            delay(2600L)
            _uiState.update { it.copy(eventBanner = null) }
        }
    }

    private fun appendLog(
        player: PlayerState,
        message: String,
        category: LogCategory,
        turnNumber: Int
    ) {
        val entry = ExpeditionLogEntry(
            id = nextLogId++,
            playerName = "${player.profile.name} (${player.profile.title})",
            playerColorHex = player.profile.colorHex,
            message = message,
            category = category,
            turnNumber = turnNumber
        )
        _uiState.update { state ->
            state.copy(expeditionLog = listOf(entry) + state.expeditionLog)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.release()
    }
}
