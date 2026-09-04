import os

new_code = """package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.LocalizedText
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSuccess
import kotlinx.coroutines.launch

@Composable
fun AderaLockScreen(
    strings: LocalizedText,
    onUnlockWithPassword: suspend (String) -> Boolean,
    onUnlockWithBiometric: suspend () -> Boolean,
    onRecoverWithKey: suspend (String, String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessState by remember { mutableStateOf(false) }
    
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryCodeInput by remember { mutableStateOf("") }
    var newMasterPasswordInput by remember { mutableStateOf("") }
    var recoveryError by remember { mutableStateOf<String?>(null) }
    
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    var isUnlocking by remember { mutableStateOf(false) }

    // Minimal elegant entrance animation
    val entranceAlpha = remember { Animatable(0f) }
    val entranceOffset = remember { Animatable(20f) }
    
    LaunchedEffect(Unit) {
        entranceAlpha.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        entranceOffset.animateTo(0f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .graphicsLayer {
                        alpha = entranceAlpha.value
                        translationY = entranceOffset.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                
                // Premium ADERA Logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (isSuccessState) listOf(AderaSuccess, Color(0xFF059669))
                                else listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
                            )
                        )
                        .padding(if (isSuccessState) 0.dp else 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSuccessState) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_adera_logo),
                            contentDescription = "ADERA Logo",
                            tint = AderaPrimary,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "ADERA",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "SECURE VAULT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 48.dp)
                )

                // Sleek input field with Rounded icons
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { 
                        passwordInput = it
                        errorMessage = null 
                    },
                    placeholder = { Text(strings.enterMasterPassword + " • ማስተር ፓስዎርድ", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.6f)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AderaPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = if (passwordInput.isNotEmpty()) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                            contentDescription = "Lock",
                            tint = if (passwordInput.isNotEmpty()) AderaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlock_vault_input")
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = AderaDanger,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Unlock Button
                Button(
                    onClick = {
                        if (isUnlocking) return@Button
                        isUnlocking = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch {
                            val success = onUnlockWithPassword(passwordInput)
                            if (success) {
                                isSuccessState = true
                            } else {
                                errorMessage = strings.incorrectPassword
                            }
                            isUnlocking = false
                        }
                    },
                    enabled = passwordInput.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccessState) AderaSuccess else AderaPrimary
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlock_vault_button")
                ) {
                    Text(
                        text = if (isSuccessState) "UNLOCKED" else strings.unlockVault.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Sub-actions layout (Biometric & Recovery)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showRecoveryDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VpnKey,
                            contentDescription = "Recovery Key",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recovery",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    TextButton(
                        onClick = {
                            if (isUnlocking) return@TextButton
                            isUnlocking = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                val success = onUnlockWithBiometric()
                                if (success) {
                                    isSuccessState = true
                                } else {
                                    errorMessage = strings.incorrectPassword
                                }
                                isUnlocking = false
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Fingerprint,
                            contentDescription = "Biometric Unlock",
                            tint = AderaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Biometrics",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = AderaPrimary
                        )
                    }
                }
            }
        }
    }

    // Emergency Recovery Key Dialog
    if (showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryDialog = false },
            title = { Text(strings.emergencyRecoveryCode) },
            text = {
                Column {
                    Text(
                        text = strings.zeroKnowledgeWarning,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recoveryCodeInput,
                        onValueChange = { recoveryCodeInput = it },
                        label = { Text("Emergency Key (16 chars)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newMasterPasswordInput,
                        onValueChange = { newMasterPasswordInput = it },
                        label = { Text("New Master Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    recoveryError?.let { err ->
                        Text(
                            text = err,
                            color = AderaDanger,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isUnlocking) return@Button
                        isUnlocking = true
                        scope.launch {
                            val ok = onRecoverWithKey(recoveryCodeInput, newMasterPasswordInput)
                            if (ok) {
                                showRecoveryDialog = false
                            } else {
                                recoveryError = "Invalid Recovery Key"
                            }
                            isUnlocking = false
                        }
                    }
                ) {
                    Text("Recover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}
"""

with open("app/src/main/java/com/example/ui/components/AderaLockScreen.kt", "w") as f:
    f.write(new_code)
