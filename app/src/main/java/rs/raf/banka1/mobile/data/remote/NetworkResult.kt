package rs.raf.banka1.mobile.data.remote

import rs.raf.banka1.mobile.presentation.components.ErrorData

sealed class NetworkResult<T : Any> {
    class Success<T : Any>(val data: T) : NetworkResult<T>()

    class Error<T : Any>(
        val code: String,
        val httpCode: Int? = null,
        val message: String? = null,
        val title: String? = null
    ) : NetworkResult<T>() {
        fun toErrorMessage(): String {
            return when (code) {
                AppErrorCodes.NO_NETWORK.value -> "No internet connection"
                AppErrorCodes.MEMORY_ALLOCATION_FAILURE.value -> "Memory allocation failure"
                else -> message ?: title ?: "Something went wrong"
            }
        }

        fun toErrorData(): ErrorData {
            return ErrorData(
                code = code,
                title = when (code) {
                    AppErrorCodes.NO_NETWORK.value -> "No Internet"
                    AppErrorCodes.MEMORY_ALLOCATION_FAILURE.value -> "Memory Error"
                    else -> title ?: "Error"
                },
                message = when (code) {
                    AppErrorCodes.NO_NETWORK.value -> "No internet connection. Please check your network."
                    AppErrorCodes.MEMORY_ALLOCATION_FAILURE.value -> "Memory allocation failure."
                    else -> message ?: "Something went wrong"
                }
            )
        }
    }

    class Exception<T : Any>(val e: Throwable) : NetworkResult<T>() {
        fun toErrorMessage(): String {
            return e.localizedMessage ?: "An unexpected error occurred"
        }

        fun toErrorData(): ErrorData {
            return ErrorData(
                code = null,
                title = "Error",
                message = e.localizedMessage ?: "An unexpected error occurred"
            )
        }
    }

    class Ignored<T : Any> : NetworkResult<T>()

    fun <R : Any> cast(): NetworkResult<R> {
        return when (this) {
            is Success -> throw IllegalStateException("Cannot cast NetworkResult.Success. Use .map() instead.")
            is Error -> Error(code, httpCode, message, title)
            is Exception -> Exception(e)
            is Ignored -> Ignored()
        }
    }

    fun <R : Any> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(code, httpCode, message, title)
            is Exception -> Exception(e)
            is Ignored -> Ignored()
        }
    }

    fun errorMessageOrNull(): String? {
        return when (this) {
            is Success -> null
            is Error -> toErrorMessage()
            is Exception -> toErrorMessage()
            is Ignored -> null
        }
    }
}