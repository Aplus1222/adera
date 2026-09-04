package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalizedText
import com.example.ui.theme.AderaPrimary

enum class AderaTab(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", Icons.Rounded.Home, Icons.Outlined.Home),
    VAULT("vault", Icons.Rounded.Lock, Icons.Outlined.Lock),
    SECURITY("security", Icons.Rounded.Security, Icons.Outlined.Security),
    SETTINGS("settings", Icons.Rounded.Settings, Icons.Outlined.Settings)
}

@Composable
fun AderaBottomNav(
    currentTab: AderaTab,
    onTabSelected: (AderaTab) -> Unit,
    onFabClick: () -> Unit,
    strings: LocalizedText,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.height(72.dp)
            ) {
                val tabsLeft = listOf(AderaTab.HOME, AderaTab.VAULT)
                tabsLeft.forEach { tab ->
                    val selected = currentTab == tab
                    val label = when (tab) {
                        AderaTab.HOME -> strings.navHome
                        AderaTab.VAULT -> strings.navVault
                        else -> ""
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AderaPrimary,
                            selectedTextColor = AderaPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            indicatorColor = AderaPrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.route}")
                    )
                }

                // Center Spacer for Floating Action Button
                Box(modifier = Modifier.weight(1f))

                val tabsRight = listOf(AderaTab.SECURITY, AderaTab.SETTINGS)
                tabsRight.forEach { tab ->
                    val selected = currentTab == tab
                    val label = when (tab) {
                        AderaTab.SECURITY -> strings.navSecurity
                        AderaTab.SETTINGS -> strings.navSettings
                        else -> ""
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AderaPrimary,
                            selectedTextColor = AderaPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            indicatorColor = AderaPrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.route}")
                    )
                }
            }
        }

        // Elevated Center Quick-Add FAB
        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isPressed) 0.85f else 1f,
            animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
            label = "fabScale"
        )
        
        FloatingActionButton(
            onClick = onFabClick,
            interactionSource = interactionSource,
            shape = CircleShape,
            containerColor = AderaPrimary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(60.dp)
                .scale(scale)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                .testTag("center_quick_add_fab")
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Quick Add",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun AderaNavigationRail(
    currentTab: AderaTab,
    onTabSelected: (AderaTab) -> Unit,
    onFabClick: () -> Unit,
    strings: LocalizedText,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "ADERA Logo",
                    tint = AderaPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ADERA",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AderaPrimary
                )
            }
        },
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Render all tabs inside the rail
            AderaTab.values().forEach { tab ->
                val selected = currentTab == tab
                val label = when (tab) {
                    AderaTab.HOME -> strings.navHome
                    AderaTab.VAULT -> strings.navVault
                    AderaTab.SECURITY -> strings.navSecurity
                    AderaTab.SETTINGS -> strings.navSettings
                }
                
                NavigationRailItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = AderaPrimary,
                        selectedTextColor = AderaPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        indicatorColor = AderaPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .testTag("nav_rail_item_${tab.route}")
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom floating action button for quick actions on tablets
            FloatingActionButton(
                onClick = onFabClick,
                shape = CircleShape,
                containerColor = AderaPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                ),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .size(56.dp)
                    .testTag("rail_quick_add_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Quick Add",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

