package com.github.misham72.communalpayments.presentation.screen.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.DisplayElectricityScreen
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.DisplayGarbageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.gas.DisplayGasScreen
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.heating.DisplayZONTScreen
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.internet.DisplayInternetScreen
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.mts.DisplayMTSScreen
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.osago.DisplayOsagoScreen
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.DisplayTaxesScreen
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.DisplayTinkoffScreen
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.DisplayTroykaScreen
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.water.DisplayWaterScreen
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterViewModelFactory


@Composable
fun getListInitialScreen(): List<InitialScreen> {
    return listOf(
        InitialScreen("⚡", stringResource(R.string.service_display_name_electricity), "ЛС: 2324 0001 3040", stringResource(R.string.service_key_electricity), {
            val context = LocalContext.current
            DisplayElectricityScreen(
                viewModel = viewModel(factory = ElectricityViewModelFactory(context))
            )
        }),
        InitialScreen("🔥", stringResource(R.string.service_display_name_gas), "ЛС: 1230 0102 5113", stringResource(R.string.service_key_gas), {
            val context = LocalContext.current
            DisplayGasScreen(
                viewModel = viewModel(factory = GasViewModelFactory(context))
            )
        }),
        InitialScreen("💧", stringResource(R.string.service_display_name_water), "ЛС: 000 007 894", stringResource(R.string.service_key_water), {
            val context = LocalContext.current
            DisplayWaterScreen(
                viewModel = viewModel(factory = WaterViewModelFactory(context))
            )
        }),
        InitialScreen("🗑️", stringResource(R.string.service_display_name_garbage), "ЛС: 210 1010 8366", stringResource(R.string.service_key_garbage), {
            val context = LocalContext.current
            DisplayGarbageScreen(
                viewModel = viewModel(factory = GarbageViewModelFactory(context))
            )
        }),

        InitialScreen("🌡️", stringResource(R.string.service_display_name_zont), "тел. +7(910) 133-00-85", stringResource(R.string.service_key_zont), {
            val context = LocalContext.current
            DisplayZONTScreen(
                viewModel = viewModel(factory = ZONTViewModelFactory(context))
            )
        }),
        InitialScreen("📶", stringResource(R.string.service_display_name_internet), "ЛС: 2300 0343 3205", stringResource(R.string.service_key_internet), {
            val context = LocalContext.current
            DisplayInternetScreen(
                viewModel = viewModel(factory = InternetViewModelFactory(context))
            )
        }),
        InitialScreen("\uD83D\uDD34\uD83D\uDCDE", stringResource(R.string.service_display_name_mts), "тел. +7(918) 48-48-989", stringResource(R.string.service_key_mts), {
            val context = LocalContext.current
            DisplayMTSScreen(
                viewModel = viewModel(factory = MTSViewModelFactory(context))
            )
        }),
        InitialScreen("\uD83D\uDD35\uD83D\uDCDE", stringResource(R.string.service_display_name_tinkoff), "тел. +7(995) 00-585-44", stringResource(R.string.service_key_tinkoff), {
            val context = LocalContext.current
            DisplayTinkoffScreen(
                viewModel = viewModel(factory = TinkoffViewModelFactory(context))
            )
        }),
        InitialScreen("💰", stringResource(R.string.service_display_name_taxes), "ИНН: 2323 0478 5694", stringResource(R.string.service_key_taxes), {
            val context = LocalContext.current
            DisplayTaxesScreen(
                viewModel = viewModel(factory = TaxesViewModelFactory(context))
            )
        }),
        InitialScreen("🚇", stringResource(R.string.service_display_name_troyka), "0048 747 011", stringResource(R.string.service_key_troyka), {
            val context = LocalContext.current
            DisplayTroykaScreen(
                viewModel = viewModel(factory = TroykaViewModelFactory(context))
            )
        }),
        InitialScreen("🚗", stringResource(R.string.service_display_name_osago), "№ XXX 0574 944 292", stringResource(R.string.service_key_osago), {
            val context = LocalContext.current
            DisplayOsagoScreen(
                viewModel = viewModel(factory = OsagoViewModelFactory(context))
            )
        })
    )
}
