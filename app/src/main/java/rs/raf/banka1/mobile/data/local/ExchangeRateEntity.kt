package rs.raf.banka1.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey
    val currencyCode: String,
    val buyingRate: Double,
    val sellingRate: Double,
    val date: String,
    val fetchedAt: Long = System.currentTimeMillis()
)