package com.example

import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.data.DecryptedVaultItem
import com.example.localization.AderaLanguage
import com.example.model.Category
import com.example.model.VaultItemType
import com.example.ui.components.AderaBottomNav
import com.example.ui.components.AderaNavigationRail
import com.example.ui.components.AderaLockScreen
import com.example.ui.components.AderaBiometricSplashScreen
import com.example.ui.components.AderaQuickActionDialog
import com.example.ui.components.AderaTab
import com.example.ui.screens.AddEditVaultItemScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PasswordGeneratorScreen
import com.example.ui.screens.SecurityDashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.VaultSetupScreen
import com.example.ui.theme.AderaTheme
import com.example.util.BiometricAuthManager
import com.example.util.BiometricHelper
import com.example.viewmodel.AderaViewModel

sealed class AppDestination {
    object Onboarding : AppDestination()
    object SetupVault : AppDestination()
    object AddEditItem : AppDestination()
}

class MainActivity : FragmentActivity() {

    private val viewModel: AderaViewModel by viewModels()
    private var backgroundTimestamp: Long = 0L

    override fun onStop() {
        super.onStop()
        backgroundTimestamp = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
        checkAutoLock()
    }

    override fun onResume() {
        super.onResume()
        updateWindowPrivacyFlags()
    }

    private fun checkAutoLock() {
        if (backgroundTimestamp > 0L) {
            val elapsedMillis = System.currentTimeMillis() - backgroundTimestamp
            val autoLockMins = viewModel.getAutoLockMins()
            val autoLockThresholdMs = if (autoLockMins == 0) 0L else autoLockMins * 60 * 1000L

            if (viewModel.isVaultSetup.value && viewModel.isUnlocked.value) {
                if (elapsedMillis >= autoLockThresholdMs) {
                    viewModel.lockVault()
                }
            }
            backgroundTimestamp = 0L
        }
    }

