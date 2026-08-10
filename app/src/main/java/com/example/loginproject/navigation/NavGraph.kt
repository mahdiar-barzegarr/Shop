package com.example.loginproject.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginproject.ui.category.CategoriesScreen

import com.example.loginproject.ui.home.HomeScreen
import com.example.loginproject.ui.login.LoginScreen
import com.example.loginproject.ui.register.RegisterScreen
import com.example.loginproject.ui.splash.SplashScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavController = rememberNavController()
) {


    NavHost(navController = navController as NavHostController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen({},{},{}, navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("category") { CategoriesScreen(navController = navController) }
    }
}
