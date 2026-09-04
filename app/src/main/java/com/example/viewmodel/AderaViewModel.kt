package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AderaDatabase
import com.example.data.DecryptedVaultItem
import com.example.data.SecurityCrypto
import com.example.data.VaultRepository
import com.example.localization.AderaLanguage
import com.example.localization.AderaStrings
import com.example.localization.LocalizedText
import com.example.model.Category
import com.example.model.PasswordHealth
import com.example.model.VaultItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AderaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VaultRepository

    private val _isVaultSetup = MutableStateFlow(false)
    val isVaultSetup: StateFlow<Boolean> = _isVaultSetup.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _language = MutableStateFlow(AderaLanguage.ENGLISH)
    val language: StateFlow<AderaLanguage> = _language.asStateFlow()

    private val _strings = MutableStateFlow<LocalizedText>(AderaStrings.get(AderaLanguage.ENGLISH))
    val strings: StateFlow<LocalizedText> = _strings.asStateFlow()

    private val _theme = MutableStateFlow("SYSTEM")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _allVaultItems = MutableStateFlow<List<DecryptedVaultItem>>(emptyList())
    val allVaultItems: StateFlow<List<DecryptedVaultItem>> = _allVaultItems.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow<VaultItemType?>(null)
    val selectedType: StateFlow<VaultItemType?> = _selectedType.asStateFlow()

    private val _passwordHealth = MutableStateFlow(PasswordHealth())
    val passwordHealth: StateFlow<PasswordHealth> = _passwordHealth.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _quickActionDialogOpen = MutableStateFlow(false)
    val quickActionDialogOpen: StateFlow<Boolean> = _quickActionDialogOpen.asStateFlow()

    private val _quickActionPreselectedType = MutableStateFlow(VaultItemType.LOGIN)
    val quickActionPreselectedType: StateFlow<VaultItemType> = _quickActionPreselectedType.asStateFlow()

    private var clipboardJob: Job? = null

    init {
        val database = AderaDatabase.getInstance(application)
        repository = VaultRepository(application, database.vaultDao())

        val currentLang = repository.getLanguage()
        _language.value = currentLang
        _strings.value = AderaStrings.get(currentLang)
        _theme.value = repository.getTheme()
        _isVaultSetup.value = repository.isVaultSetup()

        observeVaultItems()
    }

    private fun observeVaultItems() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllVaultItems().collect { items ->
                val health = repository.analyzePasswordHealth(items)
                withContext(Dispatchers.Main) {
                    _allVaultItems.value = items
                    _passwordHealth.value = health
                }
            }
        }
    }

    fun setLanguage(lang: AderaLanguage) {
        _language.value = lang
        _strings.value = AderaStrings.get(lang)
        repository.setLanguage(lang)
    }

    fun setTheme(theme: String) {
        _theme.value = theme
        repository.setTheme(theme)
    }

    suspend fun setupVault(masterPassword: String, emergencyCode: String): Boolean = withContext(Dispatchers.IO) {
        val success = repository.setupMasterPassword(masterPassword, emergencyCode)
        if (success) {
            withContext(Dispatchers.Main) {
                _isVaultSetup.value = true
                _isUnlocked.value = true
                seedDemoDataIfEmpty()
            }
        }
        success
    }

    suspend fun unlockVault(masterPassword: String): Boolean = withContext(Dispatchers.IO) {
        val success = repository.verifyMasterPassword(masterPassword)
        if (success) {
            withContext(Dispatchers.Main) {
                _isUnlocked.value = true
            }
        }
        success
    }

    suspend fun verifyMasterPassword(masterPassword: String): Boolean = withContext(Dispatchers.IO) {
        repository.verifyMasterPassword(masterPassword)
    }

    suspend fun unlockWithBiometrics(): Boolean = withContext(Dispatchers.IO) {
        val success = repository.unlockWithBiometrics()
        if (success) {
            withContext(Dispatchers.Main) {
                _isUnlocked.value = true
            }
        }
        success
    }

    fun lockVault() {
        repository.lockVault()
        _isUnlocked.value = false
        showToast(strings.value.vaultLockedNotification)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun setSelectedType(type: VaultItemType?) {
        _selectedType.value = type
    }

    fun saveVaultItem(item: DecryptedVaultItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveVaultItem(item)
            withContext(Dispatchers.Main) {
                showToast(strings.value.saveButton)
            }
        }
    }

    fun deleteVaultItem(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteVaultItem(id)
            withContext(Dispatchers.Main) {
                showToast(strings.value.deleteButton)
            }
        }
    }

    fun toggleFavorite(item: DecryptedVaultItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = item.copy(isFavorite = !item.isFavorite)
            repository.saveVaultItem(updated)
        }
    }

    fun copyToClipboard(label: String, text: String, isSensitive: Boolean = true) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        
        if (isSensitive && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            clip.description.extras = android.os.PersistableBundle().apply {
                putBoolean(android.content.ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }
        
        clipboard.setPrimaryClip(clip)

        showToast(strings.value.passwordCopiedToast)

        clipboardJob?.cancel()
        val timeoutSecs = repository.getClipboardTimeoutSecs()
        clipboardJob = viewModelScope.launch {
            delay(timeoutSecs * 1000L)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                clipboard.clearPrimaryClip()
            } else {
                clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
            }
            showToast(strings.value.clipboardClearedToast)
        }
    }

    fun openQuickAction(type: VaultItemType = VaultItemType.LOGIN) {
        _quickActionPreselectedType.value = type
        _quickActionDialogOpen.value = true
    }

    fun closeQuickAction() {
        _quickActionDialogOpen.value = false
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Backup & Restore
    fun exportBackup(password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = repository.exportEncryptedBackup(password)
            withContext(Dispatchers.Main) {
                onResult(json)
            }
        }
    }

    fun importBackup(json: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.importEncryptedBackup(json, password)
            withContext(Dispatchers.Main) {
                if (success) {
                    showToast(strings.value.backupImportSuccess)
                } else {
                    showToast(strings.value.backupImportError)
                }
                onResult(success)
            }
        }
    }

    // Emergency recovery
    suspend fun recoverVault(emergencyCode: String, newMasterPassword: String): Boolean = withContext(Dispatchers.IO) {
        val success = repository.recoverWithEmergencyCode(emergencyCode, newMasterPassword)
        if (success) {
            withContext(Dispatchers.Main) {
                _isUnlocked.value = true
                showToast("Vault recovered successfully!")
            }
        }
        success
    }

    fun getEmergencyCode(): String = repository.getEmergencyCode()
    fun isBiometricsEnabled(): Boolean = repository.isBiometricsEnabled()
    fun setBiometricsEnabled(enabled: Boolean) = repository.setBiometricsEnabled(enabled)
    fun getAutoLockMins(): Int = repository.getAutoLockMins()
    fun setAutoLockMins(mins: Int) = repository.setAutoLockMins(mins)
    fun getClipboardTimeoutSecs(): Int = repository.getClipboardTimeoutSecs()
    fun setClipboardTimeoutSecs(secs: Int) = repository.setClipboardTimeoutSecs(secs)
    fun isPrivacyModeEnabled(): Boolean = repository.isPrivacyModeEnabled()
    fun setPrivacyModeEnabled(enabled: Boolean) = repository.setPrivacyModeEnabled(enabled)

    private fun seedDemoDataIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = _allVaultItems.value
            if (existing.isEmpty()) {
                val seedItems = listOf(
                    DecryptedVaultItem(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Telebirr Super App",
                        username = "+251911223344",
                        email = "user.ethiopia@gmail.com",
                        password = SecurityCrypto.generatePassword(16),
                        website = "https://telebirr.et",
                        category = Category.FINANCE,
                        itemType = VaultItemType.LOGIN,
                        notes = "Primary Ethiopian Mobile Money Vault Entry",
                        tags = listOf("telebirr", "finance", "ethiopia"),
                        isFavorite = true,
                        createdAt = System.currentTimeMillis() - 100000,
                        updatedAt = System.currentTimeMillis(),
                        lastUsedAt = System.currentTimeMillis()
                    ),
                    DecryptedVaultItem(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Commercial Bank of Ethiopia (CBE)",
                        username = "cbe_user_992",
                        email = "cbe.user@gmail.com",
                        password = SecurityCrypto.generatePassword(18),
                        website = "https://cbe.com.et",
                        category = Category.FINANCE,
                        itemType = VaultItemType.LOGIN,
                        notes = "CBE Birr & Mobile Banking account",
                        tags = listOf("cbe", "banking"),
                        isFavorite = true,
                        createdAt = System.currentTimeMillis() - 200000,
                        updatedAt = System.currentTimeMillis(),
                        lastUsedAt = System.currentTimeMillis() - 50000
                    ),
                    DecryptedVaultItem(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Telegram Messenger",
                        username = "@adera_user",
                        email = "user.ethiopia@gmail.com",
                        password = SecurityCrypto.generatePassword(20),
                        website = "https://telegram.org",
                        category = Category.SOCIAL,
                        itemType = VaultItemType.LOGIN,
                        notes = "Primary messaging and channel admin",
                        tags = listOf("social", "telegram"),
                        isFavorite = true,
                        createdAt = System.currentTimeMillis() - 300000,
                        updatedAt = System.currentTimeMillis(),
                        lastUsedAt = System.currentTimeMillis() - 20000
                    ),
                    DecryptedVaultItem(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Google Workspace",
                        username = "dev.ethiopia@gmail.com",
                        email = "dev.ethiopia@gmail.com",
                        password = SecurityCrypto.generatePassword(16),
                        website = "https://accounts.google.com",
                        category = Category.EMAIL,
                        itemType = VaultItemType.LOGIN,
                        notes = "Work and personal email account",
                        tags = listOf("google", "email", "work"),
                        isFavorite = false,
                        createdAt = System.currentTimeMillis() - 400000,
                        updatedAt = System.currentTimeMillis(),
                        lastUsedAt = System.currentTimeMillis() - 100000
                    ),
                    DecryptedVaultItem(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Ethiopian Airlines ShebaMiles",
                        username = "SM-90283411",
                        email = "user.ethiopia@gmail.com",
                        password = SecurityCrypto.generatePassword(16),
                        website = "https://ethiopianairlines.com",
                        category = Category.APPS,
                        itemType = VaultItemType.LOGIN,
                        notes = "Frequent flyer account and tickets",
                        tags = listOf("travel", "airline"),
                        isFavorite = false,
                        createdAt = System.currentTimeMillis() - 500000,
                        updatedAt = System.currentTimeMillis(),
                        lastUsedAt = System.currentTimeMillis() - 150000
                    )
                )

                for (item in seedItems) {
                    repository.saveVaultItem(item)
                }
            }
        }
    }
}
