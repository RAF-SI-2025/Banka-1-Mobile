package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewPaymentDto(
    @field:Json(name = "fromAccountNumber")
    val fromAccountNumber: String,

    @field:Json(name = "toAccountNumber")
    val toAccountNumber: String,

    @field:Json(name = "amount")
    val amount: Double,

    @field:Json(name = "recipientName")
    val recipientName: String,

    @field:Json(name = "paymentCode")
    val paymentCode: String,

    @field:Json(name = "referenceNumber")
    val referenceNumber: String? = null,

    @field:Json(name = "paymentPurpose")
    val paymentPurpose: String,

    @field:Json(name = "verificationSessionId")
    val verificationSessionId: Long
)