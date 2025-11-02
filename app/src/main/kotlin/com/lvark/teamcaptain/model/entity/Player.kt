package com.lvark.teamcaptain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val number: Int? = null,
    val preferredPositions: List<Position> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

enum class Position {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD,
}
