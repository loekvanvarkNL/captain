package com.lvark.teamcaptain.di

import android.content.Context
import androidx.room.Room
import com.lvark.teamcaptain.data.local.AttendanceDao
import com.lvark.teamcaptain.data.local.LineupAssignmentDao
import com.lvark.teamcaptain.data.local.MatchDao
import com.lvark.teamcaptain.data.local.PlayerDao
import com.lvark.teamcaptain.data.local.TeamCaptainDatabase
import com.lvark.teamcaptain.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTeamCaptainDatabase(
        @ApplicationContext context: Context,
    ): TeamCaptainDatabase {
        return Room.databaseBuilder(
            context,
            TeamCaptainDatabase::class.java,
            TeamCaptainDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePlayerDao(database: TeamCaptainDatabase): PlayerDao {
        return database.playerDao()
    }

    @Provides
    fun provideMatchDao(database: TeamCaptainDatabase): MatchDao {
        return database.matchDao()
    }

    @Provides
    fun provideAttendanceDao(database: TeamCaptainDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    fun provideLineupAssignmentDao(database: TeamCaptainDatabase): LineupAssignmentDao {
        return database.lineupAssignmentDao()
    }

    @Provides
    fun provideUserDao(database: TeamCaptainDatabase): UserDao {
        return database.userDao()
    }
}
