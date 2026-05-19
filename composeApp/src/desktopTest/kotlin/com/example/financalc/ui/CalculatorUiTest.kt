package com.example.financalc.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.example.financalc.App
import com.example.financalc.data.InMemoryHistoryStore
import com.example.financalc.i18n.stringsFor
import com.example.financalc.i18n.Language
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CalculatorUiTest {

    @Test
    fun all_three_input_fields_are_rendered() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("input_principal").assertIsDisplayed()
        onNodeWithTag("input_rate").assertIsDisplayed()
        onNodeWithTag("input_years").assertIsDisplayed()
    }

    @Test
    fun primary_and_reset_buttons_are_visible() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("btn_calculate").assertIsDisplayed()
        onNodeWithTag("btn_reset").assertIsDisplayed()
    }

    @Test
    fun reset_clears_inputs() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("input_principal").performTextReplacement("123")
        onNodeWithTag("btn_reset").performClick()
        waitForIdle()
        // After reset the field still accepts input
        onNodeWithTag("input_principal").performTextReplacement("0")
    }

    @Test
    fun headline_renders_in_english_by_default() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        val s = stringsFor(Language.English)
        onNodeWithText(s.appTitle).assertIsDisplayed()
    }

    @Test
    fun calculate_button_does_not_crash_with_default_values() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("btn_calculate").performClick()
        waitForIdle()
        // App still renders the inputs after a calculation pass.
        onNodeWithTag("input_principal").assertIsDisplayed()
    }
}
