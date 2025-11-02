package com.lvark.teamcaptain.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object Team : Screen("team")

    data object PlayerDetail : Screen("team/player/{playerId}") {
        fun createRoute(playerId: Long) = "team/player/$playerId"
    }

    data object AddPlayer : Screen("team/add")

    data object Matches : Screen("matches")

    data object MatchDetail : Screen("matches/{matchId}") {
        fun createRoute(matchId: Long) = "matches/$matchId"
    }

    data object AddMatch : Screen("matches/add")

    data object Attendance : Screen("attendance/{matchId}") {
        fun createRoute(matchId: Long) = "attendance/$matchId"
    }

    data object Lineup : Screen("lineup/{matchId}") {
        fun createRoute(matchId: Long) = "lineup/$matchId"
    }
}
