package com.dkwin.app.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
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
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun CrashGamesScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    BackHandler { vm.closeGame() }

    val selectedVariant by vm.selectedCrashVariant.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val variants = listOf(
        "aviator" to Triple("✈️ Spribe Aviator", "ক্লাসিক লাল প্লেন ক্র্যাশ", DkCrimson),
        "jetx" to Triple("🛩️ JetX Fighter", "সুপারসনিক ফাইটার জেট", DkSky),
        "spacexy" to Triple("🚀 SpaceXY Rocket", "গ্যালাক্সি রকেট ক্র্যাশ", DkViolet),
        "cricketx" to Triple("🏏 Crash Cricket X", "সিক্সার বল বাউন্ডারি ক্র্যাশ", DkGold)
    )
    val currentMeta = variants.firstOrNull { it.first == selectedVariant }?.second
        ?: Triple("✈️ Spribe Aviator", "ক্লাসিক লাল প্লেন ক্র্যাশ", DkCrimson)

    var flightState by remember { mutableStateOf("waiting") } // waiting, flying, crashed
    var countdown by remember { mutableIntStateOf(4) }
    var currentMult by remember { mutableDoubleStateOf(1.00) }
    var targetCrash by remember { mutableDoubleStateOf(2.15) }
    val recentCrashes = remember { mutableStateListOf(1.45, 2.80, 11.40, 1.12, 3.50, 1.92, 6.25) }

    var betInput by remember { mutableStateOf("10") }
    var autoCashoutInput by remember { mutableStateOf("2.00") }
    var autoCashoutEnabled by remember { mutableStateOf(false) }

    var activeBetId by remember { mutableStateOf<Int?>(null) }
    var activeBetStake by remember { mutableDoubleStateOf(0.0) }
    var cashedOutThisRound by remember { mutableStateOf(false) }

    // Continuous Crash Game Loop (Respects Admin Panel forcedCrashMode & forcedCrashMultiplier!)
    LaunchedEffect(selectedVariant, settings.forcedCrashMode, settings.forcedCrashMultiplier) {
        while (true) {
            flightState = "waiting"
            currentMult = 1.00
            cashedOutThisRound = false

            // Determine crash point from Admin Panel or balanced distribution
            val crashPt = if (settings.forcedCrashMode == "FORCE_CRASH") {
                settings.forcedCrashMultiplier.coerceAtLeast(1.01)
            } else {
                val r = Random.nextDouble()
                val raw = when {
                    r < 0.35 -> 1.05 + Random.nextDouble() * 0.75
                    r < 0.80 -> 1.80 + Random.nextDouble() * 2.80
                    r < 0.95 -> 4.60 + Random.nextDouble() * 10.0
                    else -> 15.0 + Random.nextDouble() * 35.0
                }
                (raw * 100.0).roundToInt() / 100.0
            }
            targetCrash = crashPt

            for (sec in 4 downTo 1) {
                countdown = sec
                delay(1000L)
            }

            flightState = "flying"
            val startMs = System.currentTimeMillis()
            while (flightState == "flying") {
                val elapsedSec = (System.currentTimeMillis() - startMs) / 1000.0
                val nextM = ((1.0 + (elapsedSec * 0.72).pow(1.75)) * 100.0).roundToInt() / 100.0
                if (nextM >= targetCrash) {
                    currentMult = targetCrash
                    flightState = "crashed"
                    recentCrashes.add(0, targetCrash)
                    if (recentCrashes.size > 10) recentCrashes.removeAt(recentCrashes.lastIndex)

                    // Settle uncashed active bet as loss
                    val bId = activeBetId
                    if (bId != null && !cashedOutThisRound) {
                        vm.repo.settleInstantBet(bId, false, 0.0, "Crashed @ ${targetCrash}x")
                        activeBetId = null
                    }
                    break
                } else {
                    currentMult = nextM
                    // Check Auto Cashout
                    val autoTarget = autoCashoutInput.toDoubleOrNull() ?: 2.0
                    val bId = activeBetId
                    if (autoCashoutEnabled && bId != null && !cashedOutThisRound && currentMult >= autoTarget) {
                        cashedOutThisRound = true
                        activeBetId = null
                        val res = vm.repo.settleInstantBet(bId, true, autoTarget, "Auto Cashout @ ${autoTarget}x")
                        res.onSuccess { winAmt ->
                            vm.showToast("অটো ক্যাশআউট সফল! +৳${winAmt.toInt()} (${autoTarget}x)")
                        }
                    }
                }
                delay(50L)
            }

            delay(2800L)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("crash_game_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header
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
                        text = currentMeta.first,
                        color = currentMeta.third,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = currentMeta.second,
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

        // 2. 4 Crash Plane Variant Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                variants.forEach { (id, meta) ->
                    val sel = selectedVariant == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (sel) meta.third else DkSurface)
                            .border(1.dp, meta.third.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { vm.setCrashVariant(id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("crash_variant_$id")
                    ) {
                        Text(
                            text = meta.first,
                            color = if (sel) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3. Recent Crash Multipliers Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recentCrashes.forEach { m ->
                    val badgeColor = when {
                        m >= 10.0 -> DkViolet
                        m >= 2.0 -> DkEmerald
                        else -> DkSurfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor.copy(alpha = 0.25f))
                            .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${String.format("%.2f", m)}x",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. Crash Radar & Flight Trajectory Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF070B14))
                        )
                    )
                    .border(1.5.dp, currentMeta.third.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                val accentColor = currentMeta.third
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Draw subtle radar grid lines
                    for (i in 1..4) {
                        val y = h * (i / 5f)
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }
                    if (flightState == "flying" || flightState == "crashed") {
                        val progress = ((currentMult - 1.0) / 8.0).coerceIn(0.08, 0.92).toFloat()
                        val endX = w * progress
                        val endY = h - (h * 0.78f * progress)
                        val path = Path().apply {
                            moveTo(24f, h - 24f)
                            quadraticTo(endX * 0.55f, h - 24f, endX, endY)
                        }
                        drawPath(
                            path = path,
                            color = if (flightState == "crashed") DkCrimson else accentColor,
                            style = Stroke(width = 6f)
                        )
                        drawCircle(
                            color = if (flightState == "crashed") DkCrimson else DkGold,
                            radius = 10f,
                            center = Offset(endX, endY)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val vehicleEmoji = when (selectedVariant) {
                        "jetx" -> "🛩️"
                        "spacexy" -> "🚀"
                        "cricketx" -> "🏏"
                        else -> "✈️"
                    }
                    when (flightState) {
                        "waiting" -> {
                            Text(
                                text = "$vehicleEmoji পরবর্তী ফ্লাইট শুরু হচ্ছে...",
                                color = DkTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${countdown}s",
                                color = DkGold,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "এখনই আপনার বেট প্লেস করুন!",
                                color = DkEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        "flying" -> {
                            Text(text = vehicleEmoji, fontSize = 36.sp)
                            Text(
                                text = "${String.format("%.2f", currentMult)}x",
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "বিমান ক্র্যাশ করার আগেই ক্যাশআউট করুন!",
                                color = DkEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        else -> {
                            Text(
                                text = "💥 FLEW AWAY / ক্র্যাশ করেছে!",
                                color = DkCrimson,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${String.format("%.2f", currentMult)}x",
                                color = DkCrimson,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 5. Crash Betting & Instant Cashout Panel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkSurfaceVariant, RoundedCornerShape(18.dp))
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = betInput,
                        onValueChange = { betInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("বেট পরিমাণ (৳১+)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DkGold,
                            unfocusedBorderColor = DkSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = autoCashoutInput,
                        onValueChange = { autoCashoutInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("অটো ক্যাশআউট (x)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DkEmerald,
                            unfocusedBorderColor = DkSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                val canCashout = activeBetId != null && flightState == "flying" && !cashedOutThisRound
                if (canCashout) {
                    val liveReturn = activeBetStake * currentMult * (1.0 - settings.wingoFeePercent / 100.0)
                    Button(
                        onClick = {
                            val bId = activeBetId ?: return@Button
                            val cashMult = currentMult
                            cashedOutThisRound = true
                            activeBetId = null
                            scope.launch {
                                vm.repo.settleInstantBet(bId, true, cashMult, "Cashed Out @ ${cashMult}x")
                                    .onSuccess { win ->
                                        vm.showToast("অভিনন্দন! ক্যাশআউট সফল: +৳${win.toInt()} (${cashMult}x)")
                                    }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DkEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("crash_cashout_btn")
                    ) {
                        Text(
                            text = "💰 ক্যাশআউট করুন: ৳${String.format("%.2f", liveReturn)} (${String.format("%.2f", currentMult)}x)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (user == null) {
                                vm.setShowAuthSheet(true)
                                return@Button
                            }
                            val amt = (betInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
                            scope.launch {
                                val res = vm.repo.startGameBet(
                                    userId = user.id,
                                    gameType = "crash_$selectedVariant",
                                    gameId = selectedVariant,
                                    betAmount = amt,
                                    prediction = "Crash Flight"
                                )
                                res.onSuccess { bet ->
                                    activeBetId = bet.id
                                    activeBetStake = amt
                                    cashedOutThisRound = false
                                    vm.showToast("বেট নিশ্চিত হয়েছে (৳${amt.toInt()})!")
                                }.onFailure { err ->
                                    vm.showToast(err.message ?: "ব্যর্থ", true)
                                    if (user.balance < amt) vm.setShowDepositSheet(true)
                                }
                            }
                        },
                        enabled = activeBetId == null && flightState == "waiting",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentMeta.third,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("crash_place_bet_btn")
                    ) {
                        Text(
                            text = when {
                                activeBetId != null -> "আপনার বেট অ্যাক্টিভ আছে (৳${activeBetStake.toInt()}) — উড্ডয়নের অপেক্ষায়..."
                                flightState != "waiting" -> "পরবর্তী রাউন্ডের জন্য অপেক্ষা করুন..."
                                else -> "বেট প্লেস করুন (৳${betInput.ifEmpty { "1" }})"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThimblesGameScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    BackHandler { vm.closeGame() }

    val scope = rememberCoroutineScope()
    var ballCountMode by remember { mutableIntStateOf(1) } // 1 Ball (2.88x) or 2 Balls (1.44x)
    var betInput by remember { mutableStateOf("10") }
    var isShuffling by remember { mutableStateOf(false) }
    var roundReadyToPick by remember { mutableStateOf(false) }
    var activeBetId by remember { mutableStateOf<Int?>(null) }
    var revealedCups by remember { mutableStateOf<List<Boolean>>(listOf(false, false, false)) }
    var selectedCupIndex by remember { mutableIntStateOf(-1) }

    val multiplier = if (ballCountMode == 1) 2.88 else 1.44

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("thimbles_game_screen"),
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
                        text = "🏆 THIMBLES (থাম্বিল গেম)",
                        color = DkGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "সঠিক কাপের নিচে লুকানো বল খুঁজুন",
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

        // Mode Selector (1 Ball vs 2 Balls)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkSurface)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (ballCountMode == 1) DkGold else Color.Transparent)
                        .clickable(enabled = !roundReadyToPick && !isShuffling) { ballCountMode = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚽ ১টি বল (2.88x পেআউট)",
                        color = if (ballCountMode == 1) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (ballCountMode == 2) DkEmerald else Color.Transparent)
                        .clickable(enabled = !roundReadyToPick && !isShuffling) { ballCountMode = 2 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚽⚽ ২টি বল (1.44x পেআউট)",
                        color = if (ballCountMode == 2) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 3 Thimbles Cups Stage
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF451A03), Color(0xFF111827))
                        )
                    )
                    .border(1.5.dp, DkGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = when {
                        isShuffling -> "🔄 কাপগুলো শাফল হচ্ছে... মনোযোগ দিয়ে দেখুন!"
                        roundReadyToPick -> "👆 যেকোনো ১টি কাপে ট্যাপ করে বল খুঁজুন!"
                        else -> "নিচে বেট এমাউন্ট দিয়ে গেম শুরু করুন"
                    },
                    color = DkGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (cupIdx in 0..2) {
                        val hasBall = revealedCups[cupIdx]
                        val isPicked = selectedCupIndex == cupIdx
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isPicked) DkGold.copy(alpha = 0.25f)
                                    else Color.Black.copy(alpha = 0.35f)
                                )
                                .border(
                                    2.dp,
                                    if (roundReadyToPick) DkGold else DkSurfaceVariant,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable(enabled = roundReadyToPick && !isShuffling) {
                                    val bId = activeBetId ?: return@clickable
                                    selectedCupIndex = cupIdx
                                    roundReadyToPick = false

                                    // Determine win/loss governed by Admin Panel's thimblesWinRatePercent!
                                    val winChance = settings.thimblesWinRatePercent.coerceIn(0, 100)
                                    val isWin = Random.nextInt(100) < winChance

                                    val newReveal = MutableList(3) { false }
                                    if (isWin) {
                                        newReveal[cupIdx] = true
                                        if (ballCountMode == 2) {
                                            val other = (0..2).filter { it != cupIdx }.random()
                                            newReveal[other] = true
                                        }
                                    } else {
                                        val others = (0..2).filter { it != cupIdx }.shuffled()
                                        newReveal[others[0]] = true
                                        if (ballCountMode == 2) {
                                            newReveal[others[1]] = true
                                        }
                                    }
                                    revealedCups = newReveal
                                    activeBetId = null

                                    scope.launch {
                                        vm.repo.settleInstantBet(
                                            betId = bId,
                                            isWin = isWin,
                                            multiplier = multiplier,
                                            outcomeText = if (isWin) "Cup #${cupIdx + 1} Ball Found" else "Cup #${cupIdx + 1} Empty"
                                        ).onSuccess { payout ->
                                            if (isWin) {
                                                vm.showToast("অভিনন্দন! সঠিক কাপে বল পেয়েছেন: +৳${payout.toInt()} (${multiplier}x)")
                                            } else {
                                                vm.showToast("দুঃখিত! এই কাপে বল ছিল না। আবার চেষ্টা করুন!", true)
                                            }
                                        }
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                                .testTag("thimble_cup_$cupIdx"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isShuffling) "🏺" else if (selectedCupIndex != -1 && hasBall) "⚽" else "🏺",
                                fontSize = 42.sp
                            )
                            Text(
                                text = "কাপ #${cupIdx + 1}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Bet Controls
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
                    label = { Text("বেট পরিমাণ (সর্বনিম্ন ৳১)", fontSize = 11.sp) },
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
                            val res = vm.repo.startGameBet(
                                userId = user.id,
                                gameType = "thimbles",
                                gameId = "thimbles_${ballCountMode}ball",
                                betAmount = amt,
                                prediction = "${ballCountMode} Ball ($multiplier x)"
                            )
                            res.onSuccess { bet ->
                                activeBetId = bet.id
                                selectedCupIndex = -1
                                revealedCups = listOf(false, false, false)
                                isShuffling = true
                                delay(900L)
                                isShuffling = false
                                roundReadyToPick = true
                            }.onFailure { err ->
                                vm.showToast(err.message ?: "ব্যর্থ", true)
                                if (user.balance < amt) vm.setShowDepositSheet(true)
                            }
                        }
                    },
                    enabled = !isShuffling && !roundReadyToPick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DkGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("thimbles_start_btn")
                ) {
                    Text(
                        text = if (roundReadyToPick) "উপরে যেকোনো ১টি কাপ সিলেক্ট করুন!" else "কাপ শাফল করুন ও খেলুন (৳${betInput.ifEmpty { "1" }})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
