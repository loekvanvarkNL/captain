package com.lvark.teamcaptain.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.openid.appauth.AuthState

class AuthStateManager private constructor(context: Context) {
    private val masterKey =
        MasterKey
            .Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val encryptedPrefs =
        EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    fun saveAuthState(authState: AuthState) {
        encryptedPrefs
            .edit()
            .putString(KEY_AUTH_STATE, authState.jsonSerializeString())
            .apply()
    }

    fun getAuthState(): AuthState? {
        val jsonString = encryptedPrefs.getString(KEY_AUTH_STATE, null)
        return jsonString?.let { AuthState.jsonDeserialize(it) }
    }

    fun clearAuthState() {
        encryptedPrefs.edit().remove(KEY_AUTH_STATE).apply()
    }

    companion object {
        private const val KEY_AUTH_STATE = "auth_state"

        @Volatile
        private var instance: AuthStateManager? = null

        fun getInstance(context: Context): AuthStateManager {
            return instance ?: synchronized(this) {
                instance ?: AuthStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
