package rs.raf.banka1.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationCodeDao {

    @Query("SELECT * FROM verification_codes ORDER BY receivedAt DESC")
    fun observeAll(): Flow<List<VerificationCodeEntity>>

    @Query("SELECT COUNT(*) FROM verification_codes WHERE isUsed = 0 AND expiresAt > :now")
    fun observeActiveCount(now: Long): Flow<Int>

    @Insert
    suspend fun insert(entity: VerificationCodeEntity)

    @Query("UPDATE verification_codes SET isUsed = 1 WHERE id = :id")
    suspend fun markUsed(id: Long)

    @Query("DELETE FROM verification_codes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM verification_codes WHERE expiresAt < :now")
    suspend fun deleteExpired(now: Long)

    @Query("DELETE FROM verification_codes")
    suspend fun deleteAll()
}
