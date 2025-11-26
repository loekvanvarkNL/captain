package com.lvark.teamcaptain.data.repository

import com.lvark.teamcaptain.data.local.LineupAssignmentDao
import com.lvark.teamcaptain.model.entity.LineupAssignment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LineupRepository {
    fun getLineupForMatch(matchId: Long): Flow<List<LineupAssignment>>

    fun getLineupForBlock(
        matchId: Long,
        blockNumber: Int,
    ): Flow<List<LineupAssignment>>

    fun getStartingLineup(matchId: Long): Flow<List<LineupAssignment>>

    fun getAssignmentsForPlayer(playerId: Long): Flow<List<LineupAssignment>>

    suspend fun insertAssignment(assignment: LineupAssignment): Long

    suspend fun insertAssignments(assignments: List<LineupAssignment>)

    suspend fun updateAssignment(assignment: LineupAssignment)

    suspend fun deleteAllAssignmentsForMatch(matchId: Long)

    suspend fun deleteAssignmentsForBlock(
        matchId: Long,
        blockNumber: Int,
    )

    suspend fun saveLineupForBlock(
        matchId: Long,
        blockNumber: Int,
        assignments: List<LineupAssignment>,
    )
}

@Singleton
class LineupRepositoryImpl
    @Inject
    constructor(
        private val lineupAssignmentDao: LineupAssignmentDao,
    ) : LineupRepository {
        override fun getLineupForMatch(matchId: Long): Flow<List<LineupAssignment>> = lineupAssignmentDao.getLineupForMatch(matchId)

        override fun getLineupForBlock(
            matchId: Long,
            blockNumber: Int,
        ): Flow<List<LineupAssignment>> = lineupAssignmentDao.getLineupForBlock(matchId, blockNumber)

        override fun getStartingLineup(matchId: Long): Flow<List<LineupAssignment>> = lineupAssignmentDao.getStartingLineup(matchId)

        override fun getAssignmentsForPlayer(playerId: Long): Flow<List<LineupAssignment>> =
            lineupAssignmentDao.getAssignmentsForPlayer(playerId)

        override suspend fun insertAssignment(assignment: LineupAssignment): Long {
            val updatedAssignment =
                assignment.copy(
                    updatedAt = System.currentTimeMillis(),
                    createdAt = if (assignment.id == 0L) System.currentTimeMillis() else assignment.createdAt,
                )
            return lineupAssignmentDao.insertAssignment(updatedAssignment)
        }

        override suspend fun insertAssignments(assignments: List<LineupAssignment>) {
            val updatedAssignments =
                assignments.map {
                    it.copy(
                        updatedAt = System.currentTimeMillis(),
                        createdAt = if (it.id == 0L) System.currentTimeMillis() else it.createdAt,
                    )
                }
            lineupAssignmentDao.insertAssignments(updatedAssignments)
        }

        override suspend fun updateAssignment(assignment: LineupAssignment) {
            val updatedAssignment = assignment.copy(updatedAt = System.currentTimeMillis())
            lineupAssignmentDao.updateAssignment(updatedAssignment)
        }

        override suspend fun deleteAllAssignmentsForMatch(matchId: Long) {
            lineupAssignmentDao.deleteAllAssignmentsForMatch(matchId)
        }

        override suspend fun deleteAssignmentsForBlock(
            matchId: Long,
            blockNumber: Int,
        ) {
            lineupAssignmentDao.deleteAssignmentsForBlock(matchId, blockNumber)
        }

        override suspend fun saveLineupForBlock(
            matchId: Long,
            blockNumber: Int,
            assignments: List<LineupAssignment>,
        ) {
            // Delete existing assignments for this block
            deleteAssignmentsForBlock(matchId, blockNumber)
            // Insert new assignments
            insertAssignments(assignments)
        }
    }
