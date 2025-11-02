package com.lvark.teamcaptain.di

import com.lvark.teamcaptain.data.repository.AttendanceRepository
import com.lvark.teamcaptain.data.repository.AttendanceRepositoryImpl
import com.lvark.teamcaptain.data.repository.LineupRepository
import com.lvark.teamcaptain.data.repository.LineupRepositoryImpl
import com.lvark.teamcaptain.data.repository.MatchRepository
import com.lvark.teamcaptain.data.repository.MatchRepositoryImpl
import com.lvark.teamcaptain.data.repository.PlayerRepository
import com.lvark.teamcaptain.data.repository.PlayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlayerRepository(playerRepositoryImpl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(matchRepositoryImpl: MatchRepositoryImpl): MatchRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(attendanceRepositoryImpl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindLineupRepository(lineupRepositoryImpl: LineupRepositoryImpl): LineupRepository
}
