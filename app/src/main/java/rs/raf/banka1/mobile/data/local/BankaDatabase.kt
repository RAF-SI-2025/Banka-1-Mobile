package rs.raf.banka1.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [VerificationCodeEntity::class, ExchangeRateEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BankaDatabase : RoomDatabase() {
    abstract fun verificationCodeDao(): VerificationCodeDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}
