package rs.raf.banka1.mobile.data.apis

import retrofit2.http.GET
import retrofit2.http.Path
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.ClientInfoResponseDto

interface ClientApi {

    @GET("customers/{id}")
    suspend fun getClientById(@Path("id") id: Long): NetworkResult<ClientInfoResponseDto>
}
