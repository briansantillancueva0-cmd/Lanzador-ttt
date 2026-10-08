package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TagTeamViewModel

data class NavTabItem(
    val id: String,
    val labelKey: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: TagTeamViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val mountedIso by viewModel.mountedIso.collectAsState()

    // BackHandler: returns to dashboard if on another tab
    BackHandler(enabled = currentTab != "dashboard") {
        viewModel.setTab("dashboard")
    }

    val navTabs = listOf(
        NavTabItem("dashboard", "tab_dashboard", Icons.Default.Home),
        NavTabItem("iso", "tab_iso", Icons.Default.DiscFull),
        NavTabItem("dlc", "tab_dlc", Icons.Default.Extension),
        NavTabItem("pck1", "tab_pck1", Icons.Default.PersonSearch, badge = "NEW"),
        NavTabItem("psp", "tab_psp", Icons.Default.SportsEsports),
        NavTabItem("saves", "tab_saves", Icons.Default.Save),
        NavTabItem("cloud", "tab_cloud", Icons.Default.CloudSync),
        NavTabItem("settings", "tab_settings", Icons.Default.Settings)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tag_team_logo),
                            contentDescription = "Tag Team Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, TagGold, RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = "Tag Team Port Hub",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (mountedIso != null) "ISO: ${mountedIso!!.gameId}" else "Sin ISO",
                                color = if (mountedIso != null) TagGold else TextTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    // Cloud Status Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceHigh,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = TagCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusSuccess)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = currentLang.flagEmoji,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                navTabs.forEach { tab ->
                    val isSelected = currentTab == tab.id
                    val label = viewModel.getString(tab.labelKey)

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab.id) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab.badge != null) {
                                        Badge(containerColor = TagOrange) {
                                            Text(tab.badge, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                maxLines = 1,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBg,
                            selectedTextColor = TagGold,
                            indicatorColor = TagGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.id}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                "dashboard" -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { tab -> viewModel.setTab(tab) }
                )
                "iso" -> IsoManagerScreen(viewModel = viewModel)
                "dlc" -> DlcManagerScreen(viewModel = viewModel)
                "pck1" -> Pck1MenuScreen(
                    viewModel = viewModel,
                    onNavigateToArena = { viewModel.setTab("psp") }
                )
                "psp" -> PspControlsScreen(viewModel = viewModel)
                "saves" -> SaveManagerScreen(viewModel = viewModel)
                "cloud" -> CloudSyncScreen(viewModel = viewModel)
                "settings" -> SettingsScreen(viewModel = viewModel)
                else -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { tab -> viewModel.setTab(tab) }
                )
            }
        }
    }
}
