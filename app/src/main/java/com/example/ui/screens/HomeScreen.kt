package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.DecryptedVaultItem
import com.example.localization.LocalizedText
import com.example.model.Category
import com.example.model.VaultItemType
import com.example.ui.components.getBrandIcon
import com.example.ui.components.AnimatedMeshBackground
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.model.PasswordHealth
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSecondary
import com.example.ui.theme.AderaSuccess
import com.example.ui.theme.AderaWarning
import com.example.ui.theme.CategoryAppsBg
import com.example.ui.theme.CategoryAppsFg
import com.example.ui.theme.CategoryCryptoBg
import com.example.ui.theme.CategoryCryptoFg
import com.example.ui.theme.CategoryEducationBg
import com.example.ui.theme.CategoryEducationFg
import com.example.ui.theme.CategoryEmailBg
import com.example.ui.theme.CategoryEmailFg
import com.example.ui.theme.CategoryFinanceBg
import com.example.ui.theme.CategoryFinanceFg
import com.example.ui.theme.CategoryOtherBg
import com.example.ui.theme.CategoryOtherFg
import com.example.ui.theme.CategoryShoppingBg
import com.example.ui.theme.CategoryShoppingFg
import com.example.ui.theme.CategorySocialBg
import com.example.ui.theme.CategorySocialFg
import com.example.ui.theme.CategoryWorkBg
import com.example.ui.theme.CategoryWorkFg

