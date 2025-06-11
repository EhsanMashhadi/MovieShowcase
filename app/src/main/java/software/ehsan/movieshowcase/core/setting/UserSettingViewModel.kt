package software.ehsan.movieshowcase.core.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.core.data.repository.UserSettingRepository

@HiltViewModel
class UserSettingViewModel @Inject constructor(private val userSettingRepository: UserSettingRepository) :
    ViewModel() {

    val userSettingState = userSettingRepository.isDarkTheme.map {
        UserSettingState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettingState(false)
    )

    fun toggleTheme() {
        viewModelScope.launch {
            userSettingRepository.toggleDarkMode(!userSettingState.value.isDarkMode)
        }
    }
}

data class UserSettingState(val isDarkMode: Boolean)