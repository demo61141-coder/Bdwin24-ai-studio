package com.dkwin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dkwin.app.ui.ActiveGameScreen
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.MainTab
import com.dkwin.app.ui.games.CrashGamesScreen
import com.dkwin.app.ui.games.MinesAndMiniGamesScreen
import com.dkwin.app.ui.games.SlotsGameScreen
import com.dkwin.app.ui.games.SportsMultiBetScreen
import com.dkwin.app.ui.games.ThimblesGameScreen
import com.dkwin.app.ui.games.WingoGameScreen
import com.dkwin.app.ui.screens.ActivityTabScreen
import com.dkwin.app.ui.screens.AdminPanelScreen
import com.dkwin.app.ui.screens.AuthModalDialog
import com.dkwin.app.ui.screens.DepositModalDialog
import com.dkwin.app.ui.screens.HomeScreen
import com.dkwin.app.ui.screens.MineProfileTabScreen
import com.dkwin.app.ui.screens.PromoReferralTabScreen
import com.dkwin.app.ui.screens.SupportChatModalDialog
import com.dkwin.app.ui.screens.WalletTabScreen
import com.dkwin.app.ui.screens.WithdrawModalDialog
import com.dkwin.app.ui.theme.DkBackground
import com.dkwin.app.ui.theme.DkCrimson
import com.dkwin.app.ui.theme.DkEmerald
import com.dkwin.app.ui.theme.DkGold
import com.dkwin.app.ui.theme.DkSky
import com.dkwin.app.ui.theme.DkSurface
import com.dkwin.app.ui.theme.DkSurfaceVariant
import com.dkwin.app.ui.theme.DkTextSecondary
import com.dkwin.app.ui.theme.DkwinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DkwinTheme {
                DkwinAppShell()
            }
        }
    }
}

@Composable
fun DkwinAppShell(vm: DkwinViewModel = viewModel()) {
    val user by vm.currentUser.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val activeTab by vm.activeTab.collectAsStateWithLifecycle()
    val activeGame by vm.activeGame.collectAsStateWithLifecycle()
    val showAuth by vm.showAuthSheet.collectAsStateWithLifecycle()
    val showDeposit by vm.showDepositSheet.collectAsStateWithLifecycle()
    val showWithdraw by vm.showWithdrawSheet.collectAsStateWithLifecycle()
    val showSupport by vm.showSupportChatSheet.collectAsStateWithLifecycle()
    val toast by vm.toast.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DkBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Scaffold(
            containerColor = DkBackground,
            topBar = {
                if (activeGame == ActiveGameScreen.NONE) {
                    DkwinTopBar(
                        vm = vm,
                        username = user?.username,
                        balance = user?.balance ?: 0.0
                    )
                }
            },
            bottomBar = {
                DkwinBottomNav(
                    activeTab = activeTab,
                    onSelectTab = { vm.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeGame) {
                    ActiveGameScreen.WINGO -> WingoGameScreen(vm = vm, user = user, settings = settings)
                    ActiveGameScreen.CRASH -> CrashGamesScreen(vm = vm, user = user, settings = settings)
                    ActiveGameScreen.THIMBLES -> ThimblesGameScreen(vm = vm, user = user, settings = settings)
                    ActiveGameScreen.SLOTS -> SlotsGameScreen(vm = vm, user = user, settings = settings)
                    ActiveGameScreen.SPORTS -> SportsMultiBetScreen(vm = vm, user = user, settings = settings)
                    ActiveGameScreen.MINES -> MinesAndMiniGamesScreen(vm = vm, user = user, settings = settings, initialMode = "mines")
                    ActiveGameScreen.DICE -> MinesAndMiniGamesScreen(vm = vm, user = user, settings = settings, initialMode = "dice")
                    ActiveGameScreen.LIVE_CASINO -> MinesAndMiniGamesScreen(vm = vm, user = user, settings = settings, initialMode = "live")
                    ActiveGameScreen.NONE -> {
                        when (activeTab) {
                            MainTab.HOME -> HomeScreen(vm = vm, user = user, settings = settings)
                            MainTab.ACTIVITY -> ActivityTabScreen(vm = vm, user = user, settings = settings)
                            MainTab.WALLET -> WalletTabScreen(vm = vm, user = user, settings = settings)
                            MainTab.PROMO -> PromoReferralTabScreen(vm = vm, user = user, settings = settings)
                            MainTab.MINE -> MineProfileTabScreen(vm = vm, user = user, settings = settings)
                            MainTab.ADMIN -> AdminPanelScreen(vm = vm, settings = settings)
                        }
                    }
                }
            }
        }

        // Global Modals
        if (showAuth) {
            AuthModalDialog(vm = vm, onDismiss = { vm.setShowAuthSheet(false) })
        }
        if (showDeposit) {
            DepositModalDialog(vm = vm, settings = settings, onDismiss = { vm.setShowDepositSheet(false) })
        }
        if (showWithdraw) {
            WithdrawModalDialog(vm = vm, user = user, settings = settings, onDismiss = { vm.setShowWithdrawSheet(false) })
        }
        if (showSupport) {
            SupportChatModalDialog(vm = vm, settings = settings, onDismiss = { vm.setShowSupportChatSheet(false) })
        }

        // Toast Banner
        toast?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 62.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (msg.isError) Color(0xFF7F1D1D) else Color(0xFF064E3B))
                    .border(
                        1.5.dp,
                        if (msg.isError) DkCrimson else DkEmerald,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DkwinTopBar(
    vm: DkwinViewModel,
    username: String?,
    balance: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DkSurface)
            .border(0.5.dp, DkGold.copy(alpha = 0.25f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { vm.selectTab(MainTab.HOME) }
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(DkGold, Color(0xFFD97706)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DK",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "DKWIN",
                    color = DkGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "CASINO & 1X SPORTS",
                    color = DkTextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Balance + Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (username == null) {
                Button(
                    onClick = { vm.setShowAuthSheet(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("top_login_register_btn")
                ) {
                    Text("লগইন / রেজিস্টার", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DkBackground)
                        .border(1.dp, DkGold.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .clickable { vm.selectTab(MainTab.WALLET) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "৳${String.format("%.2f", balance)}",
                        color = DkGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { vm.setShowDepositSheet(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("top_deposit_btn")
                ) {
                    Text("+ ডিপোজিট", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            IconButton(
                onClick = { vm.setShowSupportChatSheet(true) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DkSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Support",
                    tint = DkSky,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DkwinBottomNav(
    activeTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = DkSurface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(MainTab.HOME, "হোম", Icons.Default.Home),
            Triple(MainTab.ACTIVITY, "বোনাস", Icons.Default.CardGiftcard),
            Triple(MainTab.WALLET, "ওয়ালেট", Icons.Default.AccountBalanceWallet),
            Triple(MainTab.PROMO, "রেফার", Icons.Default.Share),
            Triple(MainTab.MINE, "প্রোফাইল", Icons.Default.Person),
            Triple(MainTab.ADMIN, "এডমিন", Icons.Default.AdminPanelSettings)
        )
        items.forEach { (tab, label, icon) ->
            val selected = activeTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = DkGold,
                    indicatorColor = DkGold,
                    unselectedIconColor = DkTextSecondary,
                    unselectedTextColor = DkTextSecondary
                ),
                modifier = Modifier.testTag("bottom_nav_${tab.name.lowercase()}")
            )
        }
    }
}
