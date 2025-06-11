package software.ehsan.movieshowcase.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import software.ehsan.movieshowcase.util.TestDispatcherProvider


class UserSettingRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var userSettingRepository: UserSettingRepositoryImpl
    private val testScope = TestScope(TestDispatcherProvider().default)

    @Before
    fun setup() {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { temporaryFolder.newFile("test_user_settings.preferences_pb") }
        )
        userSettingRepository = UserSettingRepositoryImpl(testDataStore)
    }

    @Test
    fun isDarkTheme_returnDefault_returnFalse() = runTest {
        val initialTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(false, initialTheme)
    }

    @Test
    fun isDarkTheme_toggleDarkTheme_returnTrue() = runTest {
        val initialTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(false, initialTheme)
        userSettingRepository.toggleDarkMode(true)
        val newTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(true, newTheme)
    }

    @Test
    fun isDarkTheme_toggleBackToFalse_returnFalse() = runTest {
        val initialTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(false, initialTheme)
        userSettingRepository.toggleDarkMode(true)
        val newTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(true, newTheme)
        userSettingRepository.toggleDarkMode(false)
        val toggleBackTheme = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(false, toggleBackTheme)
    }

    @Test
    fun isDarkTheme_readMultipleTime_returnSameValue() = runTest {
        userSettingRepository.toggleDarkMode(true)
        val firstAccess = userSettingRepository.isDarkTheme.first()
        val secondAccess = userSettingRepository.isDarkTheme.first()
        Assert.assertEquals(true, firstAccess)
        Assert.assertEquals(true, secondAccess)
    }

}