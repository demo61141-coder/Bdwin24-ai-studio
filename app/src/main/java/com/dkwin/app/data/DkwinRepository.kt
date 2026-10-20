package com.dkwin.app.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

data class WingoLiveBetBreakdown(
    val totalBetAmount: Double = 0.0,
    val totalBetsCount: Int = 0,
    val bigBetAmount: Double = 0.0,
    val smallBetAmount: Double = 0.0,
    val greenBetAmount: Double = 0.0,
    val redBetAmount: Double = 0.0,
    val violetBetAmount: Double = 0.0,
    val numberBets: Map<Int, Double> = (0..9).associateWith { 0.0 }
)

data class MultiBetSelection(
    val matchId: Int,
    val matchTitle: String,
    val marketName: String, // Match Winner, Toss Winner, Total Runs
    val pickName: String,
    val odds: Double
)

class DkwinRepository(private val db: DkwinDatabase) {
    private val dao = db.dao()

    val allUsersFlow: Flow<List<UserEntity>> = dao.getAllUsersFlow()
    val settingsFlow: Flow<SiteSettingsEntity?> = dao.getSettingsFlow()
    val allTransactionsFlow: Flow<List<TransactionEntity>> = dao.getAllTransactionsFlow()
    val allGameBetsFlow: Flow<List<GameBetEntity>> = dao.getAllGameBetsFlow()
    val allPendingWingoBetsFlow: Flow<List<GameBetEntity>> = dao.getAllPendingWingoBetsFlow()
    val allSportsMatchesFlow: Flow<List<SportsMatchEntity>> = dao.getAllSportsMatchesFlow()
    val allGiftCodesFlow: Flow<List<GiftCodeEntity>> = dao.getAllGiftCodesFlow()
    val allChatMessagesFlow: Flow<List<ChatMessageEntity>> = dao.getAllChatMessagesFlow()

    fun getUserFlow(userId: Int): Flow<UserEntity?> = dao.getUserFlowById(userId)
    fun getUserTransactionsFlow(userId: Int): Flow<List<TransactionEntity>> = dao.getUserTransactionsFlow(userId)
    fun getUserGameBetsFlow(userId: Int): Flow<List<GameBetEntity>> = dao.getUserGameBetsFlow(userId)
    fun getWingoHistoryFlow(timerType: String): Flow<List<WingoRoundEntity>> = dao.getWingoHistoryFlow(timerType)
    fun getReferredUsersFlow(inviteCode: String): Flow<List<UserEntity>> = dao.getReferredUsersFlow(inviteCode)

