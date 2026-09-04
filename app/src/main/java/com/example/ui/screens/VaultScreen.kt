package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecryptedVaultItem
import com.example.localization.LocalizedText
import com.example.model.Category
import com.example.model.VaultItemType
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.CategoryAppsBg
import com.example.ui.components.AnimatedMeshBackground

@Composable
fun VaultScreen(
    strings: LocalizedText,
    vaultItems: List<DecryptedVaultItem>,
    searchQuery: String,
    selectedCategory: Category?,
    selectedType: VaultItemType?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (Category?) -> Unit,
    onTypeSelect: (VaultItemType?) -> Unit,
    onCopyPassword: (String, String) -> Unit,
    onCopyUsername: (String, String) -> Unit,
    onToggleFavorite: (DecryptedVaultItem) -> Unit,
    onEditItem: (DecryptedVaultItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDuplicateItem: (DecryptedVaultItem) -> Unit,
    onAddNewClick: () -> Unit,
    onRequireAuthentication: (onSuccess: () -> Unit) -> Unit = { it() },
    modifier: Modifier = Modifier
) {
    var selectedItemForDetail by remember { mutableStateOf<DecryptedVaultItem?>(null) }
    var favoritesOnlyFilter by remember { mutableStateOf(false) }

    // Filter list based on search, category, type, and favorites
    val filteredItems = vaultItems.filter { item ->
        val matchesQuery = searchQuery.isEmpty() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.username.contains(searchQuery, ignoreCase = true) ||
                item.email.contains(searchQuery, ignoreCase = true) ||
                item.website.contains(searchQuery, ignoreCase = true) ||
                item.tags.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesCategory = selectedCategory == null || item.category == selectedCategory
        val matchesType = selectedType == null || item.itemType == selectedType
        val matchesFavorite = !favoritesOnlyFilter || item.isFavorite

        matchesQuery && matchesCategory && matchesType && matchesFavorite
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Screen Header
            Text(
                text = strings.navVault,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(strings.searchVaultPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AderaPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vault_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = favoritesOnlyFilter,
                        onClick = { favoritesOnlyFilter = !favoritesOnlyFilter },
                        label = { Text("Favorites ★") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AderaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = selectedCategory == null && selectedType == null,
                        onClick = {
                            onCategorySelect(null)
                            onTypeSelect(null)
                        },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AderaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(Category.entries.toTypedArray()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            onCategorySelect(if (selectedCategory == cat) null else cat)
                        },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AderaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vault Items List or Empty State
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyVaultState(
                        onAddClick = if (searchQuery.isEmpty()) onAddNewClick else null
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(items = filteredItems, key = { it.id }) { item ->
                        VaultItemRow(
                            item = item,
                            onClick = { selectedItemForDetail = item },
                            onCopyPassword = { onRequireAuthentication { onCopyPassword(item.title, item.password) } },
                            onToggleFavorite = { onToggleFavorite(item) },
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet Dialog
    selectedItemForDetail?.let { item ->
        VaultItemDetailBottomSheet(
            item = item,
            strings = strings,
            onDismiss = { selectedItemForDetail = null },
            onCopyField = { label, value -> onCopyPassword(label, value) },
            onDuplicate = {
                selectedItemForDetail = null
                onDuplicateItem(item.copy(id = java.util.UUID.randomUUID().toString(), title = "${item.title} (Copy)", updatedAt = System.currentTimeMillis()))
            },
            onRequireAuthentication = onRequireAuthentication,
            onEdit = {
                selectedItemForDetail = null
                onEditItem(item)
            },
            onDelete = {
                selectedItemForDetail = null
                onDeleteItem(item.id)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VaultItemDetailBottomSheet(
    item: DecryptedVaultItem,
    strings: LocalizedText,
    onDismiss: () -> Unit,
    onCopyField: (String, String) -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onRequireAuthentication: (onSuccess: () -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var secretVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val typeHeader = when (item.itemType) {
        VaultItemType.LOGIN -> "🔐 LOGIN • መግቢያ"
        VaultItemType.EMAIL -> "📧 EMAIL • ኢሜይል"
        VaultItemType.WIFI -> "📶 WI-FI • ዋይፋይ"
        VaultItemType.CREDIT_CARD -> "💳 CARD • ባንክ ካርድ"
        VaultItemType.SECURE_NOTE -> "📝 SECURE NOTE • ማስታወሻ"
        VaultItemType.IDENTITY -> "👤 IDENTITY • ማንነት / መታወቂያ"
        VaultItemType.API_KEY -> "🔑 API KEY • ኤፒአይ ቁልፍ"
        else -> "🔑 VAULT ENTRY"
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$typeHeader • ${item.category.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AderaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    IconButton(onClick = onDuplicate) {
                        Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = "Duplicate", tint = AderaPrimary)
                    }
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit", tint = AderaPrimary)
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete", tint = AderaDanger)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display specific fields per type
            when (item.itemType) {
                VaultItemType.LOGIN -> {
                    if (item.username.isNotEmpty()) DetailFieldCard("Username • የተጠቃሚ ስም", item.username) { onCopyField("Username", item.username) }
                    if (item.email.isNotEmpty()) DetailFieldCard("Email • ኢሜይል", item.email) { onCopyField("Email", item.email) }
                    SecretFieldCard("Password • የይለፍ ቃል", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Password", item.password) }, onRequireAuthentication)
                    if (item.website.isNotEmpty()) DetailFieldCard("Website • ድረ-ገጽ", item.website) { onCopyField("Website", item.website) }
                }

                VaultItemType.EMAIL -> {
                    if (item.email.isNotEmpty()) DetailFieldCard("Email Address • ኢሜይል አድራሻ", item.email) { onCopyField("Email", item.email) }
                    SecretFieldCard("Password • የይለፍ ቃል", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Password", item.password) }, onRequireAuthentication)
                    if (item.username.isNotEmpty()) DetailFieldCard("Recovery Email • መመለሻ ኢሜይል", item.username) { onCopyField("Recovery Email", item.username) }
                    if (item.website.isNotEmpty()) DetailFieldCard("Provider • ኢሜይል አቅራቢ", item.website) { onCopyField("Provider", item.website) }
                }

                VaultItemType.WIFI -> {
                    DetailFieldCard("Network Name (SSID) • የዋይፋይ ስም", item.title) { onCopyField("SSID", item.title) }
                    SecretFieldCard("Wi-Fi Password • የዋይፋይ ይለፍ ቃል", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Wi-Fi Password", item.password) }, onRequireAuthentication)
                    if (item.username.isNotEmpty()) DetailFieldCard("Security Type • የደህንነት አይነት", item.username) { onCopyField("Security Type", item.username) }
                }

                VaultItemType.CREDIT_CARD -> {
                    if (item.username.isNotEmpty()) DetailFieldCard("Cardholder Name • የካርድ ባለቤት ስም", item.username) { onCopyField("Cardholder Name", item.username) }
                    SecretFieldCard("Card Number • የካርድ ቁጥር", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Card Number", item.password) }, onRequireAuthentication)
                    if (item.email.isNotEmpty()) DetailFieldCard("Expiry Date • የሚያበቃበት ቀን", item.email) { onCopyField("Expiry Date", item.email) }
                    if (item.website.isNotEmpty()) SecretFieldCard("CVV • ሲቪቪ", item.website, secretVisible, { secretVisible = !secretVisible }, { onCopyField("CVV", item.website) }, onRequireAuthentication)
                }

                VaultItemType.SECURE_NOTE -> {
                    // Note is handled below in notes section
                }

                VaultItemType.IDENTITY -> {
                    DetailFieldCard("Name • ሙሉ ስም", item.title) { onCopyField("Name", item.title) }
                    if (item.username.isNotEmpty()) DetailFieldCard("Phone • ስልክ ቁጥር", item.username) { onCopyField("Phone", item.username) }
                    if (item.email.isNotEmpty()) DetailFieldCard("Email • ኢሜይል አድራሻ", item.email) { onCopyField("Email", item.email) }
                    if (item.website.isNotEmpty()) DetailFieldCard("Address • አድራሻ", item.website) { onCopyField("Address", item.website) }
                }

                VaultItemType.API_KEY -> {
                    DetailFieldCard("Key Name • ስም", item.title) { onCopyField("Key Name", item.title) }
                    if (item.website.isNotEmpty()) DetailFieldCard("Service • አገልግሎት", item.website) { onCopyField("Service", item.website) }
                    SecretFieldCard("API Key • ኤፒአይ ቁልፍ", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("API Key", item.password) }, onRequireAuthentication)
                }

                VaultItemType.CRYPTO_WALLET -> {
                    DetailFieldCard("Wallet Name • የዋሌት ስም", item.title) { onCopyField("Wallet Name", item.title) }
                    if (item.email.isNotEmpty()) DetailFieldCard("Network / Chain • ኔትወርክ", item.email) { onCopyField("Network", item.email) }
                    if (item.username.isNotEmpty()) DetailFieldCard("Public Wallet Address • አድራሻ", item.username) { onCopyField("Wallet Address", item.username) }
                    if (item.password.isNotEmpty()) SeedPhraseCard("12/24 Mnemonic Seed Phrase • የሲድ ሀረግ", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Seed Phrase", item.password) }, onRequireAuthentication)
                    if (item.website.isNotEmpty()) SecretFieldCard("Private Key / Keypass • ግላዊ ቁልፍ", item.website, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Private Key", item.website) }, onRequireAuthentication)
                }

                else -> {
                    if (item.username.isNotEmpty()) DetailFieldCard("Username", item.username) { onCopyField("Username", item.username) }
                    SecretFieldCard("Password", item.password, secretVisible, { secretVisible = !secretVisible }, { onCopyField("Password", item.password) }, onRequireAuthentication)
                }
            }

            if (item.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailFieldCard("Notes • ማስታወሻ", item.notes, null)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(strings.confirmDeleteTitle) },
            text = { Text(strings.confirmDeleteMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AderaDanger)
                ) {
                    Text(strings.deleteButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}

@Composable
private fun SecretFieldCard(
    label: String,
    value: String,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    onCopy: (() -> Unit)?,
    onRequireAuthentication: (onSuccess: () -> Unit) -> Unit
) {
    if (value.isEmpty()) return

    Spacer(modifier = Modifier.height(12.dp))
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (visible) value else "••••••••••••",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row {
                IconButton(onClick = { 
                    if (!visible) {
                        onRequireAuthentication { onToggleVisible() }
                    } else {
                        onToggleVisible()
                    }
                }) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = "Toggle Visibility"
                    )
                }
                if (onCopy != null) {
                    IconButton(onClick = { 
                        onRequireAuthentication { onCopy() } 
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Secret",
                            tint = AderaPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailFieldCard(
    label: String,
    value: String,
    onCopy: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (onCopy != null) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy",
                        tint = AderaPrimary
                    )
                }
            }
        }
    }
}
