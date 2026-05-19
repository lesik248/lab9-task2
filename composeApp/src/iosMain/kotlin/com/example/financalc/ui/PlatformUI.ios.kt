package com.example.financalc.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financalc.calculator.Capitalization
import com.example.financalc.calculator.CalculationResult

/**
 * iOS look: Picker-like segmented control, flat (no-shadow) buttons,
 * thin-line chart without gradient.
 */

@Composable
actual fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error, color = MaterialTheme.colorScheme.error) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    )
}

@Composable
actual fun CapitalizationSelector(
    selected: Capitalization,
    onSelected: (Capitalization) -> Unit,
    labels: Triple<String, String, String>,
    modifier: Modifier
) {
    // iOS-style segmented "picker"
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Capitalization.entries.forEach { cap ->
            val isSelected = cap == selected
            val bg = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(bg)
                    .clickable { onSelected(cap) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (cap) {
                        Capitalization.Monthly -> labels.first
                        Capitalization.Quarterly -> labels.second
                        Capitalization.Yearly -> labels.third
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
actual fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean, testTag: String) {
    // Flat / no shadow button
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp)
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
actual fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier, testTag: String) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
actual fun GrowthChart(series: List<CalculationResult.Point>, modifier: Modifier) {
    if (series.size < 2) return
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier.testTag("growth_chart")) {
        val w = size.width
        val h = size.height
        val padding = 18f
        val maxY = series.maxOf { it.amount }
        val minY = series.minOf { it.amount }
        val rangeY = (maxY - minY).coerceAtLeast(1.0)
        val stepX = (w - padding * 2) / (series.size - 1)
        val path = Path()
        series.forEachIndexed { i, p ->
            val x = padding + stepX * i
            val y = (h - padding) - ((p.amount - minY) / rangeY * (h - padding * 2)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = color, style = Stroke(width = 1.5f))
    }
}
