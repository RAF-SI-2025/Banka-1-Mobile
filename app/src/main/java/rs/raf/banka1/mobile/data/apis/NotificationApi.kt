package rs.raf.banka1.mobile.data.apis

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.FcmTokenRequest

interface NotificationApi {

    @PUT("notifications/fcm/token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): NetworkResult<Unit>

    @DELETE("notifications/fcm/token/{clientId}")
    suspend fun deregisterFcmToken(@Path("clientId") clientId: Long): NetworkResult<Unit>
}
