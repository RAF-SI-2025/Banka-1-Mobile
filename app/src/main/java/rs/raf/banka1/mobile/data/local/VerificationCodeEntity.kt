package rs.raf.banka1.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_codes")
data class VerificationCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val operationType: String,
    val sessionId: String,
    val receivedAt: Long,
    val expiresAt: Long,
    val isUsed: Boolean = false
)
