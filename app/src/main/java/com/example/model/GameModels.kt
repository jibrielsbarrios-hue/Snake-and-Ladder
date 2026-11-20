package com.example.model

enum class ConnectorType {
    BAMBOO_LADDER,
    BRAIDED_VINE,
    SERPENT
}

data class BoardConnector(
    val start: Int,
    val end: Int,
    val type: ConnectorType,
    val name: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long
)

enum class ExplorerEmblem {
    COMPASS,
    FEATHER,
    LANTERN,
    LEAF
}

data class ExplorerProfile(
    val id: Int,
    val name: String,
    val title: String,
    val colorHex: Long,
    val darkColorHex: Long,
    val emblem: ExplorerEmblem
)

data class PlayerState(
    val profile: ExplorerProfile,
    val position: Int = 1,
    val climbs: Int = 0,
    val slides: Int = 0,
    val rolls: Int = 0
)

enum class LogCategory {
    MOVE,
    LADDER,
    VINE,
    SNAKE,
    BONUS,
    BOUNCE,
    VICTORY,
    SYSTEM
}

data class ExpeditionLogEntry(
    val id: Long,
    val playerName: String,
    val playerColorHex: Long,
    val message: String,
    val category: LogCategory,
    val turnNumber: Int
)

data class BoardEventBanner(
    val title: String,
    val subtitle: String,
    val category: LogCategory
)

sealed interface ExpeditionScreen {
    data object CampStart : ExpeditionScreen
    data object JungleBoard : ExpeditionScreen
}

data class GameUiState(
    val currentScreen: ExpeditionScreen = ExpeditionScreen.CampStart,
    val showPlayerCountDialog: Boolean = false,
    val showFieldGuideDialog: Boolean = false,
    val showExpeditionLogSheet: Boolean = false,
    val showLeaveConfirmDialog: Boolean = false,
    val soundEnabled: Boolean = true,
    val players: List<PlayerState> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val lastRoll: Int = 5,
    val isDiceRolling: Boolean = false,
    val isAnimatingTurn: Boolean = false,
    val activeTrailHighlightSquare: Int? = null,
    val animatingPlayerId: Int? = null,
    val animatingConnectorProgress: Float? = null,
    val animatingConnector: BoardConnector? = null,
    val eventBanner: BoardEventBanner? = null,
    val expeditionLog: List<ExpeditionLogEntry> = emptyList(),
    val turnNumber: Int = 1,
    val winner: PlayerState? = null
) {
    val currentPlayer: PlayerState?
        get() = players.getOrNull(currentPlayerIndex)
}

object BoardConfig {
    val explorers = listOf(
        ExplorerProfile(
            id = 1,
            name = "Explorer 1",
            title = "Coral Toucan",
            colorHex = 0xFFFF6E40,
            darkColorHex = 0xFFB73A14,
            emblem = ExplorerEmblem.COMPASS
        ),
        ExplorerProfile(
            id = 2,
            name = "Explorer 2",
            title = "Lagoon Scout",
            colorHex = 0xFF26C6DA,
            darkColorHex = 0xFF007685,
            emblem = ExplorerEmblem.FEATHER
        ),
        ExplorerProfile(
            id = 3,
            name = "Explorer 3",
            title = "Sunburst Guide",
            colorHex = 0xFFFFCA28,
            darkColorHex = 0xFFA67C00,
            emblem = ExplorerEmblem.LANTERN
        ),
        ExplorerProfile(
            id = 4,
            name = "Explorer 4",
            title = "Hibiscus Ranger",
            colorHex = 0xFFEC407A,
            darkColorHex = 0xFF9C1647,
            emblem = ExplorerEmblem.LEAF
        )
    )

    val connectors = listOf(
        // Ladders & Braided Vines (7 upward shortcuts)
        BoardConnector(4, 25, ConnectorType.BRAIDED_VINE, "Strangler Vine", 0xFF66BB6A, 0xFF2E7D32),
        BoardConnector(13, 46, ConnectorType.BAMBOO_LADDER, "Canopy Bamboo Ladder", 0xFFDCE775, 0xFF827717),
        BoardConnector(33, 49, ConnectorType.BRAIDED_VINE, "Mossy Liana", 0xFF81C784, 0xFF1B5E20),
        BoardConnector(42, 63, ConnectorType.BAMBOO_LADDER, "Riverbank Ladder", 0xFFC5E063, 0xFF689F38),
        BoardConnector(50, 69, ConnectorType.BRAIDED_VINE, "Orchid Swing Vine", 0xFF4CAF50, 0xFF2E7D32),
        BoardConnector(62, 81, ConnectorType.BAMBOO_LADDER, "Ancient Rope Ladder", 0xFFD4E157, 0xFF827717),
        BoardConnector(74, 92, ConnectorType.BRAIDED_VINE, "Summit Canopy Vine", 0xFF66BB6A, 0xFF1B5E20),

        // Jungle Serpents (7 downward slides)
        BoardConnector(27, 5, ConnectorType.SERPENT, "Emerald Tree Boa", 0xFF00E676, 0xFF00695C),
        BoardConnector(40, 3, ConnectorType.SERPENT, "Coral Jungle Viper", 0xFFFF5252, 0xFFFFAB40),
        BoardConnector(43, 18, ConnectorType.SERPENT, "Golden Python", 0xFFFFCA28, 0xFFE65100),
        BoardConnector(54, 31, ConnectorType.SERPENT, "Jade Bushmaster", 0xFF26A69A, 0xFF004D40),
        BoardConnector(66, 45, ConnectorType.SERPENT, "Orchid Pit Viper", 0xFFAB47BC, 0xFFF06292),
        BoardConnector(89, 53, ConnectorType.SERPENT, "Crimson Rain Serpent", 0xFFEF5350, 0xFF880E4F),
        BoardConnector(97, 78, ConnectorType.SERPENT, "Temple Guardian Cobra", 0xFF29B6F6, 0xFF01579B)
    )

    val connectorsByStart: Map<Int, BoardConnector> = connectors.associateBy { it.start }
}
