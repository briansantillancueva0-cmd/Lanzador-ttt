package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun SettingsScreen(
    viewModel: TagTeamViewModel
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val mountedIso by viewModel.mountedIso.collectAsState()
    val graphicsBackend by viewModel.graphicsBackend.collectAsState()
    val resolution by viewModel.renderingResolution.collectAsState()
    val fpsPatch by viewModel.fpsPatchEnabled.collectAsState()
    val widescreen by viewModel.widescreenPatch.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Language Support Card (Multi-language selector)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_language_selector"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TagGold.copy(alpha = 0.15f))
                                .border(1.dp, TagGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = TagGold)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = viewModel.getString("settings_lang"),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cambio dinámico e instantáneo de idioma de la suite",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Languages List
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = currentLang == lang
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TagGold.copy(alpha = 0.15f) else DarkSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) TagGold else DarkBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setLanguage(lang) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("lang_option_${lang.code}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = lang.flagEmoji, fontSize = 20.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = lang.displayName,
                                        color = if (isSelected) TagGold else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TagGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. In-Game Audio Dubbing Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TagCyan.copy(alpha = 0.15f))
                                .border(1.dp, TagCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = TagCyan)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = viewModel.getString("settings_audio_dub"),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Parche de voces para la ISO de Tag Team",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    listOf(
                        "Español Latino (Voces Oficiales Mario Castañeda, René García)",
                        "Japonés Original (Voces Oficiales Masako Nozawa)",
                        "Inglés Dub (Funimation)"
                    ).forEach { dub ->
                        val isSelected = mountedIso?.activePatchLanguage == dub
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateLanguagePatch(dub) },
                                colors = RadioButtonDefaults.colors(selectedColor = TagCyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = dub, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Graphics & Performance Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Configuración del Motor Gráfico",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    // Backend
                    Text(text = viewModel.getString("settings_graphics_backend"), color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Vulkan (Recomendado)", "OpenGL ES 3.2").forEach { b ->
                            FilterChip(
                                selected = graphicsBackend == b,
                                onClick = { viewModel.setGraphicsBackend(b) },
                                label = { Text(b, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TagGold.copy(alpha = 0.2f),
                                    selectedLabelColor = TagGold
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Resolution
                    Text(text = viewModel.getString("settings_resolution"), color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("1x PSP", "2x PSP (960x544)", "3x PSP", "4x PSP HD").forEach { r ->
                            FilterChip(
                                selected = resolution == r,
                                onClick = { viewModel.setResolution(r) },
                                label = { Text(r, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TagCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = TagCyan
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(Modifier.height(10.dp))

                    // 60 FPS Patch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = viewModel.getString("settings_60fps"), color = TextPrimary, fontSize = 14.sp)
                            Text(text = "Elimina límite de 30 FPS para combates fluidos", color = TextTertiary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = fpsPatch,
                            onCheckedChange = { viewModel.setFpsPatch(it) }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Widescreen Patch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = viewModel.getString("settings_widescreen"), color = TextPrimary, fontSize = 14.sp)
                            Text(text = "Ajuste sin bandas negras para pantallas modernas", color = TextTertiary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = widescreen,
                            onCheckedChange = { viewModel.setWidescreenPatch(it) }
                        )
                    }
                }
            }
        }

        // 4. About App
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Tag Team Port Hub v2.5.0", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "Gestor nativo de ISOs, expansiones Pck1, controles PSP y sincronización en la nube.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
