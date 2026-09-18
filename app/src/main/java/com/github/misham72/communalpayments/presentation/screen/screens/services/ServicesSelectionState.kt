package com.github.misham72.communalpayments.presentation.screen.screens.services

data class ServiceItem(
    val key: String,
    val displayName: String,
    val emoji: String,
)

data class ServiceGroup(
    val title: String,
    val items: List<ServiceItem>,
)

data class ServicesSelectionState(
    val groups: List<ServiceGroup> = emptyList(),
    val selectedKeys: Set<String> = emptySet(),
    val isSaved: Boolean = false,
)
