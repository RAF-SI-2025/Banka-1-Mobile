package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateRequest(
    @field:Json(name = "clientId")
    val clientId: Long,

    @field:Json(name = "operationType")
    val operationType: String,

    @field:Json(name = "relatedEntityId")
    val relatedEntityId: String,

    @field:Json(name = "clientEmail")
    val clientEmail: String
)

@JsonClass(generateAdapter = true)
data class ValidateRequest(
    @field:Json(name = "sessionId")
    val sessionId: Long,

    @field:Json(name = "code")
    val code: String
)