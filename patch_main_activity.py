import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

import_statement = """import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
"""

content = content.replace("import androidx.compose.ui.Modifier\n", import_statement + "import androidx.compose.ui.Modifier\n")

require_auth_old = """                val requireAuthentication: (() -> Unit) -> Unit = { onSuccess ->
                    if (viewModel.isBiometricsEnabled() && biometricAuthManager.isBiometricAvailable()) {
                        biometricAuthManager.authenticate(
                            title = strings.biometricPromptTitle,
                            subtitle = strings.appTagline,
                            negativeButtonText = strings.cancelButton,
                            onSuccess = { onSuccess() },
                            onError = { }
                        )
                    } else {
                        onSuccess()
                    }
                }"""

require_auth_new = """                var showMasterPasswordPrompt by remember { mutableStateOf<(() -> Unit)?>(null) }
                
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
                }"""

content = content.replace(require_auth_old, require_auth_new)

dialog_code = """                            // Quick Action Bottom Sheet
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
                                    title = { Text(strings.verifyMasterPassword) },
                                    text = {
                                        Column {
                                            Text(strings.enterMasterPasswordToContinue)
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
                                                Text(strings.incorrectMasterPassword, color = androidx.compose.material3.MaterialTheme.colorScheme.error, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
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
                            }"""

content = content.replace("""                            // Quick Action Bottom Sheet
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
                            )""", dialog_code)

with open(path, "w") as f:
    f.write(content)
