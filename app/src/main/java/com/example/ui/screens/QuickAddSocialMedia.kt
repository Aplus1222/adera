package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AderaPrimary

data class SocialApp(val name: String, val website: String, val initials: String, val color: Color)

val popularSocialApps = listOf(
    SocialApp("Telegram", "telegram.org", "TG", Color(0xFF24A1DE)),
    SocialApp("Facebook", "facebook.com", "FB", Color(0xFF1877F2)),
    SocialApp("TikTok", "tiktok.com", "TT", Color(0xFF000000)),
    SocialApp("Instagram", "instagram.com", "IG", Color(0xFFE1306C)),
    SocialApp("X (Twitter)", "x.com", "X", Color(0xFF000000)),
    SocialApp("LinkedIn", "linkedin.com", "IN", Color(0xFF0A66C2)),
    SocialApp("YouTube", "youtube.com", "YT", Color(0xFFFF0000))
)

@Composable
fun QuickAddSocialMedia(
    onQuickAdd: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Add Apps • ፈጣን መተግበሪያዎች ማከያ",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(popularSocialApps) { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onQuickAdd(app.name, app.website)
                        }
                ) {
                    val brandIcon = com.example.ui.components.getBrandIcon(app.name, app.website)
                    if (brandIcon != null) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(app.color),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = brandIcon,
                                contentDescription = app.name,
                                modifier = Modifier.size(32.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(app.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.initials,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
