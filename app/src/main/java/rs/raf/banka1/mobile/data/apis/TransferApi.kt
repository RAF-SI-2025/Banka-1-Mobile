package rs.raf.banka1.mobile.data.apis

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.TransferRequestDto
import rs.raf.banka1.mobile.data.remote.responses.PageResponse
import rs.raf.banka1.mobile.data.remote.responses.TransferResponseDto

interface TransferApi {

    @POST("transfers/")
    suspend fun createTransfer(@Body request: TransferRequestDto): NetworkResult<TransferResponseDto>

    @GET("transfers/")
    suspend fun getTransfers(
        @Query("clientId") clientId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): NetworkResult<PageResponse<TransferResponseDto>>

    @GET("transfers/{orderNumber}")
    suspend fun getTransferByOrderNumber(
        @Path("orderNumber") orderNumber: String
    ): NetworkResult<TransferResponseDto>

    @GET("transfers/accounts/{accountNumber}")
    suspend fun getTransfersForAccount(
        @Path("accountNumber") accountNumber: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): NetworkResult<PageResponse<TransferResponseDto>>
}