package com.lvark.teamcaptain.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lvark.teamcaptain.model.entity.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY dateTime ASC")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE dateTime >= :currentTime ORDER BY dateTime ASC LIMIT 1")
    fun getNextMatch(currentTime: Long = System.currentTimeMillis()): Flow<Match?>

    @Query("SELECT * FROM matches WHERE dateTime >= :currentTime ORDER BY dateTime ASC")
    fun getUpcomingMatches(currentTime: Long = System.currentTimeMillis()): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE dateTime < :currentTime ORDER BY dateTime DESC")
    fun getPastMatches(currentTime: Long = System.currentTimeMillis()): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Long): Match?

    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun observeMatchById(matchId: Long): Flow<Match?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match): Long

    @Update
    suspend fun updateMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatchById(matchId: Long)
}
