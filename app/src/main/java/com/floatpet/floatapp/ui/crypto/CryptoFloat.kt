package com.floatpet.floatapp.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CryptoFloat() {
    Box(
        modifier = Modifier
            .width(8.dp)
            .height(50.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x80FFFFFF),
                        Color(0x40FFFFFF),
                        Color(0x80FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(4.dp)
            )
    )
}