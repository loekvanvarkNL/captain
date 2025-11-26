package com.lvark.teamcaptain.data.auth

import android.content.Context
import android.net.Uri
import com.lvark.teamcaptain.BuildConfig
import com.lvark.teamcaptain.data.local.UserDao
import com.lvark.teamcaptain.model.entity.AuthProvider
import com.lvark.teamcaptain.model.entity.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val userDao: UserDao,
    ) {
        private val authStateManager = AuthStateManager.getInstance(context)

        // Google OIDC configuration
        fun getGoogleServiceConfig(): AuthorizationServiceConfiguration {
            return AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"),
                Uri.parse("https://oauth2.googleapis.com/token"),
            )
        }

        // GitHub OAuth configuration
        fun getGitHubServiceConfig(): AuthorizationServiceConfiguration {
            return AuthorizationServiceConfiguration(
                Uri.parse("https://github.com/login/oauth/authorize"),
                Uri.parse("https://github.com/login/oauth/access_token"),
            )
        }

        // Build authorization request for a provider
        fun buildAuthRequest(
            provider: AuthProvider,
            serviceConfig: AuthorizationServiceConfiguration,
        ): AuthorizationRequest {
            val clientId =
                when (provider) {
                    AuthProvider.GOOGLE -> BuildConfig.GOOGLE_CLIENT_ID
                    AuthProvider.GITHUB -> BuildConfig.GITHUB_CLIENT_ID
                }

            val redirectUri =
                when (provider) {
                    AuthProvider.GOOGLE -> Uri.parse("com.lvark.teamcaptain:/oauth2redirect")
                    AuthProvider.GITHUB -> Uri.parse("com.lvark.teamcaptain://oauth2redirect/github")
                }

            val scopes =
                when (provider) {
                    AuthProvider.GOOGLE -> listOf("openid", "profile", "email")
                    AuthProvider.GITHUB -> listOf("read:user", "user:email")
                }

            return AuthorizationRequest
                .Builder(
                    serviceConfig,
                    clientId,
                    ResponseTypeValues.CODE,
                    redirectUri,
                ).setScopes(scopes)
                .build()
        }

        // Handle authorization response and exchange code for token
        suspend fun handleAuthResponse(
            authService: AuthorizationService,
            response: AuthorizationResponse,
            provider: AuthProvider,
        ): Result<User> =
            withContext(Dispatchers.IO) {
                try {
                    // Exchange authorization code for tokens
                    val tokenRequest = response.createTokenExchangeRequest()
                    val tokenResponse =
                        suspendTokenRequest(authService, tokenRequest)
                            ?: return@withContext Result.failure(Exception("Token exchange failed"))

                    // Create auth state and save it
                    val authState = AuthState()
                    authState.update(tokenResponse, null)
                    authStateManager.saveAuthState(authState)

                    // Fetch user profile from provider
                    val accessToken = tokenResponse.accessToken ?: return@withContext Result.failure(Exception("No access token"))
                    val userProfile = fetchUserProfile(accessToken, provider)

                    // Create and save user entity
                    val user =
                        User(
                            id = userProfile.id,
                            email = userProfile.email,
                            displayName = userProfile.displayName,
                            profilePictureUrl = userProfile.profilePictureUrl,
                            provider = provider,
                            lastLoginAt = System.currentTimeMillis(),
                        )

                    userDao.insertUser(user)
                    Result.success(user)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        // Fetch user profile from provider API
        private suspend fun fetchUserProfile(
            accessToken: String,
            provider: AuthProvider,
        ): UserProfile =
            withContext(Dispatchers.IO) {
                when (provider) {
                    AuthProvider.GOOGLE -> fetchGoogleUserProfile(accessToken)
                    AuthProvider.GITHUB -> fetchGitHubUserProfile(accessToken)
                }
            }

        private fun fetchGoogleUserProfile(accessToken: String): UserProfile {
            val url = URL("https://www.googleapis.com/oauth2/v3/userinfo")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $accessToken")

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            return UserProfile(
                id = json.getString("sub"),
                email = json.optString("email", ""),
                displayName = json.optString("name"),
                profilePictureUrl = json.optString("picture"),
            )
        }

        private fun fetchGitHubUserProfile(accessToken: String): UserProfile {
            val url = URL("https://api.github.com/user")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Accept", "application/json")

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            // GitHub email might be null, fetch separately if needed
            val email = json.optString("email")?.takeIf { it.isNotEmpty() } ?: fetchGitHubEmail(accessToken)

            return UserProfile(
                id = json.getString("id"),
                email = email,
                displayName = json.optString("name"),
                profilePictureUrl = json.optString("avatar_url"),
            )
        }

        private fun fetchGitHubEmail(accessToken: String): String {
            val url = URL("https://api.github.com/user/emails")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Accept", "application/json")

            val response = connection.inputStream.bufferedReader().readText()
            val jsonArray = org.json.JSONArray(response)

            // Find primary email
            for (i in 0 until jsonArray.length()) {
                val emailObj = jsonArray.getJSONObject(i)
                if (emailObj.getBoolean("primary")) {
                    return emailObj.getString("email")
                }
            }

            // Fallback to first email
            return if (jsonArray.length() > 0) {
                jsonArray.getJSONObject(0).getString("email")
            } else {
                ""
            }
        }

        // Helper to perform token request synchronously in coroutine
        private suspend fun suspendTokenRequest(
            authService: AuthorizationService,
            tokenRequest: TokenRequest,
        ) = withContext(Dispatchers.IO) {
            var result: net.openid.appauth.TokenResponse? = null
            val latch = java.util.concurrent.CountDownLatch(1)

            authService.performTokenRequest(tokenRequest) { response, _ ->
                result = response
                latch.countDown()
            }

            latch.await()
            result
        }

        // Check if user is authenticated
        fun isAuthenticated(): Boolean {
            val authState = authStateManager.getAuthState()
            return authState?.isAuthorized == true
        }

        // Logout
        suspend fun logout() =
            withContext(Dispatchers.IO) {
                authStateManager.clearAuthState()
                userDao.deleteAllUsers()
            }

        // Save auth state
        fun saveAuthState(authState: AuthState) {
            authStateManager.saveAuthState(authState)
        }

        // Get current auth state
        fun getAuthState(): AuthState? {
            return authStateManager.getAuthState()
        }
    }

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?,
)
