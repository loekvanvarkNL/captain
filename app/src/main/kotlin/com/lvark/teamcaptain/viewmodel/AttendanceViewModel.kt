package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.AttendanceRepository
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.Attendance
import com.lvark.teamcaptain.model.entity.AttendanceStatus
import com.lvark.teamcaptain.model.entity.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerAttendance(
    val player: Player,
    val status: AttendanceStatus,
)

@HiltViewModel
class AttendanceViewModel
    @Inject
    constructor(
        private val playerRepository: PlayerRepository,
        private val attendanceRepository: AttendanceRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val matchId: Long = savedStateHandle.get<Long>("matchId") ?: 0L

        val playerAttendances: StateFlow<List<PlayerAttendance>> =
            combine(
                playerRepository.getAllPlayers(),
                attendanceRepository.getAttendanceForMatch(matchId),
            ) { players: List<Player>, attendances: List<Attendance> ->
                val attendanceMap = attendances.associateBy { it.playerId }
                players.map { player ->
                    PlayerAttendance(
                        player = player,
                        status = attendanceMap[player.id]?.status ?: AttendanceStatus.UNKNOWN,
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        fun updateAttendance(
            playerId: Long,
            status: AttendanceStatus,
        ) {
            viewModelScope.launch {
                val attendance =
                    Attendance(
                        playerId = playerId,
                        matchId = matchId,
                        status = status,
                    )
                attendanceRepository.upsertAttendance(attendance)
            }
        }

        fun markAllPresent() {
            viewModelScope.launch {
                playerAttendances.value.forEach { playerAttendance ->
                    updateAttendance(playerAttendance.player.id, AttendanceStatus.PRESENT)
                }
            }
        }
    }
