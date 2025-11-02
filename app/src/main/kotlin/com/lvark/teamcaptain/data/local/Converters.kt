package com.lvark.teamcaptain.data.local

import androidx.room.TypeConverter
import com.lvark.teamcaptain.model.entity.Position

class Converters {
    @TypeConverter
    fun fromPositionList(positions: List<Position>): String {
        return positions.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toPositionList(positionsString: String): List<Position> {
        if (positionsString.isBlank()) return emptyList()
        return positionsString.split(",").mapNotNull {
            try {
                Position.valueOf(it.trim())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