    private fun updateWindowPrivacyFlags() {
        // Disabled FLAG_SECURE because it prevents the AI Studio Streaming Emulator 
        // from capturing and displaying the screen to the user.
        /*
        if (viewModel.isPrivacyModeEnabled()) {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
        */
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isVaultSetup by viewModel.isVaultSetup.collectAsState()
            val isUnlocked by viewModel.isUnlocked.collectAsState()
            val language by viewModel.language.collectAsState()
            val strings by viewModel.strings.collectAsState()
            val themeMode by viewModel.theme.collectAsState()

            val allVaultItems by viewModel.allVaultItems.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedCategory by viewModel.selectedCategory.collectAsState()
            val selectedType by viewModel.selectedType.collectAsState()
            val passwordHealth by viewModel.passwordHealth.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()
            val quickActionOpen by viewModel.quickActionDialogOpen.collectAsState()

            var currentTab by remember { mutableStateOf(AderaTab.HOME) }
            var subDestination by remember { mutableStateOf<AppDestination?>(null) }
            var editingItem by remember { mutableStateOf<DecryptedVaultItem?>(null) }
            var preselectedType by remember { mutableStateOf(VaultItemType.LOGIN) }
            var showSplashAnimation by remember { mutableStateOf(true) }

            val snackbarHostState = remember { SnackbarHostState() }

            val lifecycleOwner = LocalLifecycleOwner.current
            var backgroundTimeMillis by remember { mutableStateOf(0L) }

            androidx.activity.compose.BackHandler(enabled = subDestination != null) {
                subDestination = null
                editingItem = null
            }
            androidx.activity.compose.BackHandler(enabled = subDestination == null && currentTab != AderaTab.HOME) {
                currentTab = AderaTab.HOME
            }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_STOP) {
                        backgroundTimeMillis = SystemClock.elapsedRealtime()
                    } else if (event == Lifecycle.Event.ON_START) {
                        if (backgroundTimeMillis > 0L) {
                            val elapsedMins = (SystemClock.elapsedRealtime() - backgroundTimeMillis) / 60000L
                            val autoLockMins = viewModel.getAutoLockMins()
                            if (autoLockMins > 0 && elapsedMins >= autoLockMins) {
                                viewModel.lockVault()
                            }
                            backgroundTimeMillis = 0L
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            val biometricAuthManager = remember { BiometricAuthManager(this@MainActivity) }
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            AderaTheme(darkTheme = darkTheme) {
                var showMasterPasswordPrompt by remember { mutableStateOf<(() -> Unit)?>(null) }
                
                LaunchedEffect(isUnlocked) {
                    if (isUnlocked && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val hasStructure = this@MainActivity.intent?.hasExtra(android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE) == true
                        if (hasStructure) {
                            val structure = this@MainActivity.intent?.getParcelableExtra<android.app.assist.AssistStructure>(
                                android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
                            )
                            if (structure != null) {
                                val targetPackageName = structure.activityComponent?.packageName ?: ""
                                val allItems = viewModel.allVaultItems.value
                                
                                val parsedStructure = parseStructure(structure)
                                val domain = parsedStructure.webDomain ?: ""
                                
                                val matchingItems = allItems.filter { item ->
                                    item.itemType == com.example.model.VaultItemType.LOGIN && (
                                        (targetPackageName.isNotEmpty() && !targetPackageName.contains("chrome") && !targetPackageName.contains("browser") && !targetPackageName.contains("firefox") && (
                                            item.website.contains(targetPackageName, ignoreCase = true) || 
                                            targetPackageName.contains(item.website, ignoreCase = true) ||
                                            item.title.contains(targetPackageName, ignoreCase = true)
                                        )) ||
                                        (domain.isNotEmpty() && (
                                            item.website.contains(domain, ignoreCase = true) ||
                                            domain.contains(item.website, ignoreCase = true) ||
                                            item.title.contains(domain, ignoreCase = true)
                                        ))
                                    )
                                }.take(5)

                                if (matchingItems.isNotEmpty()) {
                                    val responseBuilder = android.service.autofill.FillResponse.Builder()
                                    var addedDataset = false

                                    for (item in matchingItems) {
                                        val datasetBuilder = android.service.autofill.Dataset.Builder()
                                        val presentation = android.widget.RemoteViews(packageName, com.example.R.layout.autofill_presentation)
                                        presentation.setTextViewText(com.example.R.id.autofill_title, item.title)
                                        val subtitle = if (item.username.isNotEmpty()) item.username else item.email
                                        presentation.setTextViewText(com.example.R.id.autofill_subtitle, subtitle)

                                        var hasValue = false
                                        if (parsedStructure.usernameId != null) {
                                            val usernameToFill = if (item.username.isNotEmpty()) item.username else item.email
                                            datasetBuilder.setValue(
                                                parsedStructure.usernameId!!,
                                                android.view.autofill.AutofillValue.forText(usernameToFill),
                                                presentation
                                            )
                                            hasValue = true
                                        }
                                        if (parsedStructure.passwordId != null) {
                                            datasetBuilder.setValue(
                                                parsedStructure.passwordId!!,
                                                android.view.autofill.AutofillValue.forText(item.password),
                                                presentation
                                            )
                                            hasValue = true
                                        }

                                        if (hasValue) {
                                            responseBuilder.addDataset(datasetBuilder.build())
                                            addedDataset = true
                                        }
                                    }

                                    if (addedDataset) {
                                        val replyIntent = android.content.Intent().apply {
                                            putExtra(android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT, responseBuilder.build())
                                        }
                                        setResult(android.app.Activity.RESULT_OK, replyIntent)
                                    } else {
                                        setResult(android.app.Activity.RESULT_CANCELED)
                                    }
                                } else {
                                    setResult(android.app.Activity.RESULT_CANCELED)
                                }
                                finish()
                            }
                        }
                    }
                }
                
                val requireAuthentication: (() -> Unit) -> Unit = { onSuccess ->
                    if (viewModel.isBiometricsEnabled() && biometricAuthManager.isBiometricAvailable()) {
                        biometricAuthManager.authenticate(
                            title = strings.biometricPromptTitle,
                            subtitle = strings.appTagline,
                            negativeButtonText = strings.cancelButton,
                            onSuccess = { onSuccess() },
                            onError = { }
                        )
                    } else {
                        // Fallback to Master Password if biometrics are unavailable or disabled
                        showMasterPasswordPrompt = onSuccess
                    }
                }

                if (showSplashAnimation) {
                    com.example.ui.components.AderaAnimatedSplashScreen(
                        onAnimationFinished = { showSplashAnimation = false }
                    )
                } else if (!isVaultSetup || !isUnlocked) {
                    // Responsive center-aligned wrapper for all onboarding, setup, and lock/unlock screen states
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        val maxW = if (maxWidth > 600.dp) 500.dp else maxWidth
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = maxW)
                        ) {
                            if (!isVaultSetup) {
                                if (subDestination == AppDestination.SetupVault) {
                                    VaultSetupScreen(
                                        strings = strings,
                                        onVaultCreated = { masterPassword, emergencyCode ->
                                            scope.launch {
                                                viewModel.setupVault(masterPassword, emergencyCode)
                                                subDestination = null
                                            }
                                        }
                                    )
                                } else {
                                    OnboardingScreen(
                                        strings = strings,
                                        currentLanguage = language,
                                        onLanguageSelected = { viewModel.setLanguage(it) },
                                        onGetStarted = { subDestination = AppDestination.SetupVault },
                                        onAlreadyHaveAccount = { subDestination = AppDestination.SetupVault }
                                    )
                                }
                            } else {
                                val biometricsEnabled = viewModel.isBiometricsEnabled()
                                var forcePasswordFallback by remember { mutableStateOf(false) }

                                LaunchedEffect(isUnlocked) {
                                    if (isUnlocked) forcePasswordFallback = false
                                }

                                if (biometricsEnabled && biometricAuthManager.isBiometricAvailable() && !forcePasswordFallback) {
                                    AderaBiometricSplashScreen(
                                        strings = strings,
                                        onAuthenticate = {
                                            biometricAuthManager.authenticate(
                                                title = strings.useBiometrics + " • ADERA Vault",
                                                subtitle = strings.appTagline,
                                                negativeButtonText = strings.cancelButton,
                                                onSuccess = {
                                                    scope.launch { viewModel.unlockWithBiometrics() }
                                                },
                                                onError = { err ->
                                                    viewModel.showToast(err)
                                                    forcePasswordFallback = true
                                                },
                                                onFailed = {
                                                    // just retry or let prompt handle
                                                }
                                            )
                                        },
                                        onFallbackToPassword = {
                                            forcePasswordFallback = true
                                        }
                                    )
                                } else {
                                    AderaLockScreen(
                                        strings = strings,
                                        onUnlockWithPassword = { password -> viewModel.unlockVault(password) },
                                        onUnlockWithBiometric = {
                                            if (biometricAuthManager.isBiometricAvailable()) {
                                                biometricAuthManager.authenticate(
                                                    title = strings.useBiometrics + " • ADERA Vault",
                                                    subtitle = strings.appTagline,
                                                    negativeButtonText = strings.cancelButton,
                                                    onSuccess = {
                                                        scope.launch { viewModel.unlockWithBiometrics() }
                                                    },
                                                    onError = { err ->
                                                        viewModel.showToast(err)
                                                    }
                                                )
                                                true
                                            } else {
                                                viewModel.unlockWithBiometrics()
                                            }
                                        },
                                        onRecoverWithKey = { key, newMasterPassword -> viewModel.recoverVault(key, newMasterPassword) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Main App Workspace when Unlocked - Optimized with adaptive NavigationRail for wide/tablet displays
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWide = maxWidth >= 600.dp

                        Row(modifier = Modifier.fillMaxSize()) {
                            if (isWide && subDestination == null) {
                                AderaNavigationRail(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    onFabClick = { viewModel.openQuickAction() },
                                    strings = strings
                                )
                            }

                            Scaffold(
                                snackbarHost = { SnackbarHost(snackbarHostState) },
                                bottomBar = {
                                    if (!isWide && subDestination == null) {
                                        AderaBottomNav(
                                            currentTab = currentTab,
                                            onTabSelected = { currentTab = it },
                                            onFabClick = { viewModel.openQuickAction() },
                                            strings = strings
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    contentAlignment = androidx.compose.ui.Alignment.TopCenter
                                ) {
                                    // Restrict content width on expanded formats to avoid visual stretching
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .widthIn(max = 640.dp)
                                    ) {
                            if (subDestination == AppDestination.AddEditItem) {
                                AddEditVaultItemScreen(
                                    strings = strings,
                                    existingItem = editingItem,
                                    initialType = preselectedType,
                                    onSaveItem = { item ->
                                        viewModel.saveVaultItem(item)
                                        subDestination = null
                                        editingItem = null
                                    },
                                    onCancel = {
                                        subDestination = null
                                        editingItem = null
                                    }
                                )
                            } else {
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "TabTransition"
                                ) { tab ->
                                    when (tab) {
                                        AderaTab.HOME -> HomeScreen(
                                            strings = strings,
                                            recentItems = allVaultItems.take(5),
                                            passwordHealth = passwordHealth,
                                            searchQuery = searchQuery,
                                            currentTheme = themeMode,
                                            onSearchQueryChange = { 
                                                viewModel.setSearchQuery(it)
                                                if (it.isNotEmpty()) currentTab = AderaTab.VAULT
                                            },
                                            onCategoryClick = { cat ->
                                                viewModel.setSelectedCategory(cat)
                                                currentTab = AderaTab.VAULT
                                            },
                                            onItemClick = { item ->
                                                editingItem = item
                                                subDestination = AppDestination.AddEditItem
                                            },
                                            onCopyPassword = { label, pwd -> viewModel.copyToClipboard(label, pwd) },
                                            onToggleFavorite = { item -> viewModel.toggleFavorite(item) },
                                            onLockVaultClick = { viewModel.lockVault() },
                                            onSeeAllHealthClick = { currentTab = AderaTab.SECURITY },
                                            onSeeAllVaultClick = { currentTab = AderaTab.VAULT },
                                            onQuickAdd = { appName, website ->
                                                editingItem = com.example.data.DecryptedVaultItem(
                                                    id = java.util.UUID.randomUUID().toString(),
                                                    title = appName,
                                                    username = "",
                                                    email = "",
                                                    password = com.example.data.SecurityCrypto.generatePassword(16),
                                                    website = website,
                                                    category = com.example.model.Category.SOCIAL,
                                                    itemType = com.example.model.VaultItemType.LOGIN,
                                                    notes = "Added via Quick Add",
                                                    isFavorite = false,
                                                    tags = emptyList(),
                                                    createdAt = System.currentTimeMillis(),
                                                    updatedAt = System.currentTimeMillis(),
                                                    lastUsedAt = 0L
                                                )
                                                subDestination = AppDestination.AddEditItem
                                            },
                                            onThemeToggle = {
                                                val nextTheme = if (themeMode == "DARK") "LIGHT" else "DARK"
                                                viewModel.setTheme(nextTheme)
                                            },
                                            onRequireAuthentication = { onSuccess -> requireAuthentication(onSuccess) }
                                        )
                                        AderaTab.VAULT -> VaultScreen(
                                            strings = strings,
                                            vaultItems = allVaultItems,
                                            searchQuery = searchQuery,
                                            selectedCategory = selectedCategory,
                                            selectedType = selectedType,
                                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                            onCategorySelect = { viewModel.setSelectedCategory(it) },
                                            onTypeSelect = { viewModel.setSelectedType(it) },
                                            onCopyPassword = { label, pwd -> viewModel.copyToClipboard(label, pwd) },
                                            onCopyUsername = { label, user -> viewModel.copyToClipboard(label, user) },
                                            onToggleFavorite = { item -> viewModel.toggleFavorite(item) },
                                            onEditItem = { item ->
                                                editingItem = item
                                                subDestination = AppDestination.AddEditItem
                                            },
                                            onDeleteItem = { id -> viewModel.deleteVaultItem(id) },
                                            onDuplicateItem = { item -> viewModel.saveVaultItem(item) },
                                            onAddNewClick = {
                                                editingItem = null
                                                preselectedType = VaultItemType.LOGIN
                                                subDestination = AppDestination.AddEditItem
                                            },
                                            onRequireAuthentication = { onSuccess -> requireAuthentication(onSuccess) }
                                        )
                                        AderaTab.SECURITY -> SecurityDashboardScreen(
                                            strings = strings,
                                            passwordHealth = passwordHealth,
                                            allVaultItems = allVaultItems,
                                            onFixItem = { item ->
                                                editingItem = item
                                                subDestination = AppDestination.AddEditItem
                                            }
                                        )
                                        AderaTab.SETTINGS -> SettingsScreen(
                                            strings = strings,
                                            currentLanguage = language,
                                            currentTheme = themeMode,
                                            biometricsEnabled = viewModel.isBiometricsEnabled(),
                                            autoLockMins = viewModel.getAutoLockMins(),
                                            clipboardTimeoutSecs = viewModel.getClipboardTimeoutSecs(),
                                            privacyModeEnabled = viewModel.isPrivacyModeEnabled(),
                                            emergencyCode = viewModel.getEmergencyCode(),
                                            vaultItems = allVaultItems,
                                            onVerifyMasterPassword = { pwd -> viewModel.verifyMasterPassword(pwd) },
                                            onLanguageSelected = { viewModel.setLanguage(it) },
                                            onThemeSelected = { viewModel.setTheme(it) },
                                            onBiometricsToggled = { enable ->
                                                if (enable && biometricAuthManager.isBiometricAvailable()) {
                                                    biometricAuthManager.authenticate(
                                                        title = "Verify Biometrics • ባዮሜትሪክ ያረጋግጡ",
                                                        subtitle = "Authenticate to enable biometric unlock",
                                                        negativeButtonText = strings.cancelButton,
                                                        onSuccess = {
                                                            viewModel.setBiometricsEnabled(true)
                                                        },
                                                        onError = { err ->
                                                            viewModel.showToast(err)
                                                        }
                                                    )
                                                } else {
                                                    viewModel.setBiometricsEnabled(enable)
                                                }
                                            },
                                            onAutoLockMinsChanged = { viewModel.setAutoLockMins(it) },
                                            onClipboardTimeoutChanged = { viewModel.setClipboardTimeoutSecs(it) },
                                            onPrivacyModeToggled = { viewModel.setPrivacyModeEnabled(it) },
                                            onExportBackup = { pwd, callback -> viewModel.exportBackup(pwd, callback) },
                                            onImportBackup = { json, pwd, callback -> viewModel.importBackup(json, pwd, callback) },
                                            onLockVault = { viewModel.lockVault() }
                                        )
                                    }
                                }
                            }

                            // Quick Action Bottom Sheet
                            AderaQuickActionDialog(
                                isOpen = quickActionOpen,
                                onDismiss = { viewModel.closeQuickAction() },
                                onSelectAction = { type ->
                                    editingItem = null
                                    preselectedType = type
                                    subDestination = AppDestination.AddEditItem
                                },
                                onSelectGenerator = {
                                    currentTab = AderaTab.SECURITY
                                },
                                strings = strings
                            )
                            
                            // Master Password Fallback Prompt
                            if (showMasterPasswordPrompt != null) {
                                var passwordInput by remember { mutableStateOf("") }
                                var isError by remember { mutableStateOf(false) }
                                AlertDialog(
                                    onDismissRequest = { showMasterPasswordPrompt = null },
                                    title = { Text(strings.enterMasterPassword) },
                                    text = {
                                        Column {
                                            Text("Please enter your Master Password to continue.")
                                            Spacer(Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = passwordInput,
                                                onValueChange = { passwordInput = it; isError = false },
                                                visualTransformation = PasswordVisualTransformation(),
                                                isError = isError,
                                                singleLine = true,
                                                label = { Text("Master Password") }
                                            )
                                            if (isError) {
                                                Text(strings.incorrectPassword, color = androidx.compose.material3.MaterialTheme.colorScheme.error, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            scope.launch {
                                                val success = viewModel.verifyMasterPassword(passwordInput)
                                                if (success) {
                                                    showMasterPasswordPrompt?.invoke()
                                                    showMasterPasswordPrompt = null
                                                } else {
                                                    isError = true
                                                }
                                            }
                                        }) {
                                            Text("Unlock")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showMasterPasswordPrompt = null }) {
                                            Text(strings.cancelButton)
                                        }
                                    }
                                )
                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.O)
    private data class ParsedStructure(
        var usernameId: android.view.autofill.AutofillId? = null,
        var passwordId: android.view.autofill.AutofillId? = null,
        var webDomain: String? = null
    )

    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.O)
    private fun parseStructure(structure: android.app.assist.AssistStructure): ParsedStructure {
        val parsed = ParsedStructure()
        val nodesToProcess = mutableListOf<android.app.assist.AssistStructure.ViewNode>()
        val numWindowNodes = structure.windowNodeCount

        for (i in 0 until numWindowNodes) {
            nodesToProcess.add(structure.getWindowNodeAt(i).rootViewNode)
        }
        
        while (nodesToProcess.isNotEmpty()) {
            val node = nodesToProcess.removeAt(0)
            
            val hints = node.autofillHints
            var isUsername = false
            var isPassword = false
            
            if (hints != null) {
                if (hints.contains(android.view.View.AUTOFILL_HINT_USERNAME) || hints.contains(android.view.View.AUTOFILL_HINT_EMAIL_ADDRESS)) {
                    isUsername = true
                }
                if (hints.contains(android.view.View.AUTOFILL_HINT_PASSWORD)) {
                    isPassword = true
                }
            } else {
                // Heuristic fallback
                val className = node.className ?: ""
                if (className.contains("EditText", ignoreCase = true)) {
                    val idEntry = node.idEntry ?: ""
                    val hint = node.hint ?: ""
                    val text = node.text?.toString() ?: ""
                    if (idEntry.contains("username", ignoreCase = true) || idEntry.contains("email", ignoreCase = true) || hint.contains("username", ignoreCase = true) || hint.contains("email", ignoreCase = true) || text.contains("email", ignoreCase = true) || text.contains("username", ignoreCase = true)) {
                        isUsername = true
                    }
                    if (idEntry.contains("password", ignoreCase = true) || hint.contains("password", ignoreCase = true) || text.contains("password", ignoreCase = true)) {
                        isPassword = true
                    }
                }
            }
            
            if (isUsername && parsed.usernameId == null && node.autofillId != null) {
                parsed.usernameId = node.autofillId
            }
            if (isPassword && parsed.passwordId == null && node.autofillId != null) {
                parsed.passwordId = node.autofillId
            }
            val domain = node.webDomain
            if (domain != null && parsed.webDomain == null) {
                parsed.webDomain = domain
            }
            
            for (i in 0 until node.childCount) {
                nodesToProcess.add(node.getChildAt(i))
            }
        }

        return parsed
    }
}

// Rebuild triggered by user
// Rebuild triggered by user request 2
// Rebuild triggered for UI enhancements
// Rebuild triggered by user request 2
// Rebuild triggered for UI enhancements
// Triggering theme change rebuild
