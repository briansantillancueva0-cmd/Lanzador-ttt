package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun DashboardScreen(
    viewModel: TagTeamViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val mountedIso by viewModel.mountedIso.collectAsState()
    val dlcs by viewModel.dlcs.collectAsState()
    val saves by viewModel.saves.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val autoSync by viewModel.autoSyncEnabled.collectAsState()

    val activeDlcsCount = dlcs.count { it.isEnabled }
    val isPck1Active = mountedIso?.hasPck1Injected == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_banner),
                        contentDescription = "Tag Team Arena",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DarkBg.copy(alpha = 0.85f),
                                        DarkBg
                                    )
                                )
                            )
                    )

                    // Overlay Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TagGold
                            ) {
                                Text(
                                    text = "PSP PORT ENGINE",
                                    color = DarkBg,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (mountedIso != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusSuccess.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess)
                                ) {
                                    Text(
                                        text = viewModel.getString("status_verified"),
                                        color = StatusSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = mountedIso?.title ?: "Dragon Ball Z: Tenkaichi Tag Team",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = viewModel.getString("hero_subtitle"),
                            color = TagCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. Primary Launch Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.launchGame(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_launch_game"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TagGold,
                        contentColor = DarkBg
                    )
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = viewModel.getString("quick_launch"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                FilledTonalButton(
                    onClick = { onNavigate("psp") },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_launch_arena"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = DarkSurfaceHigh,
                        contentColor = TagCyan
                    )
                ) {
                    Icon(Icons.Default.Gamepad, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = viewModel.getString("tab_psp"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 3. Featured Feature Cards Grid
        item {
            Text(
                text = "Módulos de Gestión",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // Pck1 Menu Personajes Feature Highlight
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("pck1") }
                    .testTag("card_pck1_highlight"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isPck1Active) TagOrange.copy(alpha = 0.2f) else DarkSurfaceHigh)
                            .border(1.dp, if (isPck1Active) TagOrange else DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🥋", fontSize = 22.sp)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Menú Personajes Pck1",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isPck1Active) StatusSuccess.copy(alpha = 0.2f) else StatusWarning.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isPck1Active) "ACTIVO" else "PENDIENTE",
                                    color = if (isPck1Active) StatusSuccess else StatusWarning,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Instala o gestiona el paquete de luchadores (Goku SSJ4, Gogeta, Vegetto, Beast Gohan)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextTertiary
                    )
                }
            }
        }

        // Controls PSP Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("psp") }
                    .testTag("card_psp_highlight"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TagBlue.copy(alpha = 0.15f))
                            .border(1.dp, TagBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = TagBlue)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.getString("psp_controls_title"),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mandos táctiles PSP: cruceta D-Pad, nub analógico, botones △○✕□ y vibración háptica.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary)
                }
            }
        }

        // Two-column Stats (DLCs & Saves)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // DLCs card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate("dlc") }
                        .testTag("card_dlcs_quick"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Extension, contentDescription = null, tint = TagPurple)
                            Text(
                                text = "$activeDlcsCount/${dlcs.size}",
                                color = TagPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = viewModel.getString("tab_dlc"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Doblaje latino, texturas HD y mods",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Saves card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate("saves") }
                        .testTag("card_saves_quick"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = TagGold)
                            Text(
                                text = "${saves.size} Slots",
                                color = TagGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = viewModel.getString("tab_saves"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Partida 100% y editor de Puntos Z",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Cloud Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("cloud") }
                    .testTag("card_cloud_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(StatusSuccess.copy(alpha = 0.15f))
                            .border(1.dp, StatusSuccess, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = StatusSuccess,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusSuccess)
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = viewModel.getString("cloud_title"),
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            if (autoSync) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StatusSuccess.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "AUTO",
                                        color = StatusSuccess,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isSyncing) "Sincronizando partidas en la nube..." else "Sincronización automática activa con Google Drive Backup",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier.testTag("btn_sync_quick")
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar", tint = TagCyan)
                    }
                }
            }
        }
    }
}