@Composable
fun HomeScreen(
    strings: LocalizedText,
    userName: String = "Fitsum",
    recentItems: List<DecryptedVaultItem>,
    passwordHealth: PasswordHealth,
    searchQuery: String,
    currentTheme: String = "SYSTEM",
    onSearchQueryChange: (String) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onItemClick: (DecryptedVaultItem) -> Unit,
    onCopyPassword: (String, String) -> Unit,
    onToggleFavorite: (DecryptedVaultItem) -> Unit,
    onLockVaultClick: () -> Unit,
    onSeeAllHealthClick: () -> Unit,
    onSeeAllVaultClick: () -> Unit,
    onQuickAdd: ((String, String) -> Unit)? = null,
    onThemeToggle: (() -> Unit)? = null,
    onRequireAuthentication: (onSuccess: () -> Unit) -> Unit = { it() },
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Animated Count-Up Score
    val animatedScore by animateIntAsState(
        targetValue = passwordHealth.score,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AnimatedScore"
    )

    // Animated Floating Motion for Shield
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldFloatHome")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatY"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header - Professional Polish Style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_adera_logo),
                        contentDescription = "ADERA Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ADERA • አደራ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = AderaPrimary
                    )
                }

                val greetingText = buildAnnotatedString {
                    append("Welcome Back • ")
                    withStyle(style = SpanStyle(color = AderaPrimary, fontWeight = FontWeight.Bold)) {
                        append("እንኳን ደህና መጡ")
                    }
                }

                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick Theme Toggle Button
                if (onThemeToggle != null) {
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onThemeToggle()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("home_theme_toggle_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (currentTheme == "DARK") Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                contentDescription = "Toggle Theme",
                                tint = AderaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Lock / Action Button Badge with Haptic
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLockVaultClick()
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("lock_vault_top_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Lock Vault",
                            tint = AderaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Profile Avatar Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AderaPrimary, AderaSecondary)
                            )
                        )
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(2).uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar - Rounded 20dp
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search your vault • በቮልትዎ ውስጥ ይፈልጉ") },
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
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Categories Quick Row
        val isAmharic = strings.appName == "አደራ"
        val categoriesList = remember(strings) {
            listOf(
                CategoryItem(Category.SOCIAL, if (isAmharic) "ማህበራዊ" else "Social", CategorySocialBg, CategorySocialFg, Icons.Rounded.Person),
                CategoryItem(Category.WORK, if (isAmharic) "ስራ" else "Work", CategoryWorkBg, CategoryWorkFg, Icons.Rounded.Work),
                CategoryItem(Category.APPS, if (isAmharic) "መተግበሪያዎች" else "Apps", CategoryAppsBg, CategoryAppsFg, Icons.Rounded.Apps),
                CategoryItem(Category.FINANCE, if (isAmharic) "ፋይናንስ" else "Finance", CategoryFinanceBg, CategoryFinanceFg, Icons.Rounded.CreditCard),
                CategoryItem(Category.SHOPPING, if (isAmharic) "ግዢ" else "Shopping", CategoryShoppingBg, CategoryShoppingFg, Icons.Rounded.ShoppingBag),
                CategoryItem(Category.EMAIL, if (isAmharic) "ኢሜል" else "Email", CategoryEmailBg, CategoryEmailFg, Icons.Rounded.Email),
                CategoryItem(Category.EDUCATION, if (isAmharic) "ትምህርት" else "Education", CategoryEducationBg, CategoryEducationFg, Icons.Rounded.School),
                CategoryItem(Category.CRYPTO, if (isAmharic) "ክሪፕቶ" else "Crypto", CategoryCryptoBg, CategoryCryptoFg, Icons.Rounded.Key),
                CategoryItem(Category.OTHER, if (isAmharic) "ሌላ" else "Other", CategoryOtherBg, CategoryOtherFg, Icons.Rounded.Apps)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categoriesList) { categoryItem ->
                PolishCategoryCard(
                    category = categoryItem,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCategoryClick(categoryItem.category)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        if (onQuickAdd != null) {
            QuickAddSocialMedia(onQuickAdd = onQuickAdd)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Highly Premium Security Status Hero Card with Custom Canvas Circular Gauge
        Surface(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSeeAllHealthClick()
            },
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = AderaPrimary.copy(alpha = 0.3f)
                )
                .testTag("home_security_health_card")
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AderaPrimary,
                                AderaSecondary
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Background subtle graphic accent - lock silhouette
                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.07f),
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.CenterEnd)
                        .graphicsLayer {
                            translationX = 30f
                            translationY = 10f
                        }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Side: Glowing Custom Canvas Circular Score Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        // Background soft pulse circle
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        )

                        androidx.compose.foundation.Canvas(modifier = Modifier.size(60.dp)) {
                            // Track
                            drawArc(
                                color = Color.White.copy(alpha = 0.25f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Animated Sweep
                            drawArc(
                                color = Color.White,
                                startAngle = -90f,
                                sweepAngle = (animatedScore / 100f) * 360f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Score Centered Number
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$animatedScore",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    // Right Side: Beautiful text descriptions
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = if (animatedScore >= 80) "ACTIVE PROTECTION • ንቁ ጥበቃ" else "ATTENTION NEEDED • ትኩረት ያስፈልገዋል",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Vault Security Score",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Dynamic localized security assessment tag
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(
                                text = if (animatedScore >= 90) "PRESTIGE LEVEL • ልዩ ደረጃ"
                                       else if (animatedScore >= 80) "SECURE • ደህንነቱ የተጠበቀ"
                                       else "VULNERABLE • ተጋላጭ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Interactive arrow cue
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Details",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recently Used Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Accounts • አካውንቶች",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = strings.showAll,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = AderaPrimary,
                modifier = Modifier
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSeeAllVaultClick()
                    }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recently Used Items List
        androidx.compose.animation.AnimatedVisibility(
            visible = true,
            enter = androidx.compose.animation.fadeIn(animationSpec = tween(600)) + androidx.compose.animation.slideInVertically(initialOffsetY = { 30 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            if (recentItems.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    EmptyVaultState(modifier = Modifier.padding(16.dp))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recentItems.forEach { item ->
                        VaultItemRow(
                            item = item,
                            onClick = { onItemClick(item) },
                            onCopyPassword = { onRequireAuthentication { onCopyPassword(item.title, item.password) } },
                            onToggleFavorite = { onToggleFavorite(item) }
                        )
                    }
                }
            }
        }

         // Clearance for bottom navigation
    }
}
    }

private data class CategoryItem(
    val category: Category,
    val title: String,
    val bgColor: Color,
    val fgColor: Color,
    val icon: ImageVector
)

@Composable
private fun PolishCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CategoryCardScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("category_card_${category.category.name}")
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(category.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.title,
                tint = category.fgColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VaultItemRow(
    item: DecryptedVaultItem,
    onClick: () -> Unit,
    onCopyPassword: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var passwordVisible by remember { mutableStateOf(false) }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "VaultItemScale"
    )
    
    val subtitleText = when (item.itemType) {
        VaultItemType.LOGIN -> if (item.username.isNotEmpty()) item.username else item.email
        VaultItemType.EMAIL -> if (item.email.isNotEmpty()) item.email else item.website
        VaultItemType.WIFI -> if (item.username.isNotEmpty()) "Security: ${item.username}" else "Wi-Fi Network"
        VaultItemType.CREDIT_CARD -> if (item.username.isNotEmpty()) item.username else "Payment Card"
        VaultItemType.SECURE_NOTE -> "Encrypted Private Note"
        VaultItemType.IDENTITY -> if (item.username.isNotEmpty()) "Phone: ${item.username}" else item.email
        VaultItemType.API_KEY -> if (item.website.isNotEmpty()) "Service: ${item.website}" else "API Secret Key"
        VaultItemType.CRYPTO_WALLET -> if (item.email.isNotEmpty()) item.email else if (item.username.isNotEmpty()) item.username else "Crypto Seed Phrase & Key"
        else -> if (item.username.isNotEmpty()) item.username else item.email
    }

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .testTag("vault_item_row_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val brandIcon = getBrandIcon(item.title, item.website)
                if (brandIcon != null) {
                    Image(
                        painter = brandIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitleText.isNotEmpty()) {
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleFavorite()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (item.isFavorite) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent, CircleShape)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (passwordVisible) item.password else "•••• ••••",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (passwordVisible) 1.sp else 4.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onCopyPassword() }.weight(1f)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            passwordVisible = !passwordVisible
                        },
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCopyPassword()
                        },
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Secret",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