    suspend fun ensureSeeded() {
        db.withTransaction {
            val currentSettings = dao.getSettings()
            if (currentSettings == null) {
                dao.saveSettings(SiteSettingsEntity())
            }

            // Seed initial Win Go rounds for all timer types if empty
            val timers = listOf("30s", "1m", "3m", "5m")
            for (t in timers) {
                val existing = dao.getWingoHistoryList(t)
                if (existing.isEmpty()) {
                    val basePeriod = computeCurrentPeriodNumber(t, System.currentTimeMillis()) - 10
                    for (i in 1..10) {
                        val num = (i * 3 + t.hashCode().let { if (it < 0) -it else it }) % 10
                        dao.insertWingoRound(
                            WingoRoundEntity(
                                periodNumber = (basePeriod + i).toString(),
                                timerType = t,
                                winningNumber = num,
                                winningColor = getColorForNumber(num),
                                winningSize = if (num >= 5) "big" else "small"
                            )
                        )
                    }
                }
            }

            // Seed default sports matches if none exist
            val sampleMatch = dao.getSportsMatchById(1)
            if (sampleMatch == null) {
                val matches = listOf(
                    SportsMatchEntity(
                        sport = "cricket",
                        tournament = "BPL T20 Bangladesh",
                        teamA = "Comilla Victorians",
                        teamB = "Fortune Barishal",
                        oddsA = 1.85,
                        oddsDraw = 12.0,
                        oddsB = 1.98,
                        tossOddsA = 1.90,
                        tossOddsB = 1.90,
                        runsLine = "172.5 Runs",
                        overOdds = 1.88,
                        underOdds = 1.88,
                        status = "live",
                        score = "142/3 (15.4 ov)",
                        startTime = "LIVE"
                    ),
                    SportsMatchEntity(
                        sport = "cricket",
                        tournament = "ICC Champions Trophy",
                        teamA = "Bangladesh",
                        teamB = "India",
                        oddsA = 2.65,
                        oddsDraw = 15.0,
                        oddsB = 1.52,
                        tossOddsA = 1.92,
                        tossOddsB = 1.92,
                        runsLine = "285.5 Runs",
                        overOdds = 1.90,
                        underOdds = 1.85,
                        status = "live",
                        score = "198/4 (34.2 ov)",
                        startTime = "LIVE"
                    ),
                    SportsMatchEntity(
                        sport = "cricket",
                        tournament = "IPL T20 Official",
                        teamA = "Chennai Super Kings",
                        teamB = "Mumbai Indians",
                        oddsA = 1.90,
                        oddsDraw = 14.0,
                        oddsB = 1.92,
                        tossOddsA = 1.90,
                        tossOddsB = 1.90,
                        runsLine = "188.5 Runs",
                        overOdds = 1.87,
                        underOdds = 1.87,
                        status = "upcoming",
                        score = "Starts Today 8:00 PM",
                        startTime = "20:00 BD"
                    ),
                    SportsMatchEntity(
                        sport = "football",
                        tournament = "UEFA Champions League",
                        teamA = "Real Madrid",
                        teamB = "Manchester City",
                        oddsA = 2.25,
                        oddsDraw = 3.40,
                        oddsB = 2.90,
                        tossOddsA = 1.90,
                        tossOddsB = 1.90,
                        runsLine = "2.5 Goals",
                        overOdds = 1.75,
                        underOdds = 2.05,
                        status = "live",
                        score = "1 - 1 (62')",
                        startTime = "LIVE"
                    ),
                    SportsMatchEntity(
                        sport = "football",
                        tournament = "English Premier League",
                        teamA = "Arsenal",
                        teamB = "Liverpool",
                        oddsA = 2.15,
                        oddsDraw = 3.30,
                        oddsB = 3.10,
                        tossOddsA = 1.90,
                        tossOddsB = 1.90,
                        runsLine = "2.5 Goals",
                        overOdds = 1.80,
                        underOdds = 1.95,
                        status = "upcoming",
                        score = "Tonight 10:30 PM",
                        startTime = "22:30 BD"
                    )
                )
                matches.forEach { dao.insertSportsMatch(it) }

                dao.insertGiftCode(
                    GiftCodeEntity(
                        code = "DKWIN2026",
                        amount = 50.0,
                        maxClaims = 500
                    )
                )
                dao.insertGiftCode(
                    GiftCodeEntity(
                        code = "WELCOME100",
                        amount = 100.0,
                        maxClaims = 200
                    )
                )

                dao.insertChatMessage(
                    ChatMessageEntity(
                        userId = 0,
                        username = "DKWIN অফিশিয়াল সাপোর্ট",
                        message = "আসসালামু আলাইকুম! DKWIN ২৪/৭ লাইভ কাস্টমার সাপোর্টে আপনাকে স্বাগতম। যেকোনো ডিপোজিট, উইথড্র বা গেম সংক্রান্ত সাহায্যের জন্য এখানে মেসেজ দিন অথবা আমাদের অফিসিয়াল টেলিগ্রামে যোগাযোগ করুন।",
                        isSupport = true
                    )
                )
            }
        }
    }

    fun getDurationSeconds(timerType: String): Int = when (timerType) {
        "30s" -> 30
        "1m" -> 60
        "3m" -> 180
        "5m" -> 300
        else -> 60
    }

