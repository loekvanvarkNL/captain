package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.Position
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlayerViewModel
    @Inject
    constructor(
        private val playerRepository: PlayerRepository,
    ) : ViewModel() {
        fun addPlayer(
            name: String,
            number: Int?,
            preferredPositions: List<Position>,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                val player =
                    Player(
                        name = name,
                        number = number,
                        preferredPositions = preferredPositions,
                    )
                playerRepository.insertPlayer(player)
                onSuccess()
            }
        }
    }
