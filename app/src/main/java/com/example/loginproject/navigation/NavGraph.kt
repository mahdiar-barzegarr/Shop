package com.example.loginproject.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginproject.data.remote.response.ProductDetailResponse
import com.example.loginproject.ui.address.AddAddressScreen
import com.example.loginproject.ui.cartPage.CartScreen
import com.example.loginproject.ui.category.CategoriesScreen
import com.example.loginproject.ui.categoryProducts.CategoryProductsScreen
import com.example.loginproject.ui.checkout.CheckoutScreen
import com.example.loginproject.ui.favorites.FavoritesScreen

import com.example.loginproject.ui.home.HomeScreen
import com.example.loginproject.ui.login.LoginScreen
import com.example.loginproject.ui.product.ProductDetailScreen
import com.example.loginproject.ui.profile.ProfileScreen
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
        composable("register") { RegisterScreen({}, {}, {}, navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("category") { CategoriesScreen(navController = navController) }
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            ProductDetailScreen(navController = navController)
        }
        composable(
            route = "category_products/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) {
            CategoryProductsScreen(navController = navController)
        }
        composable("cart") {
            CartScreen(
                navController = navController,
                onCheckout = { navController.navigate("checkout") })
        }
        composable("checkout") {
            CheckoutScreen(navController = navController)
        }
        composable("favorite") {
            FavoritesScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAddresses = { navController.navigate("add_address") },
                navController = navController
            )
        }
        composable("add_address") {
            AddAddressScreen(
                onBack = { navController.popBackStack() },
                onAddressAdded = { navController.popBackStack() }
            )

        }
    }
}
