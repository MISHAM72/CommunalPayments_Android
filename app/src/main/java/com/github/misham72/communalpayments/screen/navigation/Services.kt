package com.github.misham72.communalpayments.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.screen.components.screens.DisplayCarInsurance
import com.github.misham72.communalpayments.screen.components.screens.DisplayElectricityScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayGarbageScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayGasScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayHeatingScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayInternetScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayMTSScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayTaxesScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayTinkoffScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayTroykaScreen
import com.github.misham72.communalpayments.screen.components.screens.DisplayWaterScreen
import com.github.misham72.communalpayments.screen.navigation.models.InitialScreen


@Composable
fun getListInitialScreen(): List<InitialScreen> { // "Дай мне список всех услуг"

    val context = LocalContext.current // ← ЭТО ВАШ КЛЮЧ К РЕСУРСАМ. // LocalContext.current даёт вам Context текущего Composable

    return listOf(

        InitialScreen("⚡", context.getString(R.string.service_display_name_electricity), "ЛС: 2324 0001 3040", context.getString(R.string.service_key_electricity), { DisplayElectricityScreen() }),
        InitialScreen("🔥", context.getString(R.string.service_display_name_gas), "ЛС: 1230 0102 5113", context.getString(R.string.service_key_gas), { DisplayGasScreen() }),
        InitialScreen("💧", context.getString(R.string.service_display_name_water), "ЛС: 000 007 894", context.getString(R.string.service_key_water), { DisplayWaterScreen() }),
        InitialScreen("🌡️", context.getString(R.string.service_display_name_zont), "тел. +7(910) 133-00-85", context.getString(R.string.service_key_zont), { DisplayHeatingScreen() }),
        InitialScreen("📶", context.getString(R.string.service_display_name_internet), "ЛС: 2300 0343 3205", context.getString(R.string.service_key_internet), { DisplayInternetScreen() }),
        InitialScreen("📞", context.getString(R.string.service_display_name_mts), "тел. +7(918) 48-48-989", context.getString(R.string.service_key_mts), { DisplayMTSScreen() }),
        InitialScreen("📲", context.getString(R.string.service_display_name_tinkoff), "тел. +7(995) 00-585-44", context.getString(R.string.service_key_tinkoff), { DisplayTinkoffScreen() }),
        InitialScreen("🗑️", context.getString(R.string.service_display_name_garbage), "ЛС: 210 1010 8366", context.getString(R.string.service_key_garbage), { DisplayGarbageScreen() }),
        InitialScreen("💰", context.getString(R.string.service_display_name_taxes), "ИНН: 2323 0478 5694", context.getString(R.string.service_key_taxes), { DisplayTaxesScreen() }),
        InitialScreen("🚇", context.getString(R.string.service_display_name_troyka), "4874 701 1", context.getString(R.string.service_key_troyka), { DisplayTroykaScreen() }),
        InitialScreen("🚗", context.getString(R.string.service_display_name_osago), "№ XXX 0574 944 292", context.getString(R.string.service_key_osago), { DisplayCarInsurance() })
    )
}
