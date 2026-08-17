package com.github.misham72.communalpayments.di

import android.content.Context
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.local.income.filemanager.IncomeFileManager
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.repository.analytics.AnalyticsRepositoryImpl
import com.github.misham72.communalpayments.data.repository.backup.BackupRepositoryImpl
import com.github.misham72.communalpayments.data.repository.export.PdfHistoryRepositoryImpl
import com.github.misham72.communalpayments.data.repository.export.TextHistoryRepositoryImpl
import com.github.misham72.communalpayments.data.repository.history.HistoryRepositoryImpl
import com.github.misham72.communalpayments.data.repository.income.IncomeRepositoryImpl
import com.github.misham72.communalpayments.data.repository.meterrepository.ElectricityRepositoryImpl
import com.github.misham72.communalpayments.data.repository.meterrepository.GasRepositoryImpl
import com.github.misham72.communalpayments.data.repository.meterrepository.WaterRepositoryImpl
import com.github.misham72.communalpayments.data.repository.periodrepository.PeriodicRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.settings.UserSettingsRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.BackupRepository
import com.github.misham72.communalpayments.domain.repository.HistoryRepository
import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository
import com.github.misham72.communalpayments.domain.repository.TextHistoryRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteAllIncomeRecordsBySourceUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.usecases.ExportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.ExportHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetIncomeRecordsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.ImportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.MeterDataCollector
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataCollector
import com.github.misham72.communalpayments.domain.usecases.SaveHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.UpdateIncomeRecordUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModelFactory


class AppContainer(private val context: Context) {

    // Общие зависимости
    val fileManager: FileManager = FileManager(context)
    val accountPrefs: AccountPreferences = AccountPreferences(context.applicationContext)

    // Репозитории
    val settingsRepository: UserSettingsRepository = UserSettingsRepositoryImpl(accountPrefs)
    val providerRepository = ProviderRepositoryImpl(accountPrefs)

    // Репозитории для метрик
    val electricityRepository: MeterRepository = ElectricityRepositoryImpl(context, fileManager)
    val waterRepository: MeterRepository = WaterRepositoryImpl(context, fileManager)
    val gasRepository: MeterRepository = GasRepositoryImpl(context, fileManager)

    // Репозиторий для периодических услуг
    val periodicRepository: PeriodicRepository = PeriodicRepositoryImpl(context, fileManager)

    // UseCase
    val meterDataCollector = MeterDataCollector(electricityRepository, settingsRepository)
    val waterDataCollector = MeterDataCollector(waterRepository, settingsRepository)
    val gasDataCollector = MeterDataCollector(gasRepository, settingsRepository)

    val periodicDataCollector = PeriodicDataCollector(
        repository = periodicRepository,
        settingsRepository = settingsRepository,
        calculator = PeriodCalculatorImpl()
    )
    val pdfHistoryRepository: PdfHistoryRepository = PdfHistoryRepositoryImpl(fileManager, accountPrefs)
    val exportHistoryUseCase = ExportHistoryUseCase(pdfHistoryRepository)

    val textHistoryRepository: TextHistoryRepository = TextHistoryRepositoryImpl(fileManager)
    val textHistoryUseCase = TextHistoryUseCase(textHistoryRepository)

    // di/AppContainer.kt
    val historyRepository: HistoryRepository = HistoryRepositoryImpl(fileManager)
    val getHistoryUseCase = GetHistoryUseCase(historyRepository)
    val saveHistoryUseCase = SaveHistoryUseCase(historyRepository)

    // Доходы
    val incomeFileManager = IncomeFileManager(context)
    val incomeRepository: IncomeRepository = IncomeRepositoryImpl(incomeFileManager)
    val getYearlyIncomeUseCase = GetYearlyIncomeUseCase(incomeRepository)
    val addIncomeUseCase = AddIncomeUseCase(incomeRepository)
    val getIncomeRecordsUseCase = GetIncomeRecordsUseCase(incomeRepository)
    val updateIncomeRecordUseCase = UpdateIncomeRecordUseCase(incomeRepository)
    val deleteIncomeRecordUseCase = DeleteIncomeRecordUseCase(incomeRepository)
    val deleteAllIncomeRecordsBySourceUseCase = DeleteAllIncomeRecordsBySourceUseCase(incomeRepository)

    val incomeViewModelFactory = IncomeViewModelFactory(
        getYearlyIncomeUseCase,
        addIncomeUseCase,
        getIncomeRecordsUseCase,
        updateIncomeRecordUseCase,
        deleteIncomeRecordUseCase,
        deleteAllIncomeRecordsBySourceUseCase
    )

    // Аналитика (расходы)
    val analyticsRepository = AnalyticsRepositoryImpl(fileManager)
    val getAllServicesYearlySummaryUseCase = GetAllServicesYearlySummaryUseCase(analyticsRepository)

    // Репозиторий для бекапа
    val backupRepository: BackupRepository = BackupRepositoryImpl(context)
    val exportBackupUseCase = ExportBackupUseCase(backupRepository)
    val importBackupUseCase = ImportBackupUseCase(backupRepository)


}
