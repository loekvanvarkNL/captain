package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.model.entity.Match
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchListViewModel
    @Inject
    constructor(
        private val matchRepository: MatchRepository,
    ) : ViewModel() {
        val upcomingMatches: StateFlow<List<Match>> =
            matchRepository
                .getUpcomingMatches()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        val pastMatches: StateFlow<List<Match>> =
            matchRepository
                .getPastMatches()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        fun deleteMatch(match: Match) {
            viewModelScope.launch {
                matchRepository.deleteMatch(match)
            }
        }
    }
