package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultPhoto
import com.example.ui.components.PhotoThumbnail
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun MarketplaceScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val isThai by viewModel.isThai.collectAsState()
    val marketplacePhotos by viewModel.marketplacePhotos.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()
    val userPoints by viewModel.repository.userPoints.collectAsState()

    // Combined list: if no photos listed yet, show available vault photos with list option
    val displayPhotos = if (marketplacePhotos.isNotEmpty()) marketplacePhotos else allPhotos

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Marketplace Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = AmberSecondary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = viewModel.tr("market_title"),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = viewModel.tr("market_subtitle"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            // Points balance pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "⭐ $userPoints Pts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AmberSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Features row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MarketFeatureChip(icon = "🛡️", text = if (isThai) "ตรวจสลิปทันที" else "Instant Slip Check")
                            MarketFeatureChip(icon = "⭐", text = if (isThai) "รับแต้มลดเพิ่ม" else "Earn Points")
                            MarketFeatureChip(icon = "📜", text = if (isThai) "ลิขสิทธิ์พาณิชย์" else "Commercial Rights")
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isThai) "ผลงานภาพถ่ายยอดนิยม" else "Curated Enhanced Photos",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${displayPhotos.size} ${if (isThai) "รายการ" else "Items"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Marketplace Photo Listings
        items(displayPhotos, key = { it.id }) { photo ->
            MarketplacePhotoCard(
                photo = photo,
                isThai = isThai,
                onBuyClick = { viewModel.showBuyDialog.value = photo },
                onDetailClick = { viewModel.openPhotoDetail(photo) }
            )
        }
    }
}

@Composable
fun MarketplacePhotoCard(
    photo: VaultPhoto,
    isThai: Boolean,
    onBuyClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("market_card_${photo.id}")
            .clickable(onClick = onDetailClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                PhotoThumbnail(
                    photo = photo,
                    modifier = Modifier.fillMaxSize()
                )

                // Watermark overlay simulation
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "FotoVault Verified License",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // AI Enhanced Badge
                if (photo.aiEnhanced) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyanPrimary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "AI Enhanced",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Price Tag Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberSecondary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "฿${photo.priceThb.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⭐ ${photo.rating} (${photo.salesCount + 12} ${if (isThai) "ขายแล้ว" else "sold"})",
                                fontSize = 11.sp,
                                color = AmberSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${photo.category}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onBuyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("buy_button_${photo.id}")
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isThai) "ซื้อรูป" else "Buy",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (photo.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = photo.tags.joinToString(" ") { "#$it" },
                        fontSize = 11.sp,
                        color = CyanPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MarketFeatureChip(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
