package rs.raf.banka1.mobile.data.apis

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.remote.responses.PageResponse

interface AccountApi {

    @GET("accounts/client/accounts")
    suspend fun getMyAccounts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): NetworkResult<PageResponse<AccountDetailsResponseDto>>

    @GET("accounts/client/accounts/{id}")
    suspend fun getAccountDetails(
        @Path("id") accountId: Long
    ): NetworkResult<AccountDetailsResponseDto>

    @GET("accounts/client/api/accounts/{accountNumber}")
    suspend fun getAccountDetailsByNumber(
        @Path("accountNumber") accountNumber: String
    ): NetworkResult<AccountDetailsResponseDto>
}