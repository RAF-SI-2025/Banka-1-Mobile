package rs.raf.banka1.mobile.data.remote.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmTokenRequest(
    @field:Json(name = "clientId")
    val clientId: Long,

    @field:Json(name = "fcmToken")
    val fcmToken: String
)
