package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SaveGameEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun SaveManagerScreen(
    viewModel: TagTeamViewModel
) {
    val context = LocalContext.current
    val saves by viewModel.saves.collectAsState()
    var saveToDelete by remember { mutableStateOf<SaveGameEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("save_manager_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Card
        item {
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TagGold.copy(alpha = 0.15f))
                                .border(1.dp, TagGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = TagGold)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.getString("saves_title"),
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ranuras de SAVEDATA, copias de seguridad y partida 100%",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = viewModel.getString("saves_desc"),
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.load100PercentMasterSave() },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_load_master_save"),
                            colors = ButtonDefaults.buttonColors(containerColor = TagGold, contentColor = DarkBg),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Cargar 100% Master",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.backupCurrentSave() },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_create_backup"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TagCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TagCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Crear Respaldo",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Save Slots List Header
        item {
            Text(
                text = "Ranuras Guardadas (${saves.size})",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 3. Save Slots Items
        items(saves, key = { it.id }) { save ->
            SaveSlotCard(
                save = save,
                onMaxZ = { viewModel.maxOutZPoints(save.id) },
                onExport = {
                    Toast.makeText(context, "Partida exportada a Descargas/SAVEDATA_${save.slotIndex}.bin", Toast.LENGTH_SHORT).show()
                },
                onDelete = { saveToDelete = save }
            )
        }
    }

    if (saveToDelete != null) {
        AlertDialog(
            onDismissRequest = { saveToDelete = null },
            title = { Text("¿Eliminar partida guardada?") },
            text = { Text("Se eliminará permanentemente la ranura #${saveToDelete!!.slotIndex}: '${saveToDelete!!.title}'.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSave(saveToDelete!!.id)
                        saveToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { saveToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SaveSlotCard(
    save: SaveGameEntity,
    onMaxZ: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("save_card_${save.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TagGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TagGold)
                    ) {
                        Text(
                            text = "SLOT ${save.slotIndex}",
                            color = TagGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    if (save.is100PercentMaster) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "100% DESBLOQUEADO",
                                color = StatusSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Cloud Synced badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (save.isCloudSynced) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = if (save.isCloudSynced) StatusSuccess else TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (save.isCloudSynced) "En la nube" else "Local",
                        color = if (save.isCloudSynced) StatusSuccess else TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = save.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = save.backupNotes,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Progreso Historia: ${save.storyProgressPercent}%", color = TextSecondary, fontSize = 11.sp)
                Text(text = "${save.unlockedCharacters}/${save.totalCharacters} Personajes", color = TagCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { save.storyProgressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TagGold,
                trackColor = DarkSurfaceHigh
            )

            Spacer(Modifier.height(10.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puntos Z: ${"%,d".format(save.zPoints)}",
                    color = TagGold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${save.playtimeMinutes / 60}h ${save.playtimeMinutes % 60}m jugados",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = onMaxZ,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = DarkSurfaceHigh,
                            contentColor = TagGold
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_max_z_${save.id}")
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Max Z-Points", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onExport,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = DarkSurfaceHigh,
                            contentColor = TagCyan
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_export_${save.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Exportar", fontSize = 11.sp)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("btn_delete_save_${save.id}")
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
