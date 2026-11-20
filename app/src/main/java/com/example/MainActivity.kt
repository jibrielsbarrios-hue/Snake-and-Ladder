package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ExpeditionScreen
import com.example.ui.components.ExpeditionLogSheet
import com.example.ui.components.FieldGuideDialog
import com.example.ui.components.LeaveExpeditionDialog
import com.example.ui.components.VictoryCelebrationOverlay
import com.example.ui.screens.CampStartScreen
import com.example.ui.screens.JungleBoardScreen
import com.example.ui.theme.JungleQuestTheme
import com.example.viewmodel.JungleGameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            JungleQuestTheme {
                JungleQuestApp()
            }
        }
    }
}

@Composable
fun JungleQuestApp(
    gameViewModel: JungleGameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()

    Crossfade(
        targetState = uiState.currentScreen,
        animationSpec = tween(durationMillis = 320),
        label = "screen_crossfade"
    ) { screen ->
        when (screen) {
            ExpeditionScreen.CampStart -> {
                CampStartScreen(
                    soundEnabled = uiState.soundEnabled,
                    showPlayerCountDialog = uiState.showPlayerCountDialog,
                    onPlayClick = gameViewModel::openPlayerCountPrompt,
                    onDismissPlayerPrompt = gameViewModel::dismissPlayerCountPrompt,
                    onSelectPlayerCount = gameViewModel::startExpedition,
                    onToggleSound = gameViewModel::toggleSound,
                    onOpenFieldGuide = gameViewModel::openFieldGuide
                )
            }

            ExpeditionScreen.JungleBoard -> {
                JungleBoardScreen(
                    uiState = uiState,
                    onRollDice = gameViewModel::rollDice,
                    onRequestBackToCamp = gameViewModel::requestReturnToCamp,
                    onToggleSound = gameViewModel::toggleSound,
                    onOpenFieldGuide = gameViewModel::openFieldGuide,
                    onOpenExpeditionLog = gameViewModel::openExpeditionLog
                )
            }
        }
    }

    if (uiState.showFieldGuideDialog) {
        FieldGuideDialog(onDismiss = gameViewModel::closeFieldGuide)
    }

    if (uiState.showExpeditionLogSheet) {
        ExpeditionLogSheet(
            entries = uiState.expeditionLog,
            onDismiss = gameViewModel::closeExpeditionLog
        )
    }

    if (uiState.showLeaveConfirmDialog) {
        LeaveExpeditionDialog(
            onConfirmLeave = gameViewModel::confirmReturnToCamp,
            onDismiss = gameViewModel::dismissLeaveConfirm
        )
    }

    val winner = uiState.winner
    if (winner != null) {
        VictoryCelebrationOverlay(
            winner = winner,
            players = uiState.players,
            onPlayAgain = gameViewModel::playAgainSameParty,
            onChangeParty = gameViewModel::changePartyFromVictory
        )
    }
}
