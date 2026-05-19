package com.example.financalc.data

import com.example.financalc.i18n.Language
import java.io.File
import java.util.Properties

private class DesktopFileHistoryStore : HistoryStore {
    private val dir = File(System.getProperty("user.home"), ".financalc").apply { mkdirs() }
    private val historyFile = File(dir, "history.txt")
    private val settingsFile = File(dir, "settings.properties")

    override fun save(entry: HistoryEntry) {
        val lines = loadLines().toMutableList()
        lines.add(0, entry.serialize())
        while (lines.size > 50) lines.removeAt(lines.size - 1)
        runCatching { historyFile.writeText(lines.joinToString("\n")) }
            .onFailure { kotlin.io.println("[HistoryStore] save failed: ${it.message}") }
    }

    override fun loadAll(): List<HistoryEntry> =
        loadLines().mapNotNull { HistoryEntry.parse(it) }

    override fun clear() {
        runCatching { historyFile.delete() }
    }

    override fun loadLanguage(): Language {
        if (!settingsFile.exists()) return Language.English
        val props = Properties().apply {
            runCatching { settingsFile.inputStream().use { load(it) } }
        }
        val code = props.getProperty("language") ?: return Language.English
        return Language.entries.firstOrNull { it.code == code } ?: Language.English
    }

    override fun saveLanguage(language: Language) {
        val props = Properties().apply {
            if (settingsFile.exists()) runCatching { settingsFile.inputStream().use { load(it) } }
            setProperty("language", language.code)
        }
        runCatching { settingsFile.outputStream().use { props.store(it, "financalc settings") } }
            .onFailure { kotlin.io.println("[HistoryStore] saveLanguage failed: ${it.message}") }
    }

    private fun loadLines(): List<String> =
        if (historyFile.exists()) historyFile.readLines().filter { it.isNotBlank() } else emptyList()
}

actual fun createHistoryStore(): HistoryStore = DesktopFileHistoryStore()
