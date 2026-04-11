package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponseDto(
    @field:Json(name = "errorCode")
    val errorCode: String?,

    @field:Json(name = "errorTitle")
    val errorTitle: String?,

    @field:Json(name = "errorDesc")
    val errorDesc: String?,

    @field:Json(name = "timestamp")
    val timestamp: String? = null,

    @field:Json(name = "validationErrors")
    val validationErrors: Map<String, String>? = null
)