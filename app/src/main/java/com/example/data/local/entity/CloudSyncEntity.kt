package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_sync_records")
data class CloudSyncEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val actionType: String,      // "AUTO_SYNC", "MANUAL_UPLOAD", "MANUAL_DOWNLOAD", "BACKUP_RESTORE"
    val provider: String,        // "Google Drive Cloud Backup", "Firebase Cloud Vault", "Almacenamiento Local Cifrado"
    val status: String,          // "SUCCESS", "PENDING", "FAILED"
    val itemsSyncedCount: Int,
    val bytesTransferred: Long,
    val details: String
)
