package com.github.misham72.communalpayments.screen.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.screen.navigation.models.InitialScreen


@Composable
fun ServiceTab(
    service: InitialScreen, isSelected: Boolean, onClick: () -> Unit
) {

    val backgroundColor = if (isSelected) Color(0xFF0D44FF) else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = service.icon, fontSize = 18.sp, color = textColor
        )
    }
}
