package com.dkwin.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dkwin.app.data.ChatMessageEntity
import com.dkwin.app.data.DkwinDatabase
import com.dkwin.app.data.DkwinRepository
import com.dkwin.app.data.GameBetEntity
import com.dkwin.app.data.GiftCodeEntity
import com.dkwin.app.data.MultiBetSelection
import com.dkwin.app.data.SiteSettingsEntity
import com.dkwin.app.data.SportsMatchEntity
import com.dkwin.app.data.TransactionEntity
import com.dkwin.app.data.UserEntity
import com.dkwin.app.data.WingoLiveBetBreakdown
import com.dkwin.app.data.WingoRoundEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, ACTIVITY, WALLET, PROMO, MINE, ADMIN
}

enum class ActiveGameScreen {
    NONE,
    WINGO,
    CRASH,
    THIMBLES,
    SLOTS,
    SPORTS,
    MINES,
    DICE,
    LIVE_CASINO
}

data class ToastMessage(
    val text: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalCoroutinesApi::class)
class DkwinViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DkwinDatabase.getInstance(application)
    val repo = DkwinRepository(db)
    private val prefs = application.getSharedPreferences("dkwin_prefs", Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow(prefs.getInt("logged_in_user_id", -1))
    val currentUser: StateFlow<UserEntity?> = _currentUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(null) else repo.getUserFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val settings: StateFlow<SiteSettingsEntity> = repo.settingsFlow
        .map { it ?: SiteSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SiteSettingsEntity())

    val allUsers: StateFlow<List<UserEntity>> = repo.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repo.allTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(emptyList()) else repo.getUserTransactionsFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGameBets: StateFlow<List<GameBetEntity>> = repo.allGameBetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userGameBets: StateFlow<List<GameBetEntity>> = _currentUserId
        .flatMapLatest { id ->
            if (id <= 0) flowOf(emptyList()) else repo.getUserGameBetsFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referredUsers: StateFlow<List<UserEntity>> = currentUser
        .flatMapLatest { u ->
            if (u == null) flowOf(emptyList()) else repo.getReferredUsersFlow(u.inviteCode)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sportsMatches: StateFlow<List<SportsMatchEntity>> = repo.allSportsMatchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val giftCodes: StateFlow<List<GiftCodeEntity>> = repo.allGiftCodesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repo.allChatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & Modals
    private val _activeTab = MutableStateFlow(MainTab.HOME)
    val activeTab: StateFlow<MainTab> = _activeTab.asStateFlow()

    private val _activeGame = MutableStateFlow(ActiveGameScreen.NONE)
    val activeGame: StateFlow<ActiveGameScreen> = _activeGame.asStateFlow()

    private val _selectedCrashVariant = MutableStateFlow("aviator") // aviator, jetx, spacexy, cricketx
    val selectedCrashVariant: StateFlow<String> = _selectedCrashVariant.asStateFlow()

    private val _selectedSlotId = MutableStateFlow("jili_super_ace")
    val selectedSlotId: StateFlow<String> = _selectedSlotId.asStateFlow()

    private val _showAuthSheet = MutableStateFlow(false)
    val showAuthSheet: StateFlow<Boolean> = _showAuthSheet.asStateFlow()

    private val _showDepositSheet = MutableStateFlow(false)
    val showDepositSheet: StateFlow<Boolean> = _showDepositSheet.asStateFlow()

    private val _showWithdrawSheet = MutableStateFlow(false)
    val showWithdrawSheet: StateFlow<Boolean> = _showWithdrawSheet.asStateFlow()

    private val _showSupportChatSheet = MutableStateFlow(false)
    val showSupportChatSheet: StateFlow<Boolean> = _showSupportChatSheet.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    // Win Go Timer State (Synchronized to wall clock)
    private val _selectedWingoTimer = MutableStateFlow("1m") // 30s, 1m, 3m, 5m
    val selectedWingoTimer: StateFlow<String> = _selectedWingoTimer.asStateFlow()

    private val _wingoActivePeriod = MutableStateFlow("")
    val wingoActivePeriod: StateFlow<String> = _wingoActivePeriod.asStateFlow()

    private val _wingoRemainingSec = MutableStateFlow(60)
    val wingoRemainingSec: StateFlow<Int> = _wingoRemainingSec.asStateFlow()

    val wingoHistory: StateFlow<List<WingoRoundEntity>> = _selectedWingoTimer
        .flatMapLatest { timer -> repo.getWingoHistoryFlow(timer) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-time Win Go Bet Breakdown for Admin Panel (shows Big, Small, Green, Red, Violet, and 0..9 bet totals)
    val wingoLiveBreakdown: StateFlow<WingoLiveBetBreakdown> = combine(
        repo.allPendingWingoBetsFlow,
        _wingoActivePeriod
    ) { pendingBets, _ ->
        var total = 0.0
        var big = 0.0
        var small = 0.0
        var green = 0.0
        var red = 0.0
        var violet = 0.0
        val numMap = (0..9).associateWith { 0.0 }.toMutableMap()

        for (b in pendingBets) {
            total += b.betAmount
            when (b.prediction) {
                "big" -> big += b.betAmount
                "small" -> small += b.betAmount
                "green" -> green += b.betAmount
                "red" -> red += b.betAmount
                "violet" -> violet += b.betAmount
                else -> {
                    val n = b.prediction.toIntOrNull()
                    if (n != null && n in 0..9) {
                        numMap[n] = (numMap[n] ?: 0.0) + b.betAmount
                    }
                }
            }
        }
        WingoLiveBetBreakdown(
            totalBetAmount = total,
            totalBetsCount = pendingBets.size,
            bigBetAmount = big,
            smallBetAmount = small,
            greenBetAmount = green,
            redBetAmount = red,
            violetBetAmount = violet,
            numberBets = numMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WingoLiveBetBreakdown())

    // 1xBet Multi-Bet Slip State
    private val _multiBetSlip = MutableStateFlow<List<MultiBetSelection>>(emptyList())
    val multiBetSlip: StateFlow<List<MultiBetSelection>> = _multiBetSlip.asStateFlow()

    private val lastPeriodsByTimer = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
            startGlobalWingoClock()
        }
    }

    private fun startGlobalWingoClock() {
        viewModelScope.launch {
            val timers = listOf("30s", "1m", "3m", "5m")
            while (true) {
                val now = System.currentTimeMillis()
                for (t in timers) {
                    val currentPeriod = repo.computeCurrentPeriodNumber(t, now).toString()
                    val previousPeriod = lastPeriodsByTimer[t]
                    if (previousPeriod != null && previousPeriod != currentPeriod) {
                        // Settle the finished period automatically
                        repo.settleWingoRound(previousPeriod, t)
                    }
                    lastPeriodsByTimer[t] = currentPeriod
                }

                val activeT = _selectedWingoTimer.value
                _wingoActivePeriod.value = repo.computeCurrentPeriodNumber(activeT, now).toString()
                _wingoRemainingSec.value = repo.computeRemainingSeconds(activeT, now)

                delay(1000L)
            }
        }
    }

    fun showToast(message: String, isError: Boolean = false) {
        _toast.value = ToastMessage(message, isError)
        viewModelScope.launch {
            val currentId = _toast.value?.id
            delay(3200L)
            if (_toast.value?.id == currentId) {
                _toast.value = null
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _activeGame.value = ActiveGameScreen.NONE
        _activeTab.value = tab
    }

    fun openGame(game: ActiveGameScreen, subId: String? = null) {
        if (game == ActiveGameScreen.CRASH && subId != null) {
            _selectedCrashVariant.value = subId
        }
        if (game == ActiveGameScreen.SLOTS && subId != null) {
            _selectedSlotId.value = subId
        }
        _activeGame.value = game
    }

    fun closeGame() {
        _activeGame.value = ActiveGameScreen.NONE
    }

    fun setWingoTimer(timer: String) {
        _selectedWingoTimer.value = timer
        val now = System.currentTimeMillis()
        _wingoActivePeriod.value = repo.computeCurrentPeriodNumber(timer, now).toString()
        _wingoRemainingSec.value = repo.computeRemainingSeconds(timer, now)
    }

    fun setCrashVariant(variant: String) {
        _selectedCrashVariant.value = variant
    }

    fun setSlotTheme(slotId: String) {
        _selectedSlotId.value = slotId
    }

    fun setShowAuthSheet(show: Boolean) {
        _showAuthSheet.value = show
    }

    fun setShowDepositSheet(show: Boolean) {
        if (show && currentUser.value == null) {
            _showAuthSheet.value = true
            showToast("ডিপোজিট করতে প্রথমে আপনার একাউন্টে লগইন বা রেজিস্টার করুন", true)
            return
        }
        _showDepositSheet.value = show
    }

    fun setShowWithdrawSheet(show: Boolean) {
        if (show && currentUser.value == null) {
            _showAuthSheet.value = true
            showToast("উইথড্র করতে প্রথমে আপনার একাউন্টে লগইন করুন", true)
            return
        }
        _showWithdrawSheet.value = show
    }

    fun setShowSupportChatSheet(show: Boolean) {
        _showSupportChatSheet.value = show
    }

    // Auth Actions
    fun register(username: String, phone: String, password: String, referralCode: String) {
        viewModelScope.launch {
            val res = repo.registerUser(username, phone, password, referralCode)
            res.onSuccess { user ->
                prefs.edit().putInt("logged_in_user_id", user.id).apply()
                _currentUserId.value = user.id
                _showAuthSheet.value = false
                showToast("স্বাগতম ${user.username}! আপনার একাউন্ট সফলভাবে খোলা হয়েছে (ব্যালেন্স ৳0.00)।")
            }.onFailure { err ->
                showToast(err.message ?: "রেজিস্ট্রেশন ব্যর্থ হয়েছে", true)
            }
        }
    }

    fun login(phoneOrUsername: String, password: String) {
        viewModelScope.launch {
            val res = repo.loginUser(phoneOrUsername, password)
            res.onSuccess { user ->
                prefs.edit().putInt("logged_in_user_id", user.id).apply()
                _currentUserId.value = user.id
                _showAuthSheet.value = false
                showToast("স্বাগতম ${user.username}! সফলভাবে লগইন হয়েছে।")
            }.onFailure { err ->
                showToast(err.message ?: "লগইন ব্যর্থ হয়েছে", true)
            }
        }
    }

    fun logout() {
        prefs.edit().remove("logged_in_user_id").apply()
        _currentUserId.value = -1
        showToast("সফলভাবে লগআউট হয়েছে")
    }

    // Wallet Actions
    fun submitDeposit(amount: Double, method: String, senderNumber: String, trxId: String) {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            return
        }
        viewModelScope.launch {
            val res = repo.submitDepositRequest(u.id, amount, method, senderNumber, trxId)
            res.onSuccess {
                _showDepositSheet.value = false
                showToast("ডিপোজিট রিকোয়েস্ট পেন্ডিং (Pending) এ জমা হয়েছে! ২-১০ মিনিটের মধ্যে ব্যালেন্স যুক্ত হবে।")
            }.onFailure { err ->
                showToast(err.message ?: "ডিপোজিট সাবমিট ব্যর্থ হয়েছে", true)
            }
        }
    }

    fun submitWithdraw(amount: Double, method: String, receiverNumber: String, password: String) {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            return
        }
        viewModelScope.launch {
            val res = repo.submitWithdrawRequest(u.id, amount, method, receiverNumber, password)
            res.onSuccess {
                _showWithdrawSheet.value = false
                showToast("উইথড্র রিকোয়েস্ট পেন্ডিং (Pending) এ জমা হয়েছে! ২-১০ মিনিটের মধ্যে পেমেন্ট সম্পন্ন হবে।")
            }.onFailure { err ->
                showToast(err.message ?: "উইথড্র সাবমিট ব্যর্থ হয়েছে", true)
            }
        }
    }

    // Win Go Bet Action
    fun placeWingoBet(prediction: String, amount: Double, onComplete: () -> Unit = {}) {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            showToast("বেট করতে প্রথমে লগইন করুন", true)
            return
        }
        if (_wingoRemainingSec.value <= 4) {
            showToast("এই রাউন্ডের সময় শেষ! পরবর্তী রাউন্ডের জন্য অপেক্ষা করুন।", true)
            return
        }
        viewModelScope.launch {
            val res = repo.placeWingoBet(
                userId = u.id,
                periodNumber = _wingoActivePeriod.value,
                timerType = _selectedWingoTimer.value,
                prediction = prediction,
                amount = amount
            )
            res.onSuccess { bet ->
                showToast("বেট সফল হয়েছে: ${prediction.uppercase()} (৳${amount.toInt()}) [ফি: ৳${String.format("%.2f", bet.feeAmount)}]")
                onComplete()
            }.onFailure { err ->
                showToast(err.message ?: "বেট ব্যর্থ হয়েছে", true)
                if (u.balance < amount) {
                    _showDepositSheet.value = true
                }
            }
        }
    }

    // 1xBet Multi-Bet Slip Management
    fun toggleMultiBetSelection(selection: MultiBetSelection) {
        val current = _multiBetSlip.value.toMutableList()
        val existingIdx = current.indexOfFirst {
            it.matchId == selection.matchId && it.marketName == selection.marketName
        }
        if (existingIdx >= 0) {
            if (current[existingIdx].pickName == selection.pickName) {
                current.removeAt(existingIdx)
            } else {
                current[existingIdx] = selection
            }
        } else {
            current.add(selection)
        }
        _multiBetSlip.value = current
    }

    fun clearMultiBetSlip() {
        _multiBetSlip.value = emptyList()
    }

    fun submitSportsMultiBet(betAmount: Double) {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            showToast("বেট প্লেস করতে প্রথমে লগইন করুন", true)
            return
        }
        val slip = _multiBetSlip.value
        viewModelScope.launch {
            val res = repo.placeSportsSlipBet(u.id, slip, betAmount)
            res.onSuccess { bet ->
                _multiBetSlip.value = emptyList()
                showToast("স্পোর্টস বেট সফল হয়েছে! মোট অডস: ${bet.multiplier}x (সম্ভাব্য জয়: ৳${(bet.netStake * bet.multiplier).toInt()})")
            }.onFailure { err ->
                showToast(err.message ?: "বেট ব্যর্থ হয়েছে", true)
                if (u.balance < betAmount) {
                    _showDepositSheet.value = true
                }
            }
        }
    }

    // Activity & Rewards
    fun claimDailyCheckIn() {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            return
        }
        viewModelScope.launch {
            repo.claimDailyCheckIn(u.id)
                .onSuccess { amt -> showToast("অভিনন্দন! ডেইলি চেক-ইন বোনাস +৳${amt.toInt()} পেয়েছেন!") }
                .onFailure { err -> showToast(err.message ?: "ব্যর্থ হয়েছে", true) }
        }
    }

    fun claimGiftCode(code: String) {
        val u = currentUser.value ?: run {
            _showAuthSheet.value = true
            return
        }
        viewModelScope.launch {
            repo.claimGiftCode(u.id, code)
                .onSuccess { amt -> showToast("অভিনন্দন! গিফট কোড থেকে +৳${amt.toInt()} বোনাস পেয়েছেন!") }
                .onFailure { err -> showToast(err.message ?: "ভুল কোড", true) }
        }
    }

    fun sendUserChat(msg: String) {
        val u = currentUser.value
        val name = u?.username ?: "Guest_Player"
        val uid = u?.id ?: 0
        viewModelScope.launch {
            repo.sendChatMessage(uid, name, msg, isSupport = false)
        }
    }

    // Admin Functions
    fun loginAdmin(pin: String): Boolean {
        val validPin = settings.value.adminPin
        return if (pin.trim() == validPin) {
            _isAdminAuthenticated.value = true
            showToast("মাস্টার এডমিন প্যানেলে স্বাগতম!")
            true
        } else {
            showToast("ভুল এডমিন পিন! সঠিক পিন দিন।", true)
            false
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun saveSettings(updated: SiteSettingsEntity) {
        viewModelScope.launch {
            repo.updateSiteSettings(updated)
            showToast("সকল সেটিংস ও কন্ট্রোল সফলভাবে সেভ হয়েছে!")
        }
    }

    fun adminSetWingoForcedResult(mode: String, number: Int = -1) {
        viewModelScope.launch {
            val cur = settings.value
            repo.updateSiteSettings(
                cur.copy(
                    forcedWingoMode = mode,
                    forcedWingoNumber = number
                )
            )
            val label = when (mode) {
                "FORCE_NUMBER" -> "নাম্বার $number ফিক্সড করা হয়েছে"
                "FORCE_BIG" -> "BIG (৫-৯) ফিক্সড করা হয়েছে"
                "FORCE_SMALL" -> "SMALL (০-৪) ফিক্সড করা হয়েছে"
                "FORCE_GREEN" -> "GREEN (সবুজ) ফিক্সড করা হয়েছে"
                "FORCE_RED" -> "RED (লাল) ফিক্সড করা হয়েছে"
                "FORCE_VIOLET" -> "VIOLET (বেগুনী) ফিক্সড করা হয়েছে"
                else -> "অটো ব্যালেন্সড মোড চালু করা হয়েছে"
            }
            showToast("Win Go কন্ট্রোল: $label")
        }
    }

    fun adminProcessDeposit(txnId: Int, approve: Boolean) {
        viewModelScope.launch {
            repo.adminProcessDeposit(txnId, approve)
                .onSuccess { msg -> showToast(msg) }
                .onFailure { err -> showToast(err.message ?: "ব্যর্থ হয়েছে", true) }
        }
    }

    fun adminProcessWithdrawal(txnId: Int, approve: Boolean) {
        viewModelScope.launch {
            repo.adminProcessWithdrawal(txnId, approve)
                .onSuccess { msg -> showToast(msg) }
                .onFailure { err -> showToast(err.message ?: "ব্যর্থ হয়েছে", true) }
        }
    }

    fun adminAdjustUserBalance(userId: Int, delta: Double) {
        viewModelScope.launch {
            repo.adminAdjustUserBalance(userId, delta)
            showToast("ইউজারের ব্যালেন্স আপডেট করা হয়েছে!")
        }
    }

    fun adminToggleBanUser(userId: Int) {
        viewModelScope.launch {
            repo.adminToggleBanUser(userId)
            showToast("ইউজার স্ট্যাটাস পরিবর্তন করা হয়েছে")
        }
    }

    fun adminAddSportsMatch(match: SportsMatchEntity) {
        viewModelScope.launch {
            repo.adminAddSportsMatch(match)
            showToast("নতুন ম্যাচ সফলভাবে যুক্ত হয়েছে!")
        }
    }

    fun adminDeleteSportsMatch(matchId: Int) {
        viewModelScope.launch {
            repo.adminDeleteSportsMatch(matchId)
            showToast("ম্যাচ মুছে ফেলা হয়েছে")
        }
    }

    fun adminSettleSportsBet(betId: Int, isWin: Boolean) {
        viewModelScope.launch {
            repo.adminSettleSportsBet(betId, isWin)
                .onSuccess { showToast(it) }
                .onFailure { showToast(it.message ?: "ব্যর্থ", true) }
        }
    }

    fun adminCreateGiftCode(code: String, amount: Double, maxClaims: Int) {
        viewModelScope.launch {
            repo.adminCreateGiftCode(code, amount, maxClaims)
            showToast("গিফট কোড ${code.uppercase()} তৈরি হয়েছে!")
        }
    }

    fun adminSendSupportReply(reply: String) {
        viewModelScope.launch {
            repo.sendChatMessage(0, "DKWIN অফিশিয়াল সাপোর্ট", reply, isSupport = true)
            showToast("সাপোর্ট রিপ্লাই পাঠানো হয়েছে")
        }
    }
}
