import re

with open("app/src/main/java/com/example/ui/screens/CryptoWalletBackupScreen.kt", "r") as f:
    content = f.read()

new_code = """package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SecureScreen
import com.example.util.SecureClipboardManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoWalletBackupScreen(
    onNavigateBack: () -> Unit,
    onBackupConfirmed: (String) -> Unit,
    viewModel: CryptoWalletBackupViewModel = viewModel()
) {
    val step by viewModel.step.collectAsState()
    val context = LocalContext.current

    // Memory Safety: Teardown on back
    BackHandler {
        viewModel.wipeMemoryAndReset()
        onNavigateBack()
    }

    // Wrap the entire screen in FLAG_SECURE
    SecureScreen {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (step == BackupStep.VERIFY) "Verify Phrase" else "") },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.wipeMemoryAndReset()
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 20.dp).fillMaxSize()) {
                when (step) {
                    BackupStep.GENERATE -> {
                        val mnemonic by viewModel.mnemonic.collectAsState()
                        var showAll by remember { mutableStateOf(false) }
                        var revealedIndices by remember { mutableStateOf(setOf<Int>()) }

                        if (mnemonic.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Text(
                                text = "Your Secret Recovery Phrase", 
                                style = MaterialTheme.typography.headlineSmall, 
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            
                            Text(
                                text = "Write these 12 words down and store them securely offline. This 12 word phrase is used to recover your wallet private keys.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(20.dp))
                            
                            // Info Banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Note: if you lose your Secret Recovery Phrase, ADERA can't help you recover your wallet and your funds will be lost forever.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2f
                                )
                            }
                            
                            Spacer(Modifier.height(24.dp))
                            
                            // 3-Column Grid
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                itemsIndexed(mnemonic) { index, word ->
                                    val isVisible = showAll || revealedIndices.contains(index)
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                            .clickable { 
                                                revealedIndices = if (isVisible) revealedIndices - index else revealedIndices + index 
                                            }
                                            .padding(horizontal = 8.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.width(20.dp)
                                        )
                                        Text(
                                            text = if (isVisible) word else "•••••••",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                            
                            // Bottom Actions
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { showAll = !showAll }) {
                                    Icon(
                                        imageVector = if (showAll) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (showAll) "Hide Words" else "Show Words", fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = { viewModel.proceedToVerification() },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                                ) {
                                    Text("Next", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    BackupStep.VERIFY -> {
                        val indices by viewModel.verificationIndices.collectAsState()
                        val answers by viewModel.verificationAnswers.collectAsState()
                        val isError by viewModel.verificationError.collectAsState()
                        
                        Text("Verify your backup", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Text("Please enter the corresponding words from your secret phrase to confirm you have backed it up correctly.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(24.dp))
                        
                        indices.forEach { idx ->
                            OutlinedTextField(
                                value = answers[idx] ?: "",
                                onValueChange = { viewModel.setVerificationAnswer(idx, it) },
                                label = { Text("Word #${idx + 1}") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                isError = isError,
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        
                        if (isError) {
                            Text("Verification failed. Please check your spelling and word order.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        
                        Spacer(Modifier.weight(1f))
                        
                        Button(
                            onClick = { 
                                viewModel.submitVerification(onSuccess = { verifiedPhrase ->
                                    viewModel.wipeMemoryAndReset()
                                    onBackupConfirmed(verifiedPhrase)
                                })
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Text("Confirm & Save", fontWeight = FontWeight.Bold)
                        }
                    }
                    BackupStep.SUCCESS -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/CryptoWalletBackupScreen.kt", "w") as f:
    f.write(new_code)
