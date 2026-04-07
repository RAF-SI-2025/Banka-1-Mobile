package rs.raf.banka1.mobile.data.repository

import rs.raf.banka1.mobile.data.apis.AuthApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.ActivateDto
import rs.raf.banka1.mobile.data.remote.requests.ForgotPasswordDto
import rs.raf.banka1.mobile.data.remote.requests.LoginRequestDto
import rs.raf.banka1.mobile.data.remote.responses.CheckActivateDto
import rs.raf.banka1.mobile.data.remote.responses.LoginResponseDto
import rs.raf.banka1.mobile.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<LoginResponseDto> {
        return authApi.login(LoginRequestDto(email, password))
    }

    override suspend fun checkActivate(token: String): NetworkResult<CheckActivateDto> {
        return authApi.checkActivate(token)
    }

    override suspend fun activate(id: Long, confirmationToken: String, password: String): NetworkResult<String> {
        return authApi.activate(ActivateDto(id, confirmationToken, password))
    }

    override suspend fun resetPassword(id: Long, confirmationToken: String, password: String): NetworkResult<String> {
        return authApi.resetPassword(ActivateDto(id, confirmationToken, password))
    }

    override suspend fun forgotPassword(email: String): NetworkResult<String> {
        return authApi.forgotPassword(ForgotPasswordDto(email))
    }

    override suspend fun resendActivation(email: String): NetworkResult<String> {
        return authApi.resendActivation(ForgotPasswordDto(email))
    }
}