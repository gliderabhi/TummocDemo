package com.example.tummocduplicate.view

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: ListOfRoutesViewModel,
    context: Context
) {
    NavHost(navController = navController, startDestination = Screens.Home.route) {
        composable(
            route = Screens.Home.route
        ) {
            MainView(viewModel, context)
        }

        composable(
            route = Screens.Maps.route
        ) {
            MapsScreen(viewModel, navController)
        }
    }
}