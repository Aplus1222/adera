package com.example.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * BiometricAuthManager utilizes the androidx.biometric:biometric-ktx library
 * to handle fingerprint and face authentication to secure vault access in ADERA.
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    /**
     * Checks if biometric authentication (fingerprint, face recognition, etc.)
     * is available and enrolled on the device.
     */
    fun isBiometricAvailable(): Boolean {
        return try {
            val biometricManager = BiometricManager.from(activity)
            val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
            biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Returns the exact status code from BiometricManager.canAuthenticate().
     */
    fun canAuthenticateStatus(): Int {
        return try {
            val biometricManager = BiometricManager.from(activity)
            val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
            biometricManager.canAuthenticate(authenticators)
        } catch (e: Exception) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        }
    }

    /**
     * Shows the Android BiometricPrompt for fingerprint or face authentication.
     *
     * @param title Title for the biometric prompt dialog.
     * @param subtitle Optional subtitle describing the action.
     * @param negativeButtonText Text for the fallback button (e.g., Master Password).
     * @param onSuccess Callback triggered on successful authentication.
     * @param onError Callback triggered when authentication encounters an error or cancellation.
     * @param onFailed Callback triggered when biometric input is rejected (e.g. unrecorded finger).
     */
    fun authenticate(
        title: String,
        subtitle: String = "",
        negativeButtonText: String = "Use Master Password",
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit = {}
    ) {
        try {
            val executor = ContextCompat.getMainExecutor(activity)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .apply {
                    if (subtitle.isNotEmpty()) {
                        setSubtitle(subtitle)
                    }
                }
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .build()

            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(e.message ?: "Biometric prompt not available")
        }
    }

    companion object {
        fun isAvailable(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
            return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
        }
    }
}
