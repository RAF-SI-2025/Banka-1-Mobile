package rs.raf.banka1.mobile.data.apis

import retrofit2.http.GET
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.LoanPageResponse

interface LoanApi {

    @GET("api/loans/client")
    suspend fun getClientLoans(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): NetworkResult<LoanPageResponse>
}
