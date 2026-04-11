package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransferRequestDto(
    @field:Json(name = "fromAccountNumber")
    val fromAccountNumber: String,

    @field:Json(name = "toAccountNumber")
    val toAccountNumber: String,

    @field:Json(name = "amount")
    val amount: Double,

    @field:Json(name = "verificationSessionId")
    val verificationSessionId: Long
)