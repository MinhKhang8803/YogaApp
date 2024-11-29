package com.example.yoganativeapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "class_list") {
        composable("class_list") {
            ClassListScreen(navController)
        }
        composable("add_class") {
            AddClassScreen(navController)
        }
        composable(
            "edit_class/{classId}",
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: ""
            EditClassScreen(navController, classId)
        }
    }
}
