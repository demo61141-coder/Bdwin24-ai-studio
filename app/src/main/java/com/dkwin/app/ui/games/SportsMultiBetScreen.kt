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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkwin.app.data.MultiBetSelection
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.SportsMatchEntity
import com.dkwin.app.data.UserEntity
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.theme.DkCrimson
import com.dkwin.app.ui.theme.DkEmerald
import com.dkwin.app.ui.theme.DkGold
import com.dkwin.app.ui.theme.DkSky
import com.dkwin.app.ui.theme.DkSurface
import com.dkwin.app.ui.theme.DkSurfaceVariant
import com.dkwin.app.ui.theme.DkTextSecondary
import kotlin.math.roundToInt

@Composable
fun SportsMultiBetScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    BackHandler { vm.closeGame() }

    val matches by vm.sportsMatches.collectAsStateWithLifecycle()
    val slip by vm.multiBetSlip.collectAsStateWithLifecycle()
    val allUserBets by vm.userGameBets.collectAsStateWithLifecycle()

    var selectedSport by remember { mutableStateOf("all") } // all, cricket, football, mySlips
    var betAmountInput by remember { mutableStateOf("50") }

    val filteredMatches = remember(matches, selectedSport) {
        if (selectedSport == "all" || selectedSport == "mySlips") matches
        else matches.filter { it.sport.equals(selectedSport, ignoreCase = true) }
    }

    val sportsBets = remember(allUserBets) {
        allUserBets.filter { it.gameType.startsWith("sports") }
    }

    val totalCombinedOdds = remember(slip) {
        if (slip.isEmpty()) 0.0
        else ((slip.fold(1.0) { acc, item -> acc * item.odds }) * 100.0).roundToInt() / 100.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sports_multi_bet_screen")
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
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
                            text = "🏏 1xBet ক্রিকেট ও মাল্টি স্পোর্টস",
                            color = DkEmerald,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "একাধিক ম্যাচ সিলেক্ট করে মাল্টি বেট (Accumulator) করুন",
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
                            text = "৳${String.format("%.0f", user?.balance ?: 0.0)}",
                            color = DkGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // 2. Category Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "all" to "সব ম্যাচ",
                        "cricket" to "🏏 ক্রিকেট (BPL/ICC)",
                        "football" to "⚽ ফুটবল",
                        "mySlips" to "📋 আমার বেট (${sportsBets.size})"
                    ).forEach { (k, label) ->
                        val active = selectedSport == k
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (active) DkEmerald else DkSurface)
                                .clickable { selectedSport = k }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (active) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            if (selectedSport == "mySlips") {
                if (sportsBets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("আপনার কোনো স্পোর্টস বেট স্লিপ নেই", color = DkTextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(sportsBets) { bet ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DkSurface)
                                .border(1.dp, DkSurfaceVariant, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (bet.gameType == "sports_multi") "🔥 মাল্টি বেট স্লিপ (${bet.multiplier}x)" else "⚡ সিঙ্গেল বেট (${bet.multiplier}x)",
                                    color = DkGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = when {
                                        bet.status == "pending" -> "⏳ চলমান (Pending)"
                                        bet.isWin -> "✅ WON (+৳${bet.winAmount.toInt()})"
                                        else -> "❌ LOST"
                                    },
                                    color = when {
                                        bet.status == "pending" -> DkGold
                                        bet.isWin -> DkEmerald
                                        else -> DkCrimson
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = bet.prediction,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "স্টেক: ৳${bet.betAmount.toInt()} · সম্ভাব্য পেআউট: ৳${(bet.netStake * bet.multiplier).toInt()}",
                                color = DkTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchBetCard(
                        match = match,
                        currentSlip = slip,
                        onSelectOption = { vm.toggleMultiBetSelection(it) }
                    )
                }
            }
        }

        // Bottom 1xBet Multi-Bet Accumulator Slip Drawer
        if (slip.isNotEmpty()) {
            val stakeVal = (betAmountInput.toDoubleOrNull() ?: 1.0).coerceAtLeast(settings.minBet)
            val netReturn = stakeVal * (1.0 - settings.wingoFeePercent / 100.0) * totalCombinedOdds

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, DkEmerald, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (slip.size > 1) {
                            "🔥 1xBet মাল্টি একুমুলেটর (${slip.size}টি ম্যাচ) · মোট অডস: ${totalCombinedOdds}x"
                        } else {
                            "⚡ সিঙ্গেল বেট স্লিপ · অডস: ${totalCombinedOdds}x"
                        },
                        color = DkEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = { vm.clearMultiBetSlip() },
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Slip",
                            tint = DkCrimson
                        )
                    }
                }

                // Selected picks summary
                slip.forEach { item ->
                    Text(
                        text = "• ${item.matchTitle} (${item.marketName}): ${item.pickName} @${item.odds}x",
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = betAmountInput,
                        onValueChange = { betAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("বেট টাকা (৳১+)", fontSize = 10.sp) },
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

                    Button(
                        onClick = { vm.submitSportsMultiBet(stakeVal) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DkEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                            .testTag("submit_multi_sports_bet_btn")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "বেট প্লেস করুন (৳${stakeVal.toInt()})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "সম্ভাব্য জয়: ৳${netReturn.toInt()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchBetCard(
    match: SportsMatchEntity,
    currentSlip: List<MultiBetSelection>,
    onSelectOption: (MultiBetSelection) -> Unit
) {
    val matchTitle = "${match.teamA} vs ${match.teamB}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DkSurface)
            .border(1.dp, DkSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tournament & Live Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${if (match.sport == "cricket") "🏏" else "⚽"} ${match.tournament}",
                color = DkGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (match.status == "live") DkCrimson.copy(alpha = 0.2f) else DkSky.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (match.status == "live") "🔴 LIVE · ${match.score}" else "🕒 ${match.startTime}",
                    color = if (match.status == "live") DkCrimson else DkSky,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Teams Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = match.teamA,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "VS",
                color = DkGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = match.teamB,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }

        // Market 1: Match Winner (1X2)
        Text(text = "ম্যাচ বিজয়ী (Match Winner 1X2):", color = DkTextSecondary, fontSize = 10.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OddsChipButton(
                modifier = Modifier.weight(1f),
                label = "W1 (${match.teamA.take(10)})",
                odds = match.oddsA,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Winner" && it.pickName == match.teamA },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Winner",
                            pickName = match.teamA,
                            odds = match.oddsA
                        )
                    )
                }
            )
            OddsChipButton(
                modifier = Modifier.weight(0.75f),
                label = "X (Draw)",
                odds = match.oddsDraw,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Winner" && it.pickName == "Draw" },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Winner",
                            pickName = "Draw",
                            odds = match.oddsDraw
                        )
                    )
                }
            )
            OddsChipButton(
                modifier = Modifier.weight(1f),
                label = "W2 (${match.teamB.take(10)})",
                odds = match.oddsB,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Winner" && it.pickName == match.teamB },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Winner",
                            pickName = match.teamB,
                            odds = match.oddsB
                        )
                    )
                }
            )
        }

        // Market 2: Toss Winner & Total Runs/Goals Over-Under
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OddsChipButton(
                modifier = Modifier.weight(1f),
                label = "টস: ${match.teamA.take(8)}",
                odds = match.tossOddsA,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Toss" && it.pickName == "Toss ${match.teamA}" },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Toss",
                            pickName = "Toss ${match.teamA}",
                            odds = match.tossOddsA
                        )
                    )
                }
            )
            OddsChipButton(
                modifier = Modifier.weight(1f),
                label = "ওভার (${match.runsLine})",
                odds = match.overOdds,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Total" && it.pickName == "Over ${match.runsLine}" },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Total",
                            pickName = "Over ${match.runsLine}",
                            odds = match.overOdds
                        )
                    )
                }
            )
            OddsChipButton(
                modifier = Modifier.weight(1f),
                label = "আন্ডার (${match.runsLine})",
                odds = match.underOdds,
                isSelected = currentSlip.any { it.matchId == match.id && it.marketName == "Total" && it.pickName == "Under ${match.runsLine}" },
                onClick = {
                    onSelectOption(
                        MultiBetSelection(
                            matchId = match.id,
                            matchTitle = matchTitle,
                            marketName = "Total",
                            pickName = "Under ${match.runsLine}",
                            odds = match.underOdds
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun OddsChipButton(
    modifier: Modifier = Modifier,
    label: String,
    odds: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) DkEmerald else DkSurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${odds}x",
            color = if (isSelected) Color.Black else DkGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}
