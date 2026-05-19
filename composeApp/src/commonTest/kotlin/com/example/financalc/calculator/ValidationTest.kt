package com.example.financalc.calculator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidationTest {

    @Test
    fun principal_accepts_comma_decimal() {
        assertEquals(1234.5, Validation.parsePrincipal("1234,5"))
    }

    @Test
    fun principal_rejects_empty_input() {
        val ex = assertFailsWith<ValidationException> { Validation.parsePrincipal("   ") }
        assertEquals(ValidationException.Field.Principal, ex.field)
        assertEquals("err_empty", ex.reasonKey)
    }

    @Test
    fun principal_rejects_non_numeric() {
        val ex = assertFailsWith<ValidationException> { Validation.parsePrincipal("abc") }
        assertEquals("err_not_a_number", ex.reasonKey)
    }

    @Test
    fun principal_rejects_out_of_range_zero() {
        val ex = assertFailsWith<ValidationException> { Validation.parsePrincipal("0") }
        assertEquals("err_range", ex.reasonKey)
    }

    @Test
    fun rate_accepts_zero_and_max() {
        assertEquals(0.0, Validation.parseRate("0"))
        assertEquals(1000.0, Validation.parseRate("1000"))
    }

    @Test
    fun rate_rejects_above_max() {
        val ex = assertFailsWith<ValidationException> { Validation.parseRate("1500") }
        assertEquals("err_range", ex.reasonKey)
    }

    @Test
    fun years_rejects_zero_and_negative_and_above_max() {
        assertFailsWith<ValidationException> { Validation.parseYears("0") }
        assertFailsWith<ValidationException> { Validation.parseYears("-1") }
        assertFailsWith<ValidationException> { Validation.parseYears("101") }
    }
}
