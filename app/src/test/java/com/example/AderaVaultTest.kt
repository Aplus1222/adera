package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AderaDatabase
import com.example.data.SecurityCrypto
import com.example.data.VaultRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AderaVaultTest {

    private lateinit var context: Context
    private lateinit var repository: VaultRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val db = AderaDatabase.getInstance(context)
        repository = VaultRepository(context, db.vaultDao())
    }

    @Test
    fun testSecurityCrypto_EncryptDecrypt() {
        val masterPassword = "TestSecretPassword123!"
        val salt = SecurityCrypto.generateSalt()
        val key = SecurityCrypto.deriveKey(masterPassword, salt)

        val plainText = "My Secret Account Password"
        val encrypted = SecurityCrypto.encrypt(plainText, key)

        assertTrue(encrypted.isNotEmpty())
        assertFalse(encrypted == plainText)

        val decrypted = SecurityCrypto.decrypt(encrypted, key)
        assertEquals(plainText, decrypted)
    }

    @Test
    fun testSecurityCrypto_PasswordGenerator() {
        val pass = SecurityCrypto.generatePassword(length = 20, includeUpper = true, includeLower = true, includeNumbers = true, includeSymbols = true)
        assertEquals(20, pass.length)

        val score = SecurityCrypto.calculateStrengthScore(pass)
        assertTrue(score >= 70)
    }

    @Test
    fun testVaultRepository_SetupAndUnlock() {
        assertFalse(repository.isVaultSetup())

        val emergencyCode = "EMERGENCY-1234-5678"
        repository.setupMasterPassword("MasterKeyPass123!", emergencyCode)
        assertTrue(repository.isVaultSetup())
        assertTrue(repository.isVaultUnlocked())

        // Lock vault
        repository.lockVault()
        assertFalse(repository.isVaultUnlocked())

        // Unlock with correct password
        val unlockSuccess = repository.verifyMasterPassword("MasterKeyPass123!")
        assertTrue(unlockSuccess)
        assertTrue(repository.isVaultUnlocked())

        // Lock and unlock with wrong password
        repository.lockVault()
        val wrongUnlock = repository.verifyMasterPassword("WrongPassword")
        assertFalse(wrongUnlock)
        assertFalse(repository.isVaultUnlocked())
    }

    @Test
    fun testVaultRepository_EmergencyRecovery() {
        val emergencyCode = "EMERGENCY-9999-0000"
        repository.setupMasterPassword("OriginalPass123", emergencyCode)
        repository.lockVault()

        val recoverySuccess = repository.recoverWithEmergencyCode(emergencyCode, "NewMasterPass456!")
        assertTrue(recoverySuccess)
        assertTrue(repository.isVaultUnlocked())

        // Test unlocking with new password
        repository.lockVault()
        assertTrue(repository.verifyMasterPassword("NewMasterPass456!"))
    }
}
