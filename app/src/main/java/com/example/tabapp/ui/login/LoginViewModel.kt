package com.example.tabapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabapp.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val id: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: String? = null,
)

/** ViewModel 에 상태를 두어 화면 회전(가로↔세로) 시에도 입력값이 유지됩니다. */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdChange(value: String) = _uiState.update { it.copy(id = value.trim(), errorMessage = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return
        when {
            state.id.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "아이디를 입력하세요.") }
                return
            }
            state.password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "비밀번호를 입력하세요.") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            AuthRepository.login(state.id, state.password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, password = "", loggedInUser = user) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    /** 메인 화면으로 이동한 뒤 호출하여 중복 이동을 막습니다. */
    fun onLoginHandled() = _uiState.update { it.copy(loggedInUser = null) }
}
