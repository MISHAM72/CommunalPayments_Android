package com.github.misham72.communalpayments.presentation.screen.navigation

import androidx.compose.runtime.Composable

data class InitialScreen(
    val icon: String, val name: String, val fileKey: String, val screen: @Composable () -> Unit, val onEditClick: () -> Unit = {}
)