package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_games")
data class SaveGameEntity(
    @PrimaryKey
    val id: String,
    val gameId: String,               // "ULUS-10537"
    val slotIndex: Int,               // 1, 2, 3, etc.
    val title: String,                // "Partida Completa 100% - Rango Z Supremo"
    val playtimeMinutes: Int,         // e.g. 1420
    val storyProgressPercent: Int,    // 0..100
    val unlockedCharacters: Int,      // e.g. 70
    val totalCharacters: Int,         // e.g. 70
    val zPoints: Long,                // e.g. 9_999_999L
    val dateModified: Long,
    val is100PercentMaster: Boolean,
    val isCloudSynced: Boolean,
    val cloudSyncTimestamp: Long,
    val rawDataSize: Long,            // e.g. 484_352 bytes
    val backupNotes: String
)
