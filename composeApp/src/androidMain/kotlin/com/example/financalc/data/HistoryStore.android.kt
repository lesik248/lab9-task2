package com.example.financalc.data

import android.content.Context
import com.example.financalc.i18n.Language

object AndroidAppContext {
    lateinit var appContext: Context
}

private class AndroidPrefsHistoryStore(context: Context) : HistoryStore {
    private val prefs = context.getSharedPreferences("financalc_prefs", Context.MODE_PRIVATE)
    private val KEY_HISTORY = "history"
    private val KEY_LANG = "language"

    override fun save(entry: HistoryEntry) {
        val lines = (prefs.getString(KEY_HISTORY, null)?.lines().orEmpty().filter { it.isNotBlank() }).toMutableList()
        lines.add(0, entry.serialize())
        while (lines.size > 50) lines.removeAt(lines.size - 1)
        prefs.edit().putString(KEY_HISTORY, lines.joinToString("\n")).apply()
    }

    override fun loadAll(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return raw.lines().mapNotNull { HistoryEntry.parse(it) }
    }

    override fun clear() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    override fun loadLanguage(): Language {
        val code = prefs.getString(KEY_LANG, null) ?: return Language.English
        return Language.entries.firstOrNull { it.code == code } ?: Language.English
    }

    override fun saveLanguage(language: Language) {
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }
}

actual fun createHistoryStore(): HistoryStore =
    if (AndroidAppContext::appContext.isInitialized) AndroidPrefsHistoryStore(AndroidAppContext.appContext)
    else InMemoryHistoryStore()
