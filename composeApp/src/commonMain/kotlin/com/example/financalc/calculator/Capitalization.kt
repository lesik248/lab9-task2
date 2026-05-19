package com.example.financalc.calculator

enum class Capitalization(val perYear: Int) {
    Monthly(12),
    Quarterly(4),
    Yearly(1);

    companion object {
        fun fromOrdinal(i: Int): Capitalization = entries.getOrElse(i) { Yearly }
    }
}
