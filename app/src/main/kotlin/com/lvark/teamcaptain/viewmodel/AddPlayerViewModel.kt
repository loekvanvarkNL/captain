package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.Position
import com.lvark.teamcaptain.model.entity.PreferredFoot
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
            firstName: String,
            surname: String,
            number: Int?,
            preferredFoot: PreferredFoot,
            preferredPositions: List<Position>,
            onSuccess: () -> Unit,
        ) {
            viewModelScope.launch {
                val player =
                    Player(
                        firstName = firstName,
                        surname = surname,
                        number = number,
                        preferredFoot = preferredFoot,
                        preferredPositions = preferredPositions,
                    )
                playerRepository.insertPlayer(player)
                onSuccess()
            }
        }
    }
