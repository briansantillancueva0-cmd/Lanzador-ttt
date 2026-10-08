package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iso_games")
data class IsoGameEntity(
    @PrimaryKey
    val gameId: String,               // e.g. "ULUS-10537"
    val title: String,                // "Dragon Ball Z: Tenkaichi Tag Team"
    val fileName: String,             // "DBZ_Tenkaichi_Tag_Team.iso"
    val fileUri: String,              // content:// or virtual path
    val fileSizeBytes: Long,          // e.g. 1_254_211_584L (1.17 GB)
    val region: String,               // "USA / NTSC-U"
    val discStructure: String,        // "ISO 9660 / UDF 1.02 (PSP_GAME)"
    val crc32: String,                // "A94C82E1"
    val sha1: String,                 // "72B84D..."
    val status: String,               // "VERIFIED_READY"
    val isMounted: Boolean,
    val lastMountedTime: Long,
    val hasPck1Injected: Boolean,     // indicates whether Pck1 is injected into this ISO
    val activePatchLanguage: String   // "Latino Dub", "Original Japanese", "English"
)
