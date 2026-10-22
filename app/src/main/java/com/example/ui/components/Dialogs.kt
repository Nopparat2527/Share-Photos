package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VaultViewModel

// --- ADD PHOTO DIALOG ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoDialog(
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Travel") }
    var tagsInput by remember { mutableStateOf("sunset, nature, vacation") }
    var selectedUri by remember { mutableStateOf<String>("") }
    var isMarketplace by remember { mutableStateOf(false) }
    var isOwnerPrivate by remember { mutableStateOf(true) }
    var priceText by remember { mutableStateOf("150") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri.toString()
            if (title.isBlank()) {
                title = if (isThai) "รูปภาพที่นำเข้าใหม่" else "Imported Memory"
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isThai) "เพิ่มรูปภาพลงคลังนิรภัย" else "Add Photo to Secure Vault",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo picker preview container
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (selectedUri.isNotBlank()) {
                            Text(
                                text = if (isThai) "✓ เลือกรูปภาพแล้ว (แตะเพื่อเปลี่ยน)" else "✓ Photo Selected (Tap to change)",
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isThai) "แตะเพื่อเลือกรูปภาพ (Photo Picker)" else "Tap to choose photo (Photo Picker)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isThai) "ชื่อรูปภาพ" else "Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_photo_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isThai) "คำอธิบาย" else "Description") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text(if (isThai) "แท็ก (คั่นด้วยเครื่องหมายจุลภาค)" else "Tags (comma separated)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Owner Private Partition toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isThai) "เก็บไว้ในส่วนตัวของเจ้าของแอพ" else "Store in Owner's Private Partition",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = if (isThai) "เข้ารหัสเฉพาะบุคคล ป้องกันการเปิดเผยสาธารณะและซิงค์ลง NAS ส่วนตัว" else "Zero-Knowledge private storage, shielded from marketplace & auto-replicated to NAS",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOwnerPrivate,
                            onCheckedChange = { 
                                isOwnerPrivate = it
                                if (it) isMarketplace = false
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Marketplace listing toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isThai) "วางขายในตลาด (Marketplace)" else "List on Marketplace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isThai) "เปิดให้ผู้อื่นซื้อสิทธิ์ภาพถ่าย" else "Allow others to buy usage license",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isMarketplace,
                        onCheckedChange = { 
                            isMarketplace = it 
                            if (it) isOwnerPrivate = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary)
                    )
                }

                if (isMarketplace) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text(if (isThai) "ราคา (บาท THB)" else "Price (THB)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val tags = tagsInput.split(",").map { it.trim().removePrefix("#") }.filter { it.isNotBlank() }
                        val price = priceText.toDoubleOrNull() ?: 150.0
                        viewModel.addPhoto(
                            title = title,
                            description = description,
                            category = if (isOwnerPrivate) PhotoCategory.OWNER_PRIVATE.labelEn else selectedCategory,
                            tags = tags,
                            localUri = selectedUri,
                            priceThb = price,
                            isMarketplace = isMarketplace,
                            isPrivateOwnerOnly = isOwnerPrivate
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_add_photo")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isThai) "เข้ารหัสและบันทึกลงคลัง" else "Encrypt & Save to Vault",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- BUY PHOTO & AUTOMATIC SLIP VERIFICATION DIALOG ---
@Composable
fun BuyPhotoDialog(
    photo: VaultPhoto,
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    val userPoints by viewModel.repository.userPoints.collectAsState()
    var selectedMethod by remember { mutableStateOf(PaymentMethod.PROMPTPAY) }
    var usePoints by remember { mutableStateOf(false) }
    var isVerifyingSlip by remember { mutableStateOf(false) }

    val pointsToUse = if (usePoints) minOf(userPoints, (photo.priceThb * 10).toInt()) else 0
    val discountThb = pointsToUse / 10.0
    val netAmountThb = (photo.priceThb - discountThb).coerceAtLeast(0.0)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.tr("buy_now"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Item Summary Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            PhotoThumbnail(photo = photo, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = photo.title, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(text = "Commercial License", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "฿${photo.priceThb.toInt()}",
                                fontWeight = FontWeight.Black,
                                color = AmberSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Points Discount Toggle
                if (userPoints > 0) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = viewModel.tr("use_points"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (isThai) "มี $userPoints แต้ม (ใช้ $pointsToUse แต้ม ลด ฿${discountThb.toInt()})" else "Have $userPoints pts (Use $pointsToUse pts = -฿${discountThb.toInt()})",
                                    fontSize = 11.sp,
                                    color = AmberSecondary
                                )
                            }
                            Switch(
                                checked = usePoints,
                                onCheckedChange = { usePoints = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Payment Method Selector
                Text(
                    text = viewModel.tr("payment_method"),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = method.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isThai) method.labelTh else method.labelEn,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated PromptPay QR Box with instant slip verification instructions
                if (selectedMethod == PaymentMethod.PROMPTPAY) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PromptPay Official QR",
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Simulated QR graphic
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR", tint = Color.Black, modifier = Modifier.size(94.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isThai) "ระบบจะสแกนและตรวจสอบสลิปโอนเงินอัตโนมัติ" else "Automated slip scanning & bank signature check",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Total and Pay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isThai) "ยอดชำระสุทธิ:" else "Net Amount:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "฿${netAmountThb.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = AmberSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isVerifyingSlip = true
                        // Complete purchase and show verification result
                        viewModel.buyPhoto(photo, selectedMethod, pointsToUse)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_buy_button")
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMethod == PaymentMethod.PROMPTPAY) viewModel.tr("scan_slip") else viewModel.tr("confirm_purchase"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- SLIP VERIFICATION DIALOG ---
@Composable
fun SlipVerificationDialog(
    slip: SlipVerificationResult,
    order: PurchaseOrder,
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.tr("slip_verified"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SuccessGreen
                )

                Text(
                    text = viewModel.tr("purchase_success"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Slip Details Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Order ID:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = order.orderId, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Amount:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "฿${order.netAmountThb.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Gateway / Bank:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = slip.bankName, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Txn Ref:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = slip.transactionId, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyanPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Points Earned:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "+${order.pointsEarned} Pts ⭐", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_slip_button")
                ) {
                    Text(if (isThai) "ตกลง" else "Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- LOCK SCREEN PIN DIALOG ---
@Composable
fun LockScreenDialog(viewModel: VaultViewModel) {
    val isThai by viewModel.isThai.collectAsState()
    val pinInput by viewModel.pinInput.collectAsState()
    val errorMessage by viewModel.pinErrorMessage.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = viewModel.tr("enter_pin"),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = if (isThai) "รหัสผ่านเริ่มต้นสำหรับทดสอบคือ 1234" else "Default test PIN is 1234",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PIN Dots Indicator
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 4) {
                    val filled = i < pinInput.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (filled) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, CyanPrimary, CircleShape)
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage!!, color = ErrorRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Keypad (1-9, C, 0, OK)
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "OK")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                keys.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { key ->
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable {
                                        when (key) {
                                            "C" -> {
                                                if (pinInput.isNotEmpty()) {
                                                    viewModel.pinInput.value = pinInput.dropLast(1)
                                                }
                                            }
                                            "OK" -> viewModel.unlockVault()
                                            else -> {
                                                if (pinInput.length < 4) {
                                                    viewModel.pinInput.value += key
                                                    if (viewModel.pinInput.value.length == 4) {
                                                        viewModel.unlockVault()
                                                    }
                                                }
                                            }
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (key == "OK") CyanPrimary else if (key == "C") ErrorRed else MaterialTheme.colorScheme.onSurface
                                        )
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

// --- NOTIFICATION DRAWER / DIALOG ---
@Composable
fun NotificationDrawer(
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    val notifications by viewModel.repository.notifications.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isThai) "ศูนย์แจ้งเตือนอัจฉริยะ" else "Notification Center",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (isThai) "ไม่มีการแจ้งเตือน" else "No notifications yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications, key = { it.id }) { notif ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isThai) notif.titleTh else notif.titleEn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = when (notif.type) {
                                                NotificationType.SALE_ALERT -> AmberSecondary
                                                NotificationType.POINTS_EARNED -> SuccessGreen
                                                NotificationType.CLOUD_BACKUP -> CyanPrimary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        Text(
                                            text = notif.timeAgo,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isThai) notif.messageTh else notif.messageEn,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
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

// --- FACE SEARCH & RECOGNITION DIALOG ---
@Composable
fun FaceSearchDialog(
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    val isScanning by viewModel.isScanningFace.collectAsState()
    val scanResult by viewModel.faceScanResult.collectAsState()
    val peopleList by viewModel.detectedPeopleList.collectAsState()
    var selectedPerson by remember { mutableStateOf("Emma") }
    var uploadedFaceUri by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedFaceUri = uri.toString()
            viewModel.performFaceScan(
                sourceDesc = if (isThai) "รูปใบหน้าที่อัปโหลด" else "Uploaded Face",
                personNameTarget = "Emma"
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CyanPrimary,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Face, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = viewModel.tr("face_search_title"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isThai) "สแกนค้นหารูปภาพด้วยการตรวจจับใบหน้า AI" else "AI facial embedding search & recognition",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Upload Face vs Scan with Camera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upload_face_button")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.tr("upload_face"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.performFaceScan(
                                sourceDesc = if (isThai) "สแกนสดจากกล้อง" else "Live Camera Scan",
                                personNameTarget = selectedPerson
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_camera_face_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.tr("scan_face_cam"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Person selector chips
                Text(
                    text = viewModel.tr("filter_by_face"),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    peopleList.forEach { person ->
                        val isSelected = selectedPerson == person
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPerson = person
                                viewModel.performFaceScan(person, person)
                            },
                            label = {
                                Text(
                                    text = person,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Face Scanner HUD Box
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isScanning) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = CyanPrimary, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = viewModel.tr("face_scanning"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CyanPrimary
                                )
                                Text(
                                    text = "Extracting 68 Facial Landmarks",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else if (scanResult != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Target: ${scanResult!!.detectedPeople.firstOrNull() ?: selectedPerson}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "68 Landmarks Detected • Vector Verified",
                                    fontSize = 11.sp,
                                    color = SuccessGreen
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${scanResult!!.matchingPhotos.size} " + (if (isThai) "ภาพที่ตรงกันในคลัง" else "matching photos found"),
                                    fontSize = 12.sp,
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(Icons.Default.CameraEnhance, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isThai) "เลือกอัปโหลดรูปภาพใบหน้า หรือแตะบุคคลด้านบนเพื่อสแกน" else "Upload a face photo or tap a person above to scan",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Matching photos list preview
                if (scanResult != null && scanResult!!.matchingPhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = viewModel.tr("face_matches_found"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    scanResult!!.matchingPhotos.forEach { (photo, conf) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.openPhotoDetail(photo)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    PhotoThumbnail(photo = photo, modifier = Modifier.fillMaxSize())
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = photo.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                    Text(
                                        text = photo.detectedFaces.joinToString(", "),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SuccessGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "%.1f%%".format(conf * 100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.activeFaceFilter.value = selectedPerson
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("apply_face_filter_button")
                    ) {
                        Text(
                            text = if (isThai) "กรองแสดงเฉพาะภาพบุคคลนี้ในคลัง" else "Filter Only This Person in Vault",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- USER AUTHENTICATION & SOCIAL LOGIN DIALOG ---
@Composable
fun AuthDialog(
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var isSignUp by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf(if (currentUser.isLoggedIn) currentUser.email else "") }
    var nameInput by remember { mutableStateOf(if (currentUser.isLoggedIn) currentUser.name else "") }
    var passwordInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.tr("auth_title"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = viewModel.tr("auth_subtitle"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // If currently logged in, display profile card
                if (currentUser.isLoggedIn) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = currentUser.email,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = currentUser.provider.icon, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentUser.provider.title,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = viewModel.tr("cloud_account_active"),
                                    fontSize = 11.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "E2EE Secured",
                                    fontSize = 11.sp,
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.2f), contentColor = ErrorRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("logout_button")
                            ) {
                                Text(viewModel.tr("logout"), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isThai) "สลับเข้าสู่ระบบด้วยบัญชีอื่น:" else "Switch or link another account:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Social Login Buttons: Facebook, Google, Apple
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Facebook Login Button
                    Button(
                        onClick = { viewModel.loginWithSocial(AuthProvider.FACEBOOK) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1877F2),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_facebook_button")
                    ) {
                        Text("📘", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.tr("login_fb"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Google Login Button
                    Button(
                        onClick = { viewModel.loginWithSocial(AuthProvider.GOOGLE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_google_button")
                    ) {
                        Text("🌐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.tr("login_google"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Apple Login Button
                    Button(
                        onClick = { viewModel.loginWithSocial(AuthProvider.APPLE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_apple_button")
                    ) {
                        Text("🍎", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.tr("login_apple"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider Or Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = if (isThai) "หรือใช้อีเมล" else "or email",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email & Password Fields
                if (isSignUp) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(if (isThai) "ชื่อผู้ใช้งาน" else "Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text(viewModel.tr("email_label")) },
                    placeholder = { Text("you@example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text(viewModel.tr("password_label")) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val email = emailInput.ifBlank { "user@fotovault.com" }
                        viewModel.loginWithEmail(email, nameInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_auth_email_button")
                ) {
                    Text(
                        text = if (isSignUp) viewModel.tr("signup_email") else viewModel.tr("login_email"),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { isSignUp = !isSignUp },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isSignUp) {
                            if (isThai) "มีบัญชีอยู่แล้ว? เข้าสู่ระบบ" else "Already have an account? Sign in"
                        } else {
                            if (isThai) "ยังไม่มีบัญชี? สมัครสมาชิกใหม่" else "Don't have an account? Sign up"
                        },
                        fontSize = 12.sp,
                        color = CyanPrimary
                    )
                }
            }
        }
    }
}

// --- UNIVERSAL NAS SETUP DIALOG (ALL BRANDS) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasSetupDialog(
    viewModel: VaultViewModel,
    onDismiss: () -> Unit
) {
    val isThai by viewModel.isThai.collectAsState()
    val currentConfig by viewModel.nasConfig.collectAsState()
    val isTesting by viewModel.isTestingNas.collectAsState()

    var selectedBrand by remember { mutableStateOf<NasBrand>(currentConfig.brand) }
    var host by remember { mutableStateOf<String>(currentConfig.host) }
    var port by remember { mutableStateOf<String>(currentConfig.port.toString()) }
    var username by remember { mutableStateOf<String>(currentConfig.username) }
    var password by remember { mutableStateOf<String>(currentConfig.password) }
    var sharedFolder by remember { mutableStateOf<String>(currentConfig.sharedFolder) }
    var useHttps by remember { mutableStateOf<Boolean>(currentConfig.useHttps) }
    var syncWifiOnly by remember { mutableStateOf<Boolean>(currentConfig.autoSyncOnWifi) }
    var testResultMsg by remember { mutableStateOf<String?>(null) }
    var testSuccess by remember { mutableStateOf<Boolean>(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CyanPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Dns,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = viewModel.tr("nas_setup_title"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = viewModel.tr("nas_setup_sub"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select NAS Brand
                Text(
                    text = viewModel.tr("nas_select_brand"),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyanPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val brandChunks = remember { NasBrand.values().toList().chunked(2) }
                    for (rowBrands in brandChunks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (brand in rowBrands) {
                                val isSelected = brand == selectedBrand
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) CyanPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CyanPrimary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedBrand = brand
                                            port = brand.defaultPort.toString()
                                            useHttps = brand.defaultProtocol.contains("HTTPS")
                                            testResultMsg = null
                                        }
                                        .testTag("brand_chip_${brand.name}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = brand.brandName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = CyanPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = brand.protocolName,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brand Quick Info Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isThai) {
                                "โปรโตคอลแนะนำสำหรับ ${selectedBrand.brandName}: พอร์ตมาตรฐานคือ ${selectedBrand.defaultPort} (${selectedBrand.defaultProtocol}) ปลอดภัยด้วย E2EE End-to-End Encryption"
                            } else {
                                "Recommended protocol for ${selectedBrand.brandName}: default port is ${selectedBrand.defaultPort} (${selectedBrand.defaultProtocol}) secured with E2EE."
                            },
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Host / IP
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text(viewModel.tr("nas_host_label")) },
                    placeholder = { Text(if (selectedBrand == NasBrand.SYNOLOGY) "192.168.1.100 หรือ quickconnect.to/id" else "192.168.1.100") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nas_host")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text(viewModel.tr("nas_port_label")) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_nas_port")
                    )

                    OutlinedTextField(
                        value = sharedFolder,
                        onValueChange = { sharedFolder = it },
                        label = { Text(viewModel.tr("nas_folder_label")) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("input_nas_folder")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(viewModel.tr("nas_user_label")) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_nas_user")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(viewModel.tr("nas_pass_label")) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_nas_password")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // HTTPS & Wi-Fi Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isThai) "เชื่อมต่อผ่าน HTTPS (SSL/TLS)" else "Connect via HTTPS (SSL/TLS)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = useHttps,
                        onCheckedChange = { useHttps = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.tr("nas_wifi_only"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = syncWifiOnly,
                        onCheckedChange = { syncWifiOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                    )
                }

                if (testResultMsg != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (testSuccess) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (testSuccess) SuccessGreen else ErrorRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (testSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (testSuccess) SuccessGreen else ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = testResultMsg ?: "",
                                fontSize = 11.sp,
                                color = if (testSuccess) SuccessGreen else ErrorRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Test Connection Button
                OutlinedButton(
                    onClick = {
                        val portNum = port.toIntOrNull() ?: selectedBrand.defaultPort
                        val target = NasConfig(
                            brand = selectedBrand,
                            deviceName = "${selectedBrand.brandName} NAS",
                            host = host.ifBlank { "192.168.1.120" },
                            port = portNum,
                            protocol = if (useHttps) "HTTPS" else "HTTP",
                            username = username.ifBlank { "admin" },
                            password = password,
                            sharedFolder = sharedFolder.ifBlank { "/volume1/photo" },
                            useHttps = useHttps,
                            autoSyncOnWifi = syncWifiOnly
                        )
                        viewModel.testNas(target) { success ->
                            testSuccess = success
                            testResultMsg = if (success) {
                                if (isThai) "เชื่อมต่อกับ ${selectedBrand.brandName} (${target.host}) สำเร็จ! ตรวจสอบสิทธิ์เรียบร้อย"
                                else "Handshake with ${selectedBrand.brandName} (${target.host}) succeeded! Authenticated."
                            } else {
                                if (isThai) "ไม่สามารถเชื่อมต่อได้ กรุณาตรวจสอบ IP / พอร์ต"
                                else "Connection failed. Please check IP / Port."
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("test_nas_connection_btn")
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CyanPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isThai) "กำลังทดสอบ..." else "Testing Handshake...")
                    } else {
                        Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.tr("nas_test_connection"))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Save NAS Config Button
                Button(
                    onClick = {
                        val portNum = port.toIntOrNull() ?: selectedBrand.defaultPort
                        val target = NasConfig(
                            brand = selectedBrand,
                            deviceName = "${selectedBrand.brandName} NAS",
                            host = host.ifBlank { "192.168.1.120" },
                            port = portNum,
                            protocol = if (useHttps) "HTTPS" else "HTTP",
                            username = username.ifBlank { "admin" },
                            password = password,
                            sharedFolder = sharedFolder.ifBlank { "/volume1/photo" },
                            useHttps = useHttps,
                            autoSyncOnWifi = syncWifiOnly,
                            isConnected = true,
                            lastSyncTimestamp = System.currentTimeMillis()
                        )
                        viewModel.saveNasConfig(target)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_nas_config_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.tr("nas_save_config"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

