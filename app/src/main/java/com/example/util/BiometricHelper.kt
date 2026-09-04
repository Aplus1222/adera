package com.example.util

import android.content.Context
import androidx.fragment.app.FragmentActivity

object BiometricHelper {

    fun isBiometricAvailable(context: Context): Boolean {
        return BiometricAuthManager.isAvailable(context)
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String = "",
        negativeButtonText: String = "Use Master Password",
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit = {}
    ) {
        val manager = BiometricAuthManager(activity)
        manager.authenticate(
            title = title,
            subtitle = subtitle,
            negativeButtonText = negativeButtonText,
            onSuccess = onSuccess,
            onError = onError,
            onFailed = onFailed
        )
    }
}
