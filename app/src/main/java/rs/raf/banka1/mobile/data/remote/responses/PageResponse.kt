package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageResponse<T>(
    @field:Json(name = "content")
    val content: List<T>,

    @field:Json(name = "totalPages")
    val totalPages: Int,

    @field:Json(name = "totalElements")
    val totalElements: Long,

    @field:Json(name = "number")
    val number: Int,

    @field:Json(name = "size")
    val size: Int
)