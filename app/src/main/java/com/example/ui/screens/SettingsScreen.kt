package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun SettingsScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val isThai by viewModel.isThai.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isOledBlack by viewModel.isOledBlack.collectAsState()
    val isPowerSaving by viewModel.isPowerSaving.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val userPoints by viewModel.repository.userPoints.collectAsState()

    var showPinChangeDialog by remember { mutableStateOf(false) }
    var newPinText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = viewModel.tr("settings_title"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (isThai) "ปรับแต่งการทำงาน ความปลอดภัย ภาษา และธีมของแอปพลิเคชัน" else "Customize security, display, language, and smart preferences",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Account Profile & Multi-provider Login Card
        item {
            val currentUser by viewModel.currentUser.collectAsState()
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showAuthDialog.value = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(currentUser.provider.brandColorHex),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = currentUser.avatarEmoji, fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = currentUser.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = currentUser.provider.icon, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentUser.isLoggedIn) "${currentUser.provider.title} • Connected" else "Guest Mode",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (currentUser.isLoggedIn) CyanPrimary else AmberSecondary
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.showAuthDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentUser.isLoggedIn) (if (isThai) "จัดการบัญชี" else "Manage") else (if (isThai) "เข้าสู่ระบบ" else "Login"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Language Selector Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.tr("language"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.isThai.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isThai) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isThai) Color.Black else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_lang_th")
                        ) {
                            Text("ภาษาไทย (TH)", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.isThai.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isThai) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!isThai) Color.Black else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_lang_en")
                        ) {
                            Text("English (EN)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Points & Rewards Balance Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.tr("points_balance"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isThai) "ใช้เป็นส่วนลดซื้อรูปภาพ หรือปลดล็อกพรีเซ็ตพิเศษ" else "Redeem for photo discounts or cloud perks",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberSecondary
                    ) {
                        Text(
                            text = "⭐ $userPoints Pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Appearance & Display Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isThai) "การแสดงผลและแบตเตอรี่" else "Display & Battery",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Dark Theme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.tr("dark_mode"), style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.isDarkMode.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // OLED Pure Black
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = viewModel.tr("oled_mode"), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (isThai) "พื้นหลังดำสนิทเพื่อประหยัดพลังงานหน้าจอ OLED" else "Pure pitch black for OLED battery saving",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOledBlack,
                            onCheckedChange = { viewModel.isOledBlack.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Power Saving Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = viewModel.tr("power_save"), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (isThai) "ลดแอนิเมชันและปรับความละเอียดพรีวิวเมื่อแบตเตอรี่ต่ำ" else "Reduces animations and sync frequencies",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPowerSaving,
                            onCheckedChange = { viewModel.isPowerSaving.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary)
                        )
                    }
                }
            }
        }

        // Security & PIN Lock Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isThai) "ความปลอดภัยและรหัสผ่าน" else "Security & Protection",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = viewModel.tr("security_pin"), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (isThai) "ล็อกคลังด้วยรหัส PIN เมื่อเปิดแอป" else "Require 4-digit PIN upon app launch",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = {
                                viewModel.isPinEnabled.value = it
                                if (it) {
                                    showPinChangeDialog = true
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    if (isPinEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showPinChangeDialog = true }) {
                            Text(
                                text = if (isThai) "เปลี่ยนรหัสผ่าน PIN" else "Change PIN Code",
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Privacy Guarantee Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.tr("privacy_policy"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isThai) {
                            "ข้อมูลรูปภาพและคีย์ถอดรหัสทั้งหมดถูกเข้ารหัสลับบนอุปกรณ์ของคุณ (Zero-Knowledge Architecture) แม้กระทั่งผู้ให้บริการคลาวด์ก็ไม่สามารถมองเห็นรูปภาพของคุณได้"
                        } else {
                            "All photos and cryptographic keys are secured using client-side Zero-Knowledge Architecture. Even cloud providers cannot access your unencrypted memories."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showPinChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = { Text(if (isThai) "ตั้งรหัสผ่าน PIN 4 หลัก" else "Set 4-Digit PIN") },
            text = {
                OutlinedTextField(
                    value = newPinText,
                    onValueChange = { if (it.length <= 4) newPinText = it },
                    placeholder = { Text("1234") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinText.length == 4) {
                            viewModel.correctPin.value = newPinText
                            viewModel.isPinEnabled.value = true
                            showPinChangeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black)
                ) {
                    Text(if (isThai) "บันทึก" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinChangeDialog = false }) {
                    Text(if (isThai) "ยกเลิก" else "Cancel")
                }
            }
        )
    }
}
