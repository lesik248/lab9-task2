package com.example.financalc.data

import com.example.financalc.i18n.Language
import platform.Foundation.NSUserDefaults

private class IosUserDefaultsHistoryStore : HistoryStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val KEY_HISTORY = "financalc_history"
    private val KEY_LANG = "financalc_language"

    override fun save(entry: HistoryEntry) {
        val current = defaults.stringForKey(KEY_HISTORY)?.lines().orEmpty()
            .filter { it.isNotBlank() }.toMutableList()
        current.add(0, entry.serialize())
        while (current.size > 50) current.removeAt(current.size - 1)
        defaults.setObject(current.joinToString("\n"), forKey = KEY_HISTORY)
    }

    override fun loadAll(): List<HistoryEntry> {
        val raw = defaults.stringForKey(KEY_HISTORY) ?: return emptyList()
        return raw.lines().mapNotNull { HistoryEntry.parse(it) }
    }

    override fun clear() {
        defaults.removeObjectForKey(KEY_HISTORY)
    }

    override fun loadLanguage(): Language {
        val code = defaults.stringForKey(KEY_LANG) ?: return Language.English
        return Language.entries.firstOrNull { it.code == code } ?: Language.English
    }

    override fun saveLanguage(language: Language) {
        defaults.setObject(language.code, forKey = KEY_LANG)
    }
}

actual fun createHistoryStore(): HistoryStore = IosUserDefaultsHistoryStore()
