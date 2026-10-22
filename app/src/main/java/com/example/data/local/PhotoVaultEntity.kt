package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_photos")
data class PhotoVaultEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val tagsJson: String, // comma-separated or JSON list
    val localUri: String,
    val resDrawableName: String,
    val isEncrypted: Boolean,
    val encryptionAlgorithm: String,
    val checksum: String,
    val isSynced: Boolean,
    val syncTimestamp: Long,
    val cloudProvider: String,
    val fileSize: String,
    val resolution: String,
    val createdAt: Long,
    val isMarketplaceListed: Boolean,
    val priceThb: Double,
    val pricePoints: Int,
    val salesCount: Int,
    val rating: Float,
    val aiEnhanced: Boolean,
    val brightness: Float,
    val contrast: Float,
    val saturation: Float,
    val warmth: Float,
    val activeFilter: String,
    val facesJson: String = "",
    val isPrivateOwnerOnly: Boolean = true,
    val nasSynced: Boolean = true
)
