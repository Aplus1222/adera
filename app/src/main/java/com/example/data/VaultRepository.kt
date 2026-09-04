package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.localization.AderaLanguage
import com.example.model.Category
import com.example.model.PasswordHealth
import com.example.model.VaultItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.crypto.spec.SecretKeySpec

data class DecryptedVaultItem(
    val id: String,
    val title: String,
    val username: String,
    val email: String,
    val password: String,
    val website: String,
    val category: Category,
    val itemType: VaultItemType,
    val notes: String,
    val tags: List<String>,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val lastUsedAt: Long
)

class VaultRepository(
    private val context: Context,
    private val vaultDao: VaultDao
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("adera_secure_prefs", Context.MODE_PRIVATE)

    

    // Preferences keys
    companion object {
        var activeSecretKey: SecretKeySpec? = null
        private const val KEY_IS_SETUP = "is_vault_setup"
        private const val KEY_SALT = "master_salt"
        private const val KEY_VERIFIER = "master_verifier"
        private const val KEY_EMERGENCY_CODE = "emergency_recovery_code"
        private const val KEY_BIOMETRICS_ENABLED = "biometrics_enabled"
        private const val KEY_AUTO_LOCK_MINS = "auto_lock_mins"
        private const val KEY_CLIPBOARD_TIMEOUT_SECS = "clipboard_timeout_secs"
        private const val KEY_PRIVACY_MODE = "privacy_mode_enabled"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_THEME = "app_theme" // LIGHT, DARK, SYSTEM
    }

    fun isVaultSetup(): Boolean = prefs.getBoolean(KEY_IS_SETUP, false)

    fun setupMasterPassword(masterPassword: String, emergencyCode: String): Boolean {
        val salt = SecurityCrypto.generateSalt()
        val key = SecurityCrypto.deriveKey(masterPassword, salt)
        val verifier = SecurityCrypto.encrypt("ADERA_VALID_VAULT_KEY", key)

        val keyBase64 = android.util.Base64.encodeToString(key.encoded, android.util.Base64.NO_WRAP)
        prefs.edit()
            .putBoolean(KEY_IS_SETUP, true)
            .putString(KEY_SALT, salt)
            .putString(KEY_VERIFIER, verifier)
            .putString(KEY_EMERGENCY_CODE, emergencyCode)
            .putString("biometric_vault_key", keyBase64)
            .apply()

        activeSecretKey = key
        return true
    }

    fun verifyMasterPassword(masterPassword: String): Boolean {
        val salt = prefs.getString(KEY_SALT, null) ?: return false
        val verifier = prefs.getString(KEY_VERIFIER, null) ?: return false

        val key = SecurityCrypto.deriveKey(masterPassword, salt)
        val decrypted = SecurityCrypto.decrypt(verifier, key)

        return if (decrypted == "ADERA_VALID_VAULT_KEY") {
            activeSecretKey = key
            val keyBase64 = android.util.Base64.encodeToString(key.encoded, android.util.Base64.NO_WRAP)
            prefs.edit().putString("biometric_vault_key", keyBase64).apply()
            true
        } else {
            false
        }
    }

    fun recoverWithEmergencyCode(emergencyCode: String, newMasterPassword: String): Boolean {
        val savedCode = prefs.getString(KEY_EMERGENCY_CODE, "") ?: ""
        if (savedCode.isNotEmpty() && savedCode == emergencyCode.trim()) {
            setupMasterPassword(newMasterPassword, emergencyCode)
            return true
        }
        return false
    }

    fun lockVault() {
        activeSecretKey = null
    }

    fun unlockWithBiometrics(): Boolean {
        if (activeSecretKey != null) return true
        val keyBase64 = prefs.getString("biometric_vault_key", null)
        if (keyBase64 != null) {
            val keyBytes = android.util.Base64.decode(keyBase64, android.util.Base64.NO_WRAP)
            activeSecretKey = javax.crypto.spec.SecretKeySpec(keyBytes, "AES")
            return true
        }
        return false
    }

    fun isVaultUnlocked(): Boolean = activeSecretKey != null

    // Settings accessors
    fun isBiometricsEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, true)
    fun setBiometricsEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_BIOMETRICS_ENABLED, enabled).apply()

    fun getAutoLockMins(): Int = prefs.getInt(KEY_AUTO_LOCK_MINS, 1)
    fun setAutoLockMins(mins: Int) = prefs.edit().putInt(KEY_AUTO_LOCK_MINS, mins).apply()

    fun getClipboardTimeoutSecs(): Int = prefs.getInt(KEY_CLIPBOARD_TIMEOUT_SECS, 30)
    fun setClipboardTimeoutSecs(secs: Int) = prefs.edit().putInt(KEY_CLIPBOARD_TIMEOUT_SECS, secs).apply()

    fun isPrivacyModeEnabled(): Boolean = prefs.getBoolean(KEY_PRIVACY_MODE, true)
    fun setPrivacyModeEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_PRIVACY_MODE, enabled).apply()

    fun getLanguage(): AderaLanguage {
        val code = prefs.getString(KEY_LANGUAGE, AderaLanguage.ENGLISH.code) ?: AderaLanguage.ENGLISH.code
        return if (code == AderaLanguage.AMHARIC.code) AderaLanguage.AMHARIC else AderaLanguage.ENGLISH
    }

    fun setLanguage(lang: AderaLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
    }

    fun getTheme(): String = prefs.getString(KEY_THEME, "SYSTEM") ?: "SYSTEM"
    fun setTheme(theme: String) = prefs.edit().putString(KEY_THEME, theme).apply()

    fun getEmergencyCode(): String = prefs.getString(KEY_EMERGENCY_CODE, "") ?: ""

    // Vault Data Operations

    fun getAllVaultItems(): Flow<List<DecryptedVaultItem>> {
        return vaultDao.getAllVaultItems().map { list ->
            list.map { decryptVaultEntity(it) }
        }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    fun getFavoriteItems(): Flow<List<DecryptedVaultItem>> {
        return vaultDao.getFavoriteVaultItems().map { list ->
            list.map { decryptVaultEntity(it) }
        }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    fun getRecentlyUsedItems(): Flow<List<DecryptedVaultItem>> {
        return vaultDao.getRecentlyUsedItems().map { list ->
            list.map { decryptVaultEntity(it) }
        }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    suspend fun saveVaultItem(item: DecryptedVaultItem) {
        val key = activeSecretKey ?: return
        val entity = VaultEntity(
            id = item.id,
            title = item.title,
            encryptedUsername = SecurityCrypto.encrypt(item.username, key),
            encryptedEmail = SecurityCrypto.encrypt(item.email, key),
            encryptedPassword = SecurityCrypto.encrypt(item.password, key),
            encryptedWebsite = SecurityCrypto.encrypt(item.website, key),
            category = item.category.name,
            itemType = item.itemType.name,
            encryptedNotes = SecurityCrypto.encrypt(item.notes, key),
            tags = item.tags.joinToString(","),
            isFavorite = item.isFavorite,
            createdAt = item.createdAt,
            updatedAt = System.currentTimeMillis(),
            lastUsedAt = item.lastUsedAt
        )
        vaultDao.insertVaultItem(entity)
    }

    suspend fun markItemUsed(id: String) {
        val entity = vaultDao.getVaultItemById(id) ?: return
        vaultDao.updateVaultItem(entity.copy(lastUsedAt = System.currentTimeMillis()))
    }

    suspend fun deleteVaultItem(id: String) {
        vaultDao.deleteVaultItemById(id)
    }

    private fun decryptVaultEntity(entity: VaultEntity): DecryptedVaultItem {
        val key = activeSecretKey
        val username = key?.let { SecurityCrypto.decrypt(entity.encryptedUsername, it) } ?: "••••••"
        val email = key?.let { SecurityCrypto.decrypt(entity.encryptedEmail, it) } ?: "••••••"
        val password = key?.let { SecurityCrypto.decrypt(entity.encryptedPassword, it) } ?: "••••••••"
        val website = key?.let { SecurityCrypto.decrypt(entity.encryptedWebsite, it) } ?: ""
        val notes = key?.let { SecurityCrypto.decrypt(entity.encryptedNotes, it) } ?: ""

        val cat = try { Category.valueOf(entity.category) } catch (e: Exception) { Category.OTHER }
        val type = try { VaultItemType.valueOf(entity.itemType) } catch (e: Exception) { VaultItemType.LOGIN }

        val tagList = if (entity.tags.isBlank()) emptyList() else entity.tags.split(",").map { it.trim() }

        return DecryptedVaultItem(
            id = entity.id,
            title = entity.title,
            username = username,
            email = email,
            password = password,
            website = website,
            category = cat,
            itemType = type,
            notes = notes,
            tags = tagList,
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastUsedAt = entity.lastUsedAt
        )
    }

    // Backup & Restore
    suspend fun exportEncryptedBackup(backupPassword: String): String {
        val salt = SecurityCrypto.generateSalt()
        val backupKey = SecurityCrypto.deriveKey(backupPassword, salt)

        val items = mutableListOf<DecryptedVaultItem>()
        vaultDao.getAllVaultItems().first().forEach { entity ->
            items.add(decryptVaultEntity(entity))
        }

        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("username", item.username)
                put("email", item.email)
                put("password", item.password)
                put("website", item.website)
                put("category", item.category.name)
                put("itemType", item.itemType.name)
                put("notes", item.notes)
                put("tags", item.tags.joinToString(","))
                put("isFavorite", item.isFavorite)
                put("createdAt", item.createdAt)
            }
            jsonArray.put(obj)
        }

        val payload = jsonArray.toString()
        val encryptedPayload = SecurityCrypto.encrypt(payload, backupKey)

        val backupObj = JSONObject().apply {
            put("app", "ADERA")
            put("version", 1)
            put("salt", salt)
            put("payload", encryptedPayload)
        }

        return backupObj.toString()
    }

    suspend fun importEncryptedBackup(jsonBackup: String, backupPassword: String): Boolean {
        return try {
            val backupObj = JSONObject(jsonBackup)
            if (!backupObj.has("app") || backupObj.getString("app") != "ADERA") return false

            val salt = backupObj.getString("salt")
            val encryptedPayload = backupObj.getString("payload")

            val backupKey = SecurityCrypto.deriveKey(backupPassword, salt)
            val decryptedPayload = SecurityCrypto.decrypt(encryptedPayload, backupKey)

            if (decryptedPayload.isEmpty()) return false

            val jsonArray = JSONArray(decryptedPayload)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = DecryptedVaultItem(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    title = obj.getString("title"),
                    username = obj.optString("username", ""),
                    email = obj.optString("email", ""),
                    password = obj.optString("password", ""),
                    website = obj.optString("website", ""),
                    category = try { Category.valueOf(obj.optString("category", "OTHER")) } catch (e: Exception) { Category.OTHER },
                    itemType = try { VaultItemType.valueOf(obj.optString("itemType", "LOGIN")) } catch (e: Exception) { VaultItemType.LOGIN },
                    notes = obj.optString("notes", ""),
                    tags = if (obj.optString("tags", "").isBlank()) emptyList() else obj.getString("tags").split(","),
                    isFavorite = obj.optBoolean("isFavorite", false),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = System.currentTimeMillis(),
                    lastUsedAt = System.currentTimeMillis()
                )
                saveVaultItem(item)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Password Health Analysis
    fun analyzePasswordHealth(items: List<DecryptedVaultItem>): PasswordHealth {
        if (items.isEmpty()) return PasswordHealth()

        var safeCount = 0
        var weakCount = 0
        var reusedCount = 0
        var compromisedCount = 0
        var oldCount = 0

        val passwordCounts = items.groupingBy { it.password }.eachCount()
        val ninetyDaysAgo = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)

        for (item in items) {
            val strength = SecurityCrypto.calculateStrengthScore(item.password)
            val count = passwordCounts[item.password] ?: 1

            var isFlawed = false
            if (strength < 60) {
                weakCount++
                isFlawed = true
            }
            if (count > 1) {
                reusedCount++
                isFlawed = true
            }
            if (item.password.lowercase().contains("123456") || item.password.lowercase().contains("password") || item.password.length < 6) {
                compromisedCount++
                isFlawed = true
            }
            if (item.updatedAt < ninetyDaysAgo) {
                oldCount++
            }

            if (!isFlawed && strength >= 70) {
                safeCount++
            }
        }

        val total = items.size
        var deductions = (weakCount * 15) + (reusedCount * 12) + (compromisedCount * 25) + (oldCount * 5)
        val rawScore = 100 - deductions
        val finalScore = rawScore.coerceIn(10, 100)

        return PasswordHealth(
            score = finalScore,
            safeCount = safeCount,
            weakCount = weakCount,
            reusedCount = reusedCount,
            compromisedCount = compromisedCount,
            oldCount = oldCount,
            totalCount = total
        )
    }
}
