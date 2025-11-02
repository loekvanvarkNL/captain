package com.lvark.teamcaptain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val opponent: String,
    val dateTime: Long,
    val location: String? = null,
    val isHome: Boolean = true,
    val numberOfBlocks: Int = 3,
    val blockDurationMinutes: Int = 10,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
