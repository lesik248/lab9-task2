package com.example.financalc

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.financalc.data.HistoryStore
import com.example.financalc.data.createHistoryStore
import com.example.financalc.ui.CalculatorScreen
import com.example.financalc.ui.CalculatorViewModel

@Composable
fun App(historyStore: HistoryStore = remember { createHistoryStore() }) {
    val vm = remember { CalculatorViewModel(historyStore) }
    MaterialTheme(
        colorScheme = if (isSystemInDarkPreference()) darkColorScheme() else lightColorScheme()
    ) {
        CalculatorScreen(vm)
    }
}

@Composable
expect fun isSystemInDarkPreference(): Boolean
