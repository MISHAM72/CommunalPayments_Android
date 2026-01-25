package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.presentation.screen.navigation.InitialScreen


@Composable
fun ServiceTab(
    service: InitialScreen, isSelected: Boolean, onClick: () -> Unit
) {
// ▼▼▼ ЗАМЕНИ ЭТИ 2 СТРОКИ ▼▼▼
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    // ▲▲▲ ЗАМЕНИ ЭТИ 2 СТРОКИ ▲▲▲
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
