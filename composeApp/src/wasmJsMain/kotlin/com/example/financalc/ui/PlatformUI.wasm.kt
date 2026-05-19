package com.example.financalc.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financalc.calculator.Capitalization
import com.example.financalc.calculator.CalculationResult

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
    val values = remember { Capitalization.entries.toList() }
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        values.forEachIndexed { index, cap ->
            SegmentedButton(
                selected = cap == selected,
                onClick = { onSelected(cap) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = values.size),
                label = {
                    Text(
                        when (cap) {
                            Capitalization.Monthly -> labels.first
                            Capitalization.Quarterly -> labels.second
                            Capitalization.Yearly -> labels.third
                        }
                    )
                }
            )
        }
    }
}

@Composable
actual fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean, testTag: String) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
actual fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier, testTag: String) {
    OutlinedButton(onClick = onClick, modifier = modifier.let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }) { Text(text) }
}

@Composable
actual fun GrowthChart(series: List<CalculationResult.Point>, modifier: Modifier) {
    if (series.size < 2) return
    val primary = MaterialTheme.colorScheme.primary
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { scale = (scale * 1.25f).coerceAtMost(8f) }) { Text("+") }
            TextButton(onClick = { scale = (scale / 1.25f).coerceAtLeast(1f) }) { Text("-") }
            TextButton(onClick = { scale = 1f; offsetX = 0f }) { Text("reset") }
            Text(" zoom×${(scale * 10).toInt() / 10f}", style = MaterialTheme.typography.labelSmall)
        }
        Canvas(
            Modifier.testTag("growth_chart").fillMaxWidth().padding(top = 4.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 8f)
                        offsetX += pan.x
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { scale = 1f; offsetX = 0f })
                }
        ) {
            val w = size.width
            val h = size.height
            val padding = 24f
            val maxY = series.maxOf { it.amount }
            val minY = series.minOf { it.amount }
            val rangeY = (maxY - minY).coerceAtLeast(1.0)
            val visibleWidth = (w - padding * 2) * scale
            val stepX = visibleWidth / (series.size - 1)
            val viewLeft = (-offsetX).coerceIn(0f, visibleWidth - (w - padding * 2))

            val path = Path()
            val fillPath = Path()
            series.forEachIndexed { i, p ->
                val rawX = i * stepX
                val x = padding + (rawX - viewLeft)
                val y = (h - padding) - ((p.amount - minY) / rangeY * (h - padding * 2)).toFloat()
                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, h - padding)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
                if (i == series.lastIndex) fillPath.lineTo(x, h - padding)
            }
            fillPath.close()
            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    listOf(primary.copy(alpha = 0.3f), primary.copy(alpha = 0.05f)),
                    startY = 0f, endY = h
                )
            )
            drawPath(path, color = primary, style = Stroke(width = 3f))
        }
    }
}
