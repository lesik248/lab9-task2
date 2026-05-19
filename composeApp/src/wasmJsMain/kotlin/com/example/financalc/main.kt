package com.example.financalc

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val canvasElementId = "ComposeTarget"
    document.title = "Financial Calculator"
    CanvasBasedWindow(canvasElementId = canvasElementId) {
        App()
    }
}
