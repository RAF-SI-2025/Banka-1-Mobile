package rs.raf.banka1.mobile.domain.repository

import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.CheckActivateDto
import rs.raf.banka1.mobile.data.remote.responses.LoginResponseDto

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<LoginResponseDto>
    suspend fun checkActivate(token: String): NetworkResult<CheckActivateDto>
    suspend fun activate(id: Long, confirmationToken: String, password: String): NetworkResult<String>
    suspend fun resetPassword(id: Long, confirmationToken: String, password: String): NetworkResult<String>
    suspend fun forgotPassword(email: String): NetworkResult<String>
    suspend fun resendActivation(email: String): NetworkResult<String>
}