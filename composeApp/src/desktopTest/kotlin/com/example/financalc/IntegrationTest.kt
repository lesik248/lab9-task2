package com.example.financalc

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.example.financalc.calculator.Capitalization
import com.example.financalc.data.HistoryEntry
import com.example.financalc.data.InMemoryHistoryStore
import com.example.financalc.i18n.Language
import com.example.financalc.i18n.stringsFor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class IntegrationTest {

    @Test
    fun end_to_end_calculation_persists_to_history_store() = runComposeUiTest {
        val store = InMemoryHistoryStore()
        setContent { App(historyStore = store) }
        onNodeWithTag("input_principal").performTextReplacement("2000")
        onNodeWithTag("input_rate").performTextReplacement("4")
        onNodeWithTag("input_years").performTextReplacement("2")
        onNodeWithTag("btn_calculate").performClick()
        waitForIdle()
        val entries = store.loadAll()
        assertEquals(1, entries.size)
        assertEquals(2000.0, entries.first().principal)
        assertTrue(entries.first().finalAmount > 2000.0)
    }

    @Test
    fun history_survives_recomposition_via_store_round_trip() {
        val store = InMemoryHistoryStore()
        store.save(HistoryEntry(500.0, 5.0, 1.0, Capitalization.Yearly, 525.0, 25.0))
        store.save(HistoryEntry(700.0, 6.0, 2.0, Capitalization.Monthly, 789.6, 89.6))
        val back = store.loadAll()
        assertEquals(2, back.size)
        // Newest first
        assertEquals(700.0, back.first().principal)
    }

    @Test
    fun switching_language_changes_visible_labels() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithText(Language.English.displayName).performClick()
        waitForIdle()
        onNodeWithText(Language.Russian.displayName).performClick()
        waitForIdle()
        val ru = stringsFor(Language.Russian)
        onNodeWithText(ru.appTitle).assertIsDisplayed()
    }

    @Test
    fun clear_history_button_removes_entries_from_ui_and_store() = runComposeUiTest {
        val store = InMemoryHistoryStore()
        setContent { App(historyStore = store) }
        // Produce one entry
        onNodeWithTag("input_principal").performTextReplacement("1000")
        onNodeWithTag("input_rate").performTextReplacement("5")
        onNodeWithTag("input_years").performTextReplacement("1")
        onNodeWithTag("btn_calculate").performClick()
        waitForIdle()
        assertEquals(1, store.loadAll().size)

        val s = stringsFor(Language.English)
        // Clear-history TextButton sits at the bottom of the column → scroll it into view first
        onNodeWithText(s.clearHistory).performScrollTo().performClick()
        waitForIdle()
        assertTrue(store.loadAll().isEmpty())
    }

    @Test
    fun saved_language_is_restored_on_next_vm_instantiation() {
        val store = InMemoryHistoryStore()
        store.saveLanguage(Language.Belarusian)
        val vm = com.example.financalc.ui.CalculatorViewModel(store)
        assertEquals(Language.Belarusian, vm.language)
    }
}
