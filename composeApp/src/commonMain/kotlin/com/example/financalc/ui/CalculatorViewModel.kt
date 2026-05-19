package com.example.financalc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.financalc.calculator.CalculationInput
import com.example.financalc.calculator.CalculationResult
import com.example.financalc.calculator.Capitalization
import com.example.financalc.calculator.FinancialCalculator
import com.example.financalc.calculator.Validation
import com.example.financalc.calculator.ValidationException
import com.example.financalc.data.HistoryEntry
import com.example.financalc.data.HistoryStore
import com.example.financalc.i18n.Language
import com.example.financalc.i18n.Strings

data class FieldErrors(
    val principal: String? = null,
    val rate: String? = null,
    val years: String? = null
) {
    fun isEmpty() = principal == null && rate == null && years == null
}

class CalculatorViewModel(
    private val history: HistoryStore
) {
    var language by mutableStateOf(history.loadLanguage())
        private set

    var principalText by mutableStateOf("10000")
    var rateText by mutableStateOf("7.5")
    var yearsText by mutableStateOf("5")
    var capitalization by mutableStateOf(Capitalization.Monthly)

    var errors by mutableStateOf(FieldErrors())
        private set

    var result by mutableStateOf<CalculationResult?>(null)
        private set

    val historyEntries = mutableStateListOf<HistoryEntry>().also { it.addAll(history.loadAll()) }

    fun changeLanguage(lang: Language) {
        language = lang
        history.saveLanguage(lang)
    }

    fun calculate(strings: Strings) {
        val parsed = parseInputs(strings)
        if (parsed == null) {
            result = null
            return
        }
        try {
            val r = FinancialCalculator.compute(parsed)
            result = r
            val entry = HistoryEntry(
                principal = parsed.principal,
                ratePercent = parsed.annualRatePercent,
                years = parsed.years,
                capitalization = parsed.capitalization,
                finalAmount = r.finalAmount,
                profit = r.profit
            )
            history.save(entry)
            historyEntries.add(0, entry)
        } catch (t: Throwable) {
            // Non-recoverable math error — log and surface a generic message.
            kotlin.io.println("[FinancialCalculator] computation failed: ${t.message}")
            result = null
        }
    }

    fun resetInputs() {
        principalText = ""
        rateText = ""
        yearsText = ""
        capitalization = Capitalization.Monthly
        errors = FieldErrors()
        result = null
    }

    fun clearHistory() {
        history.clear()
        historyEntries.clear()
    }

    private fun parseInputs(strings: Strings): CalculationInput? {
        var pErr: String? = null
        var rErr: String? = null
        var yErr: String? = null
        var principal = 0.0
        var rate = 0.0
        var years = 0.0
        try {
            principal = Validation.parsePrincipal(principalText)
        } catch (e: ValidationException) {
            pErr = translate(e, strings)
        }
        try {
            rate = Validation.parseRate(rateText)
        } catch (e: ValidationException) {
            rErr = translate(e, strings)
        }
        try {
            years = Validation.parseYears(yearsText)
        } catch (e: ValidationException) {
            yErr = translate(e, strings)
        }
        errors = FieldErrors(pErr, rErr, yErr)
        if (!errors.isEmpty()) {
            kotlin.io.println("[Validation] errors=$errors")
            return null
        }
        return CalculationInput(principal, rate, years, capitalization)
    }

    private fun translate(e: ValidationException, s: Strings): String = when (e.reasonKey) {
        "err_empty" -> s.errEmpty
        "err_not_a_number" -> s.errNotANumber
        "err_range" -> when (e.field) {
            ValidationException.Field.Principal -> s.errRangePrincipal
            ValidationException.Field.Rate -> s.errRangeRate
            ValidationException.Field.Years -> s.errRangeYears
        }
        else -> e.reasonKey
    }
}
