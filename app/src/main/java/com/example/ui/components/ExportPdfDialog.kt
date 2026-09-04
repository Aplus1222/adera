package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecryptedVaultItem
import com.example.localization.LocalizedText
import com.example.model.VaultItemType
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.util.PdfExportHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportPdfDialog(
    strings: LocalizedText,
    vaultItems: List<DecryptedVaultItem>,
    onVerifyMasterPassword: suspend (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTypes by remember {
        mutableStateOf(VaultItemType.entries.toSet())
    }

    var includePasswords by remember { mutableStateOf(false) }
    var masterPasswordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isExporting by remember { mutableStateOf(false) }

    fun toggleType(type: VaultItemType) {
        selectedTypes = if (selectedTypes.contains(type)) {
            selectedTypes - type
        } else {
            selectedTypes + type
        }
    }

    fun toggleSelectAll() {
        selectedTypes = if (selectedTypes.size == VaultItemType.entries.size) {
            emptySet()
        } else {
            VaultItemType.entries.toSet()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.PictureAsPdf,
                    contentDescription = null,
                    tint = AderaDanger
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export as PDF • PDF አውርድ",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Warning Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "Warning",
                            tint = AderaDanger,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PDF files are not encrypted by default. Keep your exported file secure.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "የPDF ፋይሎች በነባሪ አይመሰጠሩም። የወጣውን ፋይል በደህና ያስቀምጡ።",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Types Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Items to Export:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { toggleSelectAll() }) {
                        Text(
                            text = if (selectedTypes.size == VaultItemType.entries.size) "Deselect All" else "Select All",
                            fontSize = 12.sp
                        )
                    }
                }

                // Chips for Vault Types
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VaultItemType.entries.forEach { type ->
                        val isSelected = selectedTypes.contains(type)
                        val label = when (type) {
                            VaultItemType.LOGIN -> "🔐 Logins"
                            VaultItemType.EMAIL -> "📧 Email"
                            VaultItemType.WIFI -> "📶 Wi-Fi"
                            VaultItemType.CREDIT_CARD -> "💳 Cards"
                            VaultItemType.SECURE_NOTE -> "📝 Notes"
                            VaultItemType.IDENTITY -> "👤 Identity"
                            VaultItemType.API_KEY -> "🔑 API Keys"
                            VaultItemType.LICENSE -> "📜 Licenses"
                            else -> "📁 Other"
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { toggleType(type) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AderaPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passwords toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Include Passwords & Secrets",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (includePasswords) "Passwords will be printed" else "Passwords will be hidden ([REDACTED])",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = includePasswords,
                        onCheckedChange = { includePasswords = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security confirmation step
                Text(
                    text = "Confirm Master Password • የዋና ይለፍ ቃል ማረጋገጫ:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = masterPasswordInput,
                    onValueChange = {
                        masterPasswordInput = it
                        passwordError = null
                    },
                    label = { Text("Master Password • ዋና የይለፍ ቃል") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    supportingText = {
                        passwordError?.let { Text(it, color = AderaDanger) }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AderaPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pdf_export_master_password_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (masterPasswordInput.isBlank()) {
                        passwordError = "Please enter your master password"
                        return@Button
                    }
                    if (selectedTypes.isEmpty()) {
                        Toast.makeText(context, "Select at least one item type to export", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    if (isExporting) return@Button
                    isExporting = true
                    
                    scope.launch {
                        val isVerified = onVerifyMasterPassword(masterPasswordInput)
                        if (!isVerified) {
                            passwordError = "Incorrect master password • የተሳሳተ የይለፍ ቃል"
                            isExporting = false
                            return@launch
                        }

                        val success = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            PdfExportHelper.generateAndSharePdf(
                                context = context,
                                items = vaultItems,
                                selectedTypes = selectedTypes,
                                includePasswords = includePasswords
                            )
                        }

                        isExporting = false
                        if (success) {
                            Toast.makeText(context, "✓ PDF exported successfully • PDF በተሳካ ሁኔታ ወጥቷል", Toast.LENGTH_LONG).show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Failed to generate PDF. Please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isExporting && selectedTypes.isNotEmpty() && masterPasswordInput.isNotBlank(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AderaPrimary),
                modifier = Modifier.testTag("confirm_export_pdf_button")
            ) {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export PDF • PDF አውርድ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel • ሰርዝ")
            }
        }
    )
}
