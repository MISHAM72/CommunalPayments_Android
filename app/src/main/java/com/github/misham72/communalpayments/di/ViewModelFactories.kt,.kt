package com.github.misham72.communalpayments.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.usecases.ExportHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterViewModel

class ElectricityViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectricityViewModel::class.java)) {
            return ElectricityViewModel(
                meterDataCollector = container.meterDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GasViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GasViewModel::class.java)) {
            return GasViewModel(
                meterDataCollector = container.gasDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WaterViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            return WaterViewModel(
                meterDataCollector = container.waterDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GarbageViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageViewModel::class.java)) {
            return GarbageViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ZONTViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ZONTViewModel::class.java)) {
            return ZONTViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HostelViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HostelViewModel::class.java)) {
            return HostelViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class InternetViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternetViewModel::class.java)) {
            return InternetViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MTSViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MTSViewModel::class.java)) {
            return MTSViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class OSAGOViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OsagoViewModel::class.java)) {
            return OsagoViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaxesViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxesViewModel::class.java)) {
            return TaxesViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TinkoffViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TinkoffViewModel::class.java)) {
            return TinkoffViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TroykaViewModelFactory(
    private val container: AppContainer,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TroykaViewModel::class.java)) {
            return TroykaViewModel(
                periodicDataCollector = container.periodicDataCollector,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = textHistoryUseCase,
                exportHistoryUseCase = exportHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

