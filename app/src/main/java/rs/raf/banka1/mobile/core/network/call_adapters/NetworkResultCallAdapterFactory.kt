package rs.raf.banka1.mobile.core.network.call_adapters

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import rs.raf.banka1.mobile.core.network.managers.AuthSessionManager
import rs.raf.banka1.mobile.core.util.JsonParser
import rs.raf.banka1.mobile.data.remote.NetworkResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResultCallAdapterFactory private constructor(
    private val jsonParser: JsonParser,
    private val authSessionManager: AuthSessionManager
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != NetworkResult::class.java) {
            return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return NetworkResultCallAdapter(resultType, jsonParser, authSessionManager)
    }

    companion object {
        fun create(
            jsonParser: JsonParser,
            authSessionManager: AuthSessionManager
        ): NetworkResultCallAdapterFactory =
            NetworkResultCallAdapterFactory(jsonParser, authSessionManager)
    }
}