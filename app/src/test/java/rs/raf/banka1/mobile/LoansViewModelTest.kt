package rs.raf.banka1.mobile

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rs.raf.banka1.mobile.data.apis.LoanApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.LoanPageResponse
import rs.raf.banka1.mobile.presentation.viewmodels.main.LoansViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoansViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty loans response does not produce parse error state`() = runTest(testDispatcher) {
        val viewModel = LoansViewModel(
            loanApi = object : LoanApi {
                override suspend fun getClientLoans(page: Int, size: Int): NetworkResult<LoanPageResponse> =
                    NetworkResult.Success(
                        LoanPageResponse(
                            content = emptyList(),
                            page = 0,
                            size = 100,
                            totalElements = 0
                        )
                    )
            }
        )

        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.loans).isEmpty()
        assertThat(viewModel.state.value.error).isNull()
    }
}
