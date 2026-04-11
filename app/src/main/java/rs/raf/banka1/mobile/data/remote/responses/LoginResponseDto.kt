package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @field:Json(name = "token")
    val token: String,

    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "ime")
    val ime: String,

    @field:Json(name = "prezime")
    val prezime: String,

    @field:Json(name = "email")
    val email: String
)