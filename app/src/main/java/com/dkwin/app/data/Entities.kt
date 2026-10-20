package com.dkwin.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val phone: String,
    val password: String,
    val balance: Double = 0.0, // Default 0 balance on registration
    val bonusBalance: Double = 0.0,
    val commissionBalance: Double = 0.0,
    val vipLevel: Int = 1,
    val vipExp: Int = 0,
    val inviteCode: String,
    val referredBy: String? = null,
    val isBanned: Boolean = false,
    val lastCheckInDate: String? = null,
    val checkInStreak: Int = 0,
    val lastSpinDate: String? = null,
    val totalDeposited: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val totalBetAmount: Double = 0.0,
    val totalWinAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val username: String,
    val phone: String,
    val type: String, // deposit, withdraw, bonus, commission, gift_code, spin_reward
    val amount: Double,
    val method: String, // bkash, nagad, rocket, upay, usdt, system
    val paymentMode: String = "Send Money", // Send Money, Cash Out, Payment
    val accountNumber: String,
    val trxId: String = "",
    val status: String = "pending", // pending, completed, rejected
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_bets")
data class GameBetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val username: String,
    val gameType: String, // wingo, crash_aviator, crash_jetx, crash_spacexy, crash_cricket, thimbles, slots, sports_single, sports_multi, mines, dice, dragon_tiger, roulette
    val gameId: String = "",
    val betAmount: Double,
    val feeAmount: Double = 0.0,
    val netStake: Double = 0.0,
    val winAmount: Double = 0.0,
    val multiplier: Double = 0.0,
    val prediction: String, // green, red, violet, big, small, 0..9, or sports selections
    val outcome: String = "",
    val status: String = "pending", // pending, settled
    val isWin: Boolean = false,
    val periodNumber: String = "",
    val timerType: String = "1m",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wingo_history")
data class WingoRoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val periodNumber: String,
    val timerType: String = "1m", // 30s, 1m, 3m, 5m
    val winningNumber: Int,
    val winningColor: String, // red, green, red_violet, green_violet
    val winningSize: String, // big, small
    val totalBetPool: Double = 0.0,
    val totalPayout: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sports_matches")
data class SportsMatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sport: String, // cricket, football, tennis
    val tournament: String,
    val teamA: String,
    val teamB: String,
    val oddsA: Double,
    val oddsDraw: Double = 3.20,
    val oddsB: Double,
    val tossOddsA: Double = 1.90,
    val tossOddsB: Double = 1.90,
    val runsLine: String = "165.5 Runs",
    val overOdds: Double = 1.85,
    val underOdds: Double = 1.85,
    val status: String = "live", // live, upcoming, finished
    val score: String = "0/0 (0.0 ov)",
    val startTime: String = "LIVE",
    val winner: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "gift_codes")
data class GiftCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val amount: Double,
    val maxClaims: Int = 500,
    val claimsCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val username: String,
    val message: String,
    val isSupport: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_settings")
data class SiteSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val bkashNumber: String = "01788223344",
    val bkashRule: String = "সেন্ড মানি (Send Money)",
    val nagadNumber: String = "01899112233",
    val nagadRule: String = "ক্যাশ আউট (Cash Out)",
    val rocketNumber: String = "01955443322",
    val rocketRule: String = "সেন্ড মানি (Send Money)",
    val upayNumber: String = "01622334455",
    val upayRule: String = "ক্যাশ আউট (Cash Out)",
    val usdtAddress: String = "TQx7Y2bnWpL8mQ9vZxKrP3sD1fG5hJ4kL8",
    val depositRulesText: String = "১. উপরে প্রদর্শিত নাম্বার কপি করুন।\n২. নির্ধারিত নিয়ম অনুযায়ী (Send Money অথবা Cash Out) টাকা পাঠান।\n৩. টাকা পাঠানোর পর সঠিক TrxID ও আপনার প্রেরক নাম্বার দিয়ে সাবমিট করুন।",
    val pendingProcessingNotice: String = "আপনার ব্যালেন্স পেতে ২ মিনিট থেকে সর্বোচ্চ ১০ মিনিট সময় লাগতে পারে। এডমিন যাচাই করে অনুমোদন করা মাত্রই ব্যালেন্স যুক্ত হবে।",
    val minDeposit: Double = 50.0,
    val minWithdraw: Double = 100.0,
    val minBet: Double = 1.0,
    val depositBonusPercent: Double = 5.0,
    val referralDepositCommissionPercent: Double = 3.0,
    val referralBetCommissionPercent: Double = 1.0,
    val referralFixedBonus: Double = 25.0,
    val wingoFeePercent: Double = 2.0, // 1% to 3% house fee per bet on both win and loss
    val telegramSupportUsername: String = "@DKWIN_Official_Support",
    val adminPin: String = "admin1234",
    val siteNotice: String = "DKWIN অফিশিয়াল গেমিং প্ল্যাটফর্মে স্বাগতম! সর্বনিম্ন ডিপোজিট ৫০ টাকা এবং সর্বনিম্ন বেট মাত্র ১ টাকা। বিকাশ ও নগদে দ্রুত পেমেন্ট!",
    // Admin Game Result Control
    val forcedWingoMode: String = "AUTO_BALANCED", // AUTO_BALANCED, FORCE_BIG, FORCE_SMALL, FORCE_GREEN, FORCE_RED, FORCE_VIOLET, FORCE_NUMBER
    val forcedWingoNumber: Int = -1, // 0..9 when FORCE_NUMBER
    val forcedCrashMode: String = "AUTO", // AUTO, FORCE_CRASH
    val forcedCrashMultiplier: Double = 1.80,
    val thimblesWinRatePercent: Int = 45, // 0..100
    val slotsWinRatePercent: Int = 45 // 0..100
)
