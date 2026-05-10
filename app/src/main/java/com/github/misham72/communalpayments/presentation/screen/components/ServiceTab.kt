package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.domain.utils.DateUtils
import com.github.misham72.communalpayments.presentation.screen.navigation.InitialScreen

@Composable
fun ServiceTab(
    modifier: Modifier = Modifier,
    service: InitialScreen,
    isSelected: Boolean,
    dueDate: String?,
    onClick: () -> Unit,
    onSound: (() -> Unit)? = null  // ← НОВЫЙ ПАРАМЕТР

) {
    val backgroundColor = if (dueDate != null) {
        val daysLeft = DateUtils.daysUntil(dueDate)
        when {
            daysLeft <= 0 -> Color(0xFFC62828)
            daysLeft <= 3 -> Color(0xFFFFC107)
            else -> Color(0xFF2E7D32)
        }
    } else {
        Color.Gray
    }
    val contentColor = when (backgroundColor) {
        Color(0xFFFFC107) -> Color.Black
        else -> Color.White
    }

    val finalBackground = if (isSelected) backgroundColor.copy(alpha = 0.5f) else backgroundColor
    Button(
        onClick = {
            onSound?.invoke()  // ← Сначала звук (если передан)
            onClick()          // ← Потом переключение вкладки
        },
        modifier = modifier.padding(horizontal = 4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 12.dp else 0.dp,
            pressedElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = finalBackground,
            contentColor = contentColor
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(service.icon, fontSize = 28.sp)
            Text(service.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = contentColor)

        }
    }
}