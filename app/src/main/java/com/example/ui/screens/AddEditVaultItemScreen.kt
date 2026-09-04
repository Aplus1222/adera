package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.DecryptedVaultItem
import com.example.data.SecurityCrypto
import com.example.localization.LocalizedText
import com.example.model.Category
import com.example.model.VaultItemType
import com.example.ui.components.PasswordStrengthMeter
import com.example.ui.theme.AderaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVaultItemScreen(
    strings: LocalizedText,
    existingItem: DecryptedVaultItem? = null,
    initialType: VaultItemType = VaultItemType.LOGIN,
    onSaveItem: (DecryptedVaultItem) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeType = existingItem?.itemType ?: initialType

    // Form fields mapped cleanly per item type
    var title by remember { mutableStateOf(existingItem?.title ?: "") }
    var username by remember { mutableStateOf(existingItem?.username ?: "") }
    var email by remember { mutableStateOf(existingItem?.email ?: "") }
    var password by remember { mutableStateOf(existingItem?.password ?: "") }
    var website by remember { mutableStateOf(existingItem?.website ?: "") }
    var notes by remember { mutableStateOf(existingItem?.notes ?: "") }
    var isFavorite by remember { mutableStateOf(existingItem?.isFavorite ?: false) }

    // Toggle for hidden secret fields (password, wifi pass, card number, CVV, API key)
    var secretVisible by remember { mutableStateOf(false) }
    var showCryptoBackup by remember { mutableStateOf(false) }

    if (showCryptoBackup) {
        CryptoWalletInputScreen(
            onNavigateBack = { showCryptoBackup = false },
            onBackupConfirmed = { generatedSeed -> 
                password = generatedSeed
                showCryptoBackup = false
            }
        )
        return
    }

    // Derive category and title for specific forms if blank
    val derivedCategory = when (activeType) {
        VaultItemType.LOGIN -> existingItem?.category ?: Category.SOCIAL
        VaultItemType.EMAIL -> Category.EMAIL
        VaultItemType.WIFI -> Category.OTHER
        VaultItemType.CREDIT_CARD -> Category.FINANCE
        VaultItemType.SECURE_NOTE -> Category.WORK
        VaultItemType.IDENTITY -> Category.OTHER
        VaultItemType.API_KEY -> Category.WORK
        VaultItemType.CRYPTO_WALLET -> Category.CRYPTO
        else -> Category.OTHER
    }

    val isFormValid = when (activeType) {
        VaultItemType.LOGIN -> title.isNotBlank()
        VaultItemType.EMAIL -> email.isNotBlank() || title.isNotBlank()
        VaultItemType.WIFI -> title.isNotBlank()
        VaultItemType.CREDIT_CARD -> title.isNotBlank() || password.isNotBlank() || username.isNotBlank()
        VaultItemType.SECURE_NOTE -> title.isNotBlank()
        VaultItemType.IDENTITY -> title.isNotBlank()
        VaultItemType.API_KEY -> title.isNotBlank() || password.isNotBlank()
        VaultItemType.CRYPTO_WALLET -> title.isNotBlank() || password.isNotBlank() || username.isNotBlank()
        else -> title.isNotBlank()
    }

    val (screenTitle, headerIcon) = when (activeType) {
        VaultItemType.LOGIN -> Pair("🔐 LOGIN • መግቢያ", Icons.Rounded.Lock)
        VaultItemType.EMAIL -> Pair("📧 EMAIL • ኢሜይል", Icons.Rounded.Email)
        VaultItemType.WIFI -> Pair("📶 WI-FI • ዋይፋይ", Icons.Rounded.Wifi)
        VaultItemType.CREDIT_CARD -> Pair("💳 CARD • ባንክ ካርድ", Icons.Rounded.CreditCard)
        VaultItemType.SECURE_NOTE -> Pair("📝 SECURE NOTE • ማስታወሻ", Icons.AutoMirrored.Filled.NoteAdd)
        VaultItemType.IDENTITY -> Pair("👤 IDENTITY • ማንነት / መታወቂያ", Icons.Rounded.Person)
        VaultItemType.API_KEY -> Pair("🔑 API KEY • ኤፒአይ ቁልፍ", Icons.Rounded.VpnKey)
        VaultItemType.CRYPTO_WALLET -> Pair("🪙 CRYPTO WALLET • ክሪፕቶ ዋሌት", Icons.Rounded.Key)
        else -> Pair("🔑 VAULT ENTRY", Icons.Rounded.Key)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingItem == null) screenTitle else "EDIT • $screenTitle",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Render specific simple form based on activeType
            when (activeType) {
                VaultItemType.LOGIN -> {
                    LoginForm(
                        title = title, onTitleChange = { title = it },
                        username = username, onUsernameChange = { username = it },
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it },
                        website = website, onWebsiteChange = { website = it },
                        notes = notes, onNotesChange = { notes = it },
                        passwordVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible },
                        onAutoGeneratePassword = {
                            password = SecurityCrypto.generatePassword(20)
                            secretVisible = true
                        }
                    )
                }

                VaultItemType.EMAIL -> {
                    EmailForm(
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it },
                        recoveryEmail = username, onRecoveryEmailChange = { username = it },
                        provider = website, onProviderChange = { website = it },
                        notes = notes, onNotesChange = { notes = it },
                        passwordVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible },
                        onAutoGeneratePassword = {
                            password = SecurityCrypto.generatePassword(20)
                            secretVisible = true
                        }
                    )
                }

                VaultItemType.WIFI -> {
                    WifiForm(
                        networkName = title, onNetworkNameChange = { title = it },
                        wifiPassword = password, onWifiPasswordChange = { password = it },
                        securityType = username, onSecurityTypeChange = { username = it },
                        notes = notes, onNotesChange = { notes = it },
                        passwordVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible }
                    )
                }

                VaultItemType.CREDIT_CARD -> {
                    CardForm(
                        cardTitle = title, onCardTitleChange = { title = it },
                        cardholderName = username, onCardholderNameChange = { username = it },
                        cardNumber = password, onCardNumberChange = { password = it },
                        expiryDate = email, onExpiryDateChange = { email = it },
                        cvv = website, onCvvChange = { website = it },
                        notes = notes, onNotesChange = { notes = it },
                        secretVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible }
                    )
                }

                VaultItemType.SECURE_NOTE -> {
                    SecureNoteForm(
                        title = title, onTitleChange = { title = it },
                        note = notes, onNoteChange = { notes = it }
                    )
                }

                VaultItemType.IDENTITY -> {
                    IdentityForm(
                        name = title, onNameChange = { title = it },
                        phone = username, onPhoneChange = { username = it },
                        email = email, onEmailChange = { email = it },
                        address = website, onAddressChange = { website = it },
                        notes = notes, onNotesChange = { notes = it }
                    )
                }

                VaultItemType.API_KEY -> {
                    ApiKeyForm(
                        name = title, onNameChange = { title = it },
                        service = website, onServiceChange = { website = it },
                        apiKey = password, onApiKeyChange = { password = it },
                        notes = notes, onNotesChange = { notes = it },
                        keyVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible }
                    )
                }

                VaultItemType.CRYPTO_WALLET -> {
                    CryptoWalletForm(
                        walletName = title, onWalletNameChange = { title = it },
                        address = username, onAddressChange = { username = it },
                        chain = email, onChainChange = { email = it },
                        seedPhrase = password, onSeedPhraseChange = { password = it },
                        privateKey = website, onPrivateKeyChange = { website = it },
                        notes = notes, onNotesChange = { notes = it },
                        secretVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible },
                        onGenerateSeed = { showCryptoBackup = true }
                    )
                }

                else -> {
                    // Default fallback
                    LoginForm(
                        title = title, onTitleChange = { title = it },
                        username = username, onUsernameChange = { username = it },
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it },
                        website = website, onWebsiteChange = { website = it },
                        notes = notes, onNotesChange = { notes = it },
                        passwordVisible = secretVisible, onToggleVisibility = { secretVisible = !secretVisible },
                        onAutoGeneratePassword = {
                            password = SecurityCrypto.generatePassword(20)
                            secretVisible = true
                        }
                    )
                }
            }

            // Favorite Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add to Favorites • ወደ ተመረጡት አክል ★",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("Cancel • ሰርዝ")
                }

                Button(
                    onClick = {
                        val finalTitle = when (activeType) {
                            VaultItemType.EMAIL -> if (title.isNotBlank()) title.trim() else email.ifBlank { website.ifBlank { "Email Entry" } }
                            VaultItemType.CREDIT_CARD -> if (title.isNotBlank()) title.trim() else username.ifBlank { "Payment Card" }
                            else -> title.trim()
                        }

                        val item = DecryptedVaultItem(
                            id = existingItem?.id ?: java.util.UUID.randomUUID().toString(),
                            title = finalTitle,
                            username = username.trim(),
                            email = email.trim(),
                            password = password,
                            website = website.trim(),
                            category = derivedCategory,
                            itemType = activeType,
                            notes = notes.trim(),
                            tags = emptyList(),
                            isFavorite = isFavorite,
                            createdAt = existingItem?.createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis(),
                            lastUsedAt = existingItem?.lastUsedAt ?: System.currentTimeMillis()
                        )
                        onSaveItem(item)
                    },
                    enabled = isFormValid,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AderaPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("save_item_button")
                ) {
                    Text(
                        text = "Save • አስቀምጥ",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// INDIVIDUAL FORM COMPONENTS FOR EACH TYPE
// ==========================================

@Composable
private fun LoginForm(
    title: String, onTitleChange: (String) -> Unit,
    username: String, onUsernameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    website: String, onWebsiteChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    passwordVisible: Boolean, onToggleVisibility: () -> Unit,
    onAutoGeneratePassword: () -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Name • ስም (e.g. Facebook, Netflix)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_title_input")
    )

    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { Text("Username • የተጠቃሚ ስም") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_username_input")
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email • ኢሜይል አድራሻ") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password • የይለፍ ቃል") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Row {
                IconButton(onClick = onAutoGeneratePassword) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Auto Generate", tint = AderaPrimary)
                }
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_password_input")
    )

    if (password.isNotEmpty()) {
        PasswordStrengthMeter(password = password)
    }

    OutlinedTextField(
        value = website,
        onValueChange = onWebsiteChange,
        label = { Text("Website • ድረ-ገጽ / መተግበሪያ") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EmailForm(
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    recoveryEmail: String, onRecoveryEmailChange: (String) -> Unit,
    provider: String, onProviderChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    passwordVisible: Boolean, onToggleVisibility: () -> Unit,
    onAutoGeneratePassword: () -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email Address • ኢሜይል አድራሻ") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_email_input")
    )

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password • የይለፍ ቃል") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Row {
                IconButton(onClick = onAutoGeneratePassword) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Generate", tint = AderaPrimary)
                }
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_email_password")
    )

    OutlinedTextField(
        value = recoveryEmail,
        onValueChange = onRecoveryEmailChange,
        label = { Text("Recovery Email • መመለሻ ኢሜይል") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = provider,
        onValueChange = onProviderChange,
        label = { Text("Provider • ኢሜይል አቅራቢ (e.g. Gmail, Yahoo, Proton)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WifiForm(
    networkName: String, onNetworkNameChange: (String) -> Unit,
    wifiPassword: String, onWifiPasswordChange: (String) -> Unit,
    securityType: String, onSecurityTypeChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    passwordVisible: Boolean, onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = networkName,
        onValueChange = onNetworkNameChange,
        label = { Text("Network Name (SSID) • የዋይፋይ ስም") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_wifi_ssid")
    )

    OutlinedTextField(
        value = wifiPassword,
        onValueChange = onWifiPasswordChange,
        label = { Text("Wi-Fi Password • የዋይፋይ ይለፍ ቃል") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_wifi_password")
    )

    OutlinedTextField(
        value = securityType,
        onValueChange = onSecurityTypeChange,
        label = { Text("Security Type • የደህንነት አይነት (e.g. WPA2/WPA3 Personal)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CardForm(
    cardTitle: String, onCardTitleChange: (String) -> Unit,
    cardholderName: String, onCardholderNameChange: (String) -> Unit,
    cardNumber: String, onCardNumberChange: (String) -> Unit,
    expiryDate: String, onExpiryDateChange: (String) -> Unit,
    cvv: String, onCvvChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    secretVisible: Boolean, onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = cardTitle,
        onValueChange = onCardTitleChange,
        label = { Text("Card Name / Bank • የካርዱ ስም (e.g. CBE Gold Visa)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_card_title")
    )

    OutlinedTextField(
        value = cardholderName,
        onValueChange = onCardholderNameChange,
        label = { Text("Cardholder Name • የካርድ ባለቤት ስም") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = cardNumber,
        onValueChange = onCardNumberChange,
        label = { Text("Card Number • የካርድ ቁጥር") },
        singleLine = true,
        visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = if (secretVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_card_number")
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = expiryDate,
            onValueChange = onExpiryDateChange,
            label = { Text("Expiry (MM/YY)") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = cvv,
            onValueChange = onCvvChange,
            label = { Text("CVV") },
            singleLine = true,
            visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
            modifier = Modifier.weight(1f)
        )
    }

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SecureNoteForm(
    title: String, onTitleChange: (String) -> Unit,
    note: String, onNoteChange: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Title • ርዕስ (e.g. Master Key Backup)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_note_title")
    )

    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("Note • ሚስጥራዊ ማስታወሻ") },
        minLines = 6,
        maxLines = 10,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_note_body")
    )
}

@Composable
private fun IdentityForm(
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name • ሙሉ ስም") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_identity_name")
    )

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Phone • ስልክ ቁጥር") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email • ኢሜይል አድራሻ") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("Address • አድራሻ") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ApiKeyForm(
    name: String, onNameChange: (String) -> Unit,
    service: String, onServiceChange: (String) -> Unit,
    apiKey: String, onApiKeyChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    keyVisible: Boolean, onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name • ስም (e.g. OpenAI Production Key)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_api_key_name")
    )

    OutlinedTextField(
        value = service,
        onValueChange = onServiceChange,
        label = { Text("Service • አገልግሎት (e.g. OpenAI, Stripe, AWS)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = apiKey,
        onValueChange = onApiKeyChange,
        label = { Text("API Key • ኤፒአይ ቁልፍ") },
        singleLine = true,
        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = if (keyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth().testTag("add_item_api_key_value")
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes • ማስታወሻዎች") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CryptoWalletForm(
    walletName: String, onWalletNameChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    chain: String, onChainChange: (String) -> Unit,
    seedPhrase: String, onSeedPhraseChange: (String) -> Unit,
    privateKey: String, onPrivateKeyChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    secretVisible: Boolean, onToggleVisibility: () -> Unit,
    onGenerateSeed: () -> Unit
) {
    OutlinedTextField(
        value = walletName,
        onValueChange = onWalletNameChange,
        label = { Text("Wallet Name • የዋሌት ስም (e.g. Binance Wallet, Telegram Wallet, MetaMask)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_item_crypto_name")
    )

    OutlinedTextField(
        value = chain,
        onValueChange = onChainChange,
        label = { Text("Network / Chain • ኔትወርክ (e.g. BNB Smart Chain, Ethereum, TON, Solana)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("Public Wallet Address • የዋሌት አድራሻ (0x...)") },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = seedPhrase,
        onValueChange = onSeedPhraseChange,
        label = { Text("Secret Recovery Phrase • 12/24 Word Seed") },
        visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = if (secretVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = "Toggle")
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = onGenerateSeed,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text("Input 12-Word Seed Phrase", fontWeight = FontWeight.Bold)
    }
    
    OutlinedTextField(
        value = privateKey,
        onValueChange = onPrivateKeyChange,
        label = { Text("Private Key • ፕራይቬት ኪ (Optional)") },
        singleLine = true,
        visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes & Backup Instructions • ተጨማሪ ማስታወሻ") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
        modifier = Modifier.fillMaxWidth()
    )
}
