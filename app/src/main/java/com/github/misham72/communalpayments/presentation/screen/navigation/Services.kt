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
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityScreen
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasScreen
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTScreen
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelScreen
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetScreen
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSScreen
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoScreen
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesScreen
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffScreen
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaScreen
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterScreen
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterViewModel

@Composable
fun getListInitialScreen(appContainer: AppContainer): List<InitialScreen> {
    return listOf(
        InitialScreen("⚡", stringResource(R.string.service_display_name_electricity), ServiceKeys.ELECTRICITY, {
            val factory = ElectricityViewModelFactory(appContainer)
            val viewModel: ElectricityViewModel = viewModel(factory = factory)
            ElectricityScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("🔥", stringResource(R.string.service_display_name_gas), ServiceKeys.GAS, {
            val factory = GasViewModelFactory(appContainer)
            val viewModel: GasViewModel = viewModel(factory = factory)
            GasScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("💧", stringResource(R.string.service_display_name_water), ServiceKeys.WATER, {
            val factory = WaterViewModelFactory(appContainer)
            val viewModel: WaterViewModel = viewModel(factory = factory)
            WaterScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("🗑️", stringResource(R.string.service_display_name_garbage), ServiceKeys.GARBAGE, {
            val factory = GarbageViewModelFactory(appContainer)
            val viewModel: GarbageViewModel = viewModel(factory = factory)
            GarbageScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("🌡️", stringResource(R.string.service_display_name_zont), ServiceKeys.ZONT, {
            val factory = ZONTViewModelFactory(appContainer)
            val viewModel: ZONTViewModel = viewModel(factory = factory)
            ZONTScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("📶", stringResource(R.string.service_display_name_internet), ServiceKeys.INTERNET, {
            val factory = InternetViewModelFactory(appContainer)
            val viewModel: InternetViewModel = viewModel(factory = factory)
            InternetScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("\uD83D\uDD34\uD83D\uDCDE", stringResource(R.string.service_display_name_mts), ServiceKeys.MTS, {
            val factory = MTSViewModelFactory(appContainer)
            val viewModel: MTSViewModel = viewModel(factory = factory)
            MTSScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("\uD83D\uDD35\uD83D\uDCDE", stringResource(R.string.service_display_name_tinkoff), ServiceKeys.TINKOFF, {
            val factory = TinkoffViewModelFactory(appContainer)
            val viewModel: TinkoffViewModel = viewModel(factory = factory)
            TinkoffScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("💰", stringResource(R.string.service_display_name_taxes), ServiceKeys.TAXES, {
            val factory = TaxesViewModelFactory(appContainer)
            val viewModel: TaxesViewModel = viewModel(factory = factory)
            TaxesScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("🚇", stringResource(R.string.service_display_name_troyka), ServiceKeys.TROYKA, {
            val factory = TroykaViewModelFactory(appContainer)
            val viewModel: TroykaViewModel = viewModel(factory = factory)
            TroykaScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("🚗", stringResource(R.string.service_display_name_osago), ServiceKeys.OSAGO, {
            val factory = OSAGOViewModelFactory(appContainer)
            val viewModel: OsagoViewModel = viewModel(factory = factory)
            OsagoScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen("\uD83D\uDECF\uFE0F ", stringResource(R.string.service_display_name_hostel), ServiceKeys.HOSTEL, {
            val factory = HostelViewModelFactory(appContainer)
            val viewModel: HostelViewModel = viewModel(factory = factory)
            HostelScreen(viewModel = viewModel, appContainer = appContainer)
        })
    )
}
