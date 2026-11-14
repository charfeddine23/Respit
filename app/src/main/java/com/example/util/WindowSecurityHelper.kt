package com.example.util

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

/**
 * Applies or clears WindowManager.LayoutParams.FLAG_SECURE dynamically on an Activity window
 * or Compose Dialog window to block screenshots and screen recording.
 */
object WindowSecurityHelper {
    fun applySecureFlag(window: Window?, enabled: Boolean) {
        if (window == null) return
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun applySecureFlag(activity: Activity?, enabled: Boolean) {
        applySecureFlag(activity?.window, enabled)
    }
}

@Composable
fun SyncDialogSecureWindow(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, enabled) {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window
        WindowSecurityHelper.applySecureFlag(dialogWindow, enabled)
        onDispose { }
    }
}
