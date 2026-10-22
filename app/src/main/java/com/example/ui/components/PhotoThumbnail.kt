package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.VaultPhoto

@Composable
fun PhotoThumbnail(
    photo: VaultPhoto,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    overrideBrightness: Float? = null,
    overrideContrast: Float? = null,
    overrideSaturation: Float? = null,
    overrideWarmth: Float? = null,
    overrideFilter: String? = null,
    showOriginal: Boolean = false
) {
    val context = LocalContext.current

    val brightness = if (showOriginal) 0f else (overrideBrightness ?: photo.brightness)
    val contrast = if (showOriginal) 1f else (overrideContrast ?: photo.contrast)
    val saturation = if (showOriginal) 1f else (overrideSaturation ?: photo.saturation)
    val warmth = if (showOriginal) 0f else (overrideWarmth ?: photo.warmth)
    val filter = if (showOriginal) "None" else (overrideFilter ?: photo.activeFilter)

    val colorFilter = remember(brightness, contrast, saturation, warmth, filter, showOriginal) {
        if (showOriginal || (!photo.aiEnhanced && overrideBrightness == null && overrideFilter == null)) {
            null
        } else {
            createAdjustmentColorFilter(brightness, contrast, saturation, warmth, filter)
        }
    }

    val resId = remember(photo.resDrawableName) {
        if (photo.resDrawableName.isNotBlank()) {
            context.resources.getIdentifier(photo.resDrawableName, "drawable", context.packageName)
        } else {
            0
        }
    }

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        when {
            resId != 0 -> {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    colorFilter = colorFilter
                )
            }
            photo.localUri.isNotBlank() -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(photo.localUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    colorFilter = colorFilter
                )
            }
            else -> {
                // Fallback to sample cyber or sunset
                val fallbackRes = context.resources.getIdentifier("img_sample_sunset", "drawable", context.packageName)
                if (fallbackRes != 0) {
                    Image(
                        painter = painterResource(id = fallbackRes),
                        contentDescription = photo.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale,
                        colorFilter = colorFilter
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun createAdjustmentColorFilter(
    brightness: Float,
    contrast: Float,
    saturation: Float,
    warmth: Float,
    filterName: String
): ColorFilter {
    val matrix = ColorMatrix()

    // Base Saturation
    matrix.setToSaturation(saturation.coerceIn(0f, 2.5f))

    // Contrast and Brightness: Scale and offset
    val c = contrast.coerceIn(0.5f, 2.0f)
    val b = brightness * 255f

    // Warmth: shift red and blue channels
    val rShift = warmth * 30f
    val bShift = -warmth * 30f

    // Artistic filters tint adjustments
    val (fR, fG, fB) = when (filterName) {
        "Cyberpunk" -> Triple(1.15f, 0.95f, 1.4f)
        "Cinematic" -> Triple(1.1f, 1.05f, 0.9f)
        "Sunset" -> Triple(1.3f, 1.05f, 0.85f)
        "Emerald" -> Triple(0.9f, 1.3f, 1.05f)
        "Noir" -> Triple(1.0f, 1.0f, 1.0f)
        "Smart HDR" -> Triple(1.1f, 1.1f, 1.15f)
        else -> Triple(1f, 1f, 1f)
    }

    val array = floatArrayOf(
        c * fR, 0f, 0f, 0f, b + rShift,
        0f, c * fG, 0f, 0f, b,
        0f, 0f, c * fB, 0f, b + bShift,
        0f, 0f, 0f, 1f, 0f
    )

    val finalMatrix = ColorMatrix(array)
    finalMatrix.timesAssign(matrix)
    return ColorFilter.colorMatrix(finalMatrix)
}
