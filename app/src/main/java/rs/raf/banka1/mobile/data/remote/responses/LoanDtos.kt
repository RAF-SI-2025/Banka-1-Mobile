package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoanResponseDto(
    @field:Json(name = "loanNumber") val loanNumber: Long? = null,
    @field:Json(name = "loanType") val loanType: String? = null,
    @field:Json(name = "accountNumber") val accountNumber: String? = null,
    @field:Json(name = "amount") val amount: Double? = null,
    @field:Json(name = "repaymentMethod") val repaymentMethod: Int? = null,
    @field:Json(name = "nominalInterestRate") val nominalInterestRate: Double? = null,
    @field:Json(name = "effectiveInterestRate") val effectiveInterestRate: Double? = null,
    @field:Json(name = "interestType") val interestType: String? = null,
    @field:Json(name = "agreementDate") val agreementDate: String? = null,
    @field:Json(name = "maturityDate") val maturityDate: String? = null,
    @field:Json(name = "installmentAmount") val installmentAmount: Double? = null,
    @field:Json(name = "nextInstallmentDate") val nextInstallmentDate: String? = null,
    @field:Json(name = "remainingDebt") val remainingDebt: Double? = null,
    @field:Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanPageResponse(
    @field:Json(name = "content") val content: List<LoanResponseDto> = emptyList(),
    @field:Json(name = "page") val page: Int = 0,
    @field:Json(name = "size") val size: Int = 0,
    @field:Json(name = "totalElements") val totalElements: Long = 0
)
