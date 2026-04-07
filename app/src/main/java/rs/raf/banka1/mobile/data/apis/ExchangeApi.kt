package rs.raf.banka1.mobile.data.apis

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.ConversionResponseDto
import rs.raf.banka1.mobile.data.remote.responses.ExchangeRateDto

interface ExchangeApi {

    @GET("exchange/rates")
    suspend fun getRates(): NetworkResult<List<ExchangeRateDto>>

    @GET("exchange/rates/{currencyCode}")
    suspend fun getRateForCurrency(
        @Path("currencyCode") currencyCode: String
    ): NetworkResult<ExchangeRateDto>

    @GET("exchange/calculate")
    suspend fun calculateConversion(
        @Query("fromCurrency") fromCurrency: String,
        @Query("toCurrency") toCurrency: String,
        @Query("amount") amount: String
    ): NetworkResult<ConversionResponseDto>
}