package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TagCharacter
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun Pck1MenuScreen(
    viewModel: TagTeamViewModel,
    onNavigateToArena: () -> Unit
) {
    val mountedIso by viewModel.mountedIso.collectAsState()
    val isPck1Installed = mountedIso?.hasPck1Injected == true
    val characters = viewModel.pck1Characters
    var selectedChar by remember { mutableStateOf<TagCharacter?>(characters.firstOrNull()) }
    var showImportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("pck1_menu_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Pck1 Installer Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isPck1Installed) TagOrange.copy(alpha = 0.2f) else DarkSurfaceHigh)
                            .border(1.dp, if (isPck1Installed) TagOrange else DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🥋", fontSize = 24.sp)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Menú Personajes Pck1",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isPck1Installed) StatusSuccess.copy(alpha = 0.2f) else StatusWarning.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isPck1Installed) "INSTALADO" else "NO INSTALADO",
                                    color = if (isPck1Installed) StatusSuccess else StatusWarning,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Archivo: USRDIR/pck1.bin • 185.4 MB • 12 Personajes Especiales",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = "El archivo Pck1 contiene el paquete oficial extendido de personajes y modelos 3D de alta fidelidad, desbloqueando formas SSJ4, fusiones definitivas y guerreros del Torneo del Poder.",
                    color = TextTertiary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.installOrTogglePck1(!isPck1Installed) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_toggle_pck1_install"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPck1Installed) StatusError.copy(alpha = 0.85f) else TagOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isPck1Installed) Icons.Default.DeleteOutline else Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isPck1Installed) "Desinstalar Pck1" else "Instalar Pck1 en ISO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_import_external_pck1"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TagCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TagCyan)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Importar Pck1 .bin",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. Character Detail Sheet (if selected)
        if (selectedChar != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pck1_character_detail_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedChar!!.iconEmoji, fontSize = 28.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedChar!!.name,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = selectedChar!!.formTitle,
                                    color = Color(selectedChar!!.auraHexColor),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.selectCharacter(selectedChar!!)
                                onNavigateToArena()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TagGold, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_test_char_arena")
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Probar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Moves Info
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "💥 Ráfaga 1: ${selectedChar!!.blast1Move}",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "⚡ Ráfaga 2: ${selectedChar!!.blast2Move}",
                                color = TagCyan,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "🔥 Definitiva: ${selectedChar!!.ultimateMove}",
                                color = TagOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Stats Bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatPill("Cuerpo a cuerpo", selectedChar!!.meleeAttack, TagOrange, Modifier.weight(1f))
                        StatPill("Ráfaga Ki", selectedChar!!.blastPower, TagCyan, Modifier.weight(1f))
                        StatPill("Defensa", selectedChar!!.defense, TagGold, Modifier.weight(1f))
                        StatPill("Velocidad", selectedChar!!.speed, TagPurple, Modifier.weight(1f))
                    }
                }
            }
        }

        // 3. Characters Grid Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Roster de Personajes Incluidos en Pck1 (${characters.size})",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Characters Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .testTag("pck1_characters_grid"),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(characters, key = { it.id }) { character ->
                val isSelected = selectedChar?.id == character.id
                val auraColor = Color(character.auraHexColor)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedChar = character }
                        .testTag("char_card_${character.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) DarkSurfaceHigh else DarkSurfaceVariant
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, auraColor) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = character.iconEmoji, fontSize = 22.sp)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(auraColor)
                            )
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = character.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = character.formTitle,
                            color = auraColor,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "ATK ${character.meleeAttack} • KI ${character.blastPower}",
                            color = TextTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Import Pck1 Dialog
    if (showImportDialog) {
        var fileName by remember { mutableStateOf("pck1_roster_v3.bin") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Importar Paquete Pck1 Externo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Selecciona o especifica el nombre del archivo de personajes a inyectar en USRDIR/pck1.bin:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("Nombre del archivo .bin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importCustomPck1File(fileName)
                        showImportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TagCyan, contentColor = DarkBg)
                ) {
                    Text("Inyectar Pck1", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImportDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StatPill(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurface)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$value", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextTertiary, fontSize = 9.sp, maxLines = 1)
    }
}
