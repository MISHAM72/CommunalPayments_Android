package com.github.misham72.communalpayments.presentation.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ElectricityViewModelFactory
import com.github.misham72.communalpayments.di.GarbageViewModelFactory
import com.github.misham72.communalpayments.di.GasViewModelFactory
import com.github.misham72.communalpayments.di.HostelViewModelFactory
import com.github.misham72.communalpayments.di.InternetViewModelFactory
import com.github.misham72.communalpayments.di.MTSViewModelFactory
import com.github.misham72.communalpayments.di.OSAGOViewModelFactory
import com.github.misham72.communalpayments.di.TaxesViewModelFactory
import com.github.misham72.communalpayments.di.TinkoffViewModelFactory
import com.github.misham72.communalpayments.di.TroykaViewModelFactory
import com.github.misham72.communalpayments.di.WaterViewModelFactory
import com.github.misham72.communalpayments.di.ZONTViewModelFactory
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.DisplayElectricityScreen
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.DisplayGarbageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.gas.DisplayGasScreen
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.heating.DisplayZONTScreen
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.DisplayHostelScreen
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.DisplayInternetScreen
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.mts.DisplayMTSScreen
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.osago.DisplayOsagoScreen
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.DisplayTaxesScreen
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.DisplayTinkoffScreen
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.DisplayTroykaScreen
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.water.DisplayWaterScreen
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterViewModel

@Composable
fun getListInitialScreen(): List<InitialScreen> {
    val appContainer = AppContainer  // теперь синглтон
    return listOf(
        InitialScreen("⚡", stringResource(R.string.service_display_name_electricity), ServiceKeys.ELECTRICITY, {
            val factory = ElectricityViewModelFactory(appContainer)
            val viewModel: ElectricityViewModel = viewModel(factory = factory)
            DisplayElectricityScreen(viewModel = viewModel)
        }), InitialScreen("🔥", stringResource(R.string.service_display_name_gas), ServiceKeys.GAS, {
            val factory = GasViewModelFactory(appContainer)
            val viewModel: GasViewModel = viewModel(factory = factory)
            DisplayGasScreen(viewModel = viewModel)
        }), InitialScreen("💧", stringResource(R.string.service_display_name_water), ServiceKeys.WATER, {
            val factory = WaterViewModelFactory(appContainer)
            val viewModel: WaterViewModel = viewModel(factory = factory)
            DisplayWaterScreen(viewModel = viewModel)
        }), InitialScreen("🗑️", stringResource(R.string.service_display_name_garbage), ServiceKeys.GARBAGE, {
            val factory = GarbageViewModelFactory(appContainer)
            val viewModel: GarbageViewModel = viewModel(factory = factory)
            DisplayGarbageScreen(viewModel = viewModel)
        }), InitialScreen("🌡️", stringResource(R.string.service_display_name_zont), ServiceKeys.ZONT, {
            val factory = ZONTViewModelFactory(appContainer)
            val viewModel: ZONTViewModel = viewModel(factory = factory)
            DisplayZONTScreen(viewModel = viewModel)
        }), InitialScreen("📶", stringResource(R.string.service_display_name_internet), ServiceKeys.INTERNET, {
            val factory = InternetViewModelFactory(appContainer)
            val viewModel: InternetViewModel = viewModel(factory = factory)
            DisplayInternetScreen(viewModel = viewModel)
        }), InitialScreen("\uD83D\uDD34\uD83D\uDCDE", stringResource(R.string.service_display_name_mts), ServiceKeys.MTS, {
            val factory = MTSViewModelFactory(appContainer)
            val viewModel: MTSViewModel = viewModel(factory = factory)
            DisplayMTSScreen(viewModel = viewModel)
        }), InitialScreen("\uD83D\uDD35\uD83D\uDCDE", stringResource(R.string.service_display_name_tinkoff), ServiceKeys.TINKOFF, {
            val factory = TinkoffViewModelFactory(appContainer)
            val viewModel: TinkoffViewModel = viewModel(factory = factory)
            DisplayTinkoffScreen(viewModel = viewModel)
        }), InitialScreen("💰", stringResource(R.string.service_display_name_taxes), ServiceKeys.TAXES, {
            val factory = TaxesViewModelFactory(appContainer)
            val viewModel: TaxesViewModel = viewModel(factory = factory)
            DisplayTaxesScreen(viewModel = viewModel)
        }), InitialScreen("🚇", stringResource(R.string.service_display_name_troyka), ServiceKeys.TROYKA, {
            val factory = TroykaViewModelFactory(appContainer)
            val viewModel: TroykaViewModel = viewModel(factory = factory)
            DisplayTroykaScreen(viewModel = viewModel)
        }), InitialScreen("🚗", stringResource(R.string.service_display_name_osago), ServiceKeys.OSAGO, {
            val factory = OSAGOViewModelFactory(appContainer)
            val viewModel: OsagoViewModel = viewModel(factory = factory)
            DisplayOsagoScreen(viewModel = viewModel)
        }), InitialScreen("\uD83D\uDECF\uFE0F ", stringResource(R.string.service_display_name_hostel), ServiceKeys.HOSTEL, {
            val factory = HostelViewModelFactory(appContainer)
            val viewModel: HostelViewModel = viewModel(factory = factory)
            DisplayHostelScreen(viewModel = viewModel)
        })
    )
}
