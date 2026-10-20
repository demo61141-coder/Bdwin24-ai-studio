package com.dkwin.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SupportAgent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.TransactionEntity
import com.dkwin.app.data.UserEntity
import com.dkwin.app.ui.DkwinViewModel
import com.dkwin.app.ui.MainTab
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
fun WalletTabScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    val txns by vm.userTransactions.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_tab_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Wallet Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF064E3B), Color(0xFF111827), Color(0xFF1E1B4B))
                        )
                    )
                    .border(1.5.dp, DkGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "মূল ওয়ালেট ব্যালেন্স (Main Balance)",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "৳${String.format("%.2f", user?.balance ?: 0.0)}",
                    color = DkGold,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("বোনাস ব্যালেন্স", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "৳${String.format("%.2f", user?.bonusBalance ?: 0.0)}",
                            color = DkEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text("রেফার কমিশন", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "৳${String.format("%.2f", user?.commissionBalance ?: 0.0)}",
                            color = DkSky,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("মোট উত্তোলন", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "৳${String.format("%.0f", user?.totalWithdrawn ?: 0.0)}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { vm.setShowDepositSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("wallet_deposit_btn")
                    ) {
                        Text("➕ ডিপোজিট (৳${settings.minDeposit.toInt()}+)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Button(
                        onClick = { vm.setShowWithdrawSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("wallet_withdraw_btn")
                    ) {
                        Text("⬆️ উইথড্র (৳${settings.minWithdraw.toInt()}+)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }

        // Pending Notice Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DkGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = settings.pendingProcessingNotice,
                    color = Color(0xFFFDE68A),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // Transactions List
        item {
            Text(
                text = "লেনদেন ও পেন্ডিং রিকোয়েস্ট হিস্ট্রি (${txns.size})",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (txns.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("এখনো কোনো ডিপোজিট বা উইথড্র রিকোয়েস্ট নেই", color = DkTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(txns, key = { it.id }) { txn ->
                TransactionRowCard(txn = txn, pendingNotice = settings.pendingProcessingNotice)
            }
        }
    }
}

@Composable
private fun TransactionRowCard(
    txn: TransactionEntity,
    pendingNotice: String
) {
    val isPending = txn.status == "pending"
    val isCompleted = txn.status == "completed"
    val statusColor = when {
        isCompleted -> DkEmerald
        isPending -> DkGold
        else -> DkCrimson
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DkSurface)
            .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${txn.type.uppercase()} · ${txn.method.uppercase()} (${txn.paymentMode})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "নাম্বার: ${txn.accountNumber} ${if (txn.trxId.isNotBlank()) "| TrxID: ${txn.trxId}" else ""}",
                    color = DkTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (txn.type == "withdraw") "-" else "+"}৳${txn.amount.toInt()}",
                    color = statusColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (txn.status) {
                            "pending" -> "⏳ PENDING"
                            "completed" -> "✅ APPROVED"
                            else -> "❌ REJECTED"
                        },
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (isPending) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DkGold.copy(alpha = 0.12f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "⏳ পেন্ডিং: $pendingNotice",
                    color = DkGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
            }
        } else if (txn.note.isNotBlank()) {
            Text(
                text = txn.note,
                color = DkTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ActivityTabScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    var giftInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("activity_tab_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily Check-in Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "📅 ৭ দিনের ডেইলি চেক-ইন বোনাস",
                    color = DkGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "প্রতিদিন লগইন করে ফ্রি ক্যাশ বোনাস ক্লেইম করুন! বর্তমান স্ট্রিক: ${user?.checkInStreak ?: 0} দিন",
                    color = DkTextSecondary,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 15, 20, 30, 50, 100).forEachIndexed { idx, amt ->
                        val claimed = (user?.checkInStreak ?: 0) > idx
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (claimed) DkEmerald else DkSurfaceVariant)
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "D${idx + 1}",
                                color = if (claimed) Color.Black else DkTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "৳$amt",
                                color = if (claimed) Color.Black else DkGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Button(
                    onClick = { vm.claimDailyCheckIn() },
                    colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎁 আজকের বোনাস সংগ্রহ করুন", fontWeight = FontWeight.Black)
                }
            }
        }

        // Gift Code Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DkSurface)
                    .border(1.dp, DkViolet.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🎟️ অফিশিয়াল গিফট কোড রিডিম (Gift Code)",
                    color = DkViolet,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "আমাদের টেলিগ্রাম চ্যানেল (${settings.telegramSupportUsername}) থেকে পাওয়া গিফট কোড বসিয়ে বোনাস নিন।",
                    color = DkTextSecondary,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = giftInput,
                    onValueChange = { giftInput = it.uppercase() },
                    label = { Text("গিফট কোড লিখুন (যেমন: DKWIN2026)", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DkViolet,
                        unfocusedBorderColor = DkSurfaceVariant,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (giftInput.isNotBlank()) {
                            vm.claimGiftCode(giftInput)
                            giftInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DkViolet, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✨ গিফট কোড ক্লেইম করুন", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun PromoReferralTabScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    val referredUsers by vm.referredUsers.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("promo_tab_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF31102F), Color(0xFF111827), Color(0xFF064E3B))
                        )
                    )
                    .border(1.dp, DkGold.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🤝 DKWIN ভিআইপি রেফার ও এজেন্ট কমিশন",
                    color = DkGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "বন্ধুদের আমন্ত্রণ জানান এবং আজীবন অটো কমিশন আয় করুন!",
                    color = Color.White,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Text("সাইনআপ বোনাস", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "৳${settings.referralFixedBonus.toInt()}",
                            color = DkGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Text("ডিপোজিট কমিশন", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "${settings.referralDepositCommissionPercent}%",
                            color = DkEmerald,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Text("বেট কমিশন", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            "${settings.referralBetCommissionPercent}%",
                            color = DkSky,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                val code = user?.inviteCode ?: "LOGIN_FIRST"
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
                        Text("আপনার রেফার কোড:", color = DkTextSecondary, fontSize = 10.sp)
                        Text(
                            text = code,
                            color = DkGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(code))
                            vm.showToast("রেফার কোড কপি হয়েছে: $code")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি করুন", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "আপনার রেফারকৃত ইউজার তালিকা (${referredUsers.size} জন)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (referredUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("এখনো কেউ আপনার রেফার কোডে যুক্ত হয়নি", color = DkTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(referredUsers) { refUser ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DkSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(refUser.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "মোট ডিপোজিট: ৳${refUser.totalDeposited.toInt()} | মোট বেট: ৳${refUser.totalBetAmount.toInt()}",
                        color = DkEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MineProfileTabScreen(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mine_tab_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DkSurface)
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (user == null) {
                    Text(
                        text = "আপনি এখনো লগইন করেননি",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "নতুন একাউন্ট খুললে ডিফল্ট ব্যালেন্স ৳0.00 থাকবে। মিনিমাম ৳${settings.minDeposit.toInt()} ডিপোজিট করে গেম খেলুন!",
                        color = DkTextSecondary,
                        fontSize = 12.sp
                    )
                    Button(
                        onClick = { vm.setShowAuthSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("লগইন / নতুন একাউন্ট খুলুন", fontWeight = FontWeight.Black)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(DkGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.username.take(2).uppercase(),
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.username,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "📱 ${user.phone} · VIP ${user.vipLevel}",
                                    color = DkGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { vm.logout() },
                            colors = ButtonDefaults.buttonColors(containerColor = DkCrimson, contentColor = Color.White)
                        ) {
                            Text("লগআউট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMiniBox(modifier = Modifier.weight(1f), label = "মোট বেট", value = "৳${user.totalBetAmount.toInt()}")
                        StatMiniBox(modifier = Modifier.weight(1f), label = "মোট জয়", value = "৳${user.totalWinAmount.toInt()}")
                        StatMiniBox(modifier = Modifier.weight(1f), label = "ডিপোজিট", value = "৳${user.totalDeposited.toInt()}")
                    }
                }
            }
        }

        // Support & Admin Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { vm.setShowSupportChatSheet(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = DkSky, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "২৪/৭ লাইভ সাপোর্ট ও টেলিগ্রাম (${settings.telegramSupportUsername})",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { vm.selectTab(MainTab.ADMIN) },
                    colors = ButtonDefaults.buttonColors(containerColor = DkSurfaceVariant, contentColor = DkGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_open_admin_btn")
                ) {
                    Text("🛡️ DKWIN মাস্টার এডমিন প্যানেল (Admin Control)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun StatMiniBox(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DkSurfaceVariant)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = DkTextSecondary, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

// ============================================================================
// MODALS: Auth, Deposit (Custom Amount + Rules + Pending Notice), Withdraw, Support Chat + Telegram
// ============================================================================

@Composable
fun AuthModalDialog(
    vm: DkwinViewModel,
    onDismiss: () -> Unit
) {
    var isRegister by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(DkSurface)
                .border(1.5.dp, DkGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRegister) "নতুন একাউন্ট খুলুন (ব্যালেন্স ৳0.00)" else "একাউন্টে লগইন করুন",
                    color = DkGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DkBackground)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRegister) DkGold else Color.Transparent)
                        .clickable { isRegister = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "রেজিস্টার (Register)",
                        color = if (isRegister) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isRegister) DkGold else Color.Transparent)
                        .clickable { isRegister = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "লগইন (Login)",
                        color = if (!isRegister) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (isRegister) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("ইউজারনেম (Username)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (isRegister) "মোবাইল নাম্বার (১১ ডিজিট)" else "মোবাইল নাম্বার বা ইউজারনেম", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("পাসওয়ার্ড (Password)", fontSize = 11.sp) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (isRegister) {
                OutlinedTextField(
                    value = referralCode,
                    onValueChange = { referralCode = it.uppercase() },
                    label = { Text("রেফার কোড (ঐচ্ছিক - Optional)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (isRegister) {
                        vm.register(username, phone, password, referralCode)
                    } else {
                        vm.login(phone, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_submit_btn")
            ) {
                Text(
                    text = if (isRegister) "একাউন্ট তৈরি করুন (ব্যালেন্স ৳0)" else "লগইন করুন",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DepositModalDialog(
    vm: DkwinViewModel,
    settings: SiteSettingsEntity,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var method by remember { mutableStateOf("bkash") }
    var customAmountInput by remember { mutableStateOf(settings.minDeposit.toInt().toString()) }
    var senderPhone by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }

    val activeNumber = when (method) {
        "bkash" -> settings.bkashNumber
        "nagad" -> settings.nagadNumber
        "rocket" -> settings.rocketNumber
        "upay" -> settings.upayNumber
        else -> settings.usdtAddress
    }

    val activeRuleMode = when (method) {
        "bkash" -> settings.bkashRule
        "nagad" -> settings.nagadRule
        "rocket" -> settings.rocketRule
        "upay" -> settings.upayRule
        else -> "USDT TRC20 Transfer"
    }

    val parsedAmount = customAmountInput.toDoubleOrNull() ?: 0.0
    val bonusAmt = (parsedAmount * settings.depositBonusPercent) / 100.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(DkSurface)
                .border(1.5.dp, DkEmerald.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ওয়ালেট ডিপোজিট (Deposit)",
                        color = DkEmerald,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "সর্বনিম্ন ডিপোজিট ৳${settings.minDeposit.toInt()} · +${settings.depositBonusPercent.toInt()}% এক্সট্রা বোনাস",
                        color = DkGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Payment Gateways
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "bkash" to "বিকাশ",
                    "nagad" to "নগদ",
                    "rocket" to "রকেট",
                    "upay" to "উপায়"
                ).forEach { (id, label) ->
                    val sel = method == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) DkEmerald else DkBackground)
                            .clickable { method = id }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (sel) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Official Number + Admin Controlled Rule Badge (Send Money / Cash Out)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkBackground)
                    .border(1.dp, DkGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "অফিসিয়াল ${method.uppercase()} নাম্বার:",
                        color = DkTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(DkGold)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "নিয়ম: $activeRuleMode",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeNumber,
                        color = DkGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(activeNumber))
                            vm.showToast("নাম্বার কপি হয়েছে: $activeNumber")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DkSurfaceVariant, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("কপি করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Deposit Instructions controlled from Admin Panel
                Text(
                    text = settings.depositRulesText,
                    color = Color(0xFFCBD5E1),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }

            // Quick Chips + Custom Deposit Amount Input
            Text(
                text = "ডিপোজিট এমাউন্ট সিলেক্ট করুন বা নিজের মতো লিখুন (৳${settings.minDeposit.toInt()}+):",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(50, 100, 200, 500, 1000, 5000).forEach { amt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (customAmountInput == amt.toString()) DkEmerald else DkSurfaceVariant)
                            .clickable { customAmountInput = amt.toString() }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "৳$amt",
                            color = if (customAmountInput == amt.toString()) Color.Black else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            OutlinedTextField(
                value = customAmountInput,
                onValueChange = { customAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("নিজের মতো ডিপোজিট টাকা বসান (৳)", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("deposit_custom_amount_input")
            )

            Text(
                text = "আপনি পাবেন: ৳${parsedAmount.toInt()} + বোনাস ৳${bonusAmt.toInt()} = মোট ৳${(parsedAmount + bonusAmt).toInt()}",
                color = DkEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )

            OutlinedTextField(
                value = senderPhone,
                onValueChange = { senderPhone = it },
                label = { Text("আপনার যে নাম্বার থেকে টাকা পাঠিয়েছেন (১১ ডিজিট)", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = trxId,
                onValueChange = { trxId = it.uppercase() },
                label = { Text("ট্রানজেকশন আইডি (TrxID)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Admin-Editable 2 to 10 Min Pending Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DkGold.copy(alpha = 0.14f))
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "⏳ নোট: ${settings.pendingProcessingNotice}",
                    color = DkGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }

            Button(
                onClick = {
                    vm.submitDeposit(parsedAmount, method, senderPhone, trxId)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DkEmerald, contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("deposit_submit_btn")
            ) {
                Text("ডিপোজিট রিকোয়েস্ট সাবমিট করুন", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun WithdrawModalDialog(
    vm: DkwinViewModel,
    user: UserEntity?,
    settings: SiteSettingsEntity,
    onDismiss: () -> Unit
) {
    var method by remember { mutableStateOf("bkash") }
    var amountInput by remember { mutableStateOf(settings.minWithdraw.toInt().toString()) }
    var receiverPhone by remember { mutableStateOf(user?.phone ?: "") }
    var password by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(DkSurface)
                .border(1.5.dp, DkGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ব্যালেন্স উইথড্র (Withdraw)",
                        color = DkGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "উত্তোলনযোগ্য ব্যালেন্স: ৳${String.format("%.2f", user?.balance ?: 0.0)}",
                        color = DkEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("bkash" to "বিকাশ", "nagad" to "নগদ", "rocket" to "রকেট", "upay" to "উপায়").forEach { (id, label) ->
                    val sel = method == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) DkGold else DkBackground)
                            .clickable { method = id }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (sel) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("উইথড্র এমাউন্ট (সর্বনিম্ন ৳${settings.minWithdraw.toInt()})", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = receiverPhone,
                onValueChange = { receiverPhone = it },
                label = { Text("আপনার পার্সোনাল ${method.uppercase()} নাম্বার", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("আপনার লগইন পাসওয়ার্ড দিন", fontSize = 11.sp) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DkGold.copy(alpha = 0.14f))
                    .border(1.dp, DkGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "⏳ নোট: ${settings.pendingProcessingNotice}",
                    color = DkGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 15.sp
                )
            }

            Button(
                onClick = {
                    val amt = amountInput.toDoubleOrNull() ?: 0.0
                    vm.submitWithdraw(amt, method, receiverPhone, password)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("withdraw_submit_btn")
            ) {
                Text("উইথড্র রিকোয়েস্ট সাবমিট করুন", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SupportChatModalDialog(
    vm: DkwinViewModel,
    settings: SiteSettingsEntity,
    onDismiss: () -> Unit
) {
    val messages by vm.chatMessages.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var msgInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(560.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DkSurface)
                .border(1.5.dp, DkSky.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💬 DKWIN ২৪/৭ লাইভ ও টেলিগ্রাম সাপোর্ট",
                    color = DkSky,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Telegram Support Card (Admin-controlled Telegram Username)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0C4A6E))
                    .border(1.dp, DkSky, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "✈️ অফিশিয়াল টেলিগ্রাম কাস্টমার সাপোর্ট:",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = settings.telegramSupportUsername,
                        color = DkGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                clipboard.setText(AnnotatedString(settings.telegramSupportUsername))
                                vm.showToast("টেলিগ্রাম ইউজারনেম কপি হয়েছে!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkSurface, contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("কপি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                val cleanHandle = settings.telegramSupportUsername.removePrefix("@").trim()
                                runCatching {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$cleanHandle"))
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DkGold, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("টেলিগ্রামে যান", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DkBackground)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { m ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (m.isSupport) Color(0xFF064E3B) else DkSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (m.isSupport) "🛡️ ${m.username}" else "👤 ${m.username}",
                            color = if (m.isSupport) DkGold else DkSky,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = m.message,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Send Message Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = msgInput,
                    onValueChange = { msgInput = it },
                    placeholder = { Text("আপনার সমস্যা বা মেসেজ লিখুন...", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (msgInput.isNotBlank()) {
                            vm.sendUserChat(msgInput)
                            msgInput = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DkSky)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}
