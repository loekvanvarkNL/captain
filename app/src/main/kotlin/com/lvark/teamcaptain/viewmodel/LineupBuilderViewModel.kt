package com.lvark.teamcaptain.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvark.teamcaptain.data.repository.AttendanceRepository
import com.lvark.teamcaptain.data.repository.LineupRepository
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.model.entity.AttendanceStatus
import com.lvark.teamcaptain.model.entity.LineupAssignment
import com.lvark.teamcaptain.model.entity.Match
import com.lvark.teamcaptain.model.entity.Player
import com.lvark.teamcaptain.model.entity.Position
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerPosition(
    val player: Player,
    val position: Position?,
    val isOnBench: Boolean = false,
)

data class BlockLineup(
    val blockNumber: Int,
    val players: List<PlayerPosition>,
)

@HiltViewModel
class LineupBuilderViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val matchRepository: MatchRepository,
        private val playerRepository: PlayerRepository,
        private val attendanceRepository: AttendanceRepository,
        private val lineupRepository: LineupRepository,
    ) : ViewModel() {
        private val matchId: Long = checkNotNull(savedStateHandle["matchId"])

        val match: StateFlow<Match?> =
            matchRepository.getMatchById(matchId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        private val presentPlayers: StateFlow<List<Player>> =
            combine(
                playerRepository.getAllPlayers(),
                attendanceRepository.getAttendanceForMatch(matchId),
            ) { players, attendances ->
                val presentPlayerIds =
                    attendances
                        .filter { it.status == AttendanceStatus.PRESENT }
                        .map { it.playerId }
                        .toSet()
                players.filter { it.id in presentPlayerIds }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        private val _currentBlockNumber = MutableStateFlow(1)
        val currentBlockNumber: StateFlow<Int> = _currentBlockNumber.asStateFlow()

        private val _fieldPlayers = MutableStateFlow<List<PlayerPosition>>(emptyList())
        val fieldPlayers: StateFlow<List<PlayerPosition>> = _fieldPlayers.asStateFlow()

        private val _benchPlayers = MutableStateFlow<List<Player>>(emptyList())
        val benchPlayers: StateFlow<List<Player>> = _benchPlayers.asStateFlow()

        private val _allBlockLineups = MutableStateFlow<List<BlockLineup>>(emptyList())
        val allBlockLineups: StateFlow<List<BlockLineup>> = _allBlockLineups.asStateFlow()

        init {
            viewModelScope.launch {
                presentPlayers.collect { players ->
                    initializeLineup(players)
                }
            }
        }

        private fun initializeLineup(players: List<Player>) {
            if (players.size < 6) {
                // Not enough players for a lineup
                _fieldPlayers.value = emptyList()
                _benchPlayers.value = emptyList()
                return
            }

            // Initialize first 6 players on field, rest on bench
            val initial =
                players.take(6).mapIndexed { index, player ->
                    val position =
                        when (index) {
                            0 -> Position.GK
                            1, 2 -> Position.DF
                            3 -> Position.MF
                            4, 5 -> Position.FW
                            else -> null
                        }
                    PlayerPosition(player, position, false)
                }

            _fieldPlayers.value = initial
            _benchPlayers.value = players.drop(6)
        }

        fun movePlayerToField(
            player: Player,
            position: Position,
        ) {
            val currentField = _fieldPlayers.value.toMutableList()
            val currentBench = _benchPlayers.value.toMutableList()

            // Remove from bench if present
            currentBench.remove(player)

            // Check if player is already on field
            val existingIndex = currentField.indexOfFirst { it.player.id == player.id }
            if (existingIndex != -1) {
                // Update position
                currentField[existingIndex] = currentField[existingIndex].copy(position = position)
            } else {
                // Add to field
                currentField.add(PlayerPosition(player, position, false))
            }

            _fieldPlayers.value = currentField
            _benchPlayers.value = currentBench
        }

        fun movePlayerToBench(player: Player) {
            val currentField = _fieldPlayers.value.toMutableList()
            val currentBench = _benchPlayers.value.toMutableList()

            // Remove from field
            currentField.removeAll { it.player.id == player.id }

            // Add to bench if not already there
            if (!currentBench.any { it.id == player.id }) {
                currentBench.add(player)
            }

            _fieldPlayers.value = currentField
            _benchPlayers.value = currentBench
        }

        fun removePlayerFromPosition(position: Position) {
            val currentField = _fieldPlayers.value.toMutableList()
            val playerAtPosition = currentField.find { it.position == position }

            if (playerAtPosition != null) {
                movePlayerToBench(playerAtPosition.player)
            }
        }

        fun saveCurrentBlock() {
            viewModelScope.launch {
                val assignments =
                    _fieldPlayers.value.mapNotNull { playerPosition ->
                        playerPosition.position?.let { position ->
                            LineupAssignment(
                                matchId = matchId,
                                playerId = playerPosition.player.id,
                                blockNumber = _currentBlockNumber.value,
                                position = position,
                                isStarting = _currentBlockNumber.value == 1,
                            )
                        }
                    }

                // Save to database
                lineupRepository.saveLineupForBlock(matchId, _currentBlockNumber.value, assignments)

                // Update block lineups
                val updatedBlocks = _allBlockLineups.value.toMutableList()
                val existingIndex = updatedBlocks.indexOfFirst { it.blockNumber == _currentBlockNumber.value }

                val newBlock = BlockLineup(_currentBlockNumber.value, _fieldPlayers.value)
                if (existingIndex != -1) {
                    updatedBlocks[existingIndex] = newBlock
                } else {
                    updatedBlocks.add(newBlock)
                }

                _allBlockLineups.value = updatedBlocks.sortedBy { it.blockNumber }
            }
        }

        fun goToNextBlock() {
            val currentMatch = match.value ?: return
            if (_currentBlockNumber.value < currentMatch.numberOfBlocks) {
                saveCurrentBlock()
                _currentBlockNumber.value += 1
                loadBlockLineup(_currentBlockNumber.value)
            }
        }

        fun goToPreviousBlock() {
            if (_currentBlockNumber.value > 1) {
                saveCurrentBlock()
                _currentBlockNumber.value -= 1
                loadBlockLineup(_currentBlockNumber.value)
            }
        }

        private fun loadBlockLineup(blockNumber: Int) {
            val existingBlock = _allBlockLineups.value.find { it.blockNumber == blockNumber }
            if (existingBlock != null) {
                _fieldPlayers.value = existingBlock.players
                // Update bench to exclude field players
                val allPresent = presentPlayers.value
                _benchPlayers.value =
                    allPresent.filter { player ->
                        existingBlock.players.none { it.player.id == player.id }
                    }
            }
        }

        fun generateAutoRotation() {
            viewModelScope.launch {
                val currentMatch = match.value ?: return@launch
                val players = presentPlayers.value

                if (players.size < 6) return@launch

                val rotations =
                    calculateEqualPlayingTime(
                        players = players,
                        numberOfBlocks = currentMatch.numberOfBlocks,
                        playersPerBlock = 6,
                    )

                val newBlockLineups =
                    rotations.mapIndexed { index, blockPlayers ->
                        val playerPositions =
                            blockPlayers.mapIndexed { playerIndex, player ->
                                val position =
                                    when (playerIndex) {
                                        0 -> Position.GK
                                        1, 2 -> Position.DF
                                        3 -> Position.MF
                                        4, 5 -> Position.FW
                                        else -> null
                                    }
                                PlayerPosition(player, position, false)
                            }
                        BlockLineup(index + 1, playerPositions)
                    }

                _allBlockLineups.value = newBlockLineups

                // Load first block
                _currentBlockNumber.value = 1
                loadBlockLineup(1)
            }
        }

        private fun calculateEqualPlayingTime(
            players: List<Player>,
            numberOfBlocks: Int,
            playersPerBlock: Int,
        ): List<List<Player>> {
            val rotations = mutableListOf<List<Player>>()
            val totalPlayers = players.size

            // Calculate how many blocks each player should play
            val totalSlots = numberOfBlocks * playersPerBlock
            val blocksPerPlayer = totalSlots / totalPlayers

            // Track how many blocks each player has played
            val playerBlockCount =
                mutableMapOf<Long, Int>().apply {
                    players.forEach { this[it.id] = 0 }
                }

            for (blockNum in 0 until numberOfBlocks) {
                val blockPlayers = mutableListOf<Player>()

                // Select players who have played the least
                val sortedPlayers = players.sortedBy { playerBlockCount[it.id] ?: 0 }

                for (i in 0 until minOf(playersPerBlock, sortedPlayers.size)) {
                    val player = sortedPlayers[i]
                    blockPlayers.add(player)
                    playerBlockCount[player.id] = (playerBlockCount[player.id] ?: 0) + 1
                }

                rotations.add(blockPlayers)
            }

            return rotations
        }

        fun saveAllBlocks(onSuccess: () -> Unit) {
            viewModelScope.launch {
                saveCurrentBlock()

                // Save all blocks to database
                _allBlockLineups.value.forEach { block ->
                    val assignments =
                        block.players.mapNotNull { playerPosition ->
                            playerPosition.position?.let { position ->
                                LineupAssignment(
                                    matchId = matchId,
                                    playerId = playerPosition.player.id,
                                    blockNumber = block.blockNumber,
                                    position = position,
                                    isStarting = block.blockNumber == 1,
                                )
                            }
                        }
                    lineupRepository.saveLineupForBlock(matchId, block.blockNumber, assignments)
                }

                onSuccess()
            }
        }
    }
