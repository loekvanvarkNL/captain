package com.lvark.teamcaptain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lvark.teamcaptain.model.entity.Attendance
import com.lvark.teamcaptain.model.entity.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE matchId = :matchId")
    fun getAttendanceForMatch(matchId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE playerId = :playerId")
    fun getAttendanceForPlayer(playerId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE matchId = :matchId AND playerId = :playerId")
    suspend fun getAttendance(
        matchId: Long,
        playerId: Long,
    ): Attendance?

    @Query("SELECT * FROM attendance WHERE matchId = :matchId AND playerId = :playerId")
    fun observeAttendance(
        matchId: Long,
        playerId: Long,
    ): Flow<Attendance?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance WHERE matchId = :matchId AND playerId = :playerId")
    suspend fun deleteAttendance(
        matchId: Long,
        playerId: Long,
    )

    @Query("SELECT COUNT(*) FROM attendance WHERE matchId = :matchId AND status = :status")
    fun getAttendanceCountByStatus(
        matchId: Long,
        status: AttendanceStatus,
    ): Flow<Int>

    @Transaction
    suspend fun upsertAttendance(attendance: Attendance) {
        val existing = getAttendance(attendance.matchId, attendance.playerId)
        if (existing != null) {
            updateAttendance(attendance.copy(id = existing.id))
        } else {
            insertAttendance(attendance)
        }
    }
}
