package com.lvark.teamcaptain.data.repository

import com.lvark.teamcaptain.data.local.AttendanceDao
import com.lvark.teamcaptain.model.entity.Attendance
import com.lvark.teamcaptain.model.entity.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface AttendanceRepository {
    fun getAttendanceForMatch(matchId: Long): Flow<List<Attendance>>

    fun getAttendanceForPlayer(playerId: Long): Flow<List<Attendance>>

    fun getAttendance(
        matchId: Long,
        playerId: Long,
    ): Flow<Attendance?>

    suspend fun upsertAttendance(attendance: Attendance)

    suspend fun deleteAttendance(
        matchId: Long,
        playerId: Long,
    )

    fun getAttendanceCountByStatus(
        matchId: Long,
        status: AttendanceStatus,
    ): Flow<Int>
}

@Singleton
class AttendanceRepositoryImpl
    @Inject
    constructor(
        private val attendanceDao: AttendanceDao,
    ) : AttendanceRepository {
        override fun getAttendanceForMatch(matchId: Long): Flow<List<Attendance>> = attendanceDao.getAttendanceForMatch(matchId)

        override fun getAttendanceForPlayer(playerId: Long): Flow<List<Attendance>> = attendanceDao.getAttendanceForPlayer(playerId)

        override fun getAttendance(
            matchId: Long,
            playerId: Long,
        ): Flow<Attendance?> = attendanceDao.observeAttendance(matchId, playerId)

        override suspend fun upsertAttendance(attendance: Attendance) {
            val updatedAttendance = attendance.copy(updatedAt = System.currentTimeMillis())
            attendanceDao.upsertAttendance(updatedAttendance)
        }

        override suspend fun deleteAttendance(
            matchId: Long,
            playerId: Long,
        ) {
            attendanceDao.deleteAttendance(matchId, playerId)
        }

        override fun getAttendanceCountByStatus(
            matchId: Long,
            status: AttendanceStatus,
        ): Flow<Int> = attendanceDao.getAttendanceCountByStatus(matchId, status)
    }
