package rs.raf.banka1.mobile.data.apis

import retrofit2.http.Body
import retrofit2.http.PUT
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.FcmTokenRequest

interface NotificationApi {

    @PUT("notifications/fcm/token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): NetworkResult<Unit>
}
