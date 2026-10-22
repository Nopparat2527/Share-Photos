package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoVaultDao {
    @Query("SELECT * FROM vault_photos ORDER BY createdAt DESC")
    fun getAllPhotos(): Flow<List<PhotoVaultEntity>>

    @Query("SELECT * FROM vault_photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: String): PhotoVaultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoVaultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoVaultEntity>)

    @Update
    suspend fun updatePhoto(photo: PhotoVaultEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoVaultEntity)

    @Query("DELETE FROM vault_photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: String)

    @Query("UPDATE vault_photos SET isSynced = 1, syncTimestamp = :timestamp")
    suspend fun markAllSynced(timestamp: Long)

    @Query("UPDATE vault_photos SET nasSynced = 1")
    suspend fun markAllNasSynced()
}
