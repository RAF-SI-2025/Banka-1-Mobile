package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionResponseDto(
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

    @field:Json(name = "recipientName")
    val recipientName: String? = null,

    @field:Json(name = "paymentCode")
    val paymentCode: String? = null,

    @field:Json(name = "referenceNumber")
    val referenceNumber: String? = null,

    @field:Json(name = "paymentPurpose")
    val paymentPurpose: String? = null,

    @field:Json(name = "status")
    val status: String? = null,

    @field:Json(name = "fromCurrency")
    val fromCurrency: String? = null,

    @field:Json(name = "toCurrency")
    val toCurrency: String? = null,

    @field:Json(name = "exchangeRate")
    val exchangeRate: Double? = null,

    @field:Json(name = "createdAt")
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class NewPaymentResponseDto(
    @field:Json(name = "message")
    val message: String? = null,

    @field:Json(name = "status")
    val status: String? = null
)