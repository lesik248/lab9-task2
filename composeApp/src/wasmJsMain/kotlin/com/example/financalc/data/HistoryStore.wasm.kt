package com.example.financalc.data

import com.example.financalc.i18n.Language
import kotlinx.browser.window

private class WebLocalStorageHistoryStore : HistoryStore {
    private val KEY_HISTORY = "financalc_history"
    private val KEY_LANG = "financalc_language"
    private val storage get() = window.localStorage

    override fun save(entry: HistoryEntry) {
        val raw = runCatching { storage.getItem(KEY_HISTORY) }.getOrNull() ?: ""
        val lines = raw.lines().filter { it.isNotBlank() }.toMutableList()
        lines.add(0, entry.serialize())
        while (lines.size > 50) lines.removeAt(lines.size - 1)
        runCatching { storage.setItem(KEY_HISTORY, lines.joinToString("\n")) }
            .onFailure { kotlin.io.println("[HistoryStore] save failed: ${it.message}") }
    }

    override fun loadAll(): List<HistoryEntry> {
        val raw = runCatching { storage.getItem(KEY_HISTORY) }.getOrNull() ?: return emptyList()
        return raw.lines().mapNotNull { HistoryEntry.parse(it) }
    }

    override fun clear() {
        runCatching { storage.removeItem(KEY_HISTORY) }
    }

    override fun loadLanguage(): Language {
        val code = runCatching { storage.getItem(KEY_LANG) }.getOrNull() ?: return Language.English
        return Language.entries.firstOrNull { it.code == code } ?: Language.English
    }

    override fun saveLanguage(language: Language) {
        runCatching { storage.setItem(KEY_LANG, language.code) }
    }
}

actual fun createHistoryStore(): HistoryStore = WebLocalStorageHistoryStore()
