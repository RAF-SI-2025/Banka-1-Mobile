package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClientInfoResponseDto(
    @field:Json(name = "id")
    val id: Long,

    @field:Json(name = "name")
    val name: String,

    @field:Json(name = "lastName")
    val lastName: String,

    @field:Json(name = "email")
    val email: String,

    @field:Json(name = "jmbg")
    val jmbg: String? = null,

    @field:Json(name = "phoneNumber")
    val phoneNumber: String? = null,

    @field:Json(name = "address")
    val address: String? = null,

    @field:Json(name = "gender")
    val gender: String? = null,

    @field:Json(name = "dateOfBirth")
    val dateOfBirth: Long? = null,

    @field:Json(name = "role")
    val role: String? = null,

    @field:Json(name = "active")
    val active: Boolean = true
)
