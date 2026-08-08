package com.example.loginproject.ui.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.loginproject.data.remote.response.CategoryDto
import com.example.loginproject.ui.home.HomeBottomBar


private val BrandPurple = Color(0xFF5B4FE9)
private val BrandPurpleDark = Color(0xFF4A3FD1)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF8A8FA3)
private val ScreenBg = Color(0xFFFFFFFF)
private val CardBg = Color(0xFFF7F8FC)
private val FieldBorder = Color(0xFFE2E4EC)
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {

    val categories by viewModel.categories.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }

    val filteredCategories = categories.filter {
        it.name.contains(
            searchText,
            ignoreCase = true
        )
    }
    var selectedTab by remember { mutableIntStateOf(1) }


    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            HomeBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab

                    when (tab) {

                        0 -> {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }

                        1 -> {
                            navController.navigate("category") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }

                    }

                }

            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ------------------------------------------------
            // Header
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = TextColor,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = "Browse your way",
                        fontSize = 13.sp,
                        color = SecondaryTextColor
                    )

                    Text(
                        text = "Categories",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ------------------------------------------------
            // Search
            // ------------------------------------------------

            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()


            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search bags, shoes, tech...", color = TextGray) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = TextGray
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = FieldBorder,
                    focusedBorderColor = BrandPurple,
                    unfocusedContainerColor = CardBg,
                    focusedContainerColor = CardBg
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // ------------------------------------------------
            // Content
            // ------------------------------------------------

            when {

                loading -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

                error != null -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = error ?: "Something went wrong",
                            color = Color.Red
                        )
                    }
                }

                else -> {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            bottom = 20.dp
                        )
                    ) {

                        items(
                            items = filteredCategories,
                            key = {
                                it.id
                            }
                        ) { category ->

                            CategoryCard(
                                category = category,
                                onClick = {

                                    // اینجا بعداً می‌توانی صفحه
                                    // محصولات آن category را باز کنی

                                    // مثال:
                                    // navController.navigate(
                                    //     "products/${category.id}"
                                    // )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// Category Card
// ============================================================

@Composable
private fun CategoryCard(
    category: CategoryDto,
    onClick: () -> Unit
) {

    val categoryStyle = getCategoryStyle(category.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(137.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = categoryStyle.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 19.dp,
                    vertical = 17.dp
                )
        ) {

            // Icon

            Icon(
                imageVector = categoryStyle.icon,
                contentDescription = category.name,
                modifier = Modifier.size(28.dp),
                tint = categoryStyle.iconColor
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // Category name

            Text(
                text = category.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // چون CategoryDto فعلاً تعداد محصولات را ندارد
            // چیزی به صورت fake نمایش نمی‌دهیم.
        }
    }
}


// ============================================================
// Category Style
// ============================================================

private data class CategoryStyle(
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

private fun getCategoryStyle(
    categoryName: String
): CategoryStyle {

    return when {

        categoryName.contains(
            "bag",
            ignoreCase = true
        ) ||
                categoryName.contains("کیف") -> {

            CategoryStyle(
                icon = Icons.Outlined.ShoppingBag,
                backgroundColor = BagColor,
                iconColor = DarkIconColor
            )
        }

        categoryName.contains(
            "shoe",
            ignoreCase = true
        ) ||
                categoryName.contains("کفش") -> {

            CategoryStyle(
                icon = Icons.Outlined.DirectionsWalk,
                backgroundColor = ShoesColor,
                iconColor = DarkIconColor
            )
        }

        categoryName.contains(
            "beauty",
            ignoreCase = true
        ) ||
                categoryName.contains("آرایشی") ||
                categoryName.contains("زیبایی") -> {

            CategoryStyle(
                icon = Icons.Outlined.AutoAwesome,
                backgroundColor = BeautyColor,
                iconColor = DarkIconColor
            )
        }

        categoryName.contains(
            "electronic",
            ignoreCase = true
        ) ||
                categoryName.contains("الکترون") -> {

            CategoryStyle(
                icon = Icons.Outlined.Headphones,
                backgroundColor = ElectronicsColor,
                iconColor = DarkIconColor
            )
        }

        categoryName.contains(
            "cloth",
            ignoreCase = true
        ) ||
                categoryName.contains("لباس") ||
                categoryName.contains("پوشاک") -> {

            CategoryStyle(
                icon = Icons.Outlined.Checkroom,
                backgroundColor = ClothingColor,
                iconColor = DarkIconColor
            )
        }

        categoryName.contains(
            "home",
            ignoreCase = true
        ) ||
                categoryName.contains("خانه") -> {

            CategoryStyle(
                icon = Icons.Outlined.Home,
                backgroundColor = HomeColor,
                iconColor = DarkIconColor
            )
        }

        else -> {

            CategoryStyle(
                icon = Icons.Outlined.Category,
                backgroundColor = DefaultCategoryColor,
                iconColor = DarkIconColor
            )
        }
    }
}


// ============================================================
// Bottom Navigation
// ============================================================

@Composable
private fun CategoryBottomBar(
    navController: NavController
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavigationItem(
                icon = Icons.Outlined.Home,
                title = "Home",
                selected = false,
                onClick = {
                    navController.navigate("home")
                }
            )

            BottomNavigationItem(
                icon = Icons.Outlined.Category,
                title = "Categories",
                selected = true,
                onClick = {}
            )

            BottomNavigationItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Favorites",
                selected = false,
                onClick = {
                    navController.navigate("favorites")
                }
            )

            BottomNavigationItem(
                icon = Icons.Outlined.ShoppingCart,
                title = "Cart",
                selected = false,
                onClick = {
                    navController.navigate("cart")
                }
            )

            BottomNavigationItem(
                icon = Icons.Outlined.Person,
                title = "Profile",
                selected = false,
                onClick = {
                    navController.navigate("profile")
                }
            )
        }
    }
}

//@Composable
//private fun HomeBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
//    data class Tab(val icon: ImageVector, val label: String)
//
//    val tabs = listOf(
//        Tab(Icons.Outlined.Home, "Home"),
//        Tab(Icons.Outlined.Category, "Categories"),
//        Tab(Icons.Outlined.Favorite, "Favorites"),
//        Tab(Icons.Outlined.ShoppingCart, "Cart"),
//        Tab(Icons.Outlined.Person, "Profile")
//    )
//
//    NavigationBar(containerColor = Color.White) {
//        tabs.forEachIndexed { index, tab ->
//            NavigationBarItem(
//                selected = selectedTab == index,
//                onClick = { onTabSelected(index) },
//                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
//                label = { Text(text = tab.label, fontSize = 11.sp) },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = BrandPurple,
//                    selectedTextColor = BrandPurple,
//                    unselectedIconColor = TextGray,
//                    unselectedTextColor = TextGray,
//                    indicatorColor = CardBg
//                )
//            )
//        }
//    }
//}


@Composable
private fun BottomNavigationItem(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val color =
        if (selected) {
            SelectedNavigationColor
        } else {
            UnselectedNavigationColor
        }

    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = color
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = title,
            fontSize = 11.sp,
            color = color,
            fontWeight = if (selected) {
                FontWeight.Medium
            } else {
                FontWeight.Normal
            }
        )
    }
}


// ============================================================
// Colors
// ============================================================

private val BackgroundColor = Color(0xFFFFFBFF)

private val SearchBackgroundColor = Color(0xFFF6F0F8)

private val TextColor = Color(0xFF17131A)

private val SecondaryTextColor = Color(0xFF655D69)

private val DarkIconColor = Color(0xFF29232D)

private val BagColor = Color(0xFFE5D8F8)

private val ShoesColor = Color(0xFFFFD5E0)

private val BeautyColor = Color(0xFFE2F2E4)

private val ElectronicsColor = Color(0xFFF5EEF8)

private val ClothingColor = Color(0xFFFFD3E0)

private val HomeColor = Color(0xFFE4D9F7)

private val DefaultCategoryColor = Color(0xFFF0EAF4)

private val SelectedNavigationColor = Color(0xFF6C4BC4)

private val UnselectedNavigationColor = Color(0xFF6D6472)