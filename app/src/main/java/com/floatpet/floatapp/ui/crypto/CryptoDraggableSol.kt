package com.floatpet.floatapp.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun CryptoDraggableSol(close: () -> Unit) {
    var solPrice by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showCloseButton by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

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
            .width(if (showCloseButton) 200.dp else 160.dp)
            .height(50.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCloseButton = true
                    },
                    onDragEnd = {
                        // Keep close button visible for a moment after drag ends
                        scope.launch {
                            delay(2000)
                            showCloseButton = false
                        }
                    }
                ) { _, _ ->
                    // Handle drag gestures - the floating system will handle the actual dragging
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCloseButton = true
                        scope.launch {
                            delay(3000)
                            showCloseButton = false
                        }
                    }
                )
            }
    ) {
        // Glass background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x35FFFFFF),
                            Color(0x15FFFFFF),
                            Color(0x35FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        )

        // Blur overlay for glass effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0x25FFFFFF),
                    RoundedCornerShape(16.dp)
                )
                .blur(0.8.dp)
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SOL",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Solana",
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    Text(
                        text = "...",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "$${solPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF00FF88)
                    )
                }

                // Close button appears on drag/long press
                androidx.compose.animation.AnimatedVisibility(
                    visible = showCloseButton,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
                ) {
                    IconButton(
                        onClick = { close() },
                        modifier = Modifier.size(24.dp)
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
        }
    }
}