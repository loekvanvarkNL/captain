package com.lvark.teamcaptain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?,
    val provider: AuthProvider,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
)

enum class AuthProvider {
    GOOGLE,
    GITHUB,
}
