package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultPhoto
import com.example.ui.components.PhotoThumbnail
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VaultViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: VaultPhoto,
    viewModel: VaultViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val isThai by viewModel.isThai.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = photo.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.testTag("delete_button")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = ErrorRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Photo Showcase with Zoom & Filter effects
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.Black)
            ) {
                PhotoThumbnail(
                    photo = photo,
                    modifier = Modifier.fillMaxSize()
                )

                // E2EE Badge Overlay
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${photo.encryptionAlgorithm} • Encrypted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            // Primary Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // AI Editor Button
                Button(
                    onClick = { viewModel.startAiEditor(photo) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("edit_ai_button")
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = viewModel.tr("ai_editor"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Marketplace Toggle Button
                Button(
                    onClick = { viewModel.toggleMarketplaceListing(photo) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (photo.isMarketplaceListed) AmberSecondary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (photo.isMarketplaceListed) Color.Black else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("market_toggle_button")
                ) {
                    Icon(
                        imageVector = if (photo.isMarketplaceListed) Icons.Default.Storefront else Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (photo.isMarketplaceListed) {
                            if (isThai) "กำลังวางขาย (฿${photo.priceThb.toInt()})" else "Listed (฿${photo.priceThb.toInt()})"
                        } else {
                            viewModel.tr("list_on_market")
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Social Share Button
                FilledTonalIconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out my secure photography '${photo.title}' from FotoVault! ${photo.tags.joinToString(" ") { "#$it" }}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Photo via"))
                    },
                    modifier = Modifier.testTag("share_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Description & Tags
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.tr("photo_details"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = photo.description.ifBlank { if (isThai) "ไม่มีคำอธิบายเพิ่มเติม" else "No description provided." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = viewModel.tr("tags_label"),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        photo.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 12.sp,
                                    color = CyanPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (photo.detectedFaces.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isThai) "บุคคลและใบหน้าที่ตรวจพบ (AI Face Detection)" else "People & Faces Recognized",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            photo.detectedFaces.forEach { person ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberSecondary.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary)
                                ) {
                                    Text(
                                        text = "👤 $person",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Metadata & Cryptography Specification
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.tr("security_badge"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    MetadataRow(label = viewModel.tr("category_label"), value = photo.category)
                    MetadataRow(label = viewModel.tr("resolution_label"), value = photo.resolution)
                    MetadataRow(label = viewModel.tr("size_label"), value = photo.fileSize)
                    MetadataRow(
                        label = viewModel.tr("created_label"),
                        value = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(photo.createdAt))
                    )
                    MetadataRow(
                        label = if (isThai) "พาร์ติชันความปลอดภัย" else "Security Partition",
                        value = if (photo.isPrivateOwnerOnly) (if (isThai) "🔒 ส่วนตัวเฉพาะเจ้าของ (Zero-Knowledge)" else "🔒 Owner Private Vault") else (if (isThai) "สาธารณะ/ตลาด" else "Public / Market")
                    )
                    MetadataRow(label = "Cloud Backup", value = photo.cloudProvider)
                    MetadataRow(
                        label = if (isThai) "สถานะซิงค์ NAS" else "NAS Sync Status",
                        value = if (photo.nasSynced) (if (isThai) "✓ สำรองลง NAS สำเร็จ" else "✓ Synced with NAS") else (if (isThai) "รอการซิงค์" else "Pending NAS Sync")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Integrity Checksum:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = photo.checksum,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(if (isThai) "ยืนยันการลบรูปภาพ?" else "Delete Photo?") },
                text = { Text(if (isThai) "คุณต้องการลบรูปภาพนี้ออกจากคลังนิรภัยอย่างถาวรใช่หรือไม่?" else "Are you sure you want to permanently delete this photo from your vault?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deletePhoto(photo)
                            showDeleteConfirm = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text(if (isThai) "ลบถาวร" else "Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(if (isThai) "ยกเลิก" else "Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
    }
}
