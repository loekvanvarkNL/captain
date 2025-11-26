package com.lvark.teamcaptain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lvark.teamcaptain.model.entity.Attendance
import com.lvark.teamcaptain.model.entity.LineupAssignment
import com.lvark.teamcaptain.model.entity.Match
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.User

@Database(
    entities = [
        Player::class,
        Match::class,
        Attendance::class,
        LineupAssignment::class,
        User::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class TeamCaptainDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    abstract fun matchDao(): MatchDao

    abstract fun attendanceDao(): AttendanceDao

    abstract fun lineupAssignmentDao(): LineupAssignmentDao

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "team_captain.db"
    }
}
