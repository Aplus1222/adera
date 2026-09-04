package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SecureClipboardManager {
    private var clearJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Copies sensitive text to the clipboard and schedules a deletion after [timeoutMillis].
     * By default, it clears after 45 seconds to mitigate clipboard hijacking.
     */
    fun copySensitiveText(context: Context, label: String, text: String, timeoutMillis: Long = 45000L) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Cancel any existing clear job
        clearJob?.cancel()

        // Schedule new clear job
        clearJob = scope.launch {
            delay(timeoutMillis)
            // Clear the clipboard by setting empty clip
            val emptyClip = ClipData.newPlainText("", "")
            clipboard.setPrimaryClip(emptyClip)
        }
    }
}
