package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.model.entity.Match
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMatchViewModel
    @Inject
    constructor(
        private val matchRepository: MatchRepository,
    ) : ViewModel() {
        fun addMatch(
            opponent: String,
            dateTime: Long,
            location: String?,
            isHome: Boolean,
            totalMatchDurationMinutes: Int,
            numberOfBlocks: Int,
            blockDurationMinutes: Int,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                val match =
                    Match(
                        opponent = opponent,
                        dateTime = dateTime,
                        location = location,
                        isHome = isHome,
                        totalMatchDurationMinutes = totalMatchDurationMinutes,
                        numberOfBlocks = numberOfBlocks,
                        blockDurationMinutes = blockDurationMinutes,
                    )
                matchRepository.insertMatch(match)
                onSuccess()
            }
        }
    }
