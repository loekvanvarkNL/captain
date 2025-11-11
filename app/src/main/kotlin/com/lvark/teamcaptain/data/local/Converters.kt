package com.lvark.teamcaptain.data.local

import androidx.room.TypeConverter
import com.lvark.teamcaptain.model.entity.Position
import com.lvark.teamcaptain.model.entity.PreferredFoot

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

    @TypeConverter
    fun fromPreferredFoot(preferredFoot: PreferredFoot): String {
        return preferredFoot.name
    }

    @TypeConverter
    fun toPreferredFoot(preferredFootString: String): PreferredFoot {
        return try {
            PreferredFoot.valueOf(preferredFootString)
        } catch (e: IllegalArgumentException) {
            PreferredFoot.RIGHT
        }
    }
}
