package rs.raf.banka1.mobile.data.apis

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.NewPaymentDto
import rs.raf.banka1.mobile.data.remote.responses.NewPaymentResponseDto
import rs.raf.banka1.mobile.data.remote.responses.PageResponse
import rs.raf.banka1.mobile.data.remote.responses.TransactionResponseDto

interface TransactionApi {

    @POST("transactions/payment")
    suspend fun createPayment(@Body request: NewPaymentDto): NetworkResult<NewPaymentResponseDto>

    @GET("transactions/accounts/{accountNumber}")
    suspend fun getTransactionsForAccount(
        @Path("accountNumber") accountNumber: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): NetworkResult<PageResponse<TransactionResponseDto>>

    @GET("transactions/")
    suspend fun getTransactions(
        @Query("clientId") clientId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): NetworkResult<PageResponse<TransactionResponseDto>>

    @GET("transactions/{orderNumber}")
    suspend fun getTransactionByOrderNumber(
        @Path("orderNumber") orderNumber: String
    ): NetworkResult<TransactionResponseDto>
}