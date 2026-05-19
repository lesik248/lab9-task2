package com.example.financalc.calculator

import kotlin.math.pow
import kotlin.math.roundToLong

data class CalculationInput(
    val principal: Double,
    val annualRatePercent: Double,
    val years: Double,
    val capitalization: Capitalization
)

data class CalculationResult(
    val input: CalculationInput,
    val finalAmount: Double,
    val profit: Double,
    val series: List<Point>
) {
    data class Point(val month: Int, val amount: Double)
}

object FinancialCalculator {

    /**
     * Compound-interest growth.
     * A = P * (1 + r/n)^(n*t),  где n — частота капитализации.
     */
    fun compute(input: CalculationInput): CalculationResult {
        val r = input.annualRatePercent / 100.0
        val n = input.capitalization.perYear
        val totalMonths = (input.years * 12.0).roundToLong().toInt().coerceAtLeast(1)
        val ratePerPeriod = r / n
        val periodLengthMonths = 12 / n

        val series = ArrayList<CalculationResult.Point>(totalMonths + 1)
        series.add(CalculationResult.Point(0, input.principal))
        for (m in 1..totalMonths) {
            val completedPeriods = if (periodLengthMonths == 0) 0 else m / periodLengthMonths
            val amount = input.principal * (1.0 + ratePerPeriod).pow(completedPeriods.toDouble())
            series.add(CalculationResult.Point(m, amount))
        }

        val final = series.last().amount
        return CalculationResult(
            input = input,
            finalAmount = final,
            profit = final - input.principal,
            series = series
        )
    }
}

fun Double.roundTo2(): Double = (this * 100.0).roundToLong() / 100.0
