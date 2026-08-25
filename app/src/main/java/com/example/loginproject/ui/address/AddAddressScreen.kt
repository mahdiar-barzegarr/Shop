package com.example.loginproject.ui.address


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    onBack: () -> Unit,
    onAddressAdded: () -> Unit,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        if (uiState is AddAddressUiState.Success) {
            delay(900)
            onAddressAdded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add address") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9DFFB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.height(24.dp))

                AddressField("Title", viewModel.title, viewModel::onTitleChange, Icons.Default.Label)
                AddressField("Province", viewModel.province, viewModel::onProvinceChange, Icons.Default.Map)
                AddressField("City", viewModel.city, viewModel::onCityChange, Icons.Default.LocationCity)
                AddressField("Full address", viewModel.address, viewModel::onAddressChange, Icons.Default.Home, singleLine = false)
                AddressField("Postal code", viewModel.postCode, viewModel::onPostCodeChange, Icons.Default.Numbers, keyboardType = KeyboardType.Number)

                Spacer(Modifier.height(16.dp))

                if (uiState is AddAddressUiState.Error) {
                    Text(
                        (uiState as AddAddressUiState.Error).message,
                        color = Color(0xFFD32F2F),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = { keyboard?.hide(); viewModel.submit() },
                    enabled = viewModel.isFormValid && uiState !is AddAddressUiState.Loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    AnimatedContent(targetState = uiState, label = "submitBtn") { state ->
                        if (state is AddAddressUiState.Loading)
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else
                            Text("Save address", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            AnimatedVisibility(
                visible = uiState is AddAddressUiState.Success,
                enter = fadeIn() + scaleIn(initialScale = 0.7f),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFDDF3E4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun AddressField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
    )
}