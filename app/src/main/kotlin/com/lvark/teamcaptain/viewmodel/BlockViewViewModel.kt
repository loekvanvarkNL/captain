package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.LineupRepository
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.LineupAssignment
import com.lvark.teamcaptain.model.entity.Match
import com.lvark.teamcaptain.model.entity.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayerPlayingTime(
    val player: Player,
    val blocksPlayed: List<Int>,
    val totalMinutes: Int,
)

data class BlockSummary(
    val blockNumber: Int,
    val players: List<Player>,
    val durationMinutes: Int,
)

@HiltViewModel
class BlockViewViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val matchRepository: MatchRepository,
        private val playerRepository: PlayerRepository,
        private val lineupRepository: LineupRepository,
    ) : ViewModel() {
        private val matchId: Long = checkNotNull(savedStateHandle["matchId"])

        val match: StateFlow<Match?> =
            matchRepository.getMatchById(matchId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        val lineupAssignments: StateFlow<List<LineupAssignment>> =
            lineupRepository.getLineupForMatch(matchId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val blockSummaries: StateFlow<List<BlockSummary>> =
            combine(
                match,
                lineupAssignments,
                playerRepository.getAllPlayers(),
            ) { matchData, assignments, allPlayers ->
                if (matchData == null) return@combine emptyList()

                val playerMap = allPlayers.associateBy { it.id }
                val blockMap = assignments.groupBy { it.blockNumber }

                (1..matchData.numberOfBlocks).map { blockNum ->
                    val blockAssignments = blockMap[blockNum] ?: emptyList()
                    val players =
                        blockAssignments
                            .mapNotNull { playerMap[it.playerId] }
                            .distinctBy { it.id }
                    BlockSummary(
                        blockNumber = blockNum,
                        players = players,
                        durationMinutes = matchData.blockDurationMinutes,
                    )
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val playerPlayingTimes: StateFlow<List<PlayerPlayingTime>> =
            combine(
                match,
                lineupAssignments,
                playerRepository.getAllPlayers(),
            ) { matchData, assignments, allPlayers ->
                if (matchData == null) return@combine emptyList()

                val playerMap = allPlayers.associateBy { it.id }
                val playerBlocks =
                    assignments
                        .groupBy { it.playerId }
                        .mapValues { (_, playerAssignments) ->
                            playerAssignments.map { it.blockNumber }.sorted().distinct()
                        }

                playerBlocks.mapNotNull { (playerId, blocks) ->
                    val player = playerMap[playerId] ?: return@mapNotNull null
                    val totalMinutes = blocks.size * matchData.blockDurationMinutes

                    PlayerPlayingTime(
                        player = player,
                        blocksPlayed = blocks,
                        totalMinutes = totalMinutes,
                    )
                }.sortedByDescending { it.totalMinutes }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
