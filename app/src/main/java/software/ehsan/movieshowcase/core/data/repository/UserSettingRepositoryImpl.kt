package software.ehsan.movieshowcase.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserSettingRepository {

    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")

    override val isDarkTheme: Flow<Boolean> = dataStore.data.map { it[isDarkModeKey] == true }

    override suspend fun toggleDarkMode(isDarkTheme: Boolean) {
        dataStore.edit { settings ->
            settings[isDarkModeKey] = isDarkTheme
        }
    }
}