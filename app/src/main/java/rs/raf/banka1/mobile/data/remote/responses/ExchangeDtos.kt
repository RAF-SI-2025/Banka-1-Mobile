package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRateDto(
    @field:Json(name = "currencyCode")
    val currencyCode: String? = null,

    @field:Json(name = "buyingRate")
    val buyingRate: Double? = null,

    @field:Json(name = "sellingRate")
    val sellingRate: Double? = null,

    @field:Json(name = "date")
    val date: String? = null
)

@JsonClass(generateAdapter = true)
data class ConversionResponseDto(
    @field:Json(name = "fromCurrency")
    val fromCurrency: String? = null,

    @field:Json(name = "toCurrency")
    val toCurrency: String? = null,

    @field:Json(name = "fromAmount")
    val fromAmount: Double? = null,

    @field:Json(name = "toAmount")
    val toAmount: Double? = null,

    @field:Json(name = "rate")
    val rate: Double? = null,

    @field:Json(name = "commission")
    val commission: Double? = null,

    @field:Json(name = "date")
    val date: String? = null
)