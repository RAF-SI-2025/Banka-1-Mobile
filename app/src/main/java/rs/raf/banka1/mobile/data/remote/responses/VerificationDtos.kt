package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateResponse(
    @field:Json(name = "sessionId")
    val sessionId: Long
)

@JsonClass(generateAdapter = true)
data class ValidateResponse(
    @field:Json(name = "valid")
    val valid: Boolean,

    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "remainingAttempts")
    val remainingAttempts: Int
)

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @field:Json(name = "sessionId")
    val sessionId: Long,

    @field:Json(name = "status")
    val status: String
)