package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultPhoto
import com.example.ui.components.PhotoThumbnail
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.AmberSecondary
import com.example.ui.viewmodel.VaultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiEditorScreen(
    photo: VaultPhoto,
    viewModel: VaultViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val isThai by viewModel.isThai.collectAsState()
    val brightness by viewModel.editBrightness.collectAsState()
    val contrast by viewModel.editContrast.collectAsState()
    val saturation by viewModel.editSaturation.collectAsState()
    val warmth by viewModel.editWarmth.collectAsState()
    val activeFilter by viewModel.editFilter.collectAsState()
    val isComparingOriginal by viewModel.isComparingOriginal.collectAsState()

    val presets = listOf("None", "Smart HDR", "Cyberpunk", "Cinematic", "Sunset", "Emerald", "Noir")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.tr("ai_editor_title"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ai_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.resetAiEditor() }) {
                        Text(viewModel.tr("reset_filters"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { viewModel.saveAiEditorChanges() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_ai_button")
                    ) {
                        Text(viewModel.tr("save_changes"), fontWeight = FontWeight.Bold)
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
            // Live Preview Canvas with Hold-to-Compare
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                viewModel.isComparingOriginal.value = true
                                tryAwaitRelease()
                                viewModel.isComparingOriginal.value = false
                            }
                        )
                    }
            ) {
                PhotoThumbnail(
                    photo = photo,
                    modifier = Modifier.fillMaxSize(),
                    overrideBrightness = brightness,
                    overrideContrast = contrast,
                    overrideSaturation = saturation,
                    overrideWarmth = warmth,
                    overrideFilter = activeFilter,
                    showOriginal = isComparingOriginal
                )

                // Comparison badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (isComparingOriginal) {
                            if (isThai) "⚡ แสดงภาพต้นฉบับ" else "⚡ Original View"
                        } else {
                            viewModel.tr("compare")
                        },
                        fontSize = 11.sp,
                        color = if (isComparingOriginal) AmberSecondary else Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (activeFilter != "None") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyanPrimary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Filter: $activeFilter",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Quick Auto AI Enhance Button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = viewModel.tr("auto_enhance"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isThai) "ปรับสมดุลแสง คอนทราสต์ และสีด้วยอัลกอริทึมอัจฉริยะ" else "One-tap AI color & lighting balance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Button(
                        onClick = { viewModel.applyAutoEnhance() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("auto_enhance_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Auto AI", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Presets Horizontal Row
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = viewModel.tr("preset_filters"),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = activeFilter == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.editFilter.value = preset
                                when (preset) {
                                    "Cyberpunk" -> {
                                        viewModel.editContrast.value = 1.3f
                                        viewModel.editSaturation.value = 1.4f
                                        viewModel.editWarmth.value = -0.2f
                                    }
                                    "Cinematic" -> {
                                        viewModel.editContrast.value = 1.2f
                                        viewModel.editSaturation.value = 1.1f
                                        viewModel.editWarmth.value = 0.15f
                                    }
                                    "Sunset" -> {
                                        viewModel.editContrast.value = 1.15f
                                        viewModel.editSaturation.value = 1.35f
                                        viewModel.editWarmth.value = 0.35f
                                    }
                                    "Emerald" -> {
                                        viewModel.editContrast.value = 1.1f
                                        viewModel.editSaturation.value = 1.25f
                                        viewModel.editWarmth.value = -0.1f
                                    }
                                    "Noir" -> {
                                        viewModel.editContrast.value = 1.4f
                                        viewModel.editSaturation.value = 0f
                                        viewModel.editWarmth.value = 0f
                                    }
                                    "Smart HDR" -> {
                                        viewModel.editContrast.value = 1.25f
                                        viewModel.editSaturation.value = 1.25f
                                        viewModel.editWarmth.value = 0.05f
                                    }
                                    else -> {
                                        viewModel.editContrast.value = 1f
                                        viewModel.editSaturation.value = 1f
                                        viewModel.editWarmth.value = 0f
                                    }
                                }
                            },
                            label = { Text(preset) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // Fine-tuning Sliders
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Brightness Slider
                    SliderRow(
                        label = viewModel.tr("brightness"),
                        value = brightness,
                        range = -0.5f..0.5f,
                        onValueChange = { viewModel.editBrightness.value = it },
                        valueDisplay = "%.2f".format(brightness)
                    )

                    // Contrast Slider
                    SliderRow(
                        label = viewModel.tr("contrast"),
                        value = contrast,
                        range = 0.5f..1.8f,
                        onValueChange = { viewModel.editContrast.value = it },
                        valueDisplay = "%.2f".format(contrast)
                    )

                    // Saturation Slider
                    SliderRow(
                        label = viewModel.tr("saturation"),
                        value = saturation,
                        range = 0.0f..2.0f,
                        onValueChange = { viewModel.editSaturation.value = it },
                        valueDisplay = "%.2f".format(saturation)
                    )

                    // Warmth Slider
                    SliderRow(
                        label = viewModel.tr("warmth"),
                        value = warmth,
                        range = -0.5f..0.5f,
                        onValueChange = { viewModel.editWarmth.value = it },
                        valueDisplay = "%.2f".format(warmth)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueDisplay: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
            Text(text = valueDisplay, style = MaterialTheme.typography.bodySmall, color = CyanPrimary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = CyanPrimary,
                activeTrackColor = CyanPrimary
            )
        )
    }
}
