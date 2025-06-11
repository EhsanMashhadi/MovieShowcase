package software.ehsan.movieshowcase.core.data.repository

import kotlinx.coroutines.flow.Flow

interface UserSettingRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun toggleDarkMode(isDarkTheme: Boolean)
}