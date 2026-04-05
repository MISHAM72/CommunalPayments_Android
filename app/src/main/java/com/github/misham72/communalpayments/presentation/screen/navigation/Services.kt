package com.github.misham72.communalpayments.presentation.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.DisplayElectricityScreen
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.DisplayGarbageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.gas.DisplayGasScreen
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.heating.DisplayZONTScreen
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.internet.DisplayInternetScreen
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
        InitialScreen("⚡", stringResource(R.string.service_display_name_electricity), ServiceKeys.ELECTRICITY, {
            val context = LocalContext.current
            DisplayElectricityScreen(
                viewModel = viewModel(factory = ElectricityViewModelFactory(context))
            )
        }),
        InitialScreen("🔥", stringResource(R.string.service_display_name_gas), ServiceKeys.GAS, {
            val context = LocalContext.current
            DisplayGasScreen(
                viewModel = viewModel(factory = GasViewModelFactory(context))
            )
        }),
        InitialScreen("💧", stringResource(R.string.service_display_name_water), ServiceKeys.WATER, {
            val context = LocalContext.current
            DisplayWaterScreen(
                viewModel = viewModel(factory = WaterViewModelFactory(context))
            )
        }),
        InitialScreen("🗑️", stringResource(R.string.service_display_name_garbage), ServiceKeys.GARBAGE, {
            val context = LocalContext.current
            DisplayGarbageScreen(
                viewModel = viewModel(factory = GarbageViewModelFactory(context))
            )
        }),

        InitialScreen("🌡️", stringResource(R.string.service_display_name_zont), ServiceKeys.ZONT, {
            val context = LocalContext.current
            DisplayZONTScreen(
                viewModel = viewModel(factory = ZONTViewModelFactory(context))
            )
        }),
        InitialScreen("📶", stringResource(R.string.service_display_name_internet), ServiceKeys.INTERNET, {
            val context = LocalContext.current
            DisplayInternetScreen(
                viewModel = viewModel(factory = InternetViewModelFactory(context))
            )
        }),
        InitialScreen("\uD83D\uDD34\uD83D\uDCDE", stringResource(R.string.service_display_name_mts), ServiceKeys.MTS, {
            val context = LocalContext.current
            DisplayMTSScreen(
                viewModel = viewModel(factory = MTSViewModelFactory(context))
            )
        }),
        InitialScreen("\uD83D\uDD35\uD83D\uDCDE", stringResource(R.string.service_display_name_tinkoff), ServiceKeys.TINKOFF, {
            val context = LocalContext.current
            DisplayTinkoffScreen(
                viewModel = viewModel(factory = TinkoffViewModelFactory(context))
            )
        }),
        InitialScreen("💰", stringResource(R.string.service_display_name_taxes), ServiceKeys.TAXES, {
            val context = LocalContext.current
            DisplayTaxesScreen(
                viewModel = viewModel(factory = TaxesViewModelFactory(context))
            )
        }),
        InitialScreen("🚇", stringResource(R.string.service_display_name_troyka), ServiceKeys.TROYKA, {
            val context = LocalContext.current
            DisplayTroykaScreen(
                viewModel = viewModel(factory = TroykaViewModelFactory(context))
            )
        }),
        InitialScreen("🚗", stringResource(R.string.service_display_name_osago), ServiceKeys.OSAGO, {
            val context = LocalContext.current
            DisplayOsagoScreen(
                viewModel = viewModel(factory = OsagoViewModelFactory(context))
            )
        })
    )
}
