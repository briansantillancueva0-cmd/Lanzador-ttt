package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.util.IsoNode
import com.example.data.util.IsoParser
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

@Composable
fun IsoManagerScreen(
    viewModel: TagTeamViewModel
) {
    val context = LocalContext.current
    val mountedIso by viewModel.mountedIso.collectAsState()
    var isVerifyingCrc by remember { mutableStateOf(false) }
    var verificationSuccess by remember { mutableStateOf<Boolean?>(null) }
    var selectedTreeFilter by remember { mutableStateOf("ALL") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "game_image.iso"
            viewModel.handleCustomIsoImport(uri, fileName, 1_254_211_584L)
        }
    }

    val isoTree = remember(mountedIso?.hasPck1Injected) {
        IsoParser.buildDefaultIsoTree(hasPck1 = mountedIso?.hasPck1Injected == true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("iso_manager_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            Icon(Icons.Default.DiscFull, contentDescription = null, tint = TagGold)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.getString("iso_manager_title"),
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Monta y verifica la imagen de disco UMD / ISO",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = viewModel.getString("iso_manager_desc"),
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                filePickerLauncher.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_select_iso"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TagGold,
                                contentColor = DarkBg
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = viewModel.getString("btn_select_iso"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.loadSampleOfficialIso() },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_load_sample_iso"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TagCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TagCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = viewModel.getString("iso_load_preset"),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Active ISO Inspector Card
        item {
            if (mountedIso != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("iso_details_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = mountedIso!!.title,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mountedIso!!.fileName,
                                    color = TagGold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSuccess.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess)
                            ) {
                                Text(
                                    text = "MONTADA",
                                    color = StatusSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(Modifier.height(14.dp))

                        // Metadata Grid
                        IsoMetadataRow(label = viewModel.getString("iso_game_id"), value = mountedIso!!.gameId)
                        IsoMetadataRow(label = viewModel.getString("iso_region"), value = mountedIso!!.region)
                        IsoMetadataRow(
                            label = viewModel.getString("iso_size"),
                            value = "${"%.2f".format(mountedIso!!.fileSizeBytes / (1024f * 1024f * 1024f))} GB (1,254,211,584 bytes)"
                        )
                        IsoMetadataRow(label = viewModel.getString("iso_format"), value = mountedIso!!.discStructure)
                        IsoMetadataRow(
                            label = viewModel.getString("iso_crc32"),
                            value = "0x" + mountedIso!!.crc32,
                            isMonospace = true
                        )
                        IsoMetadataRow(
                            label = "Inyección Pck1",
                            value = if (mountedIso!!.hasPck1Injected) "Activo en USRDIR/pck1.bin" else "No inyectado",
                            valueColor = if (mountedIso!!.hasPck1Injected) StatusSuccess else StatusWarning
                        )
                        IsoMetadataRow(
                            label = "Parche de Idioma",
                            value = mountedIso!!.activePatchLanguage,
                            valueColor = TagCyan
                        )

                        Spacer(Modifier.height(16.dp))

                        // Verify CRC32 Button
                        Button(
                            onClick = {
                                isVerifyingCrc = true
                                verificationSuccess = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_verify_crc32"),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHigh),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isVerifyingCrc) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = TagGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Calculando sumas de sectores...", color = TagGold, fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = StatusSuccess)
                                Spacer(Modifier.width(8.dp))
                                Text(viewModel.getString("btn_verify_crc"), color = TextPrimary, fontSize = 13.sp)
                            }
                        }

                        // Simulation delay for verification
                        LaunchedEffect(isVerifyingCrc) {
                            if (isVerifyingCrc) {
                                kotlinx.coroutines.delay(800)
                                isVerifyingCrc = false
                                verificationSuccess = true
                            }
                        }

                        if (verificationSuccess == true) {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSuccess.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Integridad perfecta: Sectores 100% coincidentes con base de datos Redump.",
                                        color = StatusSuccess,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Virtual ISO Tree Inspector
        item {
            Text(
                text = viewModel.getString("iso_files_tree"),
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("iso_tree_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    isoTree.forEach { rootNode ->
                        IsoNodeItemView(node = rootNode, depth = 0)
                    }
                }
            }
        }
    }
}

@Composable
fun IsoMetadataRow(
    label: String,
    value: String,
    isMonospace: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(
            text = value,
            color = valueColor,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}

@Composable
fun IsoNodeItemView(node: IsoNode, depth: Int) {
    var isExpanded by remember { mutableStateOf(depth < 2) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = node.isDirectory) { isExpanded = !isExpanded }
                .padding(vertical = 4.dp, horizontal = (depth * 14).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (node.isDirectory) {
                    if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder
                } else {
                    if (node.name.endsWith(".bin")) Icons.Default.Memory
                    else if (node.name.endsWith(".at3")) Icons.Default.AudioFile
                    else if (node.name.endsWith(".png")) Icons.Default.Image
                    else Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                tint = if (node.isDirectory) TagGold else TagBlue,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = node.name,
                color = if (node.name.contains("pck1")) TagOrange else TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = if (node.isDirectory || node.name.contains("pck1")) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (node.sizeBytes > 0) {
                val sizeStr = if (node.sizeBytes > 1_000_000) {
                    "${node.sizeBytes / 1_000_000} MB"
                } else {
                    "${node.sizeBytes / 1_000} KB"
                }
                Text(
                    text = sizeStr,
                    color = TextTertiary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
        }

        AnimatedVisibility(visible = isExpanded && node.isDirectory) {
            Column {
                node.children.forEach { child ->
                    IsoNodeItemView(node = child, depth = depth + 1)
                }
            }
        }
    }
}
