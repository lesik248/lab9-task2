package com.example.financalc

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

actual val currentPlatform: PlatformKind = PlatformKind.Android

@Composable
actual fun isSystemInDarkPreference(): Boolean = isSystemInDarkTheme()
