package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForgotPasswordDto(
    @field:Json(name = "email")
    val email: String
)