package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.ShieldMoon
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecryptedVaultItem
import com.example.data.SecurityCrypto
import com.example.localization.LocalizedText
import com.example.model.PasswordHealth
import com.example.ui.components.AnimatedMeshBackground
import com.example.ui.components.SecurityScoreGauge
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSuccess
import com.example.ui.theme.AderaWarning

enum class SecurityIssueType { WEAK, REUSED, COMPROMISED, OLD }

@Composable
fun SecurityDashboardScreen(
    strings: LocalizedText,
    passwordHealth: PasswordHealth,
    allVaultItems: List<DecryptedVaultItem>,
    onFixItem: (DecryptedVaultItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIssueType by remember { mutableStateOf<SecurityIssueType?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
        Text(
            text = strings.securityDashboardTitle,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Security Gauge Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    SecurityScoreGauge(
                        score = passwordHealth.score,
                        safeCount = passwordHealth.safeCount,
                        weakCount = passwordHealth.weakCount,
                        reusedCount = passwordHealth.reusedCount,
                        compromisedCount = passwordHealth.compromisedCount,
                        modifier = Modifier.size(190.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${passwordHealth.score}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 42.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        passwordHealth.score >= 90 -> strings.healthScoreExcellent
                        passwordHealth.score >= 70 -> strings.healthScoreGood
                        else -> strings.healthScoreNeedsAttention
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AderaPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Breakdown Categories
        HealthCategoryRow(
            title = strings.safePasswords,
            count = passwordHealth.safeCount,
            color = AderaSuccess,
            icon = Icons.Rounded.VerifiedUser,
            onClick = {}
        )

        HealthCategoryRow(
            title = strings.weakPasswords,
            count = passwordHealth.weakCount,
            color = AderaWarning,
            icon = Icons.Rounded.Warning,
            onClick = { selectedIssueType = SecurityIssueType.WEAK }
        )

        HealthCategoryRow(
            title = strings.reusedPasswords,
            count = passwordHealth.reusedCount,
            color = AderaPrimary,
            icon = Icons.Rounded.Repeat,
            onClick = { selectedIssueType = SecurityIssueType.REUSED }
        )

        HealthCategoryRow(
            title = strings.compromisedPasswords,
            count = passwordHealth.compromisedCount,
            color = AderaDanger,
            icon = Icons.Rounded.Error,
            onClick = { selectedIssueType = SecurityIssueType.COMPROMISED }
        )

        HealthCategoryRow(
            title = strings.oldPasswords,
            count = passwordHealth.oldCount,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = Icons.Rounded.Timer,
            onClick = { selectedIssueType = SecurityIssueType.OLD }
        )

        
    }

    // Detail Bottom Sheet for Security Issue Fixes
    selectedIssueType?.let { issueType ->
        val passwordCounts = allVaultItems.groupingBy { it.password }.eachCount()
        val ninetyDaysAgo = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)

        val affectedItems = allVaultItems.filter { item ->
            when (issueType) {
                SecurityIssueType.WEAK -> SecurityCrypto.calculateStrengthScore(item.password) < 60
                SecurityIssueType.REUSED -> (passwordCounts[item.password] ?: 1) > 1
                SecurityIssueType.COMPROMISED -> item.password.lowercase().contains("123456") || item.password.length < 6
                SecurityIssueType.OLD -> item.updatedAt < ninetyDaysAgo
            }
        }

        SecurityIssueListBottomSheet(
            issueType = issueType,
            items = affectedItems,
            strings = strings,
            onDismiss = { selectedIssueType = null },
            onFixItem = { item ->
                selectedIssueType = null
                onFixItem(item)
            }
        )
    }
    }
}

@Composable
private fun HealthCategoryRow(
    title: String,
    count: Int,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$count entries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (count > 0) {
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityIssueListBottomSheet(
    issueType: SecurityIssueType,
    items: List<DecryptedVaultItem>,
    strings: LocalizedText,
    onDismiss: () -> Unit,
    onFixItem: (DecryptedVaultItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Security Fixes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Text(
                    text = "No security issues found in this category!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AderaSuccess,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(items) { item ->
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.username,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { onFixItem(item) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AderaPrimary)
                                ) {
                                    Icon(imageVector = Icons.Rounded.AutoAwesome, contentDescription = "Fix")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Fix")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
