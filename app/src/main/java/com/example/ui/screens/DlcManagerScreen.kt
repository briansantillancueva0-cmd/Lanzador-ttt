package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DlcExpansionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun DlcManagerScreen(
    viewModel: TagTeamViewModel
) {
    val dlcs by viewModel.dlcs.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var dlcToDelete by remember { mutableStateOf<DlcExpansionEntity?>(null) }

    // Filtered items
    val filteredDlcs = remember(dlcs, selectedCategory) {
        if (selectedCategory == "ALL") dlcs
        else dlcs.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dlc_manager_screen"),
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
                                .background(TagPurple.copy(alpha = 0.2f))
                                .border(1.dp, TagPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Extension, contentDescription = null, tint = TagPurple)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.getString("dlc_manager_title"),
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instala, elimina y activa expansiones en tiempo real",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = viewModel.getString("dlc_manager_desc"),
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_add_dlc_open"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TagPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = viewModel.getString("btn_add_dlc"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 2. Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == "ALL",
                    onClick = { selectedCategory = "ALL" },
                    label = { Text("Todos (${dlcs.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TagPurple.copy(alpha = 0.3f),
                        selectedLabelColor = TagPurple
                    ),
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = selectedCategory == "ROSTER",
                    onClick = { selectedCategory = "ROSTER" },
                    label = { Text("Roster") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TagOrange.copy(alpha = 0.3f),
                        selectedLabelColor = TagOrange
                    ),
                    modifier = Modifier.testTag("filter_roster")
                )
                FilterChip(
                    selected = selectedCategory == "AUDIO_DUB",
                    onClick = { selectedCategory = "AUDIO_DUB" },
                    label = { Text("Audio Latino") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TagCyan.copy(alpha = 0.3f),
                        selectedLabelColor = TagCyan
                    ),
                    modifier = Modifier.testTag("filter_audio")
                )
                FilterChip(
                    selected = selectedCategory == "TEXTURES",
                    onClick = { selectedCategory = "TEXTURES" },
                    label = { Text("Texturas HD") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TagGold.copy(alpha = 0.3f),
                        selectedLabelColor = TagGold
                    ),
                    modifier = Modifier.testTag("filter_textures")
                )
            }
        }

        // 3. DLC Items List
        items(filteredDlcs, key = { it.id }) { dlc ->
            DlcCardItem(
                dlc = dlc,
                onToggle = { isEnabled -> viewModel.toggleDlc(dlc.id, isEnabled) },
                onDelete = { dlcToDelete = dlc }
            )
        }
    }

    // Add DLC Dialog
    if (showAddDialog) {
        AddDlcDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, author, cat, desc, size ->
                viewModel.addCustomDlc(name, author, cat, desc, size)
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (dlcToDelete != null) {
        AlertDialog(
            onDismissRequest = { dlcToDelete = null },
            title = { Text("¿Eliminar DLC?") },
            text = {
                Text(
                    "Se desinstalará permanentemente '${dlcToDelete!!.name}' (${dlcToDelete!!.sizeBytes / 1_000_000} MB). Esta acción no afectará tu archivo ISO base."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDlc(dlcToDelete!!.id)
                        dlcToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { dlcToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DlcCardItem(
    dlc: DlcExpansionEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dlc_item_${dlc.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (dlc.category) {
                                "ROSTER" -> TagOrange.copy(alpha = 0.2f)
                                "AUDIO_DUB" -> TagCyan.copy(alpha = 0.2f)
                                "TEXTURES" -> TagGold.copy(alpha = 0.2f)
                                else -> TagPurple.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = dlc.category,
                                color = when (dlc.category) {
                                    "ROSTER" -> TagOrange
                                    "AUDIO_DUB" -> TagCyan
                                    "TEXTURES" -> TagGold
                                    else -> TagPurple
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (dlc.isCorePck1) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusSuccess.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PCK1 CORE",
                                    color = StatusSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = dlc.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Por: ${dlc.author} • ${dlc.version}",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }

                // Toggle Switch
                Switch(
                    checked = dlc.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TagPurple,
                        checkedTrackColor = TagPurple.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("switch_dlc_${dlc.id}")
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = dlc.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dlc.sizeBytes / 1_000_000} MB",
                        color = TagGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${dlc.itemsIncludedCount} elementos",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_delete_dlc_${dlc.id}")
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = StatusError.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddDlcDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, author: String, category: String, desc: String, size: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ROSTER") }
    var description by remember { mutableStateOf("") }
    var sizeMb by remember { mutableStateOf("120") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Instalar Nuevo DLC / Expansión") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la expansión") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor / Creador") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ROSTER", "AUDIO_DUB", "TEXTURES", "STORY_MODE").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.take(6), fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (name.isBlank()) "Expansión Personalizada" else name
                    val finalAuthor = if (author.isBlank()) "Comunidad" else author
                    val sizeVal = (sizeMb.toLongOrNull() ?: 120L) * 1_000_000L
                    onConfirm(finalName, finalAuthor, category, description, sizeVal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TagPurple)
            ) {
                Text("Instalar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
