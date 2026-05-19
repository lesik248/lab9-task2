package com.example.financalc.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financalc.calculator.Capitalization
import com.example.financalc.calculator.CalculationResult

/**
 * Linux/Desktop look: classic SpinBox-like numeric inputs with +/- steppers,
 * radio-list selector, fixed-size buttons, plain (non-gradient) chart.
 */

private fun stepFor(value: String): String {
    val v = value.toDoubleOrNull() ?: 0.0
    val abs = kotlin.math.abs(v)
    val step = when {
        abs >= 1000 -> 100.0
        abs >= 100 -> 10.0
        abs >= 10 -> 1.0
        else -> 0.5
    }
    val next = v + step
    return if (next == next.toLong().toDouble()) next.toLong().toString() else next.toString()
}

private fun stepDown(value: String): String {
    val v = value.toDoubleOrNull() ?: 0.0
    val abs = kotlin.math.abs(v)
    val step = when {
        abs >= 1000 -> 100.0
        abs >= 100 -> 10.0
        abs >= 10 -> 1.0
        else -> 0.5
    }
    val next = (v - step).coerceAtLeast(0.0)
    return if (next == next.toLong().toDouble()) next.toLong().toString() else next.toString()
}

@Composable
actual fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            supportingText = { if (error != null) Text(error, color = MaterialTheme.colorScheme.error) },
            singleLine = true,
            modifier = Modifier.width(260.dp)
        )
        Column {
            OutlinedButton(
                onClick = { onValueChange(stepFor(value)) },
                modifier = Modifier.size(width = 36.dp, height = 28.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) { Text("▲", style = MaterialTheme.typography.labelSmall) }
            OutlinedButton(
                onClick = { onValueChange(stepDown(value)) },
                modifier = Modifier.size(width = 36.dp, height = 28.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) { Text("▼", style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable
actual fun CapitalizationSelector(
    selected: Capitalization,
    onSelected: (Capitalization) -> Unit,
    labels: Triple<String, String, String>,
    modifier: Modifier
) {
    Column(modifier) {
        Capitalization.entries.forEach { cap ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = cap == selected, onClick = { onSelected(cap) })
                Text(
                    when (cap) {
                        Capitalization.Monthly -> labels.first
                        Capitalization.Quarterly -> labels.second
                        Capitalization.Yearly -> labels.third
                    }
                )
            }
        }
    }
}

@Composable
actual fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean, testTag: String) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .height(40.dp)
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
actual fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier, testTag: String) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .height(40.dp)
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) { Text(text) }
}

@Composable
actual fun GrowthChart(series: List<CalculationResult.Point>, modifier: Modifier) {
    if (series.size < 2) return
    val color = MaterialTheme.colorScheme.primary
    Canvas(
        modifier
            .testTag("growth_chart")
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        val w = size.width
        val h = size.height
        val padding = 12f
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
        drawPath(path, color = color, style = Stroke(width = 2f))
        // simple frame
        drawLine(Color.Gray, Offset(padding, padding), Offset(padding, h - padding), strokeWidth = 1f)
        drawLine(Color.Gray, Offset(padding, h - padding), Offset(w - padding, h - padding), strokeWidth = 1f)
    }
}
