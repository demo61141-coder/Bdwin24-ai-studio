package com.dkwin.app.ui.games

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.UserEntity
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.theme.DkCrimson
import com.dkwin.app.ui.theme.DkEmerald
import com.dkwin.app.ui.theme.DkGold
import com.dkwin.app.ui.theme.DkSky
import com.dkwin.app.ui.theme.DkSurface
import com.dkwin.app.ui.theme.DkSurfaceVariant
import com.dkwin.app.ui.theme.DkTextSecondary
import com.dkwin.app.ui.theme.DkViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class SlotThemeInfo(
    val id: String,
    val title: String,
    val provider: String,
    val icon: String,
    val symbols: List<String>,
    val colors: List<Color>
)

@Composable
fun SlotsGameScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    BackHandler { vm.closeGame() }

    val selectedSlotId by vm.selectedSlotId.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val slotThemes = remember {
        listOf(
            SlotThemeInfo(
                id = "jili_super_ace",
                title = "JILI Super Ace",
                provider = "JILI",
                icon = "🃏",
                symbols = listOf("🃏", "♠️", "♥️", "♦️", "♣️", "👑", "💎"),
                colors = listOf(Color(0xFF1D4ED8), Color(0xFF0F172A))
            ),
            SlotThemeInfo(
                id = "jili_golden_empire",
                title = "JILI Golden Empire",
                provider = "JILI",
                icon = "👑",
                symbols = listOf("👑", "🏛️", "🪙", "🦅", "💎", "🔱"),
                colors = listOf(Color(0xFFB45309), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "jili_fortune_gems",
                title = "JILI Fortune Gems 2",
                provider = "JILI",
                icon = "💎",
                symbols = listOf("💎", "🔮", "🧿", "💍", "⭐", "🪙"),
                colors = listOf(Color(0xFF7E22CE), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "jili_boxing_king",
                title = "JILI Boxing King",
                provider = "JILI",
                icon = "🥊",
                symbols = listOf("🥊", "🏆", "🔔", "🔥", "💪", "⭐"),
                colors = listOf(Color(0xFFB91C1C), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "fortune_tiger",
                title = "PG Fortune Tiger",
                provider = "PG SOFT",
                icon = "🐯",
                symbols = listOf("🐯", "🧧", "🍊", "🪙", "💎", "🎆"),
                colors = listOf(Color(0xFFEA580C), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "gates_of_olympus",
                title = "Gates of Olympus",
                provider = "PRAGMATIC",
                icon = "⚡",
                symbols = listOf("⚡", "👑", "⏳", "💍", "🏆", "💎"),
                colors = listOf(Color(0xFF4C1D95), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "sweet_bonanza",
                title = "Sweet Bonanza",
                provider = "PRAGMATIC",
                icon = "🍭",
                symbols = listOf("🍭", "🍬", "💣", "🍓", "🍇", "🍉"),
                colors = listOf(Color(0xFFBE185D), Color(0xFF111827))
            ),
            SlotThemeInfo(
                id = "crazy_777",
                title = "JILI Crazy 777",
                provider = "JILI",
                icon = "7️⃣",
                symbols = listOf("7️⃣", "🍒", "🔔", "💎", "🪙", "⭐"),
                colors = listOf(Color(0xFF991B1B), Color(0xFF111827))
            )
        )
    }

    val currentTheme = slotThemes.firstOrNull { it.id == selectedSlotId } ?: slotThemes.first()

    var betInput by remember { mutableStateOf("10") }
    var spinning by remember { mutableStateOf(false) }
    var lastWin by remember { mutableDoubleStateOf(0.0) }
    var lastMult by remember { mutableDoubleStateOf(0.0) }
    var grid by remember(currentTheme.id) {
        mutableStateOf(
            listOf(
                listOf(currentTheme.symbols[0], currentTheme.symbols[1], currentTheme.symbols[2]),
                listOf(currentTheme.symbols[0], currentTheme.symbols[0], currentTheme.symbols[0]),
                listOf(currentTheme.symbols[3], currentTheme.symbols[4], currentTheme.symbols[5])
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("slots_game_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DkSurface)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { vm.closeGame() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currentTheme.icon} ${currentTheme.title}",
                        color = DkGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${currentTheme.provider} অফিশিয়াল স্লট · মিনিমাম বেট ৳১",
                        color = DkTextSecondary,
                        fontSize = 10.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DkGold.copy(alpha = 0.15f))
                        .border(1.dp, DkGold, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "৳${String.format("%.2f", user?.balance ?: 0.0)}",
                        color = DkGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Theme Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slotThemes.forEach { t ->
                    val active = t.id == currentTheme.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) DkGold else DkSurface)
                            .clickable(enabled = !spinning) { vm.setSlotTheme(t.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${t.icon} ${t.title}",
                            color = if (active) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3x3 Slot Machine Showcase
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(currentTheme.colors))
                    .border(2.dp, DkGold.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentTheme.provider} MEGAWAY 2500X",
                        color = DkGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (lastWin > 0) "WIN: +৳${lastWin.toInt()} (${lastMult}x)" else "SPIN TO WIN!",
                        color = if (lastWin > 0) DkEmerald else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grid.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { sym ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(68.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DkSurface)
                                        .border(1.dp, DkGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = sym, fontSize = 32.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Spin Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 5, 10, 20, 50, 100, 500).forEach { chip ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (betInput == chip.toString()) DkGold else DkSurfaceVariant)
                                .clickable { betInput = chip.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "৳$chip",
                                color = if (betInput == chip.toString()) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = betInput,
                    onValueChange = { betInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("স্পিন বেট এমাউন্ট (৳১+)", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DkGold,
                        unfocusedBorderColor = DkSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (user == null) {
                            vm.setShowAuthSheet(true)
                            return@Button
                        }
                        val amt = (betInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
                        scope.launch {
                            val startRes = vm.repo.startGameBet(
                                userId = user.id,
                                gameType = "slots",
                                gameId = currentTheme.id,
                                betAmount = amt,
                                prediction = currentTheme.title
                            )
                            startRes.onSuccess { bet ->
                                spinning = true
                                lastWin = 0.0
                                repeat(6) {
                                    grid = List(3) { List(3) { currentTheme.symbols.random() } }
                                    delay(110L)
                                }
                                val winChance = settings.slotsWinRatePercent.coerceIn(0, 100)
                                val isWin = Random.nextInt(100) < winChance
                                val mult = if (isWin) listOf(1.8, 2.5, 4.0, 8.0, 15.0).random() else 0.0
                                val winSym = currentTheme.symbols.first()
                                grid = if (isWin) {
                                    listOf(
                                        listOf(currentTheme.symbols.random(), winSym, currentTheme.symbols.random()),
                                        listOf(winSym, winSym, winSym),
                                        listOf(currentTheme.symbols.random(), winSym, currentTheme.symbols.random())
                                    )
                                } else {
                                    List(3) { r ->
                                        List(3) { c ->
                                            currentTheme.symbols[(r * 2 + c + Random.nextInt(1, 4)) % currentTheme.symbols.size]
                                        }
                                    }
                                }
                                spinning = false
                                vm.repo.settleInstantBet(
                                    betId = bet.id,
                                    isWin = isWin,
                                    multiplier = mult,
                                    outcomeText = if (isWin) "${mult}x Combo" else "No Combo"
                                ).onSuccess { payout ->
                                    lastWin = payout
                                    lastMult = mult
                                    if (isWin) {
                                        vm.showToast("অভিনন্দন! স্লট স্পিনে +৳${payout.toInt()} (${mult}x) জিতেছেন!")
                                    }
                                }
                            }.onFailure { err ->
                                vm.showToast(err.message ?: "ব্যর্থ", true)
                                if (user.balance < amt) vm.setShowDepositSheet(true)
                            }
                        }
                    },
                    enabled = !spinning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DkGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("slots_spin_btn")
                ) {
                    Text(
                        text = if (spinning) "🎰 রিল ঘুরছে..." else "🎰 স্পিন করুন (৳${betInput.ifEmpty { "1" }})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MinesAndMiniGamesScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity,
    initialMode: String // mines, dice, live
) {
    BackHandler { vm.closeGame() }

    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(initialMode) }
    var betInput by remember { mutableStateOf("10") }

    // Mines State
    var minesRoundActive by remember { mutableStateOf(false) }
    var minesBetId by remember { mutableStateOf<Int?>(null) }
    var minesStake by remember { mutableDoubleStateOf(10.0) }
    var minesMult by remember { mutableDoubleStateOf(1.0) }
    var revealedCells by remember { mutableStateOf(List(25) { 0 }) } // 0 = hidden, 1 = star, 2 = bomb

    // Dice State
    var diceTarget by remember { mutableFloatStateOf(50f) }
    var lastDiceRoll by remember { mutableIntStateOf(50) }

    // Live Casino State
    var livePick by remember { mutableStateOf("DRAGON") }
    var lastLiveOutcome by remember { mutableStateOf("Dragon 9 vs Tiger 6") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DkSurface)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { vm.closeGame() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("mines" to "💣 Mines", "dice" to "🎲 Dice", "live" to "🐉 Live Casino").forEach { (k, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (mode == k) DkGold else DkSurfaceVariant)
                                .clickable { mode = k }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (mode == k) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Text(
                    text = "৳${String.format("%.0f", user?.balance ?: 0.0)}",
                    color = DkGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        if (mode == "mines") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💣 Spribe Mines (৫x৫ ডায়মন্ড হান্ট) · বর্তমান গুণক: ${String.format("%.2f", minesMult)}x",
                        color = DkGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    for (r in 0..4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0..4) {
                                val idx = r * 5 + c
                                val st = revealedCells[idx]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when (st) {
                                                1 -> DkEmerald
                                                2 -> DkCrimson
                                                else -> DkSurfaceVariant
                                            }
                                        )
                                        .clickable(enabled = minesRoundActive && st == 0) {
                                            val winChance = settings.slotsWinRatePercent.coerceIn(10, 95)
                                            val hitStar = Random.nextInt(100) < winChance
                                            val copy = revealedCells.toMutableList()
                                            if (hitStar) {
                                                copy[idx] = 1
                                                revealedCells = copy
                                                minesMult = ((minesMult * 1.24) * 100.0).roundToInt() / 100.0
                                            } else {
                                                copy[idx] = 2
                                                revealedCells = copy
                                                minesRoundActive = false
                                                val bId = minesBetId
                                                minesBetId = null
                                                if (bId != null) {
                                                    scope.launch {
                                                        vm.repo.settleInstantBet(bId, false, 0.0, "Hit Mine Bomb")
                                                        vm.showToast("💥 মাইন বম্ব পড়েছে! আবার চেষ্টা করুন।", true)
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (st) {
                                            1 -> "💎"
                                            2 -> "💣"
                                            else -> "?"
                                        },
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    if (minesRoundActive) {
                        Button(
                            onClick = {
                                val bId = minesBetId ?: return@Button
                                minesRoundActive = false
                                minesBetId = null
                                scope.launch {
                                    vm.repo.settleInstantBet(bId, true, minesMult, "Mines Cashout @ ${minesMult}x")
                                        .onSuccess { win ->
                                            vm.showToast("মাইনস ক্যাশআউট সফল! +৳${win.toInt()} (${minesMult}x)")
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💰 ক্যাশআউট করুন (${String.format("%.2f", minesMult)}x)",
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = betInput,
                            onValueChange = { betInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("বেট পরিমাণ (৳১+)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (user == null) {
                                    vm.setShowAuthSheet(true)
                                    return@Button
                                }
                                val amt = (betInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
                                scope.launch {
                                    vm.repo.startGameBet(user.id, "mines", "spribe_mines", amt, "Mines 5x5")
                                        .onSuccess { bet ->
                                            minesBetId = bet.id
                                            minesStake = amt
                                            minesMult = 1.0
                                            revealedCells = List(25) { 0 }
                                            minesRoundActive = true
                                        }.onFailure { err ->
                                            vm.showToast(err.message ?: "ব্যর্থ", true)
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💣 নতুন মাইনস রাউন্ড শুরু করুন (৳$betInput)", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        } else if (mode == "dice") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🎲 ডাইস রোল রেজাল্ট: $lastDiceRoll",
                        color = DkGold,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "টার্গেট ওভার: ${diceTarget.toInt()} (পেআউট 2.0x)",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = diceTarget,
                        onValueChange = { diceTarget = it },
                        valueRange = 20f..80f
                    )
                    OutlinedTextField(
                        value = betInput,
                        onValueChange = { betInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("বেট পরিমাণ (৳১+)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (user == null) {
                                vm.setShowAuthSheet(true)
                                return@Button
                            }
                            val amt = (betInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
                            scope.launch {
                                vm.repo.startGameBet(user.id, "dice", "spribe_dice", amt, "Over ${diceTarget.toInt()}")
                                    .onSuccess { bet ->
                                        val isWin = Random.nextInt(100) < settings.slotsWinRatePercent
                                        val roll = if (isWin) Random.nextInt(diceTarget.toInt() + 1, 100)
                                        else Random.nextInt(1, diceTarget.toInt())
                                        lastDiceRoll = roll
                                        vm.repo.settleInstantBet(bet.id, isWin, 2.0, "Roll $roll")
                                            .onSuccess { win ->
                                                if (isWin) vm.showToast("ডাইস জয়! +৳${win.toInt()}")
                                                else vm.showToast("রোল: $roll — আবার চেষ্টা করুন", true)
                                            }
                                    }.onFailure { vm.showToast(it.message ?: "ব্যর্থ", true) }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🎲 রোল করুন (৳$betInput)", fontWeight = FontWeight.Black)
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🐉 লাইভ ড্রাগন বনাম টাইগার ও রুলেট",
                        color = DkGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "সর্বশেষ টেবিল রেজাল্ট: $lastLiveOutcome",
                        color = DkEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("DRAGON" to "🐉 Dragon (2x)", "TIE" to "🤝 Tie (8x)", "TIGER" to "🐯 Tiger (2x)").forEach { (k, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (livePick == k) DkGold else DkSurfaceVariant)
                                    .clickable { livePick = k }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (livePick == k) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = betInput,
                        onValueChange = { betInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("বেট পরিমাণ (৳১+)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (user == null) {
                                vm.setShowAuthSheet(true)
                                return@Button
                            }
                            val amt = (betInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
                            scope.launch {
                                vm.repo.startGameBet(user.id, "dragon_tiger", "live_dt", amt, livePick)
                                    .onSuccess { bet ->
                                        val isWin = Random.nextInt(100) < settings.slotsWinRatePercent
                                        val mult = if (livePick == "TIE") 8.0 else 2.0
                                        lastLiveOutcome = if (isWin) "$livePick WON!" else "Opposite Side Won"
                                        vm.repo.settleInstantBet(bet.id, isWin, mult, lastLiveOutcome)
                                            .onSuccess { win ->
                                                if (isWin) vm.showToast("লাইভ ক্যাসিনো জয়! +৳${win.toInt()}")
                                                else vm.showToast("হেরেছেন — পরবর্তী ডিল চেষ্টা করুন", true)
                                            }
                                    }.onFailure { vm.showToast(it.message ?: "ব্যর্থ", true) }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🃏 কার্ড ডিল করুন: $livePick (৳$betInput)", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
