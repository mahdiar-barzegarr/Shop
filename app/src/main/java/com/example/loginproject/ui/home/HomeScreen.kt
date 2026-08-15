package com.example.loginproject.ui.home

import android.net.http.SslCertificate.saveState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import coil.compose.AsyncImage

private val BrandPurple = Color(0xFF5B4FE9)
private val BrandPurpleDark = Color(0xFF4A3FD1)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF8A8FA3)
private val ScreenBg = Color(0xFFFFFFFF)
private val CardBg = Color(0xFFF7F8FC)
private val FieldBorder = Color(0xFFE2E4EC)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val greeting by viewModel.greeting.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val popularProducts by viewModel.popularProducts.collectAsStateWithLifecycle()
    val newArrivals by viewModel.newArrivals.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching = searchQuery.isNotBlank()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            AppBottomBar(
                navController
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp
            )
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = if (userName != null) "$greeting, $userName" else greeting,
                            color = BrandPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Find your everyday",
                            color = TextDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )

                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = {
                        Text("Search bags, shoes, tech...", color = TextGray, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = if (searchQuery.isNotBlank()) BrandPurple else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = searchQuery.isNotBlank(),
                            enter = fadeIn(tween(150)),
                            exit = fadeOut(tween(150))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(TextGray.copy(alpha = 0.15f))
                                    .clickable { viewModel.onSearchQueryChanged("") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = FieldBorder,
                        focusedBorderColor = BrandPurple,
                        unfocusedContainerColor = CardBg,
                        focusedContainerColor = Color.White,
                        cursorColor = BrandPurple
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (searchQuery.isNotBlank()) 6.dp else 0.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = BrandPurple.copy(alpha = 0.3f)
                        )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (isSearching) {
                if (searchResults.isEmpty()) {
                    item {
                        Text(
                            text = "محصولی با این نام پیدا نشد",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(searchResults, key = { "search_${it.id}" }) { product ->
                        SearchResultRow(
                            product = product,
                            onClick = { navController.navigate("product_detail/${product.id}") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } else {
                item {
                    PromoBannerCard(banner = banner, onExploreClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    val scrollState = rememberScrollState()
                    SectionHeader(title = "Shop by category", onSeeAllClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        categories.forEach { category ->
                            CategoryChip(
                                category = category,
                                onClick = {
                                    val encodedName = java.net.URLEncoder.encode(category.label, "UTF-8")
                                    navController.navigate("category_products/${category.id}/$encodedName")
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    SectionHeader(title = "Popular now", onSeeAllClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(popularProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    SectionHeader(title = "New arrivals", onSeeAllClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(newArrivals, key = { it.id }) { item ->
                    NewArrivalRow(
                        item = item,
                        onClick = { navController.navigate("product_detail/${item.id}") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextDark, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "See all",
            color = BrandPurple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
private fun PromoBannerCard(banner: PromoBanner, onExploreClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = listOf(BrandPurple, BrandPurpleDark)))
            .padding(20.dp)
    ) {
        Column {
            Text(text = banner.title, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = banner.subtitle,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onExploreClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = BrandPurple
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(text = banner.buttonText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CategoryChip(category: CategoryItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.label,
                tint = BrandPurple,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = category.label, color = TextDark, fontSize = 12.sp)
    }
}

@Composable
private fun ProductCard(product: ProductItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg),
            contentAlignment = Alignment.Center
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = product.name,
                    tint = TextGray,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.name,
            color = TextDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = product.price,
            color = BrandPurple,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun NewArrivalRow(item: NewArrivalItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF4DCE4)),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = BrandPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = item.description, color = TextGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.price,
                color = BrandPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(18.dp)
        )
    }
}


data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

private val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "home"),
    BottomNavItem("category", Icons.Default.Category, "category"),
    BottomNavItem("cart", Icons.Default.ShoppingCart, "cart"),
    BottomNavItem("profile", Icons.Default.Person, "profile")
)

@Composable
fun AppBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedIndex = bottomNavItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Surface(
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
        ) {
            val itemWidth = maxWidth / bottomNavItems.size

            // ایندیکیتور متحرک پشت تب فعال
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(
                    dampingRatio = Dp.VisibilityThreshold.let { 0.7f },
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "indicatorOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    BottomNavItemContent(
                        item = item,
                        selected = selected,
                        modifier = Modifier
                            .width(itemWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemContent(
    item: BottomNavItem,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconScale"
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
        animationSpec = tween(250),
        label = "iconColor"
    )
    val iconOffsetY by animateDpAsState(
        targetValue = if (selected) (-2).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconOffsetY"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .offset(y = iconOffsetY)
                .scale(iconScale)
        )

        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(200)) + expandVertically(tween(200)),
            exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
        ) {
            Text(
                text = item.label,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SearchResultRow(product: ProductItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF4DCE4)),
            contentAlignment = Alignment.Center
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    tint = BrandPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.price, color = BrandPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(18.dp)
        )
    }
}




