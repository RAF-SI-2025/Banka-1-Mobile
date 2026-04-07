package rs.raf.banka1.mobile.data.apis

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.GenerateRequest
import rs.raf.banka1.mobile.data.remote.requests.ValidateRequest
import rs.raf.banka1.mobile.data.remote.responses.GenerateResponse
import rs.raf.banka1.mobile.data.remote.responses.StatusResponse
import rs.raf.banka1.mobile.data.remote.responses.ValidateResponse

interface VerificationApi {

    @POST("verification/generate")
    suspend fun generate(@Body request: GenerateRequest): NetworkResult<GenerateResponse>

    @POST("verification/validate")
    suspend fun validate(@Body request: ValidateRequest): NetworkResult<ValidateResponse>

    @GET("verification/{sessionId}/status")
    suspend fun getSessionStatus(
        @Path("sessionId") sessionId: Long
    ): NetworkResult<StatusResponse>
}