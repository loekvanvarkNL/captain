package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel
    @Inject
    constructor(
        private val playerRepository: PlayerRepository,
    ) : ViewModel() {
        val players: StateFlow<List<Player>> =
            playerRepository
                .getAllPlayers()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        fun deletePlayer(player: Player) {
            viewModelScope.launch {
                playerRepository.deletePlayer(player)
            }
        }
    }
