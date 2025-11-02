package com.lvark.teamcaptain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lvark.teamcaptain.model.entity.Attendance
import com.lvark.teamcaptain.model.entity.LineupAssignment
import com.lvark.teamcaptain.model.entity.Match
import com.lvark.teamcaptain.model.entity.Player

@Database(
    entities = [
        Player::class,
        Match::class,
        Attendance::class,
        LineupAssignment::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class TeamCaptainDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    abstract fun matchDao(): MatchDao

    abstract fun attendanceDao(): AttendanceDao

    abstract fun lineupAssignmentDao(): LineupAssignmentDao

    companion object {
        const val DATABASE_NAME = "team_captain.db"
    }
}
