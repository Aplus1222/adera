package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.SecureScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoWalletInputScreen( // Keeping the name the same to avoid refactoring callers, but acts as Input now
    onNavigateBack: () -> Unit,
    onBackupConfirmed: (String) -> Unit
) {
    var words by remember { mutableStateOf(List(12) { "" }) }
    
    // Wrap the entire screen in FLAG_SECURE
    SecureScreen {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Input Seed Phrase", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
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
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Enter your 12-word Secret Recovery Phrase to securely store it in your ADERA vault.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2f
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                // 2-Column Grid for text inputs
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(12) { index ->
                        OutlinedTextField(
                            value = words[index],
                            onValueChange = { newValue ->
                                // Optional: simple split if user pastes a space-separated phrase
                                if (newValue.contains(" ") && newValue.trim().split(" ").size > 1) {
                                    val pastedWords = newValue.trim().split(Regex("\\s+"))
                                    val updatedWords = words.toMutableList()
                                    for (i in pastedWords.indices) {
                                        if (index + i < 12) {
                                            updatedWords[index + i] = pastedWords[i]
                                        }
                                    }
                                    words = updatedWords
                                } else {
                                    val updatedWords = words.toMutableList()
                                    updatedWords[index] = newValue.trim()
                                    words = updatedWords
                                }
                            },
                            label = { Text("${index + 1}.") },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val allFilled = words.all { it.isNotBlank() }
                    
                    Button(
                        onClick = { 
                            if (allFilled) {
                                onBackupConfirmed(words.joinToString(" ").lowercase())
                            }
                        },
                        enabled = allFilled,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                        Text("Save Phrase", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
