package rs.raf.banka1.mobile.data.remote

enum class AppErrorCodes(val value: String) {
    NO_NETWORK("APP_ERR_NO_NETWORK"),
    MEMORY_ALLOCATION_FAILURE("APP_ERR_MEMORY_ALLOCATION"),
    HTTP_FAILURE("APP_ERR_HTTP_FAILURE"),
    SESSION_EXPIRED("APP_ERR_SESSION_EXPIRED")
}