package rs.raf.banka1.mobile.core.util

import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonParser @Inject constructor(
    private val moshi: Moshi
) {
    fun <T> fromJson(type: Type, value: String): T? {
        return moshi.adapter<T>(type).fromJson(value)
    }

    fun <T> toJson(type: Type, value: T): String {
        return moshi.adapter<T>(type).toJson(value)
    }
}