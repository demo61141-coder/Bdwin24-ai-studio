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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.SportsMatchEntity
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.theme.DkBackground
import com.dkwin.app.ui.theme.DkCrimson
import com.dkwin.app.ui.theme.DkEmerald
import com.dkwin.app.ui.theme.DkGold
import com.dkwin.app.ui.theme.DkSky
import com.dkwin.app.ui.theme.DkSurface
import com.dkwin.app.ui.theme.DkSurfaceVariant
import com.dkwin.app.ui.theme.DkTextSecondary
import com.dkwin.app.ui.theme.DkViolet

@Composable
fun AdminPanelScreen(
    vm: DkwinViewModel,
    settings: SiteSettingsEntity
) {
    val isAdminAuth by vm.isAdminAuthenticated.collectAsStateWithLifecycle()
    val allUsers by vm.allUsers.collectAsStateWithLifecycle()
    val allTxns by vm.allTransactions.collectAsStateWithLifecycle()
    val allBets by vm.allGameBets.collectAsStateWithLifecycle()
    val wingoBreakdown by vm.wingoLiveBreakdown.collectAsStateWithLifecycle()
    val wingoPeriod by vm.wingoActivePeriod.collectAsStateWithLifecycle()
    val wingoRemSec by vm.wingoRemainingSec.collectAsStateWithLifecycle()
    val sportsMatches by vm.sportsMatches.collectAsStateWithLifecycle()
    val giftCodes by vm.giftCodes.collectAsStateWithLifecycle()
    val chatMessages by vm.chatMessages.collectAsStateWithLifecycle()

    var pinInput by remember { mutableStateOf("admin1234") }
    var activeAdminTab by remember { mutableStateOf("game_control") }

    if (!isAdminAuth) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(DkSurface)
                    .border(1.5.dp, DkGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🛡️ DKWIN মাস্টার এডমিন প্যানেল",
                    color = DkGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Win Go ও Crash রেজাল্ট কন্ট্রোল, ডিপোজিট/উইথড্র অ্যাপ্রুভাল এবং সিস্টেম সেটিংস",
                    color = DkTextSecondary,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it },
                    label = { Text("এডমিন পিন দিন (ডিফল্ট: admin1234)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )
                Button(
                    onClick = { vm.loginAdmin(pinInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_btn")
                ) {
                    Text("এডমিন ড্যাশবোর্ডে প্রবেশ করুন", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
        return
    }

    val deposits = remember(allTxns) { allTxns.filter { it.type == "deposit" } }
    val withdrawals = remember(allTxns) { allTxns.filter { it.type == "withdraw" } }
    val pendingDepCount = remember(deposits) { deposits.count { it.status == "pending" } }
    val pendingWithCount = remember(withdrawals) { withdrawals.count { it.status == "pending" } }
    val totalUserBetsAllTime = remember(allUsers) { allUsers.sumOf { it.totalBetAmount } }
    val totalUserWinsAllTime = remember(allUsers) { allUsers.sumOf { it.totalWinAmount } }
    val totalFeeCollected = remember(allBets) { allBets.sumOf { it.feeAmount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Summary Metrics
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ DKWIN মাস্টার এডমিন কন্ট্রোল",
                        color = DkGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Button(
                        onClick = { vm.logoutAdmin() },
                        colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("লগআউট", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricBox(
                        modifier = Modifier.weight(1f),
                        title = "ইউজার মোট বেট",
                        value = "৳${totalUserBetsAllTime.toInt()}",
                        color = DkGold
                    )
                    AdminMetricBox(
                        modifier = Modifier.weight(1f),
                        title = "ইউজার মোট জয়",
                        value = "৳${totalUserWinsAllTime.toInt()}",
                        color = DkSky
                    )
                    AdminMetricBox(
                        modifier = Modifier.weight(1f),
                        title = "হাউস ফি লাভ",
                        value = "৳${String.format("%.1f", totalFeeCollected)}",
                        color = DkEmerald
                    )
                }
            }
        }

        // Admin Sub-Navigation Tabs
        item {
            val adminTabs = listOf(
                "game_control" to "🎯 Win Go ও গেম কন্ট্রোল",
                "deposits" to "➕ ডিপোজিট ($pendingDepCount)",
                "withdrawals" to "⬆️ উইথড্র ($pendingWithCount)",
                "settings" to "⚙️ নাম্বার, নিয়ম ও রেফার",
                "sports" to "🏏 ক্রিকেট ও ম্যাচ অ্যাড",
                "users_chat" to "👥 ইউজার, গিফট ও চ্যাট"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                adminTabs.forEach { (id, label) ->
                    val sel = activeAdminTab == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) DkGold else DkSurface)
                            .border(1.dp, if (sel) DkGold else DkSurfaceVariant, RoundedCornerShape(10.dp))
                            .clickable { activeAdminTab = id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("admin_tab_$id")
                    ) {
                        Text(
                            text = label,
                            color = if (sel) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // ====================================================================
        // TAB 1: WIN GO LIVE BET BREAKDOWN & RESULT CONTROL + CRASH/THIMBLES/SLOTS
        // ====================================================================
        if (activeAdminTab == "game_control") {
            item {
                var feeInput by remember(settings.wingoFeePercent) { mutableStateOf(settings.wingoFeePercent.toString()) }
                var crashMultInput by remember(settings.forcedCrashMultiplier) { mutableStateOf(settings.forcedCrashMultiplier.toString()) }
                var thimblesRateInput by remember(settings.thimblesWinRatePercent) { mutableStateOf(settings.thimblesWinRatePercent.toString()) }
                var slotsRateInput by remember(settings.slotsWinRatePercent) { mutableStateOf(settings.slotsWinRatePercent.toString()) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .border(1.dp, DkEmerald.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎯 Win Go লাইভ বেট ও রেজাল্ট কন্ট্রোল",
                                color = DkGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "পিরিয়ড: $wingoPeriod · বাকি সময়: ${wingoRemSec}s",
                                color = DkEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DkGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "চলমান রাউন্ড মোট বেট: ৳${wingoBreakdown.totalBetAmount.toInt()}",
                                color = DkGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Live Bet Breakdown: Big vs Small & Colors
                    Text(
                        text = "📊 এই রাউন্ডে কোনটায় কত বেট পড়েছে (লাইভ):",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BetStatPill(Modifier.weight(1f), "BIG (৫-৯)", "৳${wingoBreakdown.bigBetAmount.toInt()}", DkGold)
                        BetStatPill(Modifier.weight(1f), "SMALL (০-৪)", "৳${wingoBreakdown.smallBetAmount.toInt()}", DkSky)
                        BetStatPill(Modifier.weight(1f), "GREEN", "৳${wingoBreakdown.greenBetAmount.toInt()}", DkEmerald)
                        BetStatPill(Modifier.weight(1f), "RED", "৳${wingoBreakdown.redBetAmount.toInt()}", DkCrimson)
                        BetStatPill(Modifier.weight(1f), "VIOLET", "৳${wingoBreakdown.violetBetAmount.toInt()}", DkViolet)
                    }

                    // Numbers 0..9 Bet Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (n in 0..9) {
                            val amt = wingoBreakdown.numberBets[n] ?: 0.0
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DkBackground)
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("#$n", color = DkGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text("৳${amt.toInt()}", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }

                    // Force Next Win Go Result Controls
                    Text(
                        text = "🎮 পরবর্তী রেজাল্ট ফিক্স করুন (বর্তমান মোড: ${settings.forcedWingoMode} ${if (settings.forcedWingoNumber >= 0) "#${settings.forcedWingoNumber}" else ""}):",
                        color = DkGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "অটো ব্যালেন্সড",
                            active = settings.forcedWingoMode == "AUTO_BALANCED",
                            color = DkSky,
                            onClick = { vm.adminSetWingoForcedResult("AUTO_BALANCED", -1) }
                        )
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "FORCE BIG",
                            active = settings.forcedWingoMode == "FORCE_BIG",
                            color = DkGold,
                            onClick = { vm.adminSetWingoForcedResult("FORCE_BIG", -1) }
                        )
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "FORCE SMALL",
                            active = settings.forcedWingoMode == "FORCE_SMALL",
                            color = DkSky,
                            onClick = { vm.adminSetWingoForcedResult("FORCE_SMALL", -1) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "FORCE GREEN",
                            active = settings.forcedWingoMode == "FORCE_GREEN",
                            color = DkEmerald,
                            onClick = { vm.adminSetWingoForcedResult("FORCE_GREEN", -1) }
                        )
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "FORCE RED",
                            active = settings.forcedWingoMode == "FORCE_RED",
                            color = DkCrimson,
                            onClick = { vm.adminSetWingoForcedResult("FORCE_RED", -1) }
                        )
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "FORCE VIOLET",
                            active = settings.forcedWingoMode == "FORCE_VIOLET",
                            color = DkViolet,
                            onClick = { vm.adminSetWingoForcedResult("FORCE_VIOLET", -1) }
                        )
                    }

                    Text(
                        text = "অথবা যেকোনো নির্দিষ্ট নাম্বার (০-৯) ফিক্স করুন:",
                        color = DkTextSecondary,
                        fontSize = 11.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (n in 0..9) {
                            val isForcedNum = settings.forcedWingoMode == "FORCE_NUMBER" && settings.forcedWingoNumber == n
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isForcedNum) DkGold else DkSurfaceVariant)
                                    .clickable { vm.adminSetWingoForcedResult("FORCE_NUMBER", n) }
                                    .padding(vertical = 8.dp)
                                    .testTag("admin_force_num_$n"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$n",
                                    color = if (isForcedNum) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Win Go Auto House Fee % (1% or 3% cut on both win and loss)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = feeInput,
                            onValueChange = { feeInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("প্রতি বেটে অটো সার্ভিস ফি % (যেমন: 1 বা 3)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val f = feeInput.toDoubleOrNull() ?: 2.0
                                vm.saveSettings(settings.copy(wingoFeePercent = f))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black)
                        ) {
                            Text("ফি সেভ (${feeInput}%)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    // Crash Plane, Thimbles & Slots Control
                    Text(
                        text = "✈️ ক্র্যাশ প্লেন (4x), থাম্বিল ও JILI স্লট কন্ট্রোল:",
                        color = DkGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "ক্র্যাশ: অটো মোড",
                            active = settings.forcedCrashMode == "AUTO",
                            color = DkEmerald,
                            onClick = { vm.saveSettings(settings.copy(forcedCrashMode = "AUTO")) }
                        )
                        ForceModeButton(
                            modifier = Modifier.weight(1f),
                            label = "ক্র্যাশ: ফিক্সড মাল্টিপ্লায়ার",
                            active = settings.forcedCrashMode == "FORCE_CRASH",
                            color = DkCrimson,
                            onClick = {
                                val m = crashMultInput.toDoubleOrNull() ?: 1.50
                                vm.saveSettings(settings.copy(forcedCrashMode = "FORCE_CRASH", forcedCrashMultiplier = m))
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = crashMultInput,
                            onValueChange = { crashMultInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("ক্র্যাশ পয়েন্ট (x)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = thimblesRateInput,
                            onValueChange = { thimblesRateInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("থাম্বিল উইন % (0-100)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = slotsRateInput,
                            onValueChange = { slotsRateInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("স্লট উইন % (0-100)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            vm.saveSettings(
                                settings.copy(
                                    forcedCrashMultiplier = crashMultInput.toDoubleOrNull() ?: 1.80,
                                    thimblesWinRatePercent = (thimblesRateInput.toIntOrNull() ?: 45).coerceIn(0, 100),
                                    slotsWinRatePercent = (slotsRateInput.toIntOrNull() ?: 45).coerceIn(0, 100)
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ক্র্যাশ, থাম্বিল ও স্লট কন্ট্রোল সেভ করুন", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // ====================================================================
        // TAB 2: DEPOSITS APPROVAL (IDEMPOTENT)
        // ====================================================================
        if (activeAdminTab == "deposits") {
            if (deposits.isEmpty()) {
                item {
                    Text("কোনো ডিপোজিট রিকোয়েস্ট নেই", color = DkTextSecondary, fontSize = 12.sp)
                }
            } else {
                items(deposits, key = { it.id }) { dep ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DkSurface)
                            .border(1.dp, DkSurfaceVariant, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${dep.username} (${dep.phone})",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "৳${dep.amount.toInt()} · ${dep.method.uppercase()}",
                                color = DkEmerald,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "প্রেরক নাম্বার: ${dep.accountNumber} | TrxID: ${dep.trxId} (${dep.paymentMode})",
                            color = DkGold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        // Strictly show Approve / Reject ONLY when status == "pending"
                        if (dep.status == "pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { vm.adminProcessDeposit(dep.id, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("✅ Approve (ব্যালেন্স দিন)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { vm.adminProcessDeposit(dep.id, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("❌ Reject (বাতিল)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "স্ট্যাটাস লকড: ${dep.status.uppercase()} (${dep.note})",
                                color = if (dep.status == "completed") DkEmerald else DkCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 3: WITHDRAWALS APPROVAL (STRICTLY IDEMPOTENT - NEVER ADDS BALANCE ON COMPLETE!)
        // ====================================================================
        if (activeAdminTab == "withdrawals") {
            if (withdrawals.isEmpty()) {
                item {
                    Text("কোনো উইথড্র রিকোয়েস্ট নেই", color = DkTextSecondary, fontSize = 12.sp)
                }
            } else {
                items(withdrawals, key = { it.id }) { wd ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DkSurface)
                            .border(1.dp, DkSurfaceVariant, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${wd.username} (${wd.phone})",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "৳${wd.amount.toInt()} · ${wd.method.uppercase()}",
                                color = DkGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "রিসিভার পার্সোনাল নাম্বার: ${wd.accountNumber}",
                            color = DkEmerald,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        // Strictly show Complete / Reject ONLY when status == "pending"!
                        // Once completed or rejected, buttons disappear so repeated clicks can NEVER alter balance!
                        if (wd.status == "pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { vm.adminProcessWithdrawal(wd.id, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("✅ Complete (Paid)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { vm.adminProcessWithdrawal(wd.id, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("❌ Reject & Refund", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "স্ট্যাটাস লকড: ${wd.status.uppercase()} (${wd.note})",
                                color = if (wd.status == "completed") DkEmerald else DkCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 4: PAYMENT NUMBERS, SEND MONEY/CASH OUT RULES, 2-10 MIN NOTICE, REFERRAL & TELEGRAM
        // ====================================================================
        if (activeAdminTab == "settings") {
            item {
                var bkashNum by remember(settings) { mutableStateOf(settings.bkashNumber) }
                var bkashRule by remember(settings) { mutableStateOf(settings.bkashRule) }
                var nagadNum by remember(settings) { mutableStateOf(settings.nagadNumber) }
                var nagadRule by remember(settings) { mutableStateOf(settings.nagadRule) }
                var rocketNum by remember(settings) { mutableStateOf(settings.rocketNumber) }
                var rocketRule by remember(settings) { mutableStateOf(settings.rocketRule) }
                var upayNum by remember(settings) { mutableStateOf(settings.upayNumber) }
                var upayRule by remember(settings) { mutableStateOf(settings.upayRule) }
                var rulesText by remember(settings) { mutableStateOf(settings.depositRulesText) }
                var pendingNotice by remember(settings) { mutableStateOf(settings.pendingProcessingNotice) }
                var minDep by remember(settings) { mutableStateOf(settings.minDeposit.toInt().toString()) }
                var minWith by remember(settings) { mutableStateOf(settings.minWithdraw.toInt().toString()) }
                var minBet by remember(settings) { mutableStateOf(settings.minBet.toInt().toString()) }
                var depBonus by remember(settings) { mutableStateOf(settings.depositBonusPercent.toString()) }
                var refDepComm by remember(settings) { mutableStateOf(settings.referralDepositCommissionPercent.toString()) }
                var refBetComm by remember(settings) { mutableStateOf(settings.referralBetCommissionPercent.toString()) }
                var refFixed by remember(settings) { mutableStateOf(settings.referralFixedBonus.toString()) }
                var telegramUser by remember(settings) { mutableStateOf(settings.telegramSupportUsername) }
                var adminPinNew by remember(settings) { mutableStateOf(settings.adminPin) }
                var siteNoticeNew by remember(settings) { mutableStateOf(settings.siteNotice) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📱 পেমেন্ট নাম্বার ও ডিপোজিট নিয়ম (Send Money / Cash Out) কন্ট্রোল",
                        color = DkGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bkashNum,
                            onValueChange = { bkashNum = it },
                            label = { Text("বিকাশ নাম্বার", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bkashRule,
                            onValueChange = { bkashRule = it },
                            label = { Text("বিকাশ নিয়ম (Send Money/Cash Out)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nagadNum,
                            onValueChange = { nagadNum = it },
                            label = { Text("নগদ নাম্বার", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = nagadRule,
                            onValueChange = { nagadRule = it },
                            label = { Text("নগদ নিয়ম (Send Money/Cash Out)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rocketNum,
                            onValueChange = { rocketNum = it },
                            label = { Text("রকেট নাম্বার", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rocketRule,
                            onValueChange = { rocketRule = it },
                            label = { Text("রকেট নিয়ম", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = upayNum,
                            onValueChange = { upayNum = it },
                            label = { Text("উপায় নাম্বার", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = upayRule,
                            onValueChange = { upayRule = it },
                            label = { Text("উপায় নিয়ম", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = rulesText,
                        onValueChange = { rulesText = it },
                        label = { Text("ডিপোজিট করার বিস্তারিত নিয়মাবলী (ইউজার দেখবে)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pendingNotice,
                        onValueChange = { pendingNotice = it },
                        label = { Text("পেন্ডিং মেসেজ (২ থেকে ১০ মিনিট অপেক্ষার বার্তা)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = minDep,
                            onValueChange = { minDep = it },
                            label = { Text("মিনিমাম ডিপোজিট (৳)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minWith,
                            onValueChange = { minWith = it },
                            label = { Text("মিনিমাম উইথড্র (৳)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minBet,
                            onValueChange = { minBet = it },
                            label = { Text("মিনিমাম বেট (৳)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "🤝 রেফার কমিশন ও টেলিগ্রাম সাপোর্ট ইউজারনেম কন্ট্রোল:",
                        color = DkEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = depBonus,
                            onValueChange = { depBonus = it },
                            label = { Text("ডিপোজিট বোনাস (%)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = refDepComm,
                            onValueChange = { refDepComm = it },
                            label = { Text("রেফার ডিপোজিট (%)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = refBetComm,
                            onValueChange = { refBetComm = it },
                            label = { Text("রেফার বেট (%)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = refFixed,
                            onValueChange = { refFixed = it },
                            label = { Text("রেফার সাইনআপ বোনাস (৳)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = telegramUser,
                            onValueChange = { telegramUser = it },
                            label = { Text("সাপোর্ট টেলিগ্রাম ইউজারনেম (@)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = adminPinNew,
                            onValueChange = { adminPinNew = it },
                            label = { Text("এডমিন পিন", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                        OutlinedTextField(
                            value = siteNoticeNew,
                            onValueChange = { siteNoticeNew = it },
                            label = { Text("সাইট নোটিশ ব্যানার", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Button(
                        onClick = {
                            vm.saveSettings(
                                settings.copy(
                                    bkashNumber = bkashNum.trim(),
                                    bkashRule = bkashRule.trim(),
                                    nagadNumber = nagadNum.trim(),
                                    nagadRule = nagadRule.trim(),
                                    rocketNumber = rocketNum.trim(),
                                    rocketRule = rocketRule.trim(),
                                    upayNumber = upayNum.trim(),
                                    upayRule = upayRule.trim(),
                                    depositRulesText = rulesText.trim(),
                                    pendingProcessingNotice = pendingNotice.trim(),
                                    minDeposit = minDep.toDoubleOrNull() ?: 50.0,
                                    minWithdraw = minWith.toDoubleOrNull() ?: 100.0,
                                    minBet = minBet.toDoubleOrNull() ?: 1.0,
                                    depositBonusPercent = depBonus.toDoubleOrNull() ?: 5.0,
                                    referralDepositCommissionPercent = refDepComm.toDoubleOrNull() ?: 3.0,
                                    referralBetCommissionPercent = refBetComm.toDoubleOrNull() ?: 1.0,
                                    referralFixedBonus = refFixed.toDoubleOrNull() ?: 25.0,
                                    telegramSupportUsername = telegramUser.trim(),
                                    adminPin = adminPinNew.trim().ifEmpty { "admin1234" },
                                    siteNotice = siteNoticeNew.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💾 সকল পেমেন্ট নাম্বার, নিয়ম ও রেফার সেটিংস সেভ করুন", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // ====================================================================
        // TAB 5: 1XBET CRICKET & SPORTS MATCH MANAGER
        // ====================================================================
        if (activeAdminTab == "sports") {
            item {
                var sportType by remember { mutableStateOf("cricket") }
                var tournament by remember { mutableStateOf("BPL T20 Official") }
                var teamA by remember { mutableStateOf("") }
                var teamB by remember { mutableStateOf("") }
                var oddsA by remember { mutableStateOf("1.85") }
                var oddsB by remember { mutableStateOf("1.95") }
                var runsLine by remember { mutableStateOf("175.5 Runs") }
                var scoreText by remember { mutableStateOf("LIVE 0/0") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DkSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🏏 নতুন ক্রিকেট বা ফুটবল ম্যাচ যুক্ত করুন (1xBet স্টাইল)",
                        color = DkGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sportType,
                            onValueChange = { sportType = it },
                            label = { Text("খেলা (cricket / football)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tournament,
                            onValueChange = { tournament = it },
                            label = { Text("টুর্নামেন্ট নাম", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = teamA,
                            onValueChange = { teamA = it },
                            label = { Text("টিম ১ (Team A)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = teamB,
                            onValueChange = { teamB = it },
                            label = { Text("টিম ২ (Team B)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = oddsA,
                            onValueChange = { oddsA = it },
                            label = { Text("টিম ১ অডস", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = oddsB,
                            onValueChange = { oddsB = it },
                            label = { Text("টিম ২ অডস", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = runsLine,
                            onValueChange = { runsLine = it },
                            label = { Text("রান/গোল লাইন", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = scoreText,
                        onValueChange = { scoreText = it },
                        label = { Text("লাইভ স্কোর বা শুরুর সময়", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (teamA.isNotBlank() && teamB.isNotBlank()) {
                                vm.adminAddSportsMatch(
                                    SportsMatchEntity(
                                        sport = sportType.trim().lowercase(),
                                        tournament = tournament.trim(),
                                        teamA = teamA.trim(),
                                        teamB = teamB.trim(),
                                        oddsA = oddsA.toDoubleOrNull() ?: 1.90,
                                        oddsB = oddsB.toDoubleOrNull() ?: 1.90,
                                        runsLine = runsLine.trim(),
                                        score = scoreText.trim(),
                                        status = "live"
                                    )
                                )
                                teamA = ""
                                teamB = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("➕ নতুন ম্যাচ যুক্ত করুন", fontWeight = FontWeight.Black)
                    }
                }
            }

            // Pending Sports Bets Settlement
            val pendingSportsBets = allBets.filter { it.gameType.startsWith("sports") && it.status == "pending" }
            if (pendingSportsBets.isNotEmpty()) {
                item {
                    Text(
                        text = "পেন্ডিং স্পোর্টস বেট সেটেলমেন্ট (${pendingSportsBets.size})",
                        color = DkGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                items(pendingSportsBets, key = { it.id }) { sBet ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DkSurface)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${sBet.username} · স্টেক: ৳${sBet.betAmount.toInt()} (@${sBet.multiplier}x)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = sBet.prediction, color = DkGold, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { vm.adminSettleSportsBet(sBet.id, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("WIN দিন", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { vm.adminSettleSportsBet(sBet.id, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("LOSS দিন", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            items(sportsMatches, key = { it.id }) { m ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DkSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${m.teamA} vs ${m.teamB}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${m.tournament} · ${m.score} (${m.oddsA}x / ${m.oddsB}x)", color = DkTextSecondary, fontSize = 10.sp)
                    }
                    Button(
                        onClick = { vm.adminDeleteSportsMatch(m.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White)
                    ) {
                        Text("মুছুন", fontSize = 10.sp)
                    }
                }
            }
        }

        // ====================================================================
        // TAB 6: USERS, GIFT CODES & SUPPORT CHAT REPLY
        // ====================================================================
        if (activeAdminTab == "users_chat") {
            item {
                var newGiftCode by remember { mutableStateOf("") }
                var newGiftAmt by remember { mutableStateOf("50") }
                var adminChatReply by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DkSurface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎟️ নতুন গিফট কোড তৈরি করুন:", color = DkGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newGiftCode,
                            onValueChange = { newGiftCode = it.uppercase() },
                            label = { Text("কোড নাম", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newGiftAmt,
                            onValueChange = { newGiftAmt = it },
                            label = { Text("টাকা (৳)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                        Button(
                            onClick = {
                                if (newGiftCode.isNotBlank()) {
                                    vm.adminCreateGiftCode(newGiftCode, newGiftAmt.toDoubleOrNull() ?: 50.0, 500)
                                    newGiftCode = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black)
                        ) {
                            Text("তৈরি", fontWeight = FontWeight.Black)
                        }
                    }

                    Text("💬 সাপোর্ট চ্যাট এডমিন রিপ্লাই (${chatMessages.size} মেসেজ):", color = DkSky, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = adminChatReply,
                            onValueChange = { adminChatReply = it },
                            label = { Text("ইউজারদের উদ্দেশ্যে সাপোর্ট রিপ্লাই লিখুন...", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (adminChatReply.isNotBlank()) {
                                    vm.adminSendSupportReply(adminChatReply)
                                    adminChatReply = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkSky, contentColor = Color.Black)
                        ) {
                            Text("পাঠান", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "👥 সকল নিবন্ধিত ইউজার ও মোট বেট হিসাব (${allUsers.size} জন):",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            items(allUsers, key = { it.id }) { u ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DkSurface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${u.username} (${u.phone})", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text("ব্যালেন্স: ৳${String.format("%.2f", u.balance)}", color = DkGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Text(
                        text = "মোট বেট: ৳${u.totalBetAmount.toInt()} | মোট জয়: ৳${u.totalWinAmount.toInt()} | ডিপোজিট: ৳${u.totalDeposited.toInt()} | উইথড্র: ৳${u.totalWithdrawn.toInt()}",
                        color = DkEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { vm.adminAdjustUserBalance(u.id, 100.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+৳100 যোগ", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                        Button(
                            onClick = { vm.adminAdjustUserBalance(u.id, -100.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = DkSurfaceVariant, contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("-৳100 কর্তন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { vm.adminToggleBanUser(u.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (u.isBanned) DkSky else DkCrimson,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (u.isBanned) "আনব্যান" else "ব্যান করুন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DkBackground)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = DkTextSecondary, fontSize = 10.sp)
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun BetStatPill(
    modifier: Modifier = Modifier,
    label: String,
    amountText: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(amountText, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
    }
}

@Composable
private fun ForceModeButton(
    modifier: Modifier = Modifier,
    label: String,
    active: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) color else DkBackground)
            .border(1.5.dp, color, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (active) Color.Black else Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}
