package com.github.misham72.communalpayments.presentation.screen.screens.services

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

enum class ServiceGroupType { METERS, PERIODIC }

data class ServiceDefinition(
    val key: String,
    val emoji: String,
    @StringRes val nameRes: Int,
    val group: ServiceGroupType,
)

object ServiceRegistry {

    val all: List<ServiceDefinition> = listOf(
        ServiceDefinition(
            key = ServiceKeys.ELECTRICITY,
            emoji = "⚡",
            nameRes = R.string.service_display_name_electricity,
            group = ServiceGroupType.METERS,
        ),
        ServiceDefinition(
            key = ServiceKeys.GAS,
            emoji = "🔥",
            nameRes = R.string.service_display_name_gas,
            group = ServiceGroupType.METERS,
        ),
        ServiceDefinition(
            key = ServiceKeys.COLDWATER,
            emoji = "💧",
            nameRes = R.string.service_display_name_coldwater,
            group = ServiceGroupType.METERS,
        ),
        ServiceDefinition(
            key = ServiceKeys.GARBAGE,
            emoji = "🗑️",
            nameRes = R.string.service_display_name_garbage,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.ZONT,
            emoji = "🌡️",
            nameRes = R.string.service_display_name_zont,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.INTERNET,
            emoji = "📶",
            nameRes = R.string.service_display_name_internet,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.MTS,
            emoji = "📱",
            nameRes = R.string.service_display_name_mts,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.TINKOFF,
            emoji = "🟦",
            nameRes = R.string.service_display_name_tinkoff,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.TAXES,
            emoji = "💰",
            nameRes = R.string.service_display_name_taxes,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.TROYKA,
            emoji = "🚇",
            nameRes = R.string.service_display_name_troyka,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.OSAGO,
            emoji = "🚗",
            nameRes = R.string.service_display_name_osago,
            group = ServiceGroupType.PERIODIC,
        ),
        ServiceDefinition(
            key = ServiceKeys.HOSTEL,
            emoji = "🏢",
            nameRes = R.string.service_display_name_hostel,
            group = ServiceGroupType.PERIODIC,
        ),
    )

    fun byKey(key: String): ServiceDefinition? = all.firstOrNull { it.key == key }

    fun meters(): List<ServiceDefinition> =
        all.filter { it.group == ServiceGroupType.METERS }

    fun periodic(): List<ServiceDefinition> =
        all.filter { it.group == ServiceGroupType.PERIODIC }
}
@Composable
fun ServiceDefinition.displayName(): String =
    "$emoji ${stringResource(nameRes)}"
@Composable
fun serviceName(key: String): String =
    ServiceRegistry.byKey(key)!!.displayName()
