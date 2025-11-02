package com.lvark.teamcaptain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lvark.teamcaptain.model.entity.LineupAssignment
import kotlinx.coroutines.flow.Flow

@Dao
interface LineupAssignmentDao {
    @Query("SELECT * FROM lineup_assignments WHERE matchId = :matchId ORDER BY blockNumber ASC, isStarting DESC")
    fun getLineupForMatch(matchId: Long): Flow<List<LineupAssignment>>

    @Query("SELECT * FROM lineup_assignments WHERE matchId = :matchId AND blockNumber = :blockNumber")
    fun getLineupForBlock(
        matchId: Long,
        blockNumber: Int,
    ): Flow<List<LineupAssignment>>

    @Query("SELECT * FROM lineup_assignments WHERE matchId = :matchId AND isStarting = 1 AND blockNumber = 1")
    fun getStartingLineup(matchId: Long): Flow<List<LineupAssignment>>

    @Query("SELECT * FROM lineup_assignments WHERE playerId = :playerId")
    fun getAssignmentsForPlayer(playerId: Long): Flow<List<LineupAssignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: LineupAssignment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<LineupAssignment>)

    @Update
    suspend fun updateAssignment(assignment: LineupAssignment)

    @Query("DELETE FROM lineup_assignments WHERE matchId = :matchId")
    suspend fun deleteAllAssignmentsForMatch(matchId: Long)

    @Query("DELETE FROM lineup_assignments WHERE matchId = :matchId AND blockNumber = :blockNumber")
    suspend fun deleteAssignmentsForBlock(
        matchId: Long,
        blockNumber: Int,
    )
}
