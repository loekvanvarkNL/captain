package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.AttendanceRepository
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.model.entity.Match
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchDetailViewModel
    @Inject
    constructor(
        private val matchRepository: MatchRepository,
        private val attendanceRepository: AttendanceRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val matchId: Long = savedStateHandle.get<Long>("matchId") ?: 0L

        val match: StateFlow<Match?> =
            matchRepository
                .getMatchById(matchId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        val attendanceCount: StateFlow<Int> =
            attendanceRepository
                .getAttendanceCountByStatus(matchId, com.lvark.teamcaptain.model.entity.AttendanceStatus.PRESENT)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = 0,
                )

        fun deleteMatch(onSuccess: () -> Unit) {
            viewModelScope.launch {
                match.value?.let {
                    matchRepository.deleteMatch(it)
                    onSuccess()
                }
            }
        }
    }
