package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ui.components.AnimatedMeshBackground
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.DecryptedVaultItem
import com.example.ui.components.ExportPdfDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AderaLanguage
import com.example.localization.LocalizedText
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary

@Composable
fun SettingsScreen(
    strings: LocalizedText,
    currentLanguage: AderaLanguage,
    currentTheme: String,
    biometricsEnabled: Boolean,
    autoLockMins: Int,
    clipboardTimeoutSecs: Int,
    privacyModeEnabled: Boolean,
    emergencyCode: String,
    vaultItems: List<DecryptedVaultItem> = emptyList(),
    onVerifyMasterPassword: suspend (String) -> Boolean = { false },
    onLanguageSelected: (AderaLanguage) -> Unit,
    onThemeSelected: (String) -> Unit,
    onBiometricsToggled: (Boolean) -> Unit,
    onAutoLockMinsChanged: (Int) -> Unit,
    onClipboardTimeoutChanged: (Int) -> Unit,
    onPrivacyModeToggled: (Boolean) -> Unit,
    onExportBackup: (String, (String?) -> Unit) -> Unit,
    onImportBackup: (String, String, (Boolean) -> Unit) -> Unit,
    onLockVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showPdfExportDialog by remember { mutableStateOf(false) }

    var backupPasswordInput by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }
    var exportedJsonResult by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
        Text(
            text = strings.navSettings,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Security & Biometrics Section
        SettingsSectionHeader(title = "Security & Biometrics")

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggleRow(
                    icon = Icons.Rounded.Fingerprint,
                    title = strings.enableBiometrics,
                    subtitle = "Unlock vault using fingerprint or face ID",
                    checked = biometricsEnabled,
                    onCheckedChange = onBiometricsToggled,
                    testTag = "setting_toggle_biometrics"
                )

                SettingsToggleRow(
                    icon = Icons.Rounded.Security,
                    title = strings.privacyMode,
                    subtitle = "Hide app content in recent apps preview",
                    checked = privacyModeEnabled,
                    onCheckedChange = onPrivacyModeToggled,
                    testTag = "setting_toggle_privacy"
                )

                // Auto-Lock Timeout Row
                var autoLockMenuExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Rounded.Timer, contentDescription = null, tint = AderaPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Auto-Lock Timeout", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                            Text(
                                text = when (autoLockMins) {
                                    0 -> "Immediately"
                                    1 -> "60 seconds (1 min)"
                                    else -> "$autoLockMins minutes"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box {
                        OutlinedButton(onClick = { autoLockMenuExpanded = true }) {
                            Text(if (autoLockMins == 1) "60s" else if (autoLockMins == 0) "Immediate" else "${autoLockMins}m")
                        }
                        DropdownMenu(
                            expanded = autoLockMenuExpanded,
                            onDismissRequest = { autoLockMenuExpanded = false }
                        ) {
                            listOf(
                                Pair(1, "60 seconds (1 min) • standard"),
                                Pair(0, "Immediately on exit"),
                                Pair(2, "2 minutes"),
                                Pair(5, "5 minutes")
                            ).forEach { (mins, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onAutoLockMinsChanged(mins)
                                        autoLockMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                SettingsClickableRow(
                    icon = Icons.Rounded.VpnKey,
                    title = strings.emergencyRecoveryCode,
                    subtitle = "View your 16-character vault recovery key",
                    onClick = { showEmergencyDialog = true },
                    testTag = "setting_emergency_key"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Appearance & Language Section
        // System Integrations Section
        SettingsSectionHeader(title = "System Integrations")
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsClickableRow(
                    icon = Icons.Rounded.PhoneAndroid,
                    title = "Enable Autofill Service",
                    subtitle = "Fill passwords in other apps and browsers",
                    onClick = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback for some devices
                                context.startActivity(android.content.Intent(android.provider.Settings.ACTION_SETTINGS))
                            }
                        }
                    },
                    testTag = "setting_autofill"
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        SettingsSectionHeader(title = "Appearance & Language")

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Language Item
                var langMenuExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.Language, contentDescription = null, tint = AderaPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = strings.languageSetting, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                            Text(text = currentLanguage.nativeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Box {
                        OutlinedButton(onClick = { langMenuExpanded = true }) {
                            Text(currentLanguage.displayName)
                        }
                        DropdownMenu(
                            expanded = langMenuExpanded,
                            onDismissRequest = { langMenuExpanded = false }
                        ) {
                            AderaLanguage.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.displayName} (${lang.nativeName})") },
                                    onClick = {
                                        onLanguageSelected(lang)
                                        langMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Theme Mode Item with Interactive 3-Way Segmented Control
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.Palette, contentDescription = null, tint = AderaPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Appearance & Theme • ገጽታ",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = when (currentTheme) {
                                    "LIGHT" -> "Light Theme • ብሩህ ገጽታ"
                                    "DARK" -> "Deep Charcoal Dark • ጥቁር ገጽታ (ለማታ የሚመች)"
                                    else -> "System Default • እንደ ስልኩ ቅንብር"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setting_theme_toggle"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val themeOptions = listOf(
                            Triple("LIGHT", "Light", Icons.Rounded.LightMode),
                            Triple("DARK", "Charcoal", Icons.Rounded.DarkMode),
                            Triple("SYSTEM", "System", Icons.Rounded.PhoneAndroid)
                        )

                        themeOptions.forEach { (mode, label, icon) ->
                            val isSelected = currentTheme == mode
                            Surface(
                                onClick = { onThemeSelected(mode) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) AderaPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data & Vault Section
        SettingsSectionHeader(title = "Data & Vault • መረጃ እና ቮልት")

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsClickableRow(
                    icon = Icons.Rounded.PictureAsPdf,
                    title = "Export as PDF • PDF አውርድ",
                    subtitle = "Generate offline printable PDF report of vault entries",
                    onClick = { showPdfExportDialog = true },
                    testTag = "setting_export_pdf"
                )

                SettingsClickableRow(
                    icon = Icons.Rounded.Download,
                    title = "${strings.exportBackup} (Encrypted JSON)",
                    subtitle = "Create AES-256 encrypted restoreable backup file",
                    onClick = { showExportDialog = true },
                    testTag = "setting_export_backup"
                )

                SettingsClickableRow(
                    icon = Icons.Rounded.Upload,
                    title = strings.importBackup,
                    subtitle = "Restore vault items from encrypted backup JSON",
                    onClick = { showImportDialog = true },
                    testTag = "setting_import_backup"
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Lock Vault Button
        Button(
            onClick = onLockVault,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AderaDanger),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("setting_lock_vault_button")
        ) {
            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Lock")
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.lockVault, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // App Info Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_adera_logo),
                contentDescription = "ADERA Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "ADERA / አደራ v1.0.0",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Zero-Knowledge Local Encrypted Password Manager",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        
    }

    // Emergency Key Dialog
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = { Text(strings.emergencyRecoveryCode) },
            text = {
                Column {
                    Text(text = "Your 16-character Emergency Key:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = emergencyCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                            color = AderaPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    clipboardManager.setText(AnnotatedString(emergencyCode))
                    showEmergencyDialog = false
                }) {
                    Text("Copy Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(strings.exportBackup) },
            text = {
                Column {
                    Text("Enter a password to encrypt your backup payload:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = backupPasswordInput,
                        onValueChange = { backupPasswordInput = it },
                        label = { Text("Backup Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    exportedJsonResult?.let { json ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Backup generated! Tap copy to save JSON.")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onExportBackup(backupPasswordInput) { resultJson ->
                        exportedJsonResult = resultJson
                        resultJson?.let { clipboardManager.setText(AnnotatedString(it)) }
                    }
                }) {
                    Text("Generate & Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExportDialog = false
                    exportedJsonResult = null
                }) {
                    Text("Close")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(strings.importBackup) },
            text = {
                Column {
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        label = { Text("Encrypted Backup JSON") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = backupPasswordInput,
                        onValueChange = { backupPasswordInput = it },
                        label = { Text("Backup Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onImportBackup(importJsonInput, backupPasswordInput) { ok ->
                        if (ok) showImportDialog = false
                    }
                }) {
                    Text("Import Backup")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // PDF Export Dialog
    if (showPdfExportDialog) {
        ExportPdfDialog(
            strings = strings,
            vaultItems = vaultItems,
            onVerifyMasterPassword = onVerifyMasterPassword,
            onDismiss = { showPdfExportDialog = false }
        )
    }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AderaPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = AderaPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
