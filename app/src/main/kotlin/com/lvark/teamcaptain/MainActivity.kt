package com.lvark.teamcaptain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.lvark.teamcaptain.ui.navigation.Screen
import com.lvark.teamcaptain.ui.navigation.TeamCaptainNavHost
import com.lvark.teamcaptain.ui.theme.TeamCaptainTheme
import com.lvark.teamcaptain.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeamCaptainTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

                    // Navigate to dashboard when user logs in
                    LaunchedEffect(currentUser) {
                        if (currentUser != null && navController.currentDestination?.route == Screen.Login.route) {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else if (currentUser == null && navController.currentDestination?.route != Screen.Login.route) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    TeamCaptainNavHost(
                        navController = navController,
                        currentUser = currentUser,
                    )
                }
            }
        }
    }
}