    fun computeCurrentPeriodNumber(timerType: String, nowMillis: Long): Long {
        val duration = getDurationSeconds(timerType)
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(nowMillis))
        val secondsSinceEpoch = nowMillis / 1000L
        val slotIndex = (secondsSinceEpoch / duration) % 10000L
        return "${dateStr}${slotIndex.toString().padStart(4, '0')}".toLong()
    }

    fun computeRemainingSeconds(timerType: String, nowMillis: Long): Int {
        val duration = getDurationSeconds(timerType)
        val secondsSinceEpoch = (nowMillis / 1000L).toInt()
        val elapsed = secondsSinceEpoch % duration
        return duration - elapsed
    }

    fun getColorForNumber(num: Int): String = when (num) {
        0 -> "red_violet"
        5 -> "green_violet"
        1, 3, 7, 9 -> "green"
        else -> "red"
    }

    // Registration: Default balance is strictly 0.0 as requested
    suspend fun registerUser(
        username: String,
        phone: String,
        password: String,
        referralCodeInput: String
    ): Result<UserEntity> = db.withTransaction {
        val cleanPhone = phone.trim()
        val cleanUsername = username.trim()
        if (cleanUsername.length < 3) {
            return@withTransaction Result.failure(Exception("ইউজারনেম কমপক্ষে ৩ অক্ষরের হতে হবে"))
        }
        if (cleanPhone.length < 11) {
            return@withTransaction Result.failure(Exception("সঠিক ১১ ডিজিটের মোবাইল নাম্বার দিন"))
        }
        if (password.length < 4) {
            return@withTransaction Result.failure(Exception("পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে"))
        }

        val existingPhone = dao.getUserByPhoneOrUsername(cleanPhone)
        val existingUser = dao.getUserByPhoneOrUsername(cleanUsername)
        if (existingPhone != null || existingUser != null) {
            return@withTransaction Result.failure(Exception("এই মোবাইল নাম্বার বা ইউজারনেম দিয়ে ইতিমধ্যে একাউন্ট খোলা হয়েছে"))
        }

        val settings = dao.getSettings() ?: SiteSettingsEntity()
        var validRefCode: String? = null
        if (referralCodeInput.isNotBlank()) {
            val referrer = dao.getUserByInviteCode(referralCodeInput.trim().uppercase())
            if (referrer != null) {
                validRefCode = referrer.inviteCode
                // Credit fixed referral bonus to referrer if configured
                if (settings.referralFixedBonus > 0) {
                    val updatedRef = referrer.copy(
                        balance = referrer.balance + settings.referralFixedBonus,
                        commissionBalance = referrer.commissionBalance + settings.referralFixedBonus
                    )
                    dao.updateUser(updatedRef)
                    dao.insertTransaction(
                        TransactionEntity(
                            userId = referrer.id,
                            username = referrer.username,
                            phone = referrer.phone,
                            type = "commission",
                            amount = settings.referralFixedBonus,
                            method = "referral",
                            paymentMode = "Bonus",
                            accountNumber = cleanUsername,
                            status = "completed",
                            note = "নতুন রেফার বোনাস ($cleanUsername)"
                        )
                    )
                }
            }
        }

        val inviteCode = "DK" + Random.nextInt(100000, 999999).toString()
        val newUser = UserEntity(
            username = cleanUsername,
            phone = cleanPhone,
            password = password,
            balance = 0.0, // Strictly 0 default balance on new account
            bonusBalance = 0.0,
            commissionBalance = 0.0,
            inviteCode = inviteCode,
            referredBy = validRefCode
        )
        val newId = dao.insertUser(newUser).toInt()
        val saved = dao.getUserById(newId)!!
        Result.success(saved)
    }

    suspend fun loginUser(phoneOrUsername: String, password: String): Result<UserEntity> {
        val user = dao.getUserByPhoneOrUsername(phoneOrUsername.trim())
            ?: return Result.failure(Exception("একাউন্ট খুঁজে পাওয়া যায়নি! প্রথমে রেজিস্টার করুন।"))
        if (user.isBanned) {
            return Result.failure(Exception("আপনার একাউন্টটি সাময়িকভাবে স্থগিত করা হয়েছে। সাপোর্টে যোগাযোগ করুন।"))
        }
        if (user.password != password) {
            return Result.failure(Exception("ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।"))
        }
        return Result.success(user)
    }

    // Deposit Request: Always created as PENDING so Admin can verify and Approve/Reject
    suspend fun submitDepositRequest(
        userId: Int,
        amount: Double,
        method: String,
        senderNumber: String,
        trxId: String
    ): Result<TransactionEntity> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val settings = dao.getSettings() ?: SiteSettingsEntity()

        if (amount < settings.minDeposit) {
            return@withTransaction Result.failure(
                Exception("সর্বনিম্ন ডিপোজিট ৳${settings.minDeposit.toInt()} টাকা")
            )
        }
        if (senderNumber.trim().length < 11 && method != "usdt") {
            return@withTransaction Result.failure(Exception("সঠিক ১১ ডিজিটের প্রেরক নাম্বার দিন"))
        }
        if (trxId.trim().length < 4) {
            return@withTransaction Result.failure(Exception("সঠিক ট্রানজেকশন আইডি (TrxID) লিখুন"))
        }

        val paymentMode = when (method.lowercase()) {
            "bkash" -> settings.bkashRule
            "nagad" -> settings.nagadRule
            "rocket" -> settings.rocketRule
            "upay" -> settings.upayRule
            else -> "Crypto Transfer"
        }

        val txn = TransactionEntity(
            userId = user.id,
            username = user.username,
            phone = user.phone,
            type = "deposit",
            amount = amount,
            method = method,
            paymentMode = paymentMode,
            accountNumber = senderNumber.trim(),
            trxId = trxId.trim().uppercase(),
            status = "pending",
            note = settings.pendingProcessingNotice
        )
        val id = dao.insertTransaction(txn).toInt()
        Result.success(txn.copy(id = id))
    }

    // Withdraw Request: Deducts balance immediately upon submission & sets status = "pending"
    suspend fun submitWithdrawRequest(
        userId: Int,
        amount: Double,
        method: String,
        receiverNumber: String,
        password: String
    ): Result<TransactionEntity> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val settings = dao.getSettings() ?: SiteSettingsEntity()

        if (user.password != password) {
            return@withTransaction Result.failure(Exception("আপনার লগইন পাসওয়ার্ড সঠিক নয়!"))
        }
        if (amount < settings.minWithdraw) {
            return@withTransaction Result.failure(
                Exception("সর্বনিম্ন উইথড্র ৳${settings.minWithdraw.toInt()} টাকা")
            )
        }
        if (user.balance < amount) {
            return@withTransaction Result.failure(Exception("আপনার একাউন্টে পর্যাপ্ত ব্যালেন্স নেই!"))
        }
        if (receiverNumber.trim().length < 11) {
            return@withTransaction Result.failure(Exception("সঠিক ১১ ডিজিটের পার্সোনাল নাম্বার দিন"))
        }

        // Deduct balance right away so user cannot submit duplicate withdrawals with same balance
        val updatedUser = user.copy(balance = max(0.0, user.balance - amount))
        dao.updateUser(updatedUser)

        val txn = TransactionEntity(
            userId = user.id,
            username = user.username,
            phone = user.phone,
            type = "withdraw",
            amount = amount,
            method = method,
            paymentMode = "Personal Receive",
            accountNumber = receiverNumber.trim(),
            trxId = "WD" + System.currentTimeMillis().toString().takeLast(6),
            status = "pending",
            note = settings.pendingProcessingNotice
        )
        val id = dao.insertTransaction(txn).toInt()
        Result.success(txn.copy(id = id))
    }

    // Strictly idempotent Admin Deposit Approval / Rejection
    suspend fun adminProcessDeposit(txnId: Int, approve: Boolean): Result<String> = db.withTransaction {
        val txn = dao.getTransactionById(txnId)
            ?: return@withTransaction Result.failure(Exception("রিকোয়েস্ট পাওয়া যায়নি"))

        // Prevent duplicate approval or rejection!
        if (txn.status != "pending") {
            return@withTransaction Result.failure(
                Exception("এই ডিপোজিট রিকোয়েস্টটি ইতিমধ্যে ${txn.status.uppercase()} করা হয়েছে!")
            )
        }

        val settings = dao.getSettings() ?: SiteSettingsEntity()
        val user = dao.getUserById(txn.userId)
            ?: return@withTransaction Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        if (approve) {
            val bonus = (txn.amount * settings.depositBonusPercent) / 100.0
            val totalCredit = txn.amount + bonus
            val updatedUser = user.copy(
                balance = user.balance + totalCredit,
                bonusBalance = user.bonusBalance + bonus,
                totalDeposited = user.totalDeposited + txn.amount,
                vipExp = user.vipExp + txn.amount.toInt(),
                vipLevel = 1 + ((user.vipExp + txn.amount.toInt()) / 5000).coerceAtMost(9)
            )
            dao.updateUser(updatedUser)
            dao.updateTransaction(
                txn.copy(
                    status = "completed",
                    note = "এডমিন কর্তৃক অনুমোদিত (+৳${totalCredit.toInt()} যুক্ত হয়েছে)"
                )
            )

            // Credit referral deposit commission to referrer if user was referred
            if (!user.referredBy.isNullOrBlank() && settings.referralDepositCommissionPercent > 0) {
                val referrer = dao.getUserByInviteCode(user.referredBy)
                if (referrer != null) {
                    val comm = (txn.amount * settings.referralDepositCommissionPercent) / 100.0
                    if (comm > 0) {
                        dao.updateUser(
                            referrer.copy(
                                balance = referrer.balance + comm,
                                commissionBalance = referrer.commissionBalance + comm
                            )
                        )
                        dao.insertTransaction(
                            TransactionEntity(
                                userId = referrer.id,
                                username = referrer.username,
                                phone = referrer.phone,
                                type = "commission",
                                amount = comm,
                                method = "referral",
                                paymentMode = "Commission",
                                accountNumber = user.username,
                                status = "completed",
                                note = "${user.username} এর ডিপোজিট থেকে ${settings.referralDepositCommissionPercent}% রেফার কমিশন"
                            )
                        )
                    }
                }
            }
            Result.success("ডিপোজিট অনুমোদিত হয়েছে! ৳${totalCredit.toInt()} ইউজার ব্যালেন্সে যোগ হয়েছে।")
        } else {
            dao.updateTransaction(
                txn.copy(
                    status = "rejected",
                    note = "এডমিন কর্তৃক বাতিল করা হয়েছে (ভুল TrxID বা টাকা পাওয়া যায়নি)"
                )
            )
            Result.success("ডিপোজিট রিকোয়েস্ট বাতিল (Rejected) করা হয়েছে।")
        }
    }

    // Strictly idempotent Admin Withdrawal Approval / Rejection
    // CRITICAL FIX: Approving a withdrawal NEVER adds money to the user's balance and cannot be clicked multiple times!
    suspend fun adminProcessWithdrawal(txnId: Int, approve: Boolean): Result<String> = db.withTransaction {
        val txn = dao.getTransactionById(txnId)
            ?: return@withTransaction Result.failure(Exception("উইথড্র রিকোয়েস্ট পাওয়া যায়নি"))

        // Strictly block if already completed or rejected!
        if (txn.status != "pending") {
            return@withTransaction Result.failure(
                Exception("এই উইথড্র রিকোয়েস্টটি ইতিমধ্যে ${txn.status.uppercase()} করা হয়েছে! পুনরায় প্রসেস হবে না।")
            )
        }

        val user = dao.getUserById(txn.userId)
            ?: return@withTransaction Result.failure(Exception("ইউজার পাওয়া যায়নি"))

        if (approve) {
            // Balance was already deducted when the user submitted the withdrawal request.
            // Only mark as completed and increment totalWithdrawn. NEVER add to user.balance!
            dao.updateUser(
                user.copy(
                    totalWithdrawn = user.totalWithdrawn + txn.amount
                )
            )
            dao.updateTransaction(
                txn.copy(
                    status = "completed",
                    note = "পেমেন্ট সফলভাবে সম্পন্ন হয়েছে (Paid)"
                )
            )
            Result.success("উইথড্র রিকোয়েস্ট সফলভাবে কমপ্লিট (Paid) করা হয়েছে!")
        } else {
            // Rejecting a pending withdrawal refunds the deducted amount once and locks status = "rejected"
            dao.updateUser(
                user.copy(
                    balance = user.balance + txn.amount
                )
            )
            dao.updateTransaction(
                txn.copy(
                    status = "rejected",
                    note = "উইথড্র বাতিল করা হয়েছে এবং ৳${txn.amount.toInt()} মূল ব্যালেন্সে রিফান্ড করা হয়েছে"
                )
            )
            Result.success("উইথড্র বাতিল করা হয়েছে এবং টাকা ইউজারের ব্যালেন্সে ফেরত দেওয়া হয়েছে।")
        }
    }

    // Place Win Go Bet (Minimum bet 1 TK, deducts house fee % automatically)
    suspend fun placeWingoBet(
        userId: Int,
        periodNumber: String,
        timerType: String,
        prediction: String,
        amount: Double
    ): Result<GameBetEntity> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val settings = dao.getSettings() ?: SiteSettingsEntity()

        if (amount < settings.minBet) {
            return@withTransaction Result.failure(
                Exception("সর্বনিম্ন বেট ৳${settings.minBet.toInt()} টাকা")
            )
        }
        if (user.balance < amount) {
            return@withTransaction Result.failure(Exception("পর্যাপ্ত ব্যালেন্স নেই! অনুগ্রহ করে ডিপোজিট করুন।"))
        }

        val feePercent = settings.wingoFeePercent.coerceIn(0.0, 20.0)
        val feeAmount = (amount * feePercent) / 100.0
        val netStake = amount - feeAmount

        // Deduct bet amount from user balance
        dao.updateUser(
            user.copy(
                balance = user.balance - amount,
                totalBetAmount = user.totalBetAmount + amount
            )
        )

        // Credit referral bet commission if applicable
        creditReferralBetCommission(user, amount, settings)

        val bet = GameBetEntity(
            userId = user.id,
            username = user.username,
            gameType = "wingo",
            gameId = "wingo_$timerType",
            betAmount = amount,
            feeAmount = feeAmount,
            netStake = netStake,
            prediction = prediction,
            status = "pending",
            periodNumber = periodNumber,
            timerType = timerType
        )
        val id = dao.insertGameBet(bet).toInt()
        Result.success(bet.copy(id = id))
    }

    private suspend fun creditReferralBetCommission(
        user: UserEntity,
        betAmount: Double,
        settings: SiteSettingsEntity
    ) {
        if (!user.referredBy.isNullOrBlank() && settings.referralBetCommissionPercent > 0) {
            val referrer = dao.getUserByInviteCode(user.referredBy)
            if (referrer != null) {
                val comm = (betAmount * settings.referralBetCommissionPercent) / 100.0
                if (comm >= 0.01) {
                    dao.updateUser(
                        referrer.copy(
                            balance = referrer.balance + comm,
                            commissionBalance = referrer.commissionBalance + comm
                        )
                    )
                }
            }
        }
    }

    // Settle Win Go Round (Respects Admin Panel Forced Result or Auto-Balanced Mode)
    suspend fun settleWingoRound(periodNumber: String, timerType: String): WingoRoundEntity = db.withTransaction {
        val existing = dao.getWingoRound(periodNumber, timerType)
        if (existing != null) return@withTransaction existing

        val settings = dao.getSettings() ?: SiteSettingsEntity()
        val pendingBets = dao.getPendingWingoBets(periodNumber, timerType)

        val winningNumber: Int = when (settings.forcedWingoMode) {
            "FORCE_NUMBER" -> {
                if (settings.forcedWingoNumber in 0..9) settings.forcedWingoNumber else Random.nextInt(0, 10)
            }
            "FORCE_BIG" -> listOf(5, 6, 7, 8, 9).random()
            "FORCE_SMALL" -> listOf(0, 1, 2, 3, 4).random()
            "FORCE_GREEN" -> listOf(1, 3, 5, 7, 9).random()
            "FORCE_RED" -> listOf(0, 2, 4, 6, 8).random()
            "FORCE_VIOLET" -> listOf(0, 5).random()
            else -> {
                // AUTO_BALANCED: If there are active bets, pick a number that minimizes house loss / balances win-loss
                if (pendingBets.isEmpty()) {
                    Random.nextInt(0, 10)
                } else {
                    val candidatePayouts = (0..9).associateWith { candidateNum ->
                        pendingBets.sumOf { bet ->
                            calculateWingoPayoutForNumber(bet.prediction, bet.netStake, candidateNum).first
                        }
                    }
                    val minPayout = candidatePayouts.values.minOrNull() ?: 0.0
                    val lowestCandidates = candidatePayouts.filterValues { it <= minPayout + 1.0 }.keys.toList()
                    // 75% chance pick from lowest-payout candidates (house protection), 25% random average win
                    if (Random.nextInt(100) < 75 && lowestCandidates.isNotEmpty()) {
                        lowestCandidates.random()
                    } else {
                        Random.nextInt(0, 10)
                    }
                }
            }
        }

        val winningColor = getColorForNumber(winningNumber)
        val winningSize = if (winningNumber >= 5) "big" else "small"

        var totalPool = 0.0
        var totalPaid = 0.0

        for (bet in pendingBets) {
            totalPool += bet.betAmount
            val (payout, mult) = calculateWingoPayoutForNumber(bet.prediction, bet.netStake, winningNumber)
            val isWin = payout > 0.0
            if (isWin) {
                totalPaid += payout
                val u = dao.getUserById(bet.userId)
                if (u != null) {
                    dao.updateUser(
                        u.copy(
                            balance = u.balance + payout,
                            totalWinAmount = u.totalWinAmount + payout
                        )
                    )
                }
            }
            dao.updateGameBet(
                bet.copy(
                    winAmount = payout,
                    multiplier = mult,
                    outcome = "$winningNumber ($winningSize / $winningColor)",
                    status = "settled",
                    isWin = isWin
                )
            )
        }

        val round = WingoRoundEntity(
            periodNumber = periodNumber,
            timerType = timerType,
            winningNumber = winningNumber,
            winningColor = winningColor,
            winningSize = winningSize,
            totalBetPool = totalPool,
            totalPayout = totalPaid
        )
        dao.insertWingoRound(round)
        round
    }

    // Note: netStake already has the 1%–3% house fee deducted, so both wins and losses leave the house fee intact!
    private fun calculateWingoPayoutForNumber(
        prediction: String,
        netStake: Double,
        winningNumber: Int
    ): Pair<Double, Double> {
        val winningSize = if (winningNumber >= 5) "big" else "small"
        return when (prediction) {
            "big" -> if (winningSize == "big") Pair(netStake * 2.0, 2.0) else Pair(0.0, 0.0)
            "small" -> if (winningSize == "small") Pair(netStake * 2.0, 2.0) else Pair(0.0, 0.0)
            "green" -> when (winningNumber) {
                1, 3, 7, 9 -> Pair(netStake * 2.0, 2.0)
                5 -> Pair(netStake * 1.5, 1.5)
                else -> Pair(0.0, 0.0)
            }
            "red" -> when (winningNumber) {
                2, 4, 6, 8 -> Pair(netStake * 2.0, 2.0)
                0 -> Pair(netStake * 1.5, 1.5)
                else -> Pair(0.0, 0.0)
            }
            "violet" -> when (winningNumber) {
                0, 5 -> Pair(netStake * 4.5, 4.5)
                else -> Pair(0.0, 0.0)
            }
            else -> {
                val chosenNum = prediction.toIntOrNull()
                if (chosenNum != null && chosenNum == winningNumber) {
                    Pair(netStake * 9.0, 9.0)
                } else {
                    Pair(0.0, 0.0)
                }
            }
        }
    }

    // Play Instant Game (Crash, Thimbles, Slots, Mines, Dice, Live Casino)
    suspend fun startGameBet(
        userId: Int,
        gameType: String,
        gameId: String,
        betAmount: Double,
        prediction: String
    ): Result<GameBetEntity> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val settings = dao.getSettings() ?: SiteSettingsEntity()

        if (betAmount < settings.minBet) {
            return@withTransaction Result.failure(Exception("সর্বনিম্ন বেট ৳${settings.minBet.toInt()} টাকা"))
        }
        if (user.balance < betAmount) {
            return@withTransaction Result.failure(Exception("পর্যাপ্ত ব্যালেন্স নেই! রিচার্জ করুন।"))
        }

        val feePercent = settings.wingoFeePercent.coerceIn(0.0, 20.0)
        val feeAmount = (betAmount * feePercent) / 100.0
        val netStake = betAmount - feeAmount

        dao.updateUser(
            user.copy(
                balance = user.balance - betAmount,
                totalBetAmount = user.totalBetAmount + betAmount
            )
        )
        creditReferralBetCommission(user, betAmount, settings)

        val bet = GameBetEntity(
            userId = user.id,
            username = user.username,
            gameType = gameType,
            gameId = gameId,
            betAmount = betAmount,
            feeAmount = feeAmount,
            netStake = netStake,
            prediction = prediction,
            status = "pending"
        )
        val id = dao.insertGameBet(bet).toInt()
        Result.success(bet.copy(id = id))
    }

    suspend fun settleInstantBet(
        betId: Int,
        isWin: Boolean,
        multiplier: Double,
        outcomeText: String
    ): Result<Double> = db.withTransaction {
        val bet = dao.getGameBetById(betId) ?: return@withTransaction Result.failure(Exception("বেট পাওয়া যায়নি"))
        if (bet.status != "pending") {
            return@withTransaction Result.failure(Exception("এই বেট ইতিমধ্যে সেটেল হয়েছে"))
        }

        val payout = if (isWin) (bet.netStake * multiplier) else 0.0
        if (isWin && payout > 0) {
            val user = dao.getUserById(bet.userId)
            if (user != null) {
                dao.updateUser(
                    user.copy(
                        balance = user.balance + payout,
                        totalWinAmount = user.totalWinAmount + payout
                    )
                )
            }
        }
        dao.updateGameBet(
            bet.copy(
                winAmount = payout,
                multiplier = if (isWin) multiplier else 0.0,
                outcome = outcomeText,
                status = "settled",
                isWin = isWin
            )
        )
        Result.success(payout)
    }

    // 1xBet-Style Single or Multi/Accumulator Sports Bet
    suspend fun placeSportsSlipBet(
        userId: Int,
        selections: List<MultiBetSelection>,
        betAmount: Double
    ): Result<GameBetEntity> = db.withTransaction {
        if (selections.isEmpty()) {
            return@withTransaction Result.failure(Exception("কমপক্ষে ১টি ম্যাচ অপশন সিলেক্ট করুন"))
        }
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val settings = dao.getSettings() ?: SiteSettingsEntity()

        if (betAmount < settings.minBet) {
            return@withTransaction Result.failure(Exception("সর্বনিম্ন বেট ৳${settings.minBet.toInt()} টাকা"))
        }
        if (user.balance < betAmount) {
            return@withTransaction Result.failure(Exception("পর্যাপ্ত ব্যালেন্স নেই! ডিপোজিট করুন।"))
        }

        val combinedOdds = selections.fold(1.0) { acc, item -> acc * item.odds }
        val roundedOdds = (combinedOdds * 100.0).roundToInt() / 100.0
        val feeAmount = (betAmount * settings.wingoFeePercent) / 100.0
        val netStake = betAmount - feeAmount

        dao.updateUser(
            user.copy(
                balance = user.balance - betAmount,
                totalBetAmount = user.totalBetAmount + betAmount
            )
        )
        creditReferralBetCommission(user, betAmount, settings)

        val summary = selections.joinToString(" + ") { "${it.matchTitle}: ${it.pickName} (@${it.odds})" }
        val isMulti = selections.size > 1

        val bet = GameBetEntity(
            userId = user.id,
            username = user.username,
            gameType = if (isMulti) "sports_multi" else "sports_single",
            gameId = if (isMulti) "MULTI_${selections.size}X" else "MATCH_${selections.first().matchId}",
            betAmount = betAmount,
            feeAmount = feeAmount,
            netStake = netStake,
            multiplier = roundedOdds,
            prediction = summary,
            outcome = "Pending Settlement",
            status = "pending"
        )
        val id = dao.insertGameBet(bet).toInt()
        Result.success(bet.copy(id = id))
    }

    // Admin Settle Sports Bet (Win or Loss)
    suspend fun adminSettleSportsBet(betId: Int, isWin: Boolean): Result<String> = db.withTransaction {
        val bet = dao.getGameBetById(betId) ?: return@withTransaction Result.failure(Exception("বেট পাওয়া যায়নি"))
        if (bet.status != "pending") {
            return@withTransaction Result.failure(Exception("এই বেট ইতিমধ্যে সেটেল করা হয়েছে"))
        }
        val payout = if (isWin) bet.netStake * bet.multiplier else 0.0
        if (isWin) {
            val u = dao.getUserById(bet.userId)
            if (u != null) {
                dao.updateUser(
                    u.copy(
                        balance = u.balance + payout,
                        totalWinAmount = u.totalWinAmount + payout
                    )
                )
            }
        }
        dao.updateGameBet(
            bet.copy(
                winAmount = payout,
                outcome = if (isWin) "WON (৳${payout.toInt()})" else "LOST",
                status = "settled",
                isWin = isWin
            )
        )
        Result.success(if (isWin) "বেট জেতানো হয়েছে (+৳${payout.toInt()})" else "বেট লস হিসেবে সেটেল করা হয়েছে")
    }

    // Gift Code Claim
    suspend fun claimGiftCode(userId: Int, codeInput: String): Result<Double> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val gift = dao.getGiftCodeByCode(codeInput.trim())
            ?: return@withTransaction Result.failure(Exception("ভুল বা মেয়াদোত্তীর্ণ গিফট কোড!"))
        if (!gift.isActive || gift.claimsCount >= gift.maxClaims) {
            return@withTransaction Result.failure(Exception("এই গিফট কোডের লিমিট শেষ হয়ে গেছে!"))
        }

        dao.updateGiftCode(gift.copy(claimsCount = gift.claimsCount + 1))
        dao.updateUser(
            user.copy(
                balance = user.balance + gift.amount,
                bonusBalance = user.bonusBalance + gift.amount
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                userId = user.id,
                username = user.username,
                phone = user.phone,
                type = "gift_code",
                amount = gift.amount,
                method = "gift",
                paymentMode = "Gift Code",
                accountNumber = gift.code,
                status = "completed",
                note = "গিফট কোড বোনাস (${gift.code})"
            )
        )
        Result.success(gift.amount)
    }

    // Daily Check-in Bonus
    suspend fun claimDailyCheckIn(userId: Int): Result<Double> = db.withTransaction {
        val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(Exception("লগইন করুন"))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        if (user.lastCheckInDate == today) {
            return@withTransaction Result.failure(Exception("আজকের ডেইলি বোনাস ইতিমধ্যে সংগ্রহ করেছেন!"))
        }
        val nextStreak = (user.checkInStreak % 7) + 1
        val reward = listOf(5.0, 10.0, 15.0, 20.0, 30.0, 50.0, 100.0)[nextStreak - 1]

        dao.updateUser(
            user.copy(
                balance = user.balance + reward,
                bonusBalance = user.bonusBalance + reward,
                lastCheckInDate = today,
                checkInStreak = nextStreak
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                userId = user.id,
                username = user.username,
                phone = user.phone,
                type = "bonus",
                amount = reward,
                method = "checkin",
                paymentMode = "Daily Bonus",
                accountNumber = "Day $nextStreak",
                status = "completed",
                note = "ডেইলি চেক-ইন বোনাস (দিন $nextStreak)"
            )
        )
        Result.success(reward)
    }

    // Admin actions
    suspend fun updateSiteSettings(newSettings: SiteSettingsEntity) {
        dao.saveSettings(newSettings.copy(id = 1))
    }

    suspend fun adminAdjustUserBalance(userId: Int, delta: Double) = db.withTransaction {
        val u = dao.getUserById(userId) ?: return@withTransaction
        val nextBal = max(0.0, u.balance + delta)
        dao.updateUser(u.copy(balance = nextBal))
        dao.insertTransaction(
            TransactionEntity(
                userId = u.id,
                username = u.username,
                phone = u.phone,
                type = if (delta >= 0) "bonus" else "withdraw",
                amount = kotlin.math.abs(delta),
                method = "admin",
                paymentMode = "Admin Adjustment",
                accountNumber = "ADMIN",
                status = "completed",
                note = if (delta >= 0) "এডমিন কর্তৃক ব্যালেন্স যোগ" else "এডমিন কর্তৃক ব্যালেন্স কর্তন"
            )
        )
    }

    suspend fun adminToggleBanUser(userId: Int) = db.withTransaction {
        val u = dao.getUserById(userId) ?: return@withTransaction
        dao.updateUser(u.copy(isBanned = !u.isBanned))
    }

    suspend fun adminAddSportsMatch(match: SportsMatchEntity) {
        dao.insertSportsMatch(match)
    }

    suspend fun adminUpdateSportsMatch(match: SportsMatchEntity) {
        dao.updateSportsMatch(match)
    }

    suspend fun adminDeleteSportsMatch(matchId: Int) {
        dao.deleteSportsMatch(matchId)
    }

    suspend fun adminCreateGiftCode(code: String, amount: Double, maxClaims: Int) {
        dao.insertGiftCode(
            GiftCodeEntity(
                code = code.trim().uppercase(),
                amount = amount,
                maxClaims = maxClaims
            )
        )
    }

    suspend fun sendChatMessage(userId: Int, username: String, message: String, isSupport: Boolean) {
        if (message.isBlank()) return
        dao.insertChatMessage(
            ChatMessageEntity(
                userId = userId,
                username = username,
                message = message.trim(),
                isSupport = isSupport
            )
        )
    }
}
