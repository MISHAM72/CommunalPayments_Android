package com.github.misham72.communalpayments.presentation.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.presentation.screen.screens.carinsurance.DisplayCarInsurance
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.DisplayElectricityScreen
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.DisplayGarbageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.gas.DisplayGasScreen
import com.github.misham72.communalpayments.presentation.screen.screens.heating.DisplayHeatingScreen
import com.github.misham72.communalpayments.presentation.screen.screens.internet.DisplayInternetScreen
import com.github.misham72.communalpayments.presentation.screen.screens.mts.DisplayMTSScreen
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.DisplayTaxesScreen
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.DisplayTinkoffScreen
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.DisplayTroykaScreen
import com.github.misham72.communalpayments.presentation.screen.screens.water.DisplayWaterScreen


@Composable
fun getListInitialScreen(): List<InitialScreen> { // "Дай мне список всех услуг"

    return listOf(

        InitialScreen("⚡", stringResource(R.string.service_display_name_electricity), "ЛС: 2324 0001 3040", stringResource(R.string.service_key_electricity), { DisplayElectricityScreen() }),
        InitialScreen("🔥", stringResource(R.string.service_display_name_gas), "ЛС: 1230 0102 5113", stringResource(R.string.service_key_gas), { DisplayGasScreen() }),
        InitialScreen("💧", stringResource(R.string.service_display_name_water), "ЛС: 000 007 894", stringResource(R.string.service_key_water), { DisplayWaterScreen() }),
        InitialScreen("🌡️", stringResource(R.string.service_display_name_zont), "тел. +7(910) 133-00-85", stringResource(R.string.service_key_zont), { DisplayHeatingScreen() }),
        InitialScreen("📶", stringResource(R.string.service_display_name_internet), "ЛС: 2300 0343 3205", stringResource(R.string.service_key_internet), { DisplayInternetScreen() }),
        InitialScreen("📞", stringResource(R.string.service_display_name_mts), "тел. +7(918) 48-48-989", stringResource(R.string.service_key_mts), { DisplayMTSScreen() }),
        InitialScreen("📲", stringResource(R.string.service_display_name_tinkoff), "тел. +7(995) 00-585-44", stringResource(R.string.service_key_tinkoff), { DisplayTinkoffScreen() }),
        InitialScreen("🗑️", stringResource(R.string.service_display_name_garbage), "ЛС: 210 1010 8366", stringResource(R.string.service_key_garbage), { DisplayGarbageScreen() }),
        InitialScreen("💰", stringResource(R.string.service_display_name_taxes), "ИНН: 2323 0478 5694", stringResource(R.string.service_key_taxes), { DisplayTaxesScreen() }),
        InitialScreen("🚇", stringResource(R.string.service_display_name_troyka), "4874 701 1", stringResource(R.string.service_key_troyka), { DisplayTroykaScreen() }),
        InitialScreen("🚗", stringResource(R.string.service_display_name_osago), "№ XXX 0574 944 292", stringResource(R.string.service_key_osago), { DisplayCarInsurance() })
    )
}
