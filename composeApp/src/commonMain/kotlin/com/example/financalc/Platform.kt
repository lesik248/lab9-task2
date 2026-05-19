package com.example.financalc

enum class PlatformKind { Android, Ios, Desktop, Web }

expect val currentPlatform: PlatformKind

fun formatMoney(value: Double): String {
    val rounded = (value * 100.0).toLong()
    val sign = if (rounded < 0) "-" else ""
    val abs = kotlin.math.abs(rounded)
    val whole = abs / 100
    val frac = (abs % 100).toString().padStart(2, '0')
    val wholeStr = buildString {
        val s = whole.toString()
        for (i in s.indices) {
            if (i > 0 && (s.length - i) % 3 == 0) append(' ')
            append(s[i])
        }
    }
    return "$sign$wholeStr.$frac"
}

fun formatNumber(value: Double, fractionDigits: Int = 2): String {
    val factor = generateSequence(1L) { it * 10 }.elementAt(fractionDigits)
    val rounded = kotlin.math.round(value * factor).toLong()
    val sign = if (rounded < 0) "-" else ""
    val abs = kotlin.math.abs(rounded)
    val whole = abs / factor
    return if (fractionDigits == 0) {
        "$sign$whole"
    } else {
        val frac = (abs % factor).toString().padStart(fractionDigits, '0')
        "$sign$whole.$frac"
    }
}
