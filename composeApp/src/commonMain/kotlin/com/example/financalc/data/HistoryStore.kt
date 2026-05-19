package com.example.financalc.data

import com.example.financalc.calculator.Capitalization
import com.example.financalc.i18n.Language

data class HistoryEntry(
    val principal: Double,
    val ratePercent: Double,
    val years: Double,
    val capitalization: Capitalization,
    val finalAmount: Double,
    val profit: Double
) {
    fun serialize(): String = listOf(
        principal, ratePercent, years, capitalization.ordinal, finalAmount, profit
    ).joinToString("|")

    companion object {
        fun parse(line: String): HistoryEntry? {
            val parts = line.split("|")
            if (parts.size != 6) return null
            return runCatching {
                HistoryEntry(
                    principal = parts[0].toDouble(),
                    ratePercent = parts[1].toDouble(),
                    years = parts[2].toDouble(),
                    capitalization = Capitalization.fromOrdinal(parts[3].toInt()),
                    finalAmount = parts[4].toDouble(),
                    profit = parts[5].toDouble()
                )
            }.getOrNull()
        }
    }
}

interface HistoryStore {
    fun save(entry: HistoryEntry)
    fun loadAll(): List<HistoryEntry>
    fun clear()
    fun loadLanguage(): Language
    fun saveLanguage(language: Language)
}

/** In-memory fallback used when no native storage is available (tests / web). */
class InMemoryHistoryStore : HistoryStore {
    private val items = ArrayDeque<HistoryEntry>()
    private var language: Language = Language.English

    override fun save(entry: HistoryEntry) {
        items.addFirst(entry)
        while (items.size > 50) items.removeLast()
    }

    override fun loadAll(): List<HistoryEntry> = items.toList()
    override fun clear() { items.clear() }
    override fun loadLanguage(): Language = language
    override fun saveLanguage(language: Language) { this.language = language }
}

expect fun createHistoryStore(): HistoryStore
