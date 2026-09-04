package com.github.misham72.communalpayments.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteAllIncomeRecordsBySourceUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.GetIncomeRecordsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetReceiptsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.UpdateIncomeRecordUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.receipts.ReceiptsViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.water.WaterViewModel

class ElectricityViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectricityViewModel::class.java)) {
            return ElectricityViewModel(
                dataCollectorMeter = container.dataCollectorMeter,
                meterRepository = container.electricityRepository,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GasViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GasViewModel::class.java)) {
            return GasViewModel(
                dataCollectorMeter = container.dataCollectorMeter,
                meterRepository = container.gasRepository,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WaterViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            return WaterViewModel(
                dataCollectorMeter = container.dataCollectorMeter,
                meterRepository = container.waterRepository,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GarbageViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageViewModel::class.java)) {
            return GarbageViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ZONTViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ZONTViewModel::class.java)) {
            return ZONTViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HostelViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HostelViewModel::class.java)) {
            return HostelViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class InternetViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternetViewModel::class.java)) {
            return InternetViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MTSViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MTSViewModel::class.java)) {
            return MTSViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class OSAGOViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OsagoViewModel::class.java)) {
            return OsagoViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaxesViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxesViewModel::class.java)) {
            return TaxesViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TinkoffViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TinkoffViewModel::class.java)) {
            return TinkoffViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TroykaViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TroykaViewModel::class.java)) {
            return TroykaViewModel(
                dataCollectorPeriodic = container.dataCollectorPeriodic,
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase,
                gson = container.gson
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ReceiptsViewModelFactory(
    private val getReceiptsUseCase: GetReceiptsUseCase,
    private val deleteReceiptUseCase: DeleteReceiptUseCase,
    private val saveReceiptUseCase: SaveReceiptUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptsViewModel::class.java)) {
            return ReceiptsViewModel(getReceiptsUseCase, deleteReceiptUseCase, saveReceiptUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class IncomeViewModelFactory(
    private val getIncomeUseCase: GetYearlyIncomeUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
    private val getIncomeRecordsUseCase: GetIncomeRecordsUseCase,
    private val updateIncomeRecordUseCase: UpdateIncomeRecordUseCase,
    private val deleteIncomeRecordUseCase: DeleteIncomeRecordUseCase,
    private val deleteAllIncomeRecordsBySourceUseCase: DeleteAllIncomeRecordsBySourceUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(
                getIncomeUseCase,
                addIncomeUseCase,
                getIncomeRecordsUseCase,
                updateIncomeRecordUseCase,
                deleteIncomeRecordUseCase,
                deleteAllIncomeRecordsBySourceUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

