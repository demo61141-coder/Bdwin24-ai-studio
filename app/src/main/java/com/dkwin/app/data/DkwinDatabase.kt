package com.dkwin.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        GameBetEntity::class,
        WingoRoundEntity::class,
        SportsMatchEntity::class,
        GiftCodeEntity::class,
        ChatMessageEntity::class,
        SiteSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DkwinDatabase : RoomDatabase() {
    abstract fun dao(): DkwinDao

    companion object {
        @Volatile
        private var INSTANCE: DkwinDatabase? = null

        fun getInstance(context: Context): DkwinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DkwinDatabase::class.java,
                    "dkwin_platform.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
