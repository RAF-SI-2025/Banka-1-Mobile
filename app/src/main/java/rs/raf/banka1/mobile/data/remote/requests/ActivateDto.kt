package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActivateDto(
    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "confirmationToken")
    val confirmationToken: String,

    @field:Json(name = "password")
    val password: String
)