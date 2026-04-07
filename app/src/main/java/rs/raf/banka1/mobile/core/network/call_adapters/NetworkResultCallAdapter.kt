package rs.raf.banka1.mobile.core.network.call_adapters

import retrofit2.Call
import retrofit2.CallAdapter
import rs.raf.banka1.mobile.core.util.JsonParser
import rs.raf.banka1.mobile.data.remote.NetworkResult
import java.lang.reflect.Type

class NetworkResultCallAdapter(
    private val resultType: Type,
    private val jsonParser: JsonParser
) : CallAdapter<Type, Call<NetworkResult<Type>>> {

    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Type>): Call<NetworkResult<Type>> {
        return NetworkResultCall(call, jsonParser)
    }
}