package rs.raf.banka1.mobile.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

fun Context.safeFindActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun configureSystemBars(
    view: View,
    window: Window,
    darkMode: Boolean,
    showSystemBars: Boolean = true
) {
    val insetsController = WindowCompat.getInsetsController(window, view)
    insetsController.isAppearanceLightStatusBars = !darkMode
    insetsController.isAppearanceLightNavigationBars = !darkMode
    if (showSystemBars) {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    } else {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}