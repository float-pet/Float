package com.floatpet.floatapp.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun CryptoMiniFloat() {
    var solPrice by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://fe-api.jup.ag/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JupiterApi::class.java)
    }

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                try {
                    val response = api.getSolanaPrice()
                    solPrice = response.prices["So11111111111111111111111111111111111111112"]
                    isLoading = false
                } catch (e: Exception) {
                    isLoading = false
                }
                delay(30000) // Update every 30 seconds
            }
        }
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(45.dp)
    ) {
        // Glass background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x30FFFFFF),
                            Color(0x10FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
        )

        // Blur overlay for glass effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0x20FFFFFF),
                    RoundedCornerShape(12.dp)
                )
                .blur(0.5.dp)
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isLoading) {
                    Text(
                        text = "...",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "$${solPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF00FF88)
                    )
                }

                // Close text
                Text(
                    text = "✕",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}