package com.lvark.teamcaptain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val surname: String,
    val number: Int? = null,
    val preferredFoot: PreferredFoot = PreferredFoot.RIGHT,
    val preferredPositions: List<Position> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val fullName: String
        get() = "$firstName $surname"
}

enum class Position(
    val abbreviation: String,
    val fullName: String,
) {
    GK("GK", "Goalkeeper"),
    DF("DF", "Defender"),
    MF("MF", "Midfielder"),
    FW("FW", "Forward"),
}

enum class PreferredFoot {
    LEFT,
    RIGHT,
    BOTH,
}
