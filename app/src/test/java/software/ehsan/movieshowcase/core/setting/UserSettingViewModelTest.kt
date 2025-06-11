package software.ehsan.movieshowcase.core.setting

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.UserSettingRepository
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var userSettingRepository: UserSettingRepository
    private lateinit var mockIsDarkThemeFlow: MutableStateFlow<Boolean>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockIsDarkThemeFlow = MutableStateFlow(false)
        every { userSettingRepository.isDarkTheme } returns mockIsDarkThemeFlow
        coEvery { userSettingRepository.toggleDarkMode(any()) } answers {
            val newThemeValue = firstArg<Boolean>()
            mockIsDarkThemeFlow.value = newThemeValue
        }
    }

    @Test
    fun initiateViewModel_returnDefaultTheme_showExpectedLightTheme() = runTest {
        val viewModel = UserSettingViewModel(userSettingRepository)
        viewModel.userSettingState.test {
            Assert.assertEquals(UserSettingState(isDarkMode = false), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleTheme_themeToggled_showExpectedDarkTheme() = runTest {
        val viewModel = UserSettingViewModel(userSettingRepository)
        viewModel.userSettingState.test {
            Assert.assertEquals(UserSettingState(isDarkMode = false), awaitItem())
            viewModel.toggleTheme()
            Assert.assertEquals(UserSettingState(isDarkMode = true), awaitItem())
            coVerify(exactly = 1) { userSettingRepository.toggleDarkMode(true) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}