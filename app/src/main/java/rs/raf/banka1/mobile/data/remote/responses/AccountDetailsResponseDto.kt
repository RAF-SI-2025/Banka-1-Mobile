package rs.raf.banka1.mobile.data.remote.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountDetailsResponseDto(
    @field:Json(name = "nazivRacuna")
    val nazivRacuna: String? = null,

    @field:Json(name = "brojRacuna")
    val brojRacuna: String? = null,

    @field:Json(name = "vlasnik")
    val vlasnik: Long? = null,

    @field:Json(name = "tip")
    val tip: String? = null,

    @field:Json(name = "raspolozivoStanje")
    val raspolozivoStanje: Double? = null,

    @field:Json(name = "rezervisanaSredstva")
    val rezervisanaSredstva: Double? = null,

    @field:Json(name = "stanjeRacuna")
    val stanjeRacuna: Double? = null,

    @field:Json(name = "currency")
    val currency: String? = null,

    @field:Json(name = "dailyLimit")
    val dailyLimit: Double? = null,

    @field:Json(name = "monthlyLimit")
    val monthlyLimit: Double? = null,

    @field:Json(name = "dailySpending")
    val dailySpending: Double? = null,

    @field:Json(name = "monthlySpending")
    val monthlySpending: Double? = null,

    @field:Json(name = "status")
    val status: String? = null,

    @field:Json(name = "accountCategory")
    val accountCategory: String? = null,

    @field:Json(name = "accountType")
    val accountType: String? = null,

    @field:Json(name = "subtype")
    val subtype: String? = null,

    @field:Json(name = "creationDate")
    val creationDate: String? = null,

    @field:Json(name = "expirationDate")
    val expirationDate: String? = null,

    @field:Json(name = "nazivFirme")
    val nazivFirme: String? = null,

    @field:Json(name = "companyRegistrationNumber")
    val companyRegistrationNumber: String? = null,

    @field:Json(name = "companyTaxId")
    val companyTaxId: String? = null,

    @field:Json(name = "companyActivityCode")
    val companyActivityCode: String? = null,

    @field:Json(name = "companyAddress")
    val companyAddress: String? = null,

    @field:Json(name = "companyOwnerId")
    val companyOwnerId: Long? = null,

    @field:Json(name = "cards")
    val cards: List<CardResponseDto>? = null
)

@JsonClass(generateAdapter = true)
data class CardResponseDto(
    @field:Json(name = "id")
    val id: Long? = null,

    @field:Json(name = "cardNumber")
    val cardNumber: String? = null,

    @field:Json(name = "cardType")
    val cardType: String? = null,

    @field:Json(name = "status")
    val status: String? = null,

    @field:Json(name = "expiryDate")
    val expiryDate: String? = null,

    @field:Json(name = "accountNumber")
    val accountNumber: String? = null
)