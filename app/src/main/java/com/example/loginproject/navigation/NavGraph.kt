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
import com.example.loginproject.ui.address.AddAddressScreen
import com.example.loginproject.ui.cartPage.CartScreen
import com.example.loginproject.ui.category.CategoriesScreen
import com.example.loginproject.ui.categoryProducts.CategoryProductsScreen
import com.example.loginproject.ui.checkout.CheckoutScreen
import com.example.loginproject.ui.favorites.FavoritesScreen
import com.example.loginproject.ui.home.HomeScreen
import com.example.loginproject.ui.login.LoginScreen
import com.example.loginproject.ui.password.ChangePasswordScreen
import com.example.loginproject.ui.product.ProductDetailScreen
import com.example.loginproject.ui.profile.ProfileScreen
import com.example.loginproject.ui.register.RegisterScreen
import com.example.loginproject.ui.splash.SplashScreen

/**
 * Route names centralized here to avoid typo-prone magic strings
 * scattered across screens.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CATEGORY = "category"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CATEGORY_PRODUCTS = "category_products/{categoryId}/{categoryName}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val FAVORITE = "favorite"
    const val PROFILE = "profile"
    const val ADD_ADDRESS = "add_address"
    const val CHANGE_PASSWORD = "change_password"

    fun productDetail(productId: Int) = "product_detail/$productId"
    fun categoryProducts(categoryId: Int, categoryName: String) =
        "category_products/$categoryId/$categoryName"
}

/** Clears the entire backstack and navigates to Login — used after logout / password change. */
private fun NavController.goToLoginAndClearStack() {
    navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavController = rememberNavController()
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Routes.SPLASH
    ) {

        // ---------- Auth ----------
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }

        // ---------- Main ----------
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(Routes.CATEGORY) {
            CategoriesScreen(navController = navController)
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            ProductDetailScreen(navController = navController)
        }
        composable(
            route = Routes.CATEGORY_PRODUCTS,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) {
            CategoryProductsScreen(navController = navController)
        }
        composable(Routes.CART) {
            CartScreen(
                navController = navController,
                onCheckout = { navController.navigate(Routes.CHECKOUT) }
            )
        }
        composable(Routes.CHECKOUT) {
            CheckoutScreen(navController = navController)
        }
        composable(Routes.FAVORITE) {
            FavoritesScreen(navController = navController)
        }

        // ---------- Profile ----------
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToLogin = { navController.goToLoginAndClearStack() },
                onNavigateToAddresses = { navController.navigate(Routes.ADD_ADDRESS) },
                onNavigateToChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                navController = navController
            )
        }
        composable(Routes.ADD_ADDRESS) {
            AddAddressScreen(
                onBack = { navController.popBackStack() },
                onAddressAdded = { navController.popBackStack() }
            )
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.goToLoginAndClearStack() }
            )
        }
    }
}