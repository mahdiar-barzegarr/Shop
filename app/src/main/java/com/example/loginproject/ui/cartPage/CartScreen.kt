package com.example.loginproject.ui.cartPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.loginproject.ui.home.AppBottomBar
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel(),
    onCheckout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { AppBottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFF3F1FA))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Almost there", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Text("Your cart", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (state.isLoading) {
                Box(Modifier
                    .fillMaxWidth()
                    .padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.cartItemId }) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { viewModel.increaseQuantity(item) },
                            onDecrease = { viewModel.decreaseQuantity(item) },
                            onDelete = { viewModel.deleteItem(item.cartItemId) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                CartSummaryCard(subtotal = state.subtotal, total = state.total)
                Spacer(Modifier.height(16.dp))

                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.commitPendingChanges()
                            if (success) onCheckout()
                        }
                    },
                    enabled = !state.isSyncing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (state.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Checkout", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemUi,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F1FA)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "حذف",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(item.colorName, color = Color.Gray, fontSize = 13.sp)

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatPrice(item.price),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    QuantityStepper(
                        quantity = item.quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F1FA))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDecrease, modifier = Modifier.size(22.dp)) {
            Text("–", fontWeight = FontWeight.Bold)
        }
        Text(
            "$quantity",
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onIncrease, modifier = Modifier.size(22.dp)) {
            Text("+", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CartSummaryCard(subtotal: Double, total: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F1FA))
            .padding(16.dp)
    ) {
        SummaryRow("Subtotal", formatPrice(subtotal), MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Delivery", "Free", MaterialTheme.colorScheme.primary)
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        SummaryRow("Total", formatPrice(total), Color.Black, boldValue = true, boldLabel = true)
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelColor: Color,
    boldLabel: Boolean = false,
    boldValue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = if (boldLabel) Color.Black else labelColor,
            fontWeight = if (boldLabel) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            fontWeight = if (boldValue) FontWeight.Bold else FontWeight.Normal,
            color = if (boldValue) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}

private fun formatPrice(price: Double): String {
    val formatted = "%,.0f".format(price)
    return "$formatted تومان"
}