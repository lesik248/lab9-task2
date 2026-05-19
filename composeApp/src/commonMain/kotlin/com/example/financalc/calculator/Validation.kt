package com.example.financalc.calculator

class ValidationException(val field: Field, val reasonKey: String) : IllegalArgumentException("$field: $reasonKey") {
    enum class Field { Principal, Rate, Years }
}

object Validation {
    const val MIN_PRINCIPAL = 0.01
    const val MAX_PRINCIPAL = 1_000_000_000.0
    const val MIN_RATE = 0.0
    const val MAX_RATE = 1000.0
    const val MIN_YEARS = 0.0
    const val MAX_YEARS = 100.0

    fun parsePrincipal(raw: String): Double {
        val cleaned = raw.trim().replace(',', '.')
        if (cleaned.isEmpty()) throw ValidationException(ValidationException.Field.Principal, "err_empty")
        val v = cleaned.toDoubleOrNull()
            ?: throw ValidationException(ValidationException.Field.Principal, "err_not_a_number")
        if (v.isNaN() || v.isInfinite() || v < MIN_PRINCIPAL || v > MAX_PRINCIPAL) {
            throw ValidationException(ValidationException.Field.Principal, "err_range")
        }
        return v
    }

    fun parseRate(raw: String): Double {
        val cleaned = raw.trim().replace(',', '.')
        if (cleaned.isEmpty()) throw ValidationException(ValidationException.Field.Rate, "err_empty")
        val v = cleaned.toDoubleOrNull()
            ?: throw ValidationException(ValidationException.Field.Rate, "err_not_a_number")
        if (v.isNaN() || v.isInfinite() || v < MIN_RATE || v > MAX_RATE) {
            throw ValidationException(ValidationException.Field.Rate, "err_range")
        }
        return v
    }

    fun parseYears(raw: String): Double {
        val cleaned = raw.trim().replace(',', '.')
        if (cleaned.isEmpty()) throw ValidationException(ValidationException.Field.Years, "err_empty")
        val v = cleaned.toDoubleOrNull()
            ?: throw ValidationException(ValidationException.Field.Years, "err_not_a_number")
        if (v.isNaN() || v.isInfinite() || v <= MIN_YEARS || v > MAX_YEARS) {
            throw ValidationException(ValidationException.Field.Years, "err_range")
        }
        return v
    }
}
