package com.lvark.teamcaptain.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.auth.AuthManager
import com.lvark.teamcaptain.data.local.UserDao
import com.lvark.teamcaptain.model.entity.AuthProvider
import com.lvark.teamcaptain.model.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authManager: AuthManager,
        private val userDao: UserDao,
    ) : ViewModel() {
        val currentUser: StateFlow<User?> =
            userDao
                .observeCurrentUser()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        val isLoading = MutableStateFlow(false)
        val errorMessage = MutableStateFlow<String?>(null)

        private var authService: AuthorizationService? = null
        private var pendingProvider: AuthProvider? = null

        fun initAuthService(activity: ComponentActivity) {
            authService = AuthorizationService(activity)
        }

        fun signInWithGoogle(
            activity: ComponentActivity,
            launcher: ActivityResultLauncher<Intent>,
        ) {
            pendingProvider = AuthProvider.GOOGLE
            val serviceConfig = authManager.getGoogleServiceConfig()
            val authRequest = authManager.buildAuthRequest(AuthProvider.GOOGLE, serviceConfig)

            val authIntent =
                authService?.getAuthorizationRequestIntent(authRequest)
                    ?: return

            launcher.launch(authIntent)
        }

        fun signInWithGitHub(
            activity: ComponentActivity,
            launcher: ActivityResultLauncher<Intent>,
        ) {
            pendingProvider = AuthProvider.GITHUB
            val serviceConfig = authManager.getGitHubServiceConfig()
            val authRequest = authManager.buildAuthRequest(AuthProvider.GITHUB, serviceConfig)

            val authIntent =
                authService?.getAuthorizationRequestIntent(authRequest)
                    ?: return

            launcher.launch(authIntent)
        }

        fun handleAuthResult(
            resultCode: Int,
            data: Intent?,
        ) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                errorMessage.value = "Authentication cancelled"
                isLoading.value = false
                return
            }

            val response = AuthorizationResponse.fromIntent(data)
            val exception = AuthorizationException.fromIntent(data)

            when {
                response != null && pendingProvider != null -> {
                    isLoading.value = true
                    viewModelScope.launch {
                        val result =
                            authManager.handleAuthResponse(
                                authService!!,
                                response,
                                pendingProvider!!,
                            )
                        isLoading.value = false

                        result.fold(
                            onSuccess = {
                                errorMessage.value = null
                            },
                            onFailure = { error ->
                                errorMessage.value = error.message ?: "Authentication failed"
                            },
                        )
                    }
                }
                exception != null -> {
                    errorMessage.value = exception.message ?: "Authentication error"
                    isLoading.value = false
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                authManager.logout()
            }
        }

        fun clearError() {
            errorMessage.value = null
        }

        override fun onCleared() {
            super.onCleared()
            authService?.dispose()
        }
    }
