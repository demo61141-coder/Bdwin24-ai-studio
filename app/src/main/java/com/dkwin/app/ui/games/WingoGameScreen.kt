package com.dkwin.app.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
fun WingoGameScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    BackHandler { vm.closeGame() }

    val timerType by vm.selectedWingoTimer.collectAsStateWithLifecycle()
    val activePeriod by vm.wingoActivePeriod.collectAsStateWithLifecycle()
    val remainingSec by vm.wingoRemainingSec.collectAsStateWithLifecycle()
    val history by vm.wingoHistory.collectAsStateWithLifecycle()
    val allUserBets by vm.userGameBets.collectAsStateWithLifecycle()

    var selectedPrediction by remember { mutableStateOf<String?>(null) }
    var customStakeInput by remember { mutableStateOf("10") }
    var multiplier by remember { mutableIntStateOf(1) }
    var subTab by remember { mutableStateOf("chart") } // chart, myBets

    val wingoUserBets = remember(allUserBets) {
        allUserBets.filter { it.gameType == "wingo" }
    }

    val baseAmount = customStakeInput.toDoubleOrNull() ?: 1.0
    val totalBetAmount = (baseAmount * multiplier).coerceAtLeast(settings.minBet)
    val feeAmount = (totalBetAmount * settings.wingoFeePercent) / 100.0
    val netStake = totalBetAmount - feeAmount

    val mins = remainingSec / 60
    val secs = remainingSec % 60
    val formattedCountdown = String.format("%02d:%02d", mins, secs)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wingo_game_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Top Game Header
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
                IconButton(
                    onClick = { vm.closeGame() },
                    modifier = Modifier.testTag("wingo_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DKWIN WIN GO লটারী",
                        color = DkGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "মিনিমাম বেট ৳${settings.minBet.toInt()} · সার্ভিস চার্জ ${settings.wingoFeePercent}%",
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

        // 2. Timer Modes (30s, 1m, 3m, 5m)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("30s" to "WinGo 30s", "1m" to "WinGo 1Min", "3m" to "WinGo 3Min", "5m" to "WinGo 5Min").forEach { (key, label) ->
                    val active = timerType == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) DkGold else Color.Transparent)
                            .clickable { vm.setWingoTimer(key) }
                            .padding(vertical = 8.dp)
                            .testTag("wingo_timer_$key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (active) Color.Black else DkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 3. Period & Synchronized Countdown Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF064E3B), Color(0xFF111827), Color(0xFF31102F))
                        )
                    )
                    .border(1.dp, DkGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = DkGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "চলমান পিরিয়ড নাম্বার ($timerType)",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = activePeriod.ifEmpty { "Loading..." },
                            color = DkGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (remainingSec <= 4) "বেটিং লকড!" else "বাকি সময়",
                            color = if (remainingSec <= 4) DkCrimson else DkEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(
                                    1.dp,
                                    if (remainingSec <= 5) DkCrimson else DkEmerald,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formattedCountdown,
                                color = if (remainingSec <= 5) DkCrimson else DkEmerald,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Recent 6 Winning Balls Preview
                if (history.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "সর্বশেষ রেজাল্ট:",
                            color = DkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            history.take(6).forEach { round ->
                                WingoNumberBall(number = round.winningNumber, sizeDp = 26)
                            }
                        }
                    }
                }
            }
        }

        // 4. Color Prediction Buttons (Green, Violet, Red) + Number Balls (0-9) + Big/Small
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkSurfaceVariant, RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Colors Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorBetButton(
                        modifier = Modifier.weight(1f),
                        title = "সবুজ (Green)",
                        payout = "2x",
                        color = DkEmerald,
                        isSelected = selectedPrediction == "green",
                        tag = "bet_color_green",
                        onClick = { selectedPrediction = "green" }
                    )
                    ColorBetButton(
                        modifier = Modifier.weight(1f),
                        title = "বেগুনী (Violet)",
                        payout = "4.5x",
                        color = DkViolet,
                        isSelected = selectedPrediction == "violet",
                        tag = "bet_color_violet",
                        onClick = { selectedPrediction = "violet" }
                    )
                    ColorBetButton(
                        modifier = Modifier.weight(1f),
                        title = "লাল (Red)",
                        payout = "2x",
                        color = DkCrimson,
                        isSelected = selectedPrediction == "red",
                        tag = "bet_color_red",
                        onClick = { selectedPrediction = "red" }
                    )
                }

                // Numbers 0..9 Grid (2 rows of 5)
                Text(
                    text = "নাম্বার সিলেক্ট করুন (৯ গুণ পেআউট - 9x):",
                    color = DkTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                for (row in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..4) {
                            val num = row * 5 + col
                            val sel = selectedPrediction == num.toString()
                            val bgBrush = when (num) {
                                0 -> Brush.linearGradient(listOf(DkCrimson, DkViolet))
                                5 -> Brush.linearGradient(listOf(DkEmerald, DkViolet))
                                1, 3, 7, 9 -> Brush.linearGradient(listOf(DkEmerald, Color(0xFF047857)))
                                else -> Brush.linearGradient(listOf(DkCrimson, Color(0xFFB91C1C)))
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgBrush)
                                    .border(
                                        width = if (sel) 2.5.dp else 1.dp,
                                        color = if (sel) DkGold else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPrediction = num.toString() }
                                    .testTag("bet_num_$num"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$num",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "9x",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Big / Small Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedPrediction == "big") DkGold else DkGold.copy(alpha = 0.8f))
                            .border(
                                width = if (selectedPrediction == "big") 2.5.dp else 0.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPrediction = "big" }
                            .testTag("bet_size_big"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BIG (৫-৯) · 2x",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedPrediction == "small") DkSky else DkSky.copy(alpha = 0.85f))
                            .border(
                                width = if (selectedPrediction == "small") 2.5.dp else 0.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPrediction = "small" }
                            .testTag("bet_size_small"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SMALL (০-৪) · 2x",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 5. Bet Amount Input (Starts from 1 TK!) + Multipliers + Confirm Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বেট এমাউন্ট (সর্বনিম্ন ৳${settings.minBet.toInt()}):",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "মোট বেট: ৳${String.format("%.0f", totalBetAmount)} (নেট: ৳${String.format("%.2f", netStake)})",
                        color = DkGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Quick Chips starting from 1 TK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 5, 10, 20, 50, 100, 500).forEach { chip ->
                        val active = customStakeInput == chip.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) DkGold else DkSurfaceVariant)
                                .clickable { customStakeInput = chip.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "৳$chip",
                                color = if (active) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Custom Amount + Multiplier Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customStakeInput,
                        onValueChange = { customStakeInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("নিজের মতো টাকা বসান (৳)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DkGold,
                            unfocusedBorderColor = DkSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wingo_custom_bet_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1, 2, 5, 10).forEach { m ->
                            val isSel = multiplier == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) DkEmerald else DkSurfaceVariant)
                                    .clickable { multiplier = m }
                                    .padding(horizontal = 8.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "x$m",
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val pred = selectedPrediction
                        if (pred == null) {
                            vm.showToast("প্রথমে Big, Small, Green, Red বা যেকোনো নাম্বার সিলেক্ট করুন!", true)
                        } else {
                            vm.placeWingoBet(pred, totalBetAmount) {
                                selectedPrediction = null
                            }
                        }
                    },
                    enabled = remainingSec > 4,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DkGold,
                        contentColor = Color.Black,
                        disabledContainerColor = DkSurfaceVariant,
                        disabledContentColor = DkTextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("wingo_place_bet_btn")
                ) {
                    Text(
                        text = if (remainingSec <= 4) {
                            "এই রাউন্ড লকড — ফলাফল ঘোষণা হচ্ছে..."
                        } else if (selectedPrediction != null) {
                            "বেট নিশ্চিত করুন: ${selectedPrediction!!.uppercase()} (৳${totalBetAmount.toInt()})"
                        } else {
                            "প্রিডিকশন সিলেক্ট করে বেট করুন (৳${totalBetAmount.toInt()})"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 6. History & My Bets Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DkSurface)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (subTab == "chart") DkGold else Color.Transparent)
                        .clickable { subTab = "chart" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "খেলার ফলাফল হিস্ট্রি",
                        color = if (subTab == "chart") Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (subTab == "myBets") DkGold else Color.Transparent)
                        .clickable { subTab = "myBets" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "আমার বেট (${wingoUserBets.size})",
                        color = if (subTab == "myBets") Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (subTab == "chart") {
            items(history.take(15)) { round ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DkSurface)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = round.periodNumber,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    WingoNumberBall(number = round.winningNumber, sizeDp = 28)
                    Text(
                        text = round.winningSize.uppercase(),
                        color = if (round.winningSize == "big") DkGold else DkSky,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = round.winningColor.uppercase(),
                        color = when {
                            round.winningColor.contains("green") -> DkEmerald
                            round.winningColor.contains("red") -> DkCrimson
                            else -> DkViolet
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            if (wingoUserBets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("এখনো কোনো বেট প্লেস করা হয়নি", color = DkTextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                items(wingoUserBets.take(20)) { bet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DkSurface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "সিলেকশন: ${bet.prediction.uppercase()} (পিরিয়ড: ${bet.periodNumber.takeLast(5)})",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "বেট: ৳${bet.betAmount.toInt()} | সার্ভিস ফি: ৳${String.format("%.2f", bet.feeAmount)}",
                                color = DkTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (bet.status == "pending") {
                                Text("চলমান (Pending)", color = DkGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else if (bet.isWin) {
                                Text(
                                    text = "+৳${String.format("%.2f", bet.winAmount)}",
                                    color = DkEmerald,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(text = "WIN (${bet.outcome})", color = DkEmerald, fontSize = 10.sp)
                            } else {
                                Text(
                                    text = "-৳${bet.betAmount.toInt()}",
                                    color = DkCrimson,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "LOSS (${bet.outcome})", color = DkTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorBetButton(
    modifier: Modifier = Modifier,
    title: String,
    payout: String,
    color: Color,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = "$payout পেআউট",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WingoNumberBall(number: Int, sizeDp: Int) {
    val brush = when (number) {
        0 -> Brush.linearGradient(listOf(DkCrimson, DkViolet))
        5 -> Brush.linearGradient(listOf(DkEmerald, DkViolet))
        1, 3, 7, 9 -> Brush.linearGradient(listOf(DkEmerald, Color(0xFF047857)))
        else -> Brush.linearGradient(listOf(DkCrimson, Color(0xFFB91C1C)))
    }
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            color = Color.White,
            fontSize = (sizeDp / 2).sp,
            fontWeight = FontWeight.Black
        )
    }
}
