package rs.raf.banka1.mobile.data.apis

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.ActivateDto
import rs.raf.banka1.mobile.data.remote.requests.ForgotPasswordDto
import rs.raf.banka1.mobile.data.remote.requests.LoginRequestDto
import rs.raf.banka1.mobile.data.remote.responses.CheckActivateDto
import rs.raf.banka1.mobile.data.remote.responses.LoginResponseDto

interface AuthApi {

    @POST("clients/auth/login")
    suspend fun login(@Body request: LoginRequestDto): NetworkResult<LoginResponseDto>

    @GET("clients/auth/check-activate")
    suspend fun checkActivate(@Query("token") token: String): NetworkResult<CheckActivateDto>

    @POST("clients/auth/activate")
    suspend fun activate(@Body request: ActivateDto): NetworkResult<String>

    @POST("clients/auth/reset-password")
    suspend fun resetPassword(@Body request: ActivateDto): NetworkResult<String>

    @POST("clients/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordDto): NetworkResult<String>

    @POST("clients/auth/resend-activation")
    suspend fun resendActivation(@Body request: ForgotPasswordDto): NetworkResult<String>
}