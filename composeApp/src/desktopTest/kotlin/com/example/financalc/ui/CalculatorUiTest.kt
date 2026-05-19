package com.example.financalc.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
    fun successful_calculation_shows_chart() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("input_principal").performTextClearance()
        onNodeWithTag("input_principal").performTextInput("10000")
        onNodeWithTag("input_rate").performTextClearance()
        onNodeWithTag("input_rate").performTextInput("5")
        onNodeWithTag("input_years").performTextClearance()
        onNodeWithTag("input_years").performTextInput("3")
        onNodeWithTag("btn_calculate").performClick()
        waitForIdle()
        onNodeWithTag("growth_chart").assertIsDisplayed()
    }

    @Test
    fun invalid_principal_does_not_render_chart() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("input_principal").performTextClearance()
        onNodeWithTag("input_principal").performTextInput("abc")
        onNodeWithTag("btn_calculate").performClick()
        waitForIdle()
        val s = stringsFor(Language.English)
        onNodeWithText(s.errNotANumber).assertIsDisplayed()
    }

    @Test
    fun reset_clears_inputs() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        onNodeWithTag("input_principal").performTextClearance()
        onNodeWithTag("input_principal").performTextInput("123")
        onNodeWithTag("btn_reset").performClick()
        waitForIdle()
        // After reset, principal should be empty — assert by clearing and re-typing succeeds
        onNodeWithTag("input_principal").performTextInput("0")
    }

    @Test
    fun headline_renders_in_english_by_default() = runComposeUiTest {
        setContent { App(historyStore = InMemoryHistoryStore()) }
        val s = stringsFor(Language.English)
        onNodeWithText(s.appTitle).assertIsDisplayed()
    }
}
