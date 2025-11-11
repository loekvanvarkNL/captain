package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.Position
import com.lvark.teamcaptain.model.entity.PreferredFoot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerDetailViewModel
    @Inject
    constructor(
        private val playerRepository: PlayerRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val playerId: Long = savedStateHandle.get<Long>("playerId") ?: 0L

        val player: StateFlow<Player?> =
            playerRepository
                .getPlayerById(playerId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        fun updatePlayer(
            firstName: String,
            surname: String,
            number: Int?,
            preferredFoot: PreferredFoot,
            preferredPositions: List<Position>,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                player.value?.let { currentPlayer ->
                    val updatedPlayer =
                        currentPlayer.copy(
                            firstName = firstName,
                            surname = surname,
                            number = number,
                            preferredFoot = preferredFoot,
                            preferredPositions = preferredPositions,
                        )
                    playerRepository.updatePlayer(updatedPlayer)
                    onSuccess()
                }
            }
        }

        fun deletePlayer(onSuccess: () -> Unit) {
            viewModelScope.launch {
                player.value?.let {
                    playerRepository.deletePlayer(it)
                    onSuccess()
                }
            }
        }
    }
