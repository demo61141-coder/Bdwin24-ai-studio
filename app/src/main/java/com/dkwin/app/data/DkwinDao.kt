package com.dkwin.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DkwinDao {
    // Users
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlowById(id: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone OR username = :phone LIMIT 1")
    suspend fun getUserByPhoneOrUsername(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE inviteCode = :code LIMIT 1")
    suspend fun getUserByInviteCode(code: String): UserEntity?

    @Query("SELECT * FROM users WHERE referredBy = :inviteCode ORDER BY id DESC")
    fun getReferredUsersFlow(inviteCode: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Settings
    @Query("SELECT * FROM site_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SiteSettingsEntity?>

    @Query("SELECT * FROM site_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SiteSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SiteSettingsEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserTransactionsFlow(userId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(txn: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(txn: TransactionEntity)

    // Game Bets
    @Query("SELECT * FROM game_bets ORDER BY createdAt DESC LIMIT 250")
    fun getAllGameBetsFlow(): Flow<List<GameBetEntity>>

    @Query("SELECT * FROM game_bets WHERE userId = :userId ORDER BY createdAt DESC LIMIT 100")
    fun getUserGameBetsFlow(userId: Int): Flow<List<GameBetEntity>>

    @Query("SELECT * FROM game_bets WHERE gameType = 'wingo' AND status = 'pending' AND periodNumber = :periodNumber AND timerType = :timerType")
    suspend fun getPendingWingoBets(periodNumber: String, timerType: String): List<GameBetEntity>

    @Query("SELECT * FROM game_bets WHERE gameType = 'wingo' AND status = 'pending'")
    fun getAllPendingWingoBetsFlow(): Flow<List<GameBetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameBet(bet: GameBetEntity): Long

    @Update
    suspend fun updateGameBet(bet: GameBetEntity)

    @Query("SELECT * FROM game_bets WHERE id = :id LIMIT 1")
    suspend fun getGameBetById(id: Int): GameBetEntity?

    // Win Go History
    @Query("SELECT * FROM wingo_history WHERE timerType = :timerType ORDER BY id DESC LIMIT 40")
    fun getWingoHistoryFlow(timerType: String): Flow<List<WingoRoundEntity>>

    @Query("SELECT * FROM wingo_history WHERE timerType = :timerType ORDER BY id DESC LIMIT 40")
    suspend fun getWingoHistoryList(timerType: String): List<WingoRoundEntity>

    @Query("SELECT * FROM wingo_history WHERE periodNumber = :periodNumber AND timerType = :timerType LIMIT 1")
    suspend fun getWingoRound(periodNumber: String, timerType: String): WingoRoundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWingoRound(round: WingoRoundEntity)

    // Sports Matches
    @Query("SELECT * FROM sports_matches ORDER BY id DESC")
    fun getAllSportsMatchesFlow(): Flow<List<SportsMatchEntity>>

    @Query("SELECT * FROM sports_matches WHERE id = :id LIMIT 1")
    suspend fun getSportsMatchById(id: Int): SportsMatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSportsMatch(match: SportsMatchEntity)

    @Update
    suspend fun updateSportsMatch(match: SportsMatchEntity)

    @Query("DELETE FROM sports_matches WHERE id = :id")
    suspend fun deleteSportsMatch(id: Int)

    // Gift Codes
    @Query("SELECT * FROM gift_codes ORDER BY id DESC")
    fun getAllGiftCodesFlow(): Flow<List<GiftCodeEntity>>

    @Query("SELECT * FROM gift_codes WHERE UPPER(code) = UPPER(:code) LIMIT 1")
    suspend fun getGiftCodeByCode(code: String): GiftCodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiftCode(giftCode: GiftCodeEntity)

    @Update
    suspend fun updateGiftCode(giftCode: GiftCodeEntity)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC LIMIT 100")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity)
}
