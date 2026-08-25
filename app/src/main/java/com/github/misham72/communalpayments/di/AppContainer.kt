package com.github.misham72.communalpayments.di

import android.annotation.SuppressLint
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
import com.github.misham72.communalpayments.data.repository.receipt.ReceiptRepositoryImpl
import com.github.misham72.communalpayments.data.repository.settings.UserSettingsRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.AnalyticsRepository
import com.github.misham72.communalpayments.domain.repository.BackupRepository
import com.github.misham72.communalpayments.domain.repository.HistoryRepository
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository
import com.github.misham72.communalpayments.domain.repository.ReceiptRepository
import com.github.misham72.communalpayments.domain.repository.TextHistoryRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteAllIncomeRecordsBySourceUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.ExportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.ExportHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetIncomeRecordsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetReceiptsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.ImportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.MeterDataCollector
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataCollector
import com.github.misham72.communalpayments.domain.usecases.SaveHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.UpdateIncomeRecordUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModelFactory
import com.google.gson.Gson

object AppContainer {
    @SuppressLint("StaticFieldLeak")
    lateinit var fileManager: FileManager
        private set
    lateinit var accountPrefs: AccountPreferences
        private set
    lateinit var settingsRepository: UserSettingsRepository
        private set
    lateinit var providerRepository: IProviderRepository
        private set
    private lateinit var electricityRepository: MeterRepository
    private lateinit var waterRepository: MeterRepository
    private lateinit var gasRepository: MeterRepository
    private lateinit var periodicRepository: PeriodicRepository
    lateinit var periodicDataCollector: PeriodicDataCollector
        private set
    private lateinit var pdfHistoryRepository: PdfHistoryRepository
    lateinit var exportHistoryUseCase: ExportHistoryUseCase
        private set
    private lateinit var textHistoryRepository: TextHistoryRepository

    lateinit var textHistoryUseCase: TextHistoryUseCase
        private set
    private lateinit var historyRepository: HistoryRepository

    lateinit var getHistoryUseCase: GetHistoryUseCase
        private set
    lateinit var saveHistoryUseCase: SaveHistoryUseCase
        private set

    @SuppressLint("StaticFieldLeak")
    lateinit var incomeFileManager: IncomeFileManager
        private set
    private lateinit var incomeRepository: IncomeRepository
    private lateinit var getYearlyIncomeUseCase: GetYearlyIncomeUseCase
    private lateinit var addIncomeUseCase: AddIncomeUseCase
    private lateinit var getIncomeRecordsUseCase: GetIncomeRecordsUseCase
    private lateinit var updateIncomeRecordUseCase: UpdateIncomeRecordUseCase
    private lateinit var deleteIncomeRecordUseCase: DeleteIncomeRecordUseCase
    private lateinit var deleteAllIncomeRecordsBySourceUseCase: DeleteAllIncomeRecordsBySourceUseCase
    lateinit var incomeViewModelFactory: IncomeViewModelFactory
        private set
    private lateinit var analyticsRepository: AnalyticsRepository
    lateinit var getAllServicesYearlySummaryUseCase: GetAllServicesYearlySummaryUseCase
        private set
    private lateinit var backupRepository: BackupRepository
    lateinit var exportBackupUseCase: ExportBackupUseCase
        private set
    lateinit var importBackupUseCase: ImportBackupUseCase
        private set
    private lateinit var receiptRepository: ReceiptRepository
    lateinit var saveReceiptUseCase: SaveReceiptUseCase
        private set
    lateinit var getReceiptsUseCase: GetReceiptsUseCase
        private set
    lateinit var deleteReceiptUseCase: DeleteReceiptUseCase
        private set
    lateinit var receiptsViewModelFactory: ReceiptsViewModelFactory
        private set
    lateinit var meterDataCollector: MeterDataCollector
        private set

    fun init(context: Context) {

        // Общие зависимости
        fileManager = FileManager(context.applicationContext)
        accountPrefs = AccountPreferences(context.applicationContext)

        // Репозитории
        settingsRepository = UserSettingsRepositoryImpl(accountPrefs)
        providerRepository = ProviderRepositoryImpl(accountPrefs)

        // Репозитории для метрик
        electricityRepository = ElectricityRepositoryImpl(context, fileManager)
        waterRepository = WaterRepositoryImpl(context, fileManager)
        gasRepository = GasRepositoryImpl(context, fileManager)

        // Репозиторий для периодических услуг
        periodicRepository = PeriodicRepositoryImpl(context, fileManager)

        // UseCase
        meterDataCollector = MeterDataCollector(electricityRepository, settingsRepository)

        periodicDataCollector = PeriodicDataCollector(
            repository = periodicRepository,
            settingsRepository = settingsRepository,
            calculator = PeriodCalculatorImpl()
        )
        pdfHistoryRepository = PdfHistoryRepositoryImpl(fileManager, accountPrefs)
        exportHistoryUseCase = ExportHistoryUseCase(pdfHistoryRepository)

        textHistoryRepository = TextHistoryRepositoryImpl(fileManager)
        textHistoryUseCase = TextHistoryUseCase(textHistoryRepository)

        // di/AppContainer.kt
        historyRepository = HistoryRepositoryImpl(fileManager)
        getHistoryUseCase = GetHistoryUseCase(historyRepository)
        saveHistoryUseCase = SaveHistoryUseCase(historyRepository)

        // Доходы
        incomeFileManager = IncomeFileManager(context.applicationContext)
        incomeRepository = IncomeRepositoryImpl(incomeFileManager)
        getYearlyIncomeUseCase = GetYearlyIncomeUseCase(incomeRepository)
        addIncomeUseCase = AddIncomeUseCase(incomeRepository)
        getIncomeRecordsUseCase = GetIncomeRecordsUseCase(incomeRepository)
        updateIncomeRecordUseCase = UpdateIncomeRecordUseCase(incomeRepository)
        deleteIncomeRecordUseCase = DeleteIncomeRecordUseCase(incomeRepository)
        deleteAllIncomeRecordsBySourceUseCase = DeleteAllIncomeRecordsBySourceUseCase(incomeRepository)

        incomeViewModelFactory = IncomeViewModelFactory(
            getYearlyIncomeUseCase,
            addIncomeUseCase,
            getIncomeRecordsUseCase,
            updateIncomeRecordUseCase,
            deleteIncomeRecordUseCase,
            deleteAllIncomeRecordsBySourceUseCase
        )

        // Аналитика (расходы)
        analyticsRepository = AnalyticsRepositoryImpl(fileManager)
        getAllServicesYearlySummaryUseCase = GetAllServicesYearlySummaryUseCase(analyticsRepository)

        // Репозиторий для бекапа
        backupRepository = BackupRepositoryImpl(context)
        exportBackupUseCase = ExportBackupUseCase(backupRepository)
        importBackupUseCase = ImportBackupUseCase(backupRepository)

        // Репозиторий для квитанций
        receiptRepository = ReceiptRepositoryImpl(fileManager, Gson())
        saveReceiptUseCase = SaveReceiptUseCase(receiptRepository)
        getReceiptsUseCase = GetReceiptsUseCase(receiptRepository)
        deleteReceiptUseCase = DeleteReceiptUseCase(receiptRepository)
        receiptsViewModelFactory = ReceiptsViewModelFactory(getReceiptsUseCase, deleteReceiptUseCase, saveReceiptUseCase)
    }
}
