package com.github.misham72.communalpayments.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.usecases.DeleteReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.GetExpensesUseCase
import com.github.misham72.communalpayments.domain.usecases.GetReceiptsUseCase
import com.github.misham72.communalpayments.domain.usecases.IncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.ExpensesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.drainage.DrainageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.gas.GasViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.heating.ZONTViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.hostel.HostelViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.internet.InternetViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.mts.MTSViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.osago.OsagoViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.receipts.ReceiptsViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.services.ServiceItem
import com.github.misham72.communalpayments.presentation.screen.screens.services.ServicesSelectionViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.taxes.TaxesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.tinkoff.TinkoffViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.troyka.TroykaViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.watercold.ColdWaterViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.waterhot.HotWaterViewModel

class ElectricityViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectricityViewModel::class.java)) {
            return ElectricityViewModel(
                meterDataUseCase = container.meterDataUseCase,
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
                meterDataUseCase = container.meterDataUseCase,
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

class ColdWaterViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColdWaterViewModel::class.java)) {
            return ColdWaterViewModel(
                meterDataUseCase = container.meterDataUseCase,
                meterRepository = container.coldWaterRepository,
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

class HotWaterViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotWaterViewModel::class.java)) {
            return HotWaterViewModel(
                meterDataUseCase = container.meterDataUseCase,
                meterRepository = container.hotWaterRepository,
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

class DrainageViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrainageViewModel::class.java)) {
            return DrainageViewModel(
                settingsRepository = container.settingsRepository,
                repository = container.providerRepository,
                drainageRepository = container.drainageRepository,   // ← новое
                fileManager = container.fileManager,
                textHistoryUseCase = container.textHistoryUseCase,
                pdfHistoryUseCase = container.pdfHistoryUseCase
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
                periodicDataUseCase = container.periodicDataUseCase,
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
    private val incomeUseCase: IncomeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(incomeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class ExpensesViewModelFactory(
    private val useCase: GetExpensesUseCase,
    private val defaultErrorMessage: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            return ExpensesViewModel(useCase, defaultErrorMessage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ServicesSelectionViewModelFactory(
    private val appContainer: AppContainer,
    private val meterItems: List<ServiceItem>,
    private val periodicItems: List<ServiceItem>,
    private val groupTitles: Pair<String, String>,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesSelectionViewModel::class.java)) {
            return ServicesSelectionViewModel(
                repository = appContainer.selectedServicesRepository,
                meterItems = meterItems,
                periodicItems = periodicItems,
                groupTitles = groupTitles,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

