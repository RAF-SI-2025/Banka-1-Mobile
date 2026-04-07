package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransferResponseDto(
    @field:Json(name = "orderNumber")
    val orderNumber: String? = null,

    @field:Json(name = "fromAccountNumber")
    val fromAccountNumber: String? = null,

    @field:Json(name = "toAccountNumber")
    val toAccountNumber: String? = null,

    @field:Json(name = "initialAmount")
    val initialAmount: Double? = null,

    @field:Json(name = "finalAmount")
    val finalAmount: Double? = null,

    @field:Json(name = "exchangeRate")
    val exchangeRate: Double? = null,

    @field:Json(name = "commission")
    val commission: Double? = null,

    @field:Json(name = "timestamp")
    val timestamp: String? = null
)