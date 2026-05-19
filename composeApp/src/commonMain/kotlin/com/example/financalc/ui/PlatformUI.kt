package com.example.financalc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financalc.calculator.Capitalization
import com.example.financalc.calculator.CalculationResult

/**
 * Platform-specific look-and-feel for the same logical controls.
 * Each platform implements these to match its native style guide.
 */

@Composable
expect fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    testTag: String = ""
)

@Composable
expect fun CapitalizationSelector(
    selected: Capitalization,
    onSelected: (Capitalization) -> Unit,
    labels: Triple<String, String, String>,
    modifier: Modifier = Modifier
)

@Composable
expect fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
)

@Composable
expect fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
)

@Composable
expect fun GrowthChart(
    series: List<CalculationResult.Point>,
    modifier: Modifier = Modifier
)
