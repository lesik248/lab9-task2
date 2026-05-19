package com.example.financalc.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

enum class Language(val code: String, val displayName: String) {
    English("en", "English"),
    Russian("ru", "Русский"),
    Belarusian("be", "Беларуская")
}

data class Strings(
    val appTitle: String,
    val principal: String,
    val annualRate: String,
    val years: String,
    val capitalization: String,
    val monthly: String,
    val quarterly: String,
    val yearly: String,
    val calculate: String,
    val reset: String,
    val finalAmount: String,
    val profit: String,
    val growthChart: String,
    val history: String,
    val noHistory: String,
    val clearHistory: String,
    val language: String,
    val errEmpty: String,
    val errNotANumber: String,
    val errRangePrincipal: String,
    val errRangeRate: String,
    val errRangeYears: String,
    val month: String,
    val months: (Int) -> String
)

private val English = Strings(
    appTitle = "Financial Calculator",
    principal = "Initial amount",
    annualRate = "Annual rate, %",
    years = "Term, years",
    capitalization = "Capitalization",
    monthly = "Monthly",
    quarterly = "Quarterly",
    yearly = "Yearly",
    calculate = "Calculate",
    reset = "Reset",
    finalAmount = "Final amount",
    profit = "Profit",
    growthChart = "Capital growth",
    history = "History",
    noHistory = "No saved results yet",
    clearHistory = "Clear history",
    language = "Language",
    errEmpty = "Field is empty",
    errNotANumber = "Not a number",
    errRangePrincipal = "Must be 0.01 … 1 000 000 000",
    errRangeRate = "Must be 0 … 1000",
    errRangeYears = "Must be > 0 and ≤ 100",
    month = "Month",
    months = { n -> "$n mo" }
)

private val Russian = Strings(
    appTitle = "Финансовый калькулятор",
    principal = "Начальная сумма",
    annualRate = "Годовая ставка, %",
    years = "Срок, лет",
    capitalization = "Капитализация",
    monthly = "Ежемесячно",
    quarterly = "Ежеквартально",
    yearly = "Ежегодно",
    calculate = "Рассчитать",
    reset = "Сбросить",
    finalAmount = "Итоговая сумма",
    profit = "Прибыль",
    growthChart = "Рост капитала",
    history = "История",
    noHistory = "Пока нет сохранённых расчётов",
    clearHistory = "Очистить историю",
    language = "Язык",
    errEmpty = "Поле пустое",
    errNotANumber = "Не число",
    errRangePrincipal = "Должно быть 0,01 … 1 000 000 000",
    errRangeRate = "Должно быть 0 … 1000",
    errRangeYears = "Должно быть > 0 и ≤ 100",
    month = "Месяц",
    months = { n -> "$n мес." }
)

private val Belarusian = Strings(
    appTitle = "Фінансавы калькулятар",
    principal = "Пачатковая сума",
    annualRate = "Гадавая стаўка, %",
    years = "Тэрмін, гадоў",
    capitalization = "Капіталізацыя",
    monthly = "Штомесяц",
    quarterly = "Штоквартал",
    yearly = "Штогод",
    calculate = "Разлічыць",
    reset = "Скінуць",
    finalAmount = "Выніковая сума",
    profit = "Прыбытак",
    growthChart = "Рост капіталу",
    history = "Гісторыя",
    noHistory = "Пакуль няма захаваных разлікаў",
    clearHistory = "Ачысціць гісторыю",
    language = "Мова",
    errEmpty = "Поле пустое",
    errNotANumber = "Не лік",
    errRangePrincipal = "Павінна быць 0,01 … 1 000 000 000",
    errRangeRate = "Павінна быць 0 … 1000",
    errRangeYears = "Павінна быць > 0 і ≤ 100",
    month = "Месяц",
    months = { n -> "$n мес." }
)

fun stringsFor(lang: Language): Strings = when (lang) {
    Language.English -> English
    Language.Russian -> Russian
    Language.Belarusian -> Belarusian
}

val LocalStrings = staticCompositionLocalOf<Strings> { English }
val LocalLanguage = compositionLocalOf { Language.English }
