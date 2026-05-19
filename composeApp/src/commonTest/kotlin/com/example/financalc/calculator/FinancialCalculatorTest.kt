package com.example.financalc.calculator

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FinancialCalculatorTest {

    private fun approx(expected: Double, actual: Double, eps: Double = 0.01) {
        assertTrue(
            abs(expected - actual) <= eps,
            "expected $expected, actual $actual (Δ=${abs(expected - actual)})"
        )
    }

    @Test
    fun yearly_compounding_matches_textbook_formula() {
        // 10 000 @ 10% yearly for 5 years -> 10 000 * 1.1^5 = 16 105.10
        val r = FinancialCalculator.compute(
            CalculationInput(10_000.0, 10.0, 5.0, Capitalization.Yearly)
        )
        approx(16_105.10, r.finalAmount)
        approx(6_105.10, r.profit)
    }

    @Test
    fun monthly_compounding_matches_textbook_formula() {
        // 1000 @ 12% monthly for 1 year = 1000 * (1 + 0.12/12)^12 ≈ 1126.83
        val r = FinancialCalculator.compute(
            CalculationInput(1_000.0, 12.0, 1.0, Capitalization.Monthly)
        )
        approx(1126.83, r.finalAmount)
    }

    @Test
    fun quarterly_compounding_matches_textbook_formula() {
        // 5000 @ 8% quarterly for 3 years = 5000 * (1.02)^12 ≈ 6341.21
        val r = FinancialCalculator.compute(
            CalculationInput(5_000.0, 8.0, 3.0, Capitalization.Quarterly)
        )
        approx(6_341.21, r.finalAmount, eps = 0.1)
    }

    @Test
    fun zero_rate_returns_principal() {
        val r = FinancialCalculator.compute(
            CalculationInput(2500.0, 0.0, 4.0, Capitalization.Yearly)
        )
        approx(2500.0, r.finalAmount)
        approx(0.0, r.profit)
    }

    @Test
    fun series_starts_at_principal_and_grows_monotonically_for_positive_rate() {
        val r = FinancialCalculator.compute(
            CalculationInput(100.0, 5.0, 2.0, Capitalization.Monthly)
        )
        assertEquals(100.0, r.series.first().amount)
        var prev = 0.0
        for (point in r.series) {
            assertTrue(point.amount >= prev - 1e-9, "series must be non-decreasing")
            prev = point.amount
        }
    }

    @Test
    fun series_length_equals_total_months_plus_one() {
        val years = 7.0
        val r = FinancialCalculator.compute(
            CalculationInput(100.0, 3.0, years, Capitalization.Monthly)
        )
        assertEquals(((years * 12).toInt() + 1), r.series.size)
    }

    @Test
    fun very_small_principal_still_compounds_correctly() {
        val r = FinancialCalculator.compute(
            CalculationInput(0.01, 50.0, 10.0, Capitalization.Yearly)
        )
        // 0.01 * 1.5^10 ≈ 0.5766
        approx(0.5766, r.finalAmount, eps = 1e-3)
    }
}
