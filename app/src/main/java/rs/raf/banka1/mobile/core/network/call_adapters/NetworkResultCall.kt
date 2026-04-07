package rs.raf.banka1.mobile.core.network.call_adapters

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rs.raf.banka1.mobile.core.util.JsonParser
import rs.raf.banka1.mobile.data.remote.AppErrorCodes
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.ErrorResponseDto
import java.io.IOException

class NetworkResultCall<T : Any>(
    private val proxy: Call<T>,
    private val jsonParser: JsonParser
) : Call<NetworkResult<T>> {

    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        proxy.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResult = handleApiResponse(response)
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (call.isCanceled) {
                    callback.onFailure(this@NetworkResultCall, t)
                    return
                }
                val networkResult = handleClientFailure(t)
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }
        })
    }

    private fun handleClientFailure(t: Throwable): NetworkResult<T> = when {
        t is IOException -> NetworkResult.Error(code = AppErrorCodes.NO_NETWORK.value, httpCode = null)

        t is OutOfMemoryError || t.message?.contains("Failed to allocate") == true -> NetworkResult.Error(
            code = AppErrorCodes.MEMORY_ALLOCATION_FAILURE.value, httpCode = null
        )

        else -> NetworkResult.Exception(t)
    }

    private fun <T : Any> handleApiResponse(response: Response<T>): NetworkResult<T> {
        val body = response.body()

        if (response.isSuccessful && body != null) {
            return NetworkResult.Success(body)
        }

        return try {
            val errorBodyString = response.errorBody()?.string()

            val backendError = if (!errorBodyString.isNullOrEmpty()) {
                jsonParser.fromJson<ErrorResponseDto>(ErrorResponseDto::class.java, errorBodyString)
            } else null

            if (backendError != null) {
                NetworkResult.Error(
                    code = backendError.errorCode ?: AppErrorCodes.HTTP_FAILURE.value,
                    httpCode = response.code(),
                    message = backendError.errorDesc,
                    title = backendError.errorTitle
                )
            } else {
                NetworkResult.Error(
                    code = AppErrorCodes.HTTP_FAILURE.value,
                    httpCode = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                code = AppErrorCodes.HTTP_FAILURE.value,
                httpCode = response.code(),
                message = response.message()
            )
        }
    }

    override fun execute(): Response<NetworkResult<T>> = throw UnsupportedOperationException()
    override fun clone(): Call<NetworkResult<T>> = NetworkResultCall(proxy.clone(), jsonParser)
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun isCanceled(): Boolean = proxy.isCanceled
    override fun cancel() { proxy.cancel() }
}