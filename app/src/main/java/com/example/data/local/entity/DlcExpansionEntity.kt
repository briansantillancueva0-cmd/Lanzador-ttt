package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dlc_expansions")
data class DlcExpansionEntity(
    @PrimaryKey
    val id: String,                   // UUID or code e.g. "DLC_PCK1", "DLC_AF_ROSTER"
    val gameId: String,               // "ULUS-10537"
    val name: String,                 // "Pck1 Personajes Extendidos (Official Tag Pack)"
    val category: String,             // "ROSTER", "AUDIO_DUB", "TEXTURES", "STORY_MODE", "PERFORMANCE"
    val author: String,               // "Team Tag Modders / Spike"
    val version: String,              // "v2.4.0"
    val description: String,          // "Agrega personajes nuevos, transformaciones SSJ4 y fusiones al menú principal"
    val sizeBytes: Long,              // e.g. 185_420_000L
    val isEnabled: Boolean,           // Toggle state
    val isCorePck1: Boolean,          // True if this is the user-requested Pck1 package
    val priority: Int,                // Load order (1..100)
    val installDate: Long,
    val filePath: String,             // storage path or virtual pack uri
    val itemsIncludedCount: Int       // e.g. 18 characters or 320 textures
)
