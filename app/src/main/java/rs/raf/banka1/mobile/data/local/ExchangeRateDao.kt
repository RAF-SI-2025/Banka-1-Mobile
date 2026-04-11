package rs.raf.banka1.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates ORDER BY currencyCode ASC")
    fun observeAll(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates ORDER BY currencyCode ASC")
    suspend fun getAll(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :code LIMIT 1")
    suspend fun getByCode(code: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAll()
}