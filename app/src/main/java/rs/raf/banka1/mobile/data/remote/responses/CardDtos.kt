package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardSummaryDto(
    @field:Json(name = "maskedCardNumber")
    val maskedCardNumber: String? = null,

    @field:Json(name = "accountNumber")
    val accountNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class CardDetailDto(
    @field:Json(name = "cardNumber")
    val cardNumber: String? = null,

    @field:Json(name = "cardType")
    val cardType: String? = null,

    @field:Json(name = "cardName")
    val cardName: String? = null,

    @field:Json(name = "creationDate")
    val creationDate: String? = null,

    @field:Json(name = "expirationDate")
    val expirationDate: String? = null,

    @field:Json(name = "accountNumber")
    val accountNumber: String? = null,

    @field:Json(name = "cardLimit")
    val cardLimit: Double? = null,

    @field:Json(name = "status")
    val status: String? = null
)