package com.lvark.teamcaptain.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lvark.teamcaptain.model.entity.User
import com.lvark.teamcaptain.ui.screens.AddMatchScreen
import com.lvark.teamcaptain.ui.screens.AddPlayerScreen
import com.lvark.teamcaptain.ui.screens.AttendanceScreen
import com.lvark.teamcaptain.ui.screens.DashboardScreen
import com.lvark.teamcaptain.ui.screens.LoginScreen
import com.lvark.teamcaptain.ui.screens.MatchDetailScreen
import com.lvark.teamcaptain.ui.screens.MatchListScreen
import com.lvark.teamcaptain.ui.screens.PlayerDetailScreen
import com.lvark.teamcaptain.ui.screens.TeamListScreen

@Composable
@Suppress("FunctionName")
fun TeamCaptainNavHost(
    navController: NavHostController,
    currentUser: User?,
    modifier: Modifier = Modifier,
) {
    val startDestination =
        if (currentUser == null) {
            Screen.Login.route
        } else {
            Screen.Dashboard.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Screen.Login.route) {
            LoginScreen()
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTeam = { navController.navigate(Screen.Team.route) },
                onNavigateToMatches = { navController.navigate(Screen.Matches.route) },
                onNavigateToMatch = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
            )
        }

        // Team screens
        composable(Screen.Team.route) {
            TeamListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddPlayer = { navController.navigate(Screen.AddPlayer.route) },
                onNavigateToPlayerDetail = { playerId ->
                    navController.navigate(Screen.PlayerDetail.createRoute(playerId))
                },
            )
        }

        composable(Screen.AddPlayer.route) {
            AddPlayerScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.PlayerDetail.route,
            arguments = listOf(navArgument("playerId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getLong("playerId")
            PlayerDetailScreen(
                playerId = playerId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Match screens
        composable(Screen.Matches.route) {
            MatchListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddMatch = { navController.navigate(Screen.AddMatch.route) },
                onNavigateToMatchDetail = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
            )
        }

        composable(Screen.AddMatch.route) {
            AddMatchScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.MatchDetail.route,
            arguments = listOf(navArgument("matchId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getLong("matchId")
            MatchDetailScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAttendance = { mId ->
                    navController.navigate(Screen.Attendance.createRoute(mId))
                },
                onNavigateToLineup = { mId ->
                    navController.navigate(Screen.Lineup.createRoute(mId))
                },
            )
        }

        // Attendance screen
        composable(
            route = Screen.Attendance.route,
            arguments = listOf(navArgument("matchId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getLong("matchId") ?: 0L
            AttendanceScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Lineup screen - Phase 4 (not yet implemented)
        composable(
            route = Screen.Lineup.route,
            arguments = listOf(navArgument("matchId") { type = NavType.LongType }),
        ) {
            // Placeholder for Phase 4
        }
    }
}
