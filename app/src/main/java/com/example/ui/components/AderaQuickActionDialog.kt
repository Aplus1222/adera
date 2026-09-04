package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.LocalizedText
import com.example.model.VaultItemType
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSuccess
import com.example.ui.theme.CategoryAppsBg
import com.example.ui.theme.CategoryFinanceBg
import com.example.ui.theme.CategoryWorkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AderaQuickActionDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSelectAction: (VaultItemType) -> Unit,
    onSelectGenerator: () -> Unit,
    strings: LocalizedText
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Add to Vault • ወደ ヴォልት ጨምር",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickActionItemRow(
                icon = Icons.Rounded.Lock,
                iconBg = AderaPrimary,
                title = "🔐 ${strings.typeLogin} • መግቢያ",
                subtitle = "Store website or app login credentials",
                onClick = {
                    onSelectAction(VaultItemType.LOGIN)
                    onDismiss()
                },
                testTag = "quick_action_add_login"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.Email,
                iconBg = Color(0xFF3B82F6),
                title = "📧 ${strings.catEmail} • ኢሜይል",
                subtitle = "Email accounts, passwords & recovery details",
                onClick = {
                    onSelectAction(VaultItemType.EMAIL)
                    onDismiss()
                },
                testTag = "quick_action_add_email"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.Wifi,
                iconBg = Color(0xFF10B981),
                title = "📶 ${strings.typeWifi} • ዋይፋይ",
                subtitle = "Wi-Fi network names & passwords",
                onClick = {
                    onSelectAction(VaultItemType.WIFI)
                    onDismiss()
                },
                testTag = "quick_action_add_wifi"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.CreditCard,
                iconBg = Color(0xFFF59E0B),
                title = "💳 ${strings.typeCard} • ባንክ ካርድ",
                subtitle = "Payment cards, numbers & CVV details",
                onClick = {
                    onSelectAction(VaultItemType.CREDIT_CARD)
                    onDismiss()
                },
                testTag = "quick_action_add_card"
            )

            QuickActionItemRow(
                icon = Icons.AutoMirrored.Filled.NoteAdd,
                iconBg = Color(0xFF8B5CF6),
                title = "📝 ${strings.typeNote} • ሚስጥራዊ ማስታወሻ",
                subtitle = "Encrypted private notes & secret memos",
                onClick = {
                    onSelectAction(VaultItemType.SECURE_NOTE)
                    onDismiss()
                },
                testTag = "quick_action_add_note"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.Person,
                iconBg = Color(0xFFEC4899),
                title = "👤 ${strings.typeIdentity} • ማንነት / መታወቂያ",
                subtitle = "Name, phone, address & identity info",
                onClick = {
                    onSelectAction(VaultItemType.IDENTITY)
                    onDismiss()
                },
                testTag = "quick_action_add_identity"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.VpnKey,
                iconBg = Color(0xFF6366F1),
                title = "🔑 ${strings.typeApiKey} • ኤፒአይ ቁልፍ",
                subtitle = "Developer API keys & secret tokens",
                onClick = {
                    onSelectAction(VaultItemType.API_KEY)
                    onDismiss()
                },
                testTag = "quick_action_add_api_key"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.Key,
                iconBg = Color(0xFFD97706),
                title = "🪙 ${strings.typeCryptoWallet} • ክሪፕቶ ዋሌት",
                subtitle = "Binance, Telegram (TON), MetaMask seed phrases & keys",
                onClick = {
                    onSelectAction(VaultItemType.CRYPTO_WALLET)
                    onDismiss()
                },
                testTag = "quick_action_add_crypto_wallet"
            )

            QuickActionItemRow(
                icon = Icons.Rounded.AutoAwesome,
                iconBg = Color(0xFF0EA5E9),
                title = "⚡ ${strings.generatorTitle} • አመንጪ",
                subtitle = "Generate strong cryptographically random passcode",
                onClick = {
                    onSelectGenerator()
                    onDismiss()
                },
                testTag = "quick_action_generator"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionItemRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
