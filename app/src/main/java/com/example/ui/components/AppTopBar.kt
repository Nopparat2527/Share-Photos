package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun AppTopBar(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val isThai by viewModel.isThai.collectAsState()
    val userPoints by viewModel.repository.userPoints.collectAsState()
    val isSyncing by viewModel.repository.isSyncing.collectAsState()
    val isOffline by viewModel.repository.isOfflineMode.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val notifications by viewModel.repository.notifications.collectAsState()

    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.currentTab.value = com.example.ui.viewmodel.MainTab.VAULT }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "FotoVault Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FotoVault",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Cloud sync status dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color.Gray else if (isSyncing) AmberSecondary else SuccessGreen)
                        )
                    }
                    Text(
                        text = if (isOffline) {
                            if (isThai) "โหมดออฟไลน์" else "Offline Mode"
                        } else if (isSyncing) {
                            if (isThai) "กำลังซิงค์..." else "Syncing..."
                        } else {
                            if (isThai) "เข้ารหัส E2EE ปลอดภัย" else "E2EE Secured"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Action Items: Points, Sync, Notifications, Language
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Points Balance Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .testTag("points_badge")
                        .clickable { viewModel.currentTab.value = com.example.ui.viewmodel.MainTab.MARKETPLACE }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userPoints",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Sync action button
                IconButton(
                    onClick = { viewModel.triggerSync() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("sync_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = if (isSyncing) AmberSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation.value)
                    )
                }

                // Notification Bell with Badge
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = { viewModel.showNotificationDrawer.value = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (notifications.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary)
                        )
                    }
                }

                // Lock Vault button (if PIN enabled)
                if (isPinEnabled) {
                    IconButton(
                        onClick = { viewModel.lockVault() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = AmberSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // User Profile & Social Login button
                val currentUser by viewModel.currentUser.collectAsState()
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .testTag("auth_profile_button")
                        .clickable { viewModel.showAuthDialog.value = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentUser.avatarEmoji,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = currentUser.provider.icon,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Quick Language switch chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .testTag("lang_toggle")
                        .clickable { viewModel.isThai.value = !isThai }
                ) {
                    Text(
                        text = if (isThai) "TH" else "EN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    )
                }
            }
        }
    }
}
