package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PspControllerOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun PspControlsScreen(
    viewModel: TagTeamViewModel
) {
    val pspConfig by viewModel.pspConfig.collectAsState()
    val pspInput by viewModel.pspInput.collectAsState()
    val arenaState by viewModel.arenaState.collectAsState()
    var showConfigPanel by remember { mutableStateOf(false) }

    val activeFighter = if (arenaState.isPartnerActive) arenaState.partnerFighter else arenaState.playerFighter
    val partnerFighter = if (arenaState.isPartnerActive) arenaState.playerFighter else arenaState.partnerFighter
    val activeHp = if (arenaState.isPartnerActive) arenaState.partnerHp else arenaState.playerHp
    val activeMaxHp = if (arenaState.isPartnerActive) arenaState.partnerMaxHp else arenaState.playerMaxHp
    val activeKi = if (arenaState.isPartnerActive) arenaState.partnerKi else arenaState.playerKi

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("psp_controls_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Battle Arena & Controls Feedback Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎮", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Arena Tag Team • Modo Controles PSP",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { showConfigPanel = !showConfigPanel },
                    modifier = Modifier.testTag("btn_toggle_psp_config")
                ) {
                    Icon(
                        imageVector = if (showConfigPanel) Icons.Default.Close else Icons.Default.Tune,
                        contentDescription = "Ajustes de Controles",
                        tint = TagGold
                    )
                }
            }

            // Quick Settings Drawer (if expanded)
            AnimatedVisibility(visible = showConfigPanel) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceHigh)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Opacidad: ${(pspConfig.opacity * 100).toInt()}%", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = pspConfig.opacity,
                                onValueChange = { viewModel.updatePspConfig(opacity = it) },
                                valueRange = 0.3f..1.0f,
                                modifier = Modifier.width(180.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Escala: ${(pspConfig.buttonScale * 100).toInt()}%", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = pspConfig.buttonScale,
                                onValueChange = { viewModel.updatePspConfig(scale = it) },
                                valueRange = 0.8f..1.2f,
                                modifier = Modifier.width(180.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Respuesta Háptica (Vibración)", color = TextPrimary, fontSize = 12.sp)
                            Switch(
                                checked = pspConfig.hapticsEnabled,
                                onCheckedChange = { viewModel.updatePspConfig(haptics = it) }
                            )
                        }
                    }
                }
            }

            // Arena Tag Team Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("arena_status_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Tag Fighters Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Active Player
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = activeFighter.iconEmoji, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeFighter.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = TagGold
                                    ) {
                                        Text(
                                            text = "ACTIVO",
                                            color = DarkBg,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = activeFighter.formTitle,
                                    color = Color(activeFighter.auraHexColor),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Partner preview
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Tag: ${partnerFighter.name}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "HP: ${if (arenaState.isPartnerActive) arenaState.playerHp else arenaState.partnerHp}",
                                    color = StatusSuccess,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(text = partnerFighter.iconEmoji, fontSize = 20.sp)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Health Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "VIDA: $activeHp / $activeMaxHp", color = TextSecondary, fontSize = 10.sp)
                        Text(text = "${(activeHp * 100) / activeMaxHp}%", color = StatusSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (activeHp.toFloat() / activeMaxHp.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (activeHp > activeMaxHp * 0.3f) StatusSuccess else StatusError,
                        trackColor = DarkSurfaceHigh
                    )

                    Spacer(Modifier.height(6.dp))

                    // Ki Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "KI: ${"%.1f".format(activeKi)} / 7.0 BARRAS", color = TagCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "COMBO: ${arenaState.comboHits} HITS", color = TagOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (activeKi / 7.0f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TagCyan,
                        trackColor = DarkSurfaceHigh
                    )

                    Spacer(Modifier.height(8.dp))

                    // Live Action Feedback Text
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = arenaState.lastActionText,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // 2. Real Interactive PSP Controller Overlay
        PspControllerOverlay(
            config = pspConfig,
            inputState = pspInput,
            onButtonPress = { btn -> viewModel.pressPspButton(btn) },
            onButtonRelease = { btn -> viewModel.releasePspButton(btn) },
            onAnalogMove = { x, y -> viewModel.updatePspAnalog(x, y) },
            onQuickTagSwitch = { viewModel.tagSwitchFighter() },
            onQuickBurst = { viewModel.triggerBurstTag() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )
    }
}
