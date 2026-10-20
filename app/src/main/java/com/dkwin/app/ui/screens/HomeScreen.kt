package com.dkwin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.UserEntity
import com.dkwin.app.ui.ActiveGameScreen
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.MainTab
import com.dkwin.app.ui.theme.DkCrimson
import com.dkwin.app.ui.theme.DkEmerald
import com.dkwin.app.ui.theme.DkGold
import com.dkwin.app.ui.theme.DkSky
import com.dkwin.app.ui.theme.DkSurface
import com.dkwin.app.ui.theme.DkSurfaceVariant
import com.dkwin.app.ui.theme.DkTextSecondary
import com.dkwin.app.ui.theme.DkViolet

data class GameCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val provider: String, // JILI, SPRIBE, DKWIN, 1XBET, PG
    val category: String, // all, lottery, crash, thimbles, jili, slots, sports, live
    val badge: String,
    val emoji: String,
    val gradientColors: List<Color>,
    val playersCount: String,
    val onLaunch: () -> Unit
)

@Composable
fun HomeScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    var selectedCategory by remember { mutableStateOf("all") }

    val games = remember(vm) {
        listOf(
            // Win Go Color Prediction
            GameCardItem(
                id = "wingo_1m",
                title = "Win Go কালার গেম",
                subtitle = "30s / 1m / 3m / 5m · Big/Small & Green/Red",
                provider = "DKWIN ORIGINAL",
                category = "lottery",
                badge = "HOT #1",
                emoji = "🎯",
                gradientColors = listOf(Color(0xFF059669), Color(0xFF0F172A)),
                playersCount = "18,420",
                onLaunch = { vm.openGame(ActiveGameScreen.WINGO) }
            ),
            // 4 Crash Plane Games
            GameCardItem(
                id = "crash_aviator",
                title = "Aviator Crash",
                subtitle = "Spribe লাল প্লেন · 100x ক্যাশআউট",
                provider = "SPRIBE",
                category = "crash",
                badge = "SPRIBE HIT",
                emoji = "✈️",
                gradientColors = listOf(Color(0xFFE11D48), Color(0xFF1E1B4B)),
                playersCount = "24,910",
                onLaunch = { vm.openGame(ActiveGameScreen.CRASH, "aviator") }
            ),
            GameCardItem(
                id = "crash_jetx",
                title = "JetX Fighter Crash",
                subtitle = "সুপারসনিক ফাইটার জেট মাল্টিপ্লায়ার",
                provider = "SPRIBE",
                category = "crash",
                badge = "JET 200X",
                emoji = "🛩️",
                gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0F172A)),
                playersCount = "12,350",
                onLaunch = { vm.openGame(ActiveGameScreen.CRASH, "jetx") }
            ),
            GameCardItem(
                id = "crash_spacexy",
                title = "SpaceXY Rocket",
                subtitle = "মহাকাশ রকেট ক্র্যাশ গেম",
                provider = "BGAMING",
                category = "crash",
                badge = "ROCKET",
                emoji = "🚀",
                gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF0F172A)),
                playersCount = "9,880",
                onLaunch = { vm.openGame(ActiveGameScreen.CRASH, "spacexy") }
            ),
            GameCardItem(
                id = "crash_cricketx",
                title = "Crash Cricket X",
                subtitle = "সিক্সার বল ক্র্যাশ ওভার বাউন্ডারি",
                provider = "JILI / SMART",
                category = "crash",
                badge = "CRICKET",
                emoji = "🏏",
                gradientColors = listOf(Color(0xFFD97706), Color(0xFF1E293B)),
                playersCount = "15,200",
                onLaunch = { vm.openGame(ActiveGameScreen.CRASH, "cricketx") }
            ),
            // Thimbles Game
            GameCardItem(
                id = "thimbles_game",
                title = "Thimbles (থাম্বিল গেম)",
                subtitle = "৩ কাপের নিচে লুকানো বল খুঁজুন · 2.88x",
                provider = "1XGAMES",
                category = "thimbles",
                badge = "NEW HIT",
                emoji = "🏆",
                gradientColors = listOf(Color(0xFFB45309), Color(0xFF0F172A)),
                playersCount = "11,640",
                onLaunch = { vm.openGame(ActiveGameScreen.THIMBLES) }
            ),
            // 1xBet Multi Sports & Cricket
            GameCardItem(
                id = "sports_multi",
                title = "1xBet Cricket & Sports",
                subtitle = "BPL, ICC, IPL সিঙ্গেল ও মাল্টি বেট স্লিপ",
                provider = "1XBET LIVE",
                category = "sports",
                badge = "MULTI ODDS",
                emoji = "🏟️",
                gradientColors = listOf(Color(0xFF15803D), Color(0xFF090D16)),
                playersCount = "21,500",
                onLaunch = { vm.openGame(ActiveGameScreen.SPORTS) }
            ),
            // JILI & Popular Slots
            GameCardItem(
                id = "jili_super_ace",
                title = "JILI Super Ace",
                subtitle = "গোল্ডেন কার্ড ও পোকার কম্বো স্লট",
                provider = "JILI",
                category = "jili",
                badge = "JILI #1",
                emoji = "🃏",
                gradientColors = listOf(Color(0xFF1D4ED8), Color(0xFF0F172A)),
                playersCount = "19,300",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "jili_super_ace") }
            ),
            GameCardItem(
                id = "jili_golden_empire",
                title = "JILI Golden Empire",
                subtitle = "গোল্ডেন এম্পায়ার মেগাওয়েজ ২০০০x",
                provider = "JILI",
                category = "jili",
                badge = "JILI HOT",
                emoji = "👑",
                gradientColors = listOf(Color(0xFFCA8A04), Color(0xFF1E1B4B)),
                playersCount = "10,450",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "jili_golden_empire") }
            ),
            GameCardItem(
                id = "jili_fortune_gems",
                title = "JILI Fortune Gems 2",
                subtitle = "লাকি হুইল ও ১৫x জেম মাল্টিপ্লায়ার",
                provider = "JILI",
                category = "jili",
                badge = "GEMS 15X",
                emoji = "💎",
                gradientColors = listOf(Color(0xFF9333EA), Color(0xFF0F172A)),
                playersCount = "13,120",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "jili_fortune_gems") }
            ),
            GameCardItem(
                id = "jili_boxing_king",
                title = "JILI Boxing King",
                subtitle = "বক্সিং রিং ফ্রি স্পিন ও কম্বো হিট",
                provider = "JILI",
                category = "jili",
                badge = "KNOCKOUT",
                emoji = "🥊",
                gradientColors = listOf(Color(0xFFDC2626), Color(0xFF0F172A)),
                playersCount = "8,740",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "jili_boxing_king") }
            ),
            GameCardItem(
                id = "fortune_tiger",
                title = "PG Fortune Tiger",
                subtitle = "গোল্ডেন টাইগার ২৫০০x জ্যাকপট স্লট",
                provider = "PG SOFT",
                category = "slots",
                badge = "2500X",
                emoji = "🐯",
                gradientColors = listOf(Color(0xFFEA580C), Color(0xFF0F172A)),
                playersCount = "14,200",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "fortune_tiger") }
            ),
            GameCardItem(
                id = "gates_of_olympus",
                title = "Gates of Olympus",
                subtitle = "জিউস লাইটনিং ৫০০x মাল্টিপ্লায়ার",
                provider = "PRAGMATIC",
                category = "slots",
                badge = "500X",
                emoji = "⚡",
                gradientColors = listOf(Color(0xFF6D28D9), Color(0xFF0F172A)),
                playersCount = "11,900",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "gates_of_olympus") }
            ),
            GameCardItem(
                id = "sweet_bonanza",
                title = "Sweet Bonanza",
                subtitle = "ক্যান্ডি বম্ব ১০০x ফ্রি স্পিন",
                provider = "PRAGMATIC",
                category = "slots",
                badge = "CANDY",
                emoji = "🍭",
                gradientColors = listOf(Color(0xFFDB2777), Color(0xFF0F172A)),
                playersCount = "9,400",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "sweet_bonanza") }
            ),
            GameCardItem(
                id = "crazy_777",
                title = "JILI Crazy 777",
                subtitle = "ট্রিপল সেভেন ক্লাসিক রিল",
                provider = "JILI",
                category = "jili",
                badge = "777 WIN",
                emoji = "7️⃣",
                gradientColors = listOf(Color(0xFFB91C1C), Color(0xFF0F172A)),
                playersCount = "7,650",
                onLaunch = { vm.openGame(ActiveGameScreen.SLOTS, "crazy_777") }
            ),
            // Spribe Mines, Dice, Live Casino
            GameCardItem(
                id = "spribe_mines",
                title = "Spribe Mines",
                subtitle = "৫x৫ গ্রিড মাইনসুইপার ও স্টার ক্যাশআউট",
                provider = "SPRIBE",
                category = "thimbles",
                badge = "99% RTP",
                emoji = "💣",
                gradientColors = listOf(Color(0xFF0D9488), Color(0xFF0F172A)),
                playersCount = "16,800",
                onLaunch = { vm.openGame(ActiveGameScreen.MINES) }
            ),
            GameCardItem(
                id = "spribe_dice",
                title = "Spribe Dice Hi-Lo",
                subtitle = "ডাইস ওভার/আন্ডার ইনস্ট্যান্ট পেআউট",
                provider = "SPRIBE",
                category = "thimbles",
                badge = "INSTANT",
                emoji = "🎲",
                gradientColors = listOf(Color(0xFF4F46E5), Color(0xFF0F172A)),
                playersCount = "6,920",
                onLaunch = { vm.openGame(ActiveGameScreen.DICE) }
            ),
            GameCardItem(
                id = "live_dragon_tiger",
                title = "Dragon vs Tiger & Roulette",
                subtitle = "লাইভ ক্যাসিনো টেবিল ও লাকি হুইল",
                provider = "EVOLUTION",
                category = "live",
                badge = "LIVE TABLE",
                emoji = "🐉",
                gradientColors = listOf(Color(0xFF991B1B), Color(0xFF0F172A)),
                playersCount = "10,300",
                onLaunch = { vm.openGame(ActiveGameScreen.LIVE_CASINO) }
            )
        )
    }

    val filteredGames = remember(selectedCategory, games) {
        if (selectedCategory == "all") games
        else games.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Site Notice Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Notice",
                    tint = DkGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = settings.siteNotice,
                    color = Color(0xFFFDE68A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 2. Hero Promotional Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF78350F), Color(0xFF1E1B4B), Color(0xFF064E3B))
                        )
                    )
                    .border(1.dp, DkGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(DkGold)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DKWIN OFFICIAL BD",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "মিনিমাম বেট ৳${settings.minBet.toInt()} · ডিপোজিট ৳${settings.minDeposit.toInt()}",
                            color = DkEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Win Go, 4x Crash Plane, Thimbles, JILI Slots ও 1xBet ক্রিকেট মাল্টি!",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "বিকাশ, নগদ ও রকেটে +${settings.depositBonusPercent.toInt()}% বোনাস এবং রেফারে +${settings.referralDepositCommissionPercent}% কমিশন!",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { vm.openGame(ActiveGameScreen.WINGO) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DkGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hero_play_wingo_button")
                        ) {
                            Text("🎯 Win Go খেলুন", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Button(
                            onClick = { vm.openGame(ActiveGameScreen.SPORTS) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DkEmerald,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hero_play_sports_button")
                        ) {
                            Text("🏏 1xBet ক্রিকেট মাল্টি", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 3. Quick Action Wallet & Support Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "ডিপোজিট",
                    subtitle = "মিনিমাম ৳${settings.minDeposit.toInt()}",
                    accent = DkEmerald,
                    icon = Icons.Default.AccountBalanceWallet,
                    tag = "quick_deposit_btn",
                    onClick = { vm.setShowDepositSheet(true) }
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "উইথড্র",
                    subtitle = "২-১০ মি. পেমেন্ট",
                    accent = DkGold,
                    icon = Icons.Default.ArrowUpward,
                    tag = "quick_withdraw_btn",
                    onClick = { vm.setShowWithdrawSheet(true) }
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "গিফট ও বোনাস",
                    subtitle = "ডেইলি রিওয়ার্ড",
                    accent = DkViolet,
                    icon = Icons.Default.CardGiftcard,
                    tag = "quick_activity_btn",
                    onClick = { vm.selectTab(MainTab.ACTIVITY) }
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "সাপোর্ট চ্যাট",
                    subtitle = "টেলিগ্রাম ও লাইভ",
                    accent = DkSky,
                    icon = Icons.Default.SupportAgent,
                    tag = "quick_support_btn",
                    onClick = { vm.setShowSupportChatSheet(true) }
                )
            }
        }

        // 4. Category Filter Bar
        item {
            val categories = listOf(
                "all" to "🔥 সব গেমস (${games.size})",
                "lottery" to "🎯 Win Go লটারী",
                "crash" to "✈️ ক্র্যাশ প্লেন (4)",
                "thimbles" to "🏆 থাম্বিল ও মিনি",
                "jili" to "🃏 JILI স্লটস",
                "slots" to "🎰 PG / Pragmatic",
                "sports" to "🏏 1xBet ক্রিকেট মাল্টি",
                "live" to "🐉 লাইভ ক্যাসিনো"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (key, label) ->
                    val selected = selectedCategory == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) DkGold else DkSurface)
                            .border(
                                1.dp,
                                if (selected) DkGold else DkSurfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = key }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("category_chip_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 5. 2-Column Game Cards Grid
        val rows = filteredGames.chunked(2)
        items(rows.size) { rowIndex ->
            val pair = rows[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { game ->
                    GameGridCard(
                        game = game,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 6. Direct Admin Control Panel Banner at bottom for owner convenience
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DkSurface)
                    .border(1.dp, DkCrimson.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { vm.selectTab(MainTab.ADMIN) }
                    .padding(14.dp)
                    .testTag("open_admin_panel_banner"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = DkGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DKWIN মাস্টার এডমিন প্যানেল",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Win Go রেজাল্ট কন্ট্রোল, ক্র্যাশ কন্ট্রোল, ডিপোজিট/উইথড্র ও ক্রিকেট ম্যাচ অ্যাড",
                            color = DkTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    text = "প্রবেশ →",
                    color = DkGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DkSurface)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = subtitle,
            color = DkTextSecondary,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun GameGridCard(
    game: GameCardItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(game.gradientColors))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
            .clickable { game.onLaunch() }
            .padding(12.dp)
            .testTag("game_card_${game.id}"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = game.provider,
                        color = DkGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(DkCrimson)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = game.badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(text = game.emoji, fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = game.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = game.subtitle,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp,
            maxLines = 2,
            lineHeight = 13.sp,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = DkEmerald,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = game.playersCount,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DkGold)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "খেলুন",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
