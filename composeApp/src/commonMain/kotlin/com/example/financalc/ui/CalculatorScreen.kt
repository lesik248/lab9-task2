package com.example.financalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financalc.calculator.Capitalization
import com.example.financalc.formatMoney
import com.example.financalc.i18n.Language
import com.example.financalc.i18n.LocalLanguage
import com.example.financalc.i18n.LocalStrings
import com.example.financalc.i18n.stringsFor

@Composable
fun CalculatorScreen(vm: CalculatorViewModel) {
    val strings = stringsFor(vm.language)
    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalLanguage provides vm.language
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val wide = maxWidth > 720.dp
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .widthIn(max = if (wide) 1100.dp else maxWidth),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Header(vm)
                    if (wide) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                InputsBlock(vm)
                                ActionsBlock(vm)
                            }
                            Column(Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ResultBlock(vm)
                                HistoryBlock(vm)
                            }
                        }
                    } else {
                        InputsBlock(vm)
                        ActionsBlock(vm)
                        ResultBlock(vm)
                        HistoryBlock(vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(vm: CalculatorViewModel) {
    val s = LocalStrings.current
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(s.appTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        LanguageSwitcher(vm)
    }
}

@Composable
private fun LanguageSwitcher(vm: CalculatorViewModel) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(vm.language.displayName)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Language.entries.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName) },
                    onClick = {
                        vm.setLanguage(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InputsBlock(vm: CalculatorViewModel) {
    val s = LocalStrings.current
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberInput(
                value = vm.principalText,
                onValueChange = { vm.principalText = it },
                label = s.principal,
                error = vm.errors.principal,
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_principal"
            )
            NumberInput(
                value = vm.rateText,
                onValueChange = { vm.rateText = it },
                label = s.annualRate,
                error = vm.errors.rate,
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_rate"
            )
            NumberInput(
                value = vm.yearsText,
                onValueChange = { vm.yearsText = it },
                label = s.years,
                error = vm.errors.years,
                modifier = Modifier.fillMaxWidth(),
                testTag = "input_years"
            )
            Text(s.capitalization, style = MaterialTheme.typography.labelLarge)
            CapitalizationSelector(
                selected = vm.capitalization,
                onSelected = { vm.capitalization = it },
                labels = Triple(s.monthly, s.quarterly, s.yearly),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ActionsBlock(vm: CalculatorViewModel) {
    val s = LocalStrings.current
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryButton(
            text = s.calculate,
            onClick = { vm.calculate(s) },
            modifier = Modifier.weight(1f),
            testTag = "btn_calculate"
        )
        SecondaryButton(
            text = s.reset,
            onClick = { vm.resetInputs() },
            modifier = Modifier.weight(1f),
            testTag = "btn_reset"
        )
    }
}

@Composable
private fun ResultBlock(vm: CalculatorViewModel) {
    val s = LocalStrings.current
    AnimatedVisibility(
        visible = vm.result != null,
        enter = fadeIn(tween(250)) + expandVertically(tween(250)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
    ) {
        val r = vm.result ?: return@AnimatedVisibility
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val animFinal by animateFloatAsState(
                    targetValue = r.finalAmount.toFloat(),
                    animationSpec = tween(500), label = "final"
                )
                val animProfit by animateFloatAsState(
                    targetValue = r.profit.toFloat(),
                    animationSpec = tween(500), label = "profit"
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${s.finalAmount}: ", style = MaterialTheme.typography.titleMedium)
                    Text(
                        formatMoney(animFinal.toDouble()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp).alpha(1f).fillMaxWidth()
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${s.profit}: ", style = MaterialTheme.typography.titleSmall)
                    Text(
                        formatMoney(animProfit.toDouble()),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                HorizontalDivider()
                Text(s.growthChart, style = MaterialTheme.typography.labelLarge)
                Box(Modifier.fillMaxWidth().height(220.dp)) {
                    GrowthChart(series = r.series, modifier = Modifier.fillMaxWidth().height(220.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoryBlock(vm: CalculatorViewModel) {
    val s = LocalStrings.current
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(s.history, style = MaterialTheme.typography.titleMedium)
                if (vm.historyEntries.isNotEmpty()) {
                    TextButton(onClick = { vm.clearHistory() }) { Text(s.clearHistory) }
                }
            }
            if (vm.historyEntries.isEmpty()) {
                Text(s.noHistory, style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    Modifier.height(220.dp).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(vm.historyEntries) { e ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${formatMoney(e.principal)}  •  ${e.ratePercent}%  •  ${e.years}y  •  ${labelFor(e.capitalization, s)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                formatMoney(e.finalAmount),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun labelFor(c: Capitalization, s: com.example.financalc.i18n.Strings): String = when (c) {
    Capitalization.Monthly -> s.monthly
    Capitalization.Quarterly -> s.quarterly
    Capitalization.Yearly -> s.yearly
}
