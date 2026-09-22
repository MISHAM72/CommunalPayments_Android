package com.github.misham72.communalpayments.presentation.screen.screens.services

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.CapitalRepairViewModelFactory
import com.github.misham72.communalpayments.di.ColdWaterViewModelFactory
import com.github.misham72.communalpayments.di.DrainageViewModelFactory
import com.github.misham72.communalpayments.di.ElectricityViewModelFactory
import com.github.misham72.communalpayments.di.GarbageViewModelFactory
import com.github.misham72.communalpayments.di.GasViewModelFactory
import com.github.misham72.communalpayments.di.HostelViewModelFactory
import com.github.misham72.communalpayments.di.HotWaterViewModelFactory
import com.github.misham72.communalpayments.di.InternetViewModelFactory
import com.github.misham72.communalpayments.di.MTSViewModelFactory
import com.github.misham72.communalpayments.di.OSAGOViewModelFactory
import com.github.misham72.communalpayments.di.TaxesViewModelFactory
import com.github.misham72.communalpayments.di.TinkoffViewModelFactory
import com.github.misham72.communalpayments.di.TroykaViewModelFactory
import com.github.misham72.communalpayments.di.ZONTViewModelFactory
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.navigation.InitialScreen
import com.github.misham72.communalpayments.presentation.screen.screens.capitalrepair.CapitalRepairScreen
import com.github.misham72.communalpayments.presentation.screen.screens.capitalrepair.CapitalRepairViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.drainage.DrainageScreen
import com.github.misham72.communalpayments.presentation.screen.screens.drainage.DrainageViewModel
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
import com.github.misham72.communalpayments.presentation.screen.screens.watercold.ColdWaterScreen
import com.github.misham72.communalpayments.presentation.screen.screens.watercold.ColdWaterViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.waterhot.HotWaterScreen
import com.github.misham72.communalpayments.presentation.screen.screens.waterhot.HotWaterViewModel

@Composable
//Названия услуг, Ключи, Экраны (что показать при клике)
fun getListInitialScreen(appContainer: AppContainer): List<InitialScreen> {
    return listOf(
        InitialScreen(ServiceRegistry.byKey(ServiceKeys.ELECTRICITY)!!.displayName(), ServiceKeys.ELECTRICITY, {
            val factory = ElectricityViewModelFactory(appContainer)
            val viewModel: ElectricityViewModel = viewModel(factory = factory)
            ElectricityScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.GAS)!!.displayName(), ServiceKeys.GAS, {
            val factory = GasViewModelFactory(appContainer)
            val viewModel: GasViewModel = viewModel(factory = factory)
            GasScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.COLDWATER)!!.displayName(), ServiceKeys.COLDWATER, {
            val factory = ColdWaterViewModelFactory(appContainer)
            val viewModel: ColdWaterViewModel = viewModel(factory = factory)
            ColdWaterScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.HOTWATER)!!.displayName(), ServiceKeys.HOTWATER, {
            val factory = HotWaterViewModelFactory(appContainer)
            val viewModel: HotWaterViewModel = viewModel(factory = factory)
            HotWaterScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.DRAINAGE)!!.displayName(), ServiceKeys.DRAINAGE, {
            val factory = DrainageViewModelFactory(appContainer)
            val viewModel: DrainageViewModel = viewModel(factory = factory)
            DrainageScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.GARBAGE)!!.displayName(), ServiceKeys.GARBAGE, {
            val factory = GarbageViewModelFactory(appContainer)
            val viewModel: GarbageViewModel = viewModel(factory = factory)
            GarbageScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.ZONT)!!.displayName(), ServiceKeys.ZONT, {
            val factory = ZONTViewModelFactory(appContainer)
            val viewModel: ZONTViewModel = viewModel(factory = factory)
            ZONTScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.INTERNET)!!.displayName(), ServiceKeys.INTERNET, {
            val factory = InternetViewModelFactory(appContainer)
            val viewModel: InternetViewModel = viewModel(factory = factory)
            InternetScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.MTS)!!.displayName(), ServiceKeys.MTS, {
            val factory = MTSViewModelFactory(appContainer)
            val viewModel: MTSViewModel = viewModel(factory = factory)
            MTSScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.TINKOFF)!!.displayName(), ServiceKeys.TINKOFF, {
            val factory = TinkoffViewModelFactory(appContainer)
            val viewModel: TinkoffViewModel = viewModel(factory = factory)
            TinkoffScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.TAXES)!!.displayName(), ServiceKeys.TAXES, {
            val factory = TaxesViewModelFactory(appContainer)
            val viewModel: TaxesViewModel = viewModel(factory = factory)
            TaxesScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.TROYKA)!!.displayName(), ServiceKeys.TROYKA, {
            val factory = TroykaViewModelFactory(appContainer)
            val viewModel: TroykaViewModel = viewModel(factory = factory)
            TroykaScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.OSAGO)!!.displayName(), ServiceKeys.OSAGO, {
            val factory = OSAGOViewModelFactory(appContainer)
            val viewModel: OsagoViewModel = viewModel(factory = factory)
            OsagoScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.HOSTEL)!!.displayName(), ServiceKeys.HOSTEL, {
            val factory = HostelViewModelFactory(appContainer)
            val viewModel: HostelViewModel = viewModel(factory = factory)
            HostelScreen(viewModel = viewModel, appContainer = appContainer)
        }), InitialScreen(ServiceRegistry.byKey(ServiceKeys.CAPITAL_REPAIR)!!.displayName(), ServiceKeys.CAPITAL_REPAIR, {
            val factory = CapitalRepairViewModelFactory(appContainer)
            val viewModel: CapitalRepairViewModel = viewModel(factory = factory)
            CapitalRepairScreen(viewModel = viewModel, appContainer = appContainer)
        })
    )
}

@Composable
fun getSelectedScreens(appContainer: AppContainer): List<InitialScreen> {
    val allServices = getListInitialScreen(appContainer)
    val selected = appContainer.selectedServicesRepository.getSelected()
    return allServices.filter { selected.contains(it.fileKey) }
}
