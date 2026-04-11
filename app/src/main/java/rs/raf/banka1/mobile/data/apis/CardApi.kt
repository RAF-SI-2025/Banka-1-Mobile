package rs.raf.banka1.mobile.data.apis

import retrofit2.http.GET
import retrofit2.http.Path
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.CardDetailDto
import rs.raf.banka1.mobile.data.remote.responses.CardSummaryDto

interface CardApi {

    @GET("api/cards/client/{clientId}")
    suspend fun getClientCards(
        @Path("clientId") clientId: Long
    ): NetworkResult<List<CardSummaryDto>>

    @GET("api/cards/{cardNumber}")
    suspend fun getCardDetails(
        @Path("cardNumber") cardNumber: String
    ): NetworkResult<CardDetailDto>
}