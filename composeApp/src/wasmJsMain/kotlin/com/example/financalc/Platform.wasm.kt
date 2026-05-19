package com.example.financalc

import androidx.compose.runtime.Composable

actual val currentPlatform: PlatformKind = PlatformKind.Web

@Composable
actual fun isSystemInDarkPreference(): Boolean = false
