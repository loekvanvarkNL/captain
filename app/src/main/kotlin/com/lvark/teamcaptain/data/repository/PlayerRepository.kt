package com.lvark.teamcaptain.data.repository

import com.lvark.teamcaptain.data.local.PlayerDao
import com.lvark.teamcaptain.model.entity.Player
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PlayerRepository {
    fun getAllPlayers(): Flow<List<Player>>

    fun getPlayerById(playerId: Long): Flow<Player?>

    suspend fun insertPlayer(player: Player): Long

    suspend fun updatePlayer(player: Player)

    suspend fun deletePlayer(player: Player)

    fun getPlayerCount(): Flow<Int>
}

@Singleton
class PlayerRepositoryImpl
    @Inject
    constructor(
        private val playerDao: PlayerDao,
    ) : PlayerRepository {
        override fun getAllPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

        override fun getPlayerById(playerId: Long): Flow<Player?> = playerDao.observePlayerById(playerId)

        override suspend fun insertPlayer(player: Player): Long {
            val updatedPlayer =
                player.copy(
                    updatedAt = System.currentTimeMillis(),
                    createdAt = if (player.id == 0L) System.currentTimeMillis() else player.createdAt,
                )
            return playerDao.insertPlayer(updatedPlayer)
        }

        override suspend fun updatePlayer(player: Player) {
            val updatedPlayer = player.copy(updatedAt = System.currentTimeMillis())
            playerDao.updatePlayer(updatedPlayer)
        }

        override suspend fun deletePlayer(player: Player) {
            playerDao.deletePlayer(player)
        }

        override fun getPlayerCount(): Flow<Int> = playerDao.getPlayerCount()
    }
