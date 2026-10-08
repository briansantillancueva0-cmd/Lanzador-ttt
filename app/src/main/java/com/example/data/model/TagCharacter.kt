package com.example.data.model

import androidx.compose.ui.graphics.Color

data class TagCharacter(
    val id: String,
    val name: String,
    val formTitle: String,
    val packOrigin: String, // e.g. "Pck1 Official", "Base Game", "AF Expansion"
    val health: Int,        // e.g. 50000
    val kiMax: Int,         // e.g. 7 bars
    val meleeAttack: Int,   // 1 - 100
    val blastPower: Int,    // 1 - 100
    val defense: Int,       // 1 - 100
    val speed: Int,         // 1 - 100
    val blast1Move: String, // e.g. "Afterimage Strike / Instant Transmission"
    val blast2Move: String, // e.g. "Kamehameha / Big Bang Attack"
    val ultimateMove: String, // e.g. "10x Super Kamehameha / Final Shine Attack"
    val auraHexColor: Long, // 0xFF...
    val isPck1Included: Boolean = true,
    val iconEmoji: String = "⚡",
    val tags: List<String> = listOf("Saiyan", "Tag Anchor")
)
