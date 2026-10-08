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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CloudSyncEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSyncScreen(
    viewModel: TagTeamViewModel
) {
    val isSyncing by viewModel.isSyncing.collectAsState()
    val autoSync by viewModel.autoSyncEnabled.collectAsState()
    val provider by viewModel.selectedCloudProvider.collectAsState()
    val syncHistory by viewModel.syncHistory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cloud_sync_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Card with Live Status
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(StatusSuccess.copy(alpha = 0.15f))
                                .border(1.dp, StatusSuccess, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = StatusSuccess,
                                    strokeWidth = 2.5.dp
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
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StatusSuccess.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (isSyncing) "SINCRONIZANDO..." else "ACTIVO",
                                        color = StatusSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Sincronización bidireccional automática de partidas y DLCs",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = viewModel.getString("cloud_desc"),
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerCloudSync() },
                        enabled = !isSyncing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_cloud_sync_now"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TagCyan,
                            contentColor = DarkBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = DarkBg,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Subiendo datos cifrados...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = viewModel.getString("btn_sync_now"),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Auto-Sync Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.getString("auto_sync_toggle"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = viewModel.getString("auto_sync_desc"),
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = autoSync,
                        onCheckedChange = { viewModel.toggleAutoSync(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TagCyan,
                            checkedTrackColor = TagCyan.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("switch_auto_sync")
                    )
                }
            }
        }

        // 3. Provider Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.getString("cloud_provider"),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))

                    listOf(
                        "Google Drive Cloud Backup",
                        "Firebase Cloud Vault",
                        "Almacenamiento Local Cifrado P2P"
                    ).forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = provider == p,
                                onClick = { viewModel.setCloudProvider(p) },
                                colors = RadioButtonDefaults.colors(selectedColor = TagCyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = p, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 4. Sync History Log
        item {
            Text(
                text = viewModel.getString("cloud_history_title") + " (${syncHistory.size})",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(syncHistory, key = { it.id }) { record ->
            SyncHistoryItem(record = record)
        }
    }
}

@Composable
fun SyncHistoryItem(record: CloudSyncEntity) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }
    val formattedDate = remember(record.timestamp) { dateFormat.format(Date(record.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sync_record_${record.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = record.actionType,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = formattedDate,
                    color = TextTertiary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = record.details,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.provider,
                    color = TagCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${record.bytesTransferred / 1024} KB transferidos",
                    color = TagGold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}
