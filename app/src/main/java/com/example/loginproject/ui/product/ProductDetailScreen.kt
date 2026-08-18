package com.example.loginproject.ui.product

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
private val BrandPurple = Color(0xFF5B4FE9)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF8A8FA3)
private val ScreenBg = Color(0xFFFFFFFF)
private val CardBg = Color(0xFFF7F8FC)

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    navController: NavController,
) {
    val product by viewModel.product.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isSyncingFavorite by viewModel.isSyncingFavorite.collectAsStateWithLifecycle()
    val isInCart by viewModel.isInCart.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val isSyncingCart by viewModel.isSyncingCart.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            if (product != null) {
                ProductBottomBar(
                    isFavorite = isFavorite,
                    isSyncingFavorite = isSyncingFavorite,
                    isInCart = isInCart,
                    quantity = quantity,
                    isSyncingCart = isSyncingCart,
                    onFavoriteClick = viewModel::toggleFavorite,
                    onAddToCartClick = viewModel::onAddToCartClick,
                    onIncreaseQuantity = viewModel::onIncreaseQuantity,
                    onDecreaseQuantity = viewModel::onDecreaseQuantity
                )
            }
        }
    ) { innerPadding ->
        when {
            isLoading && product == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPurple)
                }
            }

            errorMessage != null && product == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
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

            product != null -> {
                ProductContent(
                    product = product!!,
                    isFavorite = isFavorite,
                    isSyncingFavorite = isSyncingFavorite,
                    onBackClick = { navController.popBackStack() },
                    onFavoriteClick = viewModel::toggleFavorite,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProductContent(
    product: ProductDetailUi,
    isFavorite: Boolean,
    isSyncingFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            ProductImagePager(
                imageUrls = product.imageUrls,
                onBackClick = onBackClick,
                isFavorite = isFavorite,
                isSyncingFavorite = isSyncingFavorite,
                onFavoriteClick = onFavoriteClick
            )
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        color = TextDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = product.price,
                        color = BrandPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (product.isAvailable) "Ready to ship" else "Out of stock",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "About this piece", color = TextDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProductImagePager(
    imageUrls: List<String>,
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    isSyncingFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size.coerceAtLeast(1) })

    Box(modifier = Modifier.fillMaxWidth()) {
        if (imageUrls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(CardBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            HorizontalPager(state = pagerState) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(CardBg),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextDark)
        }

        IconButton(
            onClick = onFavoriteClick,
            enabled = !isSyncingFavorite,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopEnd)
        ) {
            if (isSyncingFavorite) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BrandPurple, strokeWidth = 2.dp)
            } else {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "علاقه‌مندی",
                    tint = if (isFavorite) Color(0xFFE0483F) else TextDark
                )
            }
        }

        if (imageUrls.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(imageUrls.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == index) BrandPurple else Color.White.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductBottomBar(
    isFavorite: Boolean,
    isSyncingFavorite: Boolean,
    isInCart: Boolean,
    quantity: Int,
    isSyncingCart: Boolean,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onFavoriteClick,
            enabled = !isSyncingFavorite,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
            modifier = Modifier.size(52.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (isSyncingFavorite) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BrandPurple, strokeWidth = 2.dp)
            } else {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "علاقه‌مندی",
                    tint = if (isFavorite) Color(0xFFE0483F) else TextDark
                )
            }
        }

        AddToCartArea(
            isInCart = isInCart,
            quantity = quantity,
            isSyncing = isSyncingCart,
            onAddClick = onAddToCartClick,
            onIncrease = onIncreaseQuantity,
            onDecrease = onDecreaseQuantity,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AddToCartArea(
    isInCart: Boolean,
    quantity: Int,
    isSyncing: Boolean,
    onAddClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isInCart,
        transitionSpec = {
            (slideInVertically { height -> height } + androidx.compose.animation.fadeIn())
                .togetherWith(slideOutVertically { height -> -height } + androidx.compose.animation.fadeOut())
        },
        label = "addToCartArea",
        modifier = modifier
    ) { inCart ->
        if (inCart) {
            QuantityStepperBar(
                quantity = quantity,
                isSyncing = isSyncing,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        } else {
            AddToCartButton(
                isLoading = isSyncing,
                onClick = onAddClick
            )
        }
    }
}

@Composable
private fun AddToCartButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandPurple,
            disabledContainerColor = BrandPurple.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add to cart", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun QuantityStepperBar(
    quantity: Int,
    isSyncing: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSyncing) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "stepperScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEDEAFB)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDecrease, enabled = !isSyncing, modifier = Modifier.size(52.dp)) {
            Icon(imageVector = Icons.Outlined.Remove, contentDescription = "کم کردن", tint = BrandPurple)
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            if (isSyncing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BrandPurple, strokeWidth = 2.dp)
            } else {
                AnimatedContent(targetState = quantity, label = "quantityNumber") { q ->
                    Text(text = q.toString(), color = BrandPurple, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        IconButton(onClick = onIncrease, enabled = !isSyncing, modifier = Modifier.size(52.dp)) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "زیاد کردن", tint = BrandPurple)
        }
    }
}