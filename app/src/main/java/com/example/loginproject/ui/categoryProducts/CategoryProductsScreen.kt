package com.example.loginproject.ui.categoryProducts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.loginproject.ui.home.AppBottomBar
import kotlinx.coroutines.delay

private val BrandPurple = Color(0xFF5B4FE9)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF8A8FA3)
private val ScreenBg = Color(0xFFFFFFFF)
private val CardBg = Color(0xFFF7F8FC)
private val FieldBorder = Color(0xFFE2E4EC)

@Composable
fun CategoryProductsScreen(
    viewModel: CategoryProductsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchVisible by viewModel.isSearchVisible.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(1) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            AppBottomBar(
                navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundIconButton(
                    icon = Icons.Filled.ArrowBack,
                    onClick = { navController.popBackStack() })

                Text(
                    text = viewModel.categoryName,
                    color = TextDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoundIconButton(
                        icon = Icons.Filled.Search,
                        onClick = viewModel::toggleSearchVisibility
                    )
                    RoundIconButton(
                        icon = Icons.Filled.FilterList,
                        onClick = { })
                }
            }

            AnimatedVisibility(
                visible = isSearchVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "Search in ${viewModel.categoryName}",
                                color = TextGray
                            )
                        },
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Curated selection",
                color = BrandPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.categoryName,
                        color = TextDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${products.size} products", color = TextGray, fontSize = 13.sp)
                }

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CardBg)
                            .clickable { sortMenuExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapVert,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sort",
                            color = TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    viewModel.onSortOptionSelected(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading && products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPurple)
                    }
                }

                errorMessage != null && products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = errorMessage ?: "", color = TextGray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::retry,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                            ) {
                                Text("تلاش مجدد")
                            }
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            ProductGridCard(
                                product = product,
                                onCardClick = { navController.navigate("product_detail/${product.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CardBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextDark,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ProductGridCard(
    product: CategoryProductUi,
    onCardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onCardClick() }
            .padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
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
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(text = product.name, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = product.price, color = BrandPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    var justAdded by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (justAdded) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "addButtonScale"
    )

    LaunchedEffect(justAdded) {
        if (justAdded) {
            delay(900)
            justAdded = false
        }
    }

    Button(
        onClick = {
            justAdded = true
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (justAdded) Color(0xFF3FA65C) else BrandPurple
        ),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        AnimatedContent(targetState = justAdded, label = "addButtonContent") { added ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (added) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (added) "Added" else "Add",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
