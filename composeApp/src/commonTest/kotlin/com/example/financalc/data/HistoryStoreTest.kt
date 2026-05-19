package com.example.financalc.data

import com.example.financalc.calculator.Capitalization
import com.example.financalc.i18n.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HistoryStoreTest {

    @Test
    fun entry_roundtrip_preserves_fields() {
        val e = HistoryEntry(1000.0, 5.0, 3.0, Capitalization.Monthly, 1161.62, 161.62)
        val back = HistoryEntry.parse(e.serialize())!!
        assertEquals(e, back)
    }

    @Test
    fun parse_rejects_malformed_lines() {
        assertNull(HistoryEntry.parse("garbage"))
        assertNull(HistoryEntry.parse("1|2|3"))
    }

    @Test
    fun in_memory_store_keeps_newest_first() {
        val s = InMemoryHistoryStore()
        s.save(HistoryEntry(1.0, 1.0, 1.0, Capitalization.Yearly, 2.0, 1.0))
        s.save(HistoryEntry(10.0, 1.0, 1.0, Capitalization.Yearly, 20.0, 10.0))
        val list = s.loadAll()
        assertEquals(10.0, list.first().principal)
    }

    @Test
    fun clear_removes_all_history() {
        val s = InMemoryHistoryStore()
        s.save(HistoryEntry(1.0, 1.0, 1.0, Capitalization.Yearly, 2.0, 1.0))
        s.clear()
        assertTrue(s.loadAll().isEmpty())
    }

    @Test
    fun language_persists_across_load() {
        val s = InMemoryHistoryStore()
        assertEquals(Language.English, s.loadLanguage())
        s.saveLanguage(Language.Belarusian)
        assertEquals(Language.Belarusian, s.loadLanguage())
    }
}
