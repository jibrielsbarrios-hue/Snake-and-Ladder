package com.example

import com.example.model.BoardConfig
import com.example.model.ConnectorType
import com.example.ui.components.squareToGridColRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun serpentineBoardCoordinates_areCorrect() {
        // Square 1 at bottom-left (col 0, row 9)
        assertEquals(0 to 9, squareToGridColRow(1))
        // Square 10 at bottom-right (col 9, row 9)
        assertEquals(9 to 9, squareToGridColRow(10))
        // Square 11 directly above Square 10 (col 9, row 8)
        assertEquals(9 to 8, squareToGridColRow(11))
        // Square 20 at col 0, row 8
        assertEquals(0 to 8, squareToGridColRow(20))
        // Square 100 at top-left (col 0, row 0)
        assertEquals(0 to 0, squareToGridColRow(100))
    }

    @Test
    fun boardConnectors_haveNoCyclesOrOverlaps() {
        val starts = BoardConfig.connectors.map { it.start }
        val ends = BoardConfig.connectors.map { it.end }
        val allEndpoints = starts + ends

        assertEquals(allEndpoints.size, allEndpoints.toSet().size)
        assertTrue(allEndpoints.none { it == 1 || it == 100 })
        BoardConfig.connectors.forEach { connector ->
            if (connector.type == ConnectorType.SERPENT) {
                assertTrue(connector.start > connector.end)
            } else {
                assertTrue(connector.end > connector.start)
            }
        }
    }
}
