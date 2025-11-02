package com.lvark.teamcaptain.data.repository

import com.lvark.teamcaptain.data.local.MatchDao
import com.lvark.teamcaptain.model.entity.Match
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface MatchRepository {
    fun getAllMatches(): Flow<List<Match>>

    fun getNextMatch(): Flow<Match?>

    fun getUpcomingMatches(): Flow<List<Match>>

    fun getPastMatches(): Flow<List<Match>>

    fun getMatchById(matchId: Long): Flow<Match?>

    suspend fun insertMatch(match: Match): Long

    suspend fun updateMatch(match: Match)

    suspend fun deleteMatch(match: Match)
}

@Singleton
class MatchRepositoryImpl
    @Inject
    constructor(
        private val matchDao: MatchDao,
    ) : MatchRepository {
        override fun getAllMatches(): Flow<List<Match>> = matchDao.getAllMatches()

        override fun getNextMatch(): Flow<Match?> = matchDao.getNextMatch()

        override fun getUpcomingMatches(): Flow<List<Match>> = matchDao.getUpcomingMatches()

        override fun getPastMatches(): Flow<List<Match>> = matchDao.getPastMatches()

        override fun getMatchById(matchId: Long): Flow<Match?> = matchDao.observeMatchById(matchId)

        override suspend fun insertMatch(match: Match): Long {
            val updatedMatch =
                match.copy(
                    updatedAt = System.currentTimeMillis(),
                    createdAt = if (match.id == 0L) System.currentTimeMillis() else match.createdAt,
                )
            return matchDao.insertMatch(updatedMatch)
        }

        override suspend fun updateMatch(match: Match) {
            val updatedMatch = match.copy(updatedAt = System.currentTimeMillis())
            matchDao.updateMatch(updatedMatch)
        }

        override suspend fun deleteMatch(match: Match) {
            matchDao.deleteMatch(match)
        }
    }
