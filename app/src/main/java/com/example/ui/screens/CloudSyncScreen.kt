package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.*
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VaultViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSyncScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val isThai by viewModel.isThai.collectAsState()
    val isAutoBackup by viewModel.repository.isAutoBackupEnabled.collectAsState()
    val selectedProvider by viewModel.repository.selectedCloudProvider.collectAsState()
    val isOffline by viewModel.repository.isOfflineMode.collectAsState()
    val isSyncing by viewModel.repository.isSyncing.collectAsState()
    val lastSyncTime by viewModel.repository.lastSyncTime.collectAsState()
    val nasConfig by viewModel.nasConfig.collectAsState()
    val nasSyncState by viewModel.nasSyncState.collectAsState()

    val providers = listOf(
        Pair("FotoVault Cloud (E2EE)", "🔒 Zero-Knowledge AES-256"),
        Pair("Google Drive", "☁️ Google Cloud Storage"),
        Pair("Dropbox", "📦 Dropbox Secure API"),
        Pair("Nextcloud WebDAV", "🛡️ Self-Hosted Private Cloud")
    )

    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = viewModel.tr("cloud_title"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (isThai) "สำรองข้อมูลอัตโนมัติบนคลาวด์ พร้อมเข้าถึงจากทุกอุปกรณ์อย่างปลอดภัย" else "End-to-end encrypted backup and instant multi-device sync",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Cloud Status Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isOffline) Color.Gray else CyanPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isOffline) viewModel.tr("offline_mode") else viewModel.tr("all_synced"),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isOffline) {
                                        if (isThai) "พักการซิงค์ชั่วคราว (ข้อมูลเก็บในเครื่อง)" else "Paused (Local encrypted storage)"
                                    } else {
                                        "Last Sync: " + SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(lastSyncTime))
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color.Gray else SuccessGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Storage space progress
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isThai) "พื้นที่จัดเก็บคลาวด์ที่ใช้ไป" else "Cloud Storage Usage",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "14.2 MB / 15.0 GB (0.1%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.05f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyanPrimary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Manual Sync Button
                    Button(
                        onClick = { viewModel.triggerSync() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isOffline && !isSyncing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotation.value)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSyncing) (if (isThai) "กำลังซิงค์ข้อมูล..." else "Syncing...") else viewModel.tr("backup_now"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- UNIVERSAL NAS SYNC (ALL BRANDS) ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nas_sync_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = viewModel.tr("nas_sync_title"),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CyanPrimary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = nasConfig.brand.brandName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (nasConfig.isConnected) {
                                        "Connected: ${nasConfig.host}:${nasConfig.port} (${nasConfig.sharedFolder})"
                                    } else {
                                        if (isThai) "ยังไม่ได้เชื่อมต่อ NAS" else "Not connected to NAS"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Connected indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (nasConfig.isConnected) SuccessGreen else Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Storage and E2EE security note
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isThai) "ความจุ NAS ที่ใช้ไป" else "NAS Storage Usage",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "%.2f GB / %.1f GB (%.1f%%)".format(
                                        nasConfig.usedStorageGb,
                                        nasConfig.totalStorageGb,
                                        (nasConfig.usedStorageGb / nasConfig.totalStorageGb) * 100
                                    ),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { ((nasConfig.usedStorageGb / nasConfig.totalStorageGb)).toFloat().coerceIn(0.01f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isThai) {
                                        "พาร์ติชันส่วนตัวของเจ้าของแอพ: เข้ารหัส AES-256 ป้องกันภายนอก"
                                    } else {
                                        "Owner's Private Partition: AES-256 E2EE Zero-Knowledge"
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Progress bar if currently syncing to NAS
                    if (nasSyncState.isSyncing) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CyanPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = nasSyncState.currentFileName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CyanPrimary
                                    )
                                    Text(
                                        text = "${nasSyncState.progressPercent}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { nasSyncState.progressPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CyanPrimary,
                                    trackColor = MaterialTheme.colorScheme.surface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${nasSyncState.transferSpeedMbS} MB/s",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${nasSyncState.filesSynced} / ${nasSyncState.totalFiles} files",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.showNasSetupDialog.value = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_configure_nas")
                        ) {
                            Icon(Icons.Default.SettingsInputComponent, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(viewModel.tr("nas_config_btn"), fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.syncToNasNow() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !nasSyncState.isSyncing,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_sync_to_nas_now")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (nasSyncState.isSyncing) (if (isThai) "กำลังส่ง..." else "Syncing...") else viewModel.tr("nas_sync_btn"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Auto Backup & Offline Toggles
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Daily Auto Backup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.tr("daily_auto_backup"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isThai) "สำรองรูปภาพอัตโนมัติทุก 24 ชั่วโมงเมื่อต่อ Wi-Fi" else "Automatically back up memories daily when on Wi-Fi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAutoBackup,
                            onCheckedChange = { viewModel.repository.isAutoBackupEnabled.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Offline Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.tr("offline_mode"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isThai) "ทำงานออฟไลน์สมบูรณ์ ซิงค์ทันทีเมื่อเปิดเน็ต" else "Offline-only mode; syncs immediately when reconnected",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOffline,
                            onCheckedChange = {
                                viewModel.repository.isOfflineMode.value = it
                                if (!it) {
                                    // Trigger sync immediately upon reconnection!
                                    viewModel.triggerSync()
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary)
                        )
                    }
                }
            }
        }

        // Storage Provider Selection
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.tr("backup_provider"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    providers.forEach { (name, desc) ->
                        val isSelected = selectedProvider == name
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.repository.selectedCloudProvider.value = name }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
