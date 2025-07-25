package com.floatpet.floatapp.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun CryptoExpandedFloat(close: () -> Unit) {
    var tokens by remember {
        mutableStateOf(
            listOf(
                CryptoToken("SOL", "Solana", "So11111111111111111111111111111111111111112"),
                CryptoToken("FARTCOIN", "Fartcoin", "9BB6NFEcjBCtnNLFko2FqVQBq8HHM13kCyYcdQbgpump"),
                CryptoToken("BTC", "BTC", "3NZ9JMVBmGAqocybic2c7LQCJScmgsAZ6vQqTDzcqmJh"),
                CryptoToken("BONK", "bonk", "DezXAZ8z7PnrnRJjz3wXBoRgixCa6xjnB7YaB1pPB263"),
                CryptoToken("RAY", "Raydium", "4k3Dyjzvzp8eMZWUXbBCjEvwSkkk59S5iCNLY3QrkX6R")
            )
        )
    }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://fe-api.jup.ag/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JupiterExtendedApi::class.java)
    }

    fun refreshPrices() {
        scope.launch {
            isLoading = true
            try {
                val addresses = tokens.joinToString(",") { it.address }
                val response = api.getPrices(addresses)

                tokens = tokens.map { token ->
                    token.copy(price = response.prices[token.address])
                }
            } catch (e: Exception) {
                tokens = tokens.map { it.copy(price = null) }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshPrices()
        // Auto refresh every minute
        scope.launch {
            while (true) {
                delay(60000)
                refreshPrices()
            }
        }
    }

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(380.dp)
    ) {
        // Glass background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x40000000),
                            Color(0x60000000),
                            Color(0x40000000)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
        )

        // Blur overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0x30FFFFFF),
                    RoundedCornerShape(20.dp)
                )
                .blur(1.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💰 Crypto Prices",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Token",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint = if (isEditing) Color(0xFF00FF88) else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { refreshPrices() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { close() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading) {
//                    (text = "loading", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF00FF88))
                } else {
                    items(tokens) { token ->
                        EditableCryptoPriceItem(
                            token = token,
                            isEditing = isEditing,
                            onDelete = {
                                tokens = tokens.filter { it.address != token.address }
                            }
                        )
                    }
                }
            }
        }

        // Add Token Dialog
        if (showAddDialog) {
            AddTokenDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newToken ->
                    tokens = tokens + newToken
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun EditableCryptoPriceItem(
    token: CryptoToken,
    isEditing: Boolean,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x20FFFFFF),
                        Color(0x10FFFFFF),
                        Color(0x20FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = token.symbol,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = token.name,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = token.price?.let { "$%.${if (it < 1) "4" else "2"}f".format(it) } ?: "N/A",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (token.price != null) Color(0xFF00FF88) else Color.White.copy(alpha = 0.5f)
                )

                if (isEditing) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTokenDialog(
    onDismiss: () -> Unit,
    onAdd: (CryptoToken) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xDD000000),
        title = {
            Text(
                text = "Add New Token",
                color = Color.White
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it.uppercase() },
                    label = { Text("Symbol", color = Color.White.copy(0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF88),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Color.White.copy(0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF88),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Contract Address", color = Color.White.copy(0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF88),
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isNotBlank() && name.isNotBlank() && address.isNotBlank()) {
                        onAdd(CryptoToken(symbol, name, address))
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88)
                )
            ) {
                Text("Add", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}