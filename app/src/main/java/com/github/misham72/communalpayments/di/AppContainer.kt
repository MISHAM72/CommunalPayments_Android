package com.github.misham72.communalpayments.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.local.income.filemanager.IncomeFileManager
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.repository.BankRepositoryImpl
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
import com.github.misham72.communalpayments.domain.repository.BankRepository
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
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
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
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
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
    lateinit var electricityRepository: MeterRepository
        private set
    lateinit var waterRepository: MeterRepository
        private set
    lateinit var gasRepository: MeterRepository
        private set
    private lateinit var periodicRepository: PeriodicRepository
    lateinit var periodicDataCollector: PeriodicDataCollector
        private set
    private lateinit var pdfHistoryRepository: PdfHistoryRepository
    lateinit var pdfHistoryUseCase: PdfHistoryUseCase
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
    lateinit var gson: Gson
        private set
    lateinit var bankRepository: BankRepository
        private set
    private lateinit var sharedPrefs: SharedPreferences


    fun init(context: Context) {

        // Общие зависимости
        fileManager = FileManager(
            filesDir = context.filesDir,
            historyDirName = context.getString(R.string.history),
            emptyHistoryMessage = context.getString(R.string.empty_history_calculation)
        )
        sharedPrefs = context.getSharedPreferences(
            DataConstants.PREFS_NAME, Context.MODE_PRIVATE
        )
        accountPrefs = AccountPreferences(sharedPrefs)

        // Репозитории
        settingsRepository = UserSettingsRepositoryImpl(accountPrefs)
        providerRepository = ProviderRepositoryImpl(accountPrefs)

        // Репозитории для метрик
        electricityRepository = ElectricityRepositoryImpl(
            fileManager = fileManager,
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            personalAccountTemplate = context.getString(R.string.personal_account_in_text_history),
            currentReadingTemplate = context.getString(R.string.current_reading),
            previousReadingTemplate = context.getString(R.string.previous_reading),
            tariffTemplate = context.getString(R.string.tariff_card),
            consumptionTemplate = context.getString(R.string.consumption),
            currencyTemplate = context.getString(R.string.currency_rub),
            serviceName = context.getString(R.string.service_display_name_electricity),
            unit = context.getString(R.string.unit_kilowatt_hour)
        )
        waterRepository = WaterRepositoryImpl(
            fileManager = fileManager,
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            personalAccountTemplate = context.getString(R.string.personal_account_in_text_history),
            currentReadingTemplate = context.getString(R.string.current_reading),
            previousReadingTemplate = context.getString(R.string.previous_reading),
            tariffTemplate = context.getString(R.string.tariff_card),
            consumptionTemplate = context.getString(R.string.consumption),
            currencyTemplate = context.getString(R.string.currency_rub),
            serviceName = context.getString(R.string.service_display_name_water),
            unit = context.getString(R.string.unit_cubic_meter)
        )
        gasRepository = GasRepositoryImpl(
            fileManager = fileManager,
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            personalAccountTemplate = context.getString(R.string.personal_account_in_text_history),
            currentReadingTemplate = context.getString(R.string.current_reading),
            previousReadingTemplate = context.getString(R.string.previous_reading),
            tariffTemplate = context.getString(R.string.tariff_card),
            consumptionTemplate = context.getString(R.string.consumption),
            currencyTemplate = context.getString(R.string.currency_rub),
            serviceName = context.getString(R.string.service_display_name_gas),
            unit = context.getString(R.string.unit_cubic_meter)
        )

        // Репозиторий для периодических услуг

        periodicRepository = PeriodicRepositoryImpl(
            fileManager = fileManager,
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            personalAccountTemplate = context.getString(R.string.personal_account_in_text_history),
            nextPaymentTemplate = context.getString(R.string.next_payment),
            priceTariffTemplate = context.getString(R.string.tariff_card),
            periodMonthsTemplate = context.getString(R.string.period_months_format),
            currencyTemplate = context.getString(R.string.currency_rub),
            serviceDisplayNames = mapOf(
                ServiceKeys.GARBAGE to context.getString(R.string.service_display_name_garbage),
                ServiceKeys.MTS to context.getString(R.string.service_display_name_mts),
                ServiceKeys.HOSTEL to context.getString(R.string.service_display_name_hostel),
                ServiceKeys.TROYKA to context.getString(R.string.service_display_name_troyka),
                ServiceKeys.OSAGO to context.getString(R.string.service_display_name_osago),
                ServiceKeys.TAXES to context.getString(R.string.service_display_name_taxes),
                ServiceKeys.TINKOFF to context.getString(R.string.service_display_name_tinkoff),
                ServiceKeys.INTERNET to context.getString(R.string.service_display_name_internet),
                ServiceKeys.ZONT to context.getString(R.string.service_display_name_zont)
            )  // ← теперь карта, а не строка
        )

        // UseCase
        meterDataCollector = MeterDataCollector(settingsRepository)
        periodicDataCollector = PeriodicDataCollector(
            repository = periodicRepository,
            settingsRepository = settingsRepository,
            calculator = PeriodCalculatorImpl()
        )
        val serviceDisplayNames = mapOf(
            ServiceKeys.ELECTRICITY to context.getString(R.string.service_display_name_electricity),
            ServiceKeys.GAS to context.getString(R.string.service_display_name_gas),
            ServiceKeys.WATER to context.getString(R.string.service_display_name_water),
            ServiceKeys.GARBAGE to context.getString(R.string.service_display_name_garbage),
            ServiceKeys.ZONT to context.getString(R.string.service_display_name_zont),
            ServiceKeys.INTERNET to context.getString(R.string.service_display_name_internet),
            ServiceKeys.MTS to context.getString(R.string.service_display_name_mts),
            ServiceKeys.TINKOFF to context.getString(R.string.service_display_name_tinkoff),
            ServiceKeys.TAXES to context.getString(R.string.service_display_name_taxes),
            ServiceKeys.TROYKA to context.getString(R.string.service_display_name_troyka),
            ServiceKeys.OSAGO to context.getString(R.string.service_display_name_osago),
            ServiceKeys.HOSTEL to context.getString(R.string.service_display_name_hostel)
        )
        pdfHistoryRepository = PdfHistoryRepositoryImpl(
            fileManager = fileManager,
            accountPrefs = accountPrefs,
            cacheDir = context.cacheDir,
            packageName = context.packageName,
            statusCalculated = context.getString(R.string.status_calculated),
            currentReadingPdf = context.getString(R.string.current_reading_pdf),
            previousReadingPdf = context.getString(R.string.previous_reading_pdf),
            consumptionPdf = context.getString(R.string.consumption_pdf),
            toBePaid = context.getString(R.string.to_be_paid),
            tariff = context.getString(R.string.tariff),
            periodPdf = context.getString(R.string.period_pdf),
            nextPaymentPdf = context.getString(R.string.next_payment_pdf),
            pdfTitleHistory = context.getString(R.string.pdf_title_history),
            formed = context.getString(R.string.formed),
            personalAccountLabel = context.getString(R.string.personal_account_label),
            pdfTableDate = context.getString(R.string.pdf_table_date),
            pdfTablePrevious = context.getString(R.string.pdf_table_previous),
            pdfTableCurrent = context.getString(R.string.pdf_table_current),
            amount = context.getString(R.string.amount),
            pdfTableStatus = context.getString(R.string.pdf_table_status),
            pdfTablePeriod = context.getString(R.string.pdf_table_period),
            pdfAllHistoryTitle = context.getString(R.string.pdf_all_history_title),
            pdfGenerated = context.getString(R.string.pdf_generated),
            sendPdf = context.getString(R.string.send_pdf),
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            serviceDisplayNames = serviceDisplayNames,
            historyHeader = "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩",
        )

        pdfHistoryUseCase = PdfHistoryUseCase(pdfHistoryRepository)

        textHistoryRepository = TextHistoryRepositoryImpl(
            fileManager = fileManager,
            cacheDir = context.cacheDir,
            packageName = context.packageName,
            dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
            exportHistoryMessage = context.getString(R.string.export_history)
        )
        textHistoryUseCase = TextHistoryUseCase(textHistoryRepository)

        // di/AppContainer.kt
        historyRepository = HistoryRepositoryImpl(fileManager)
        getHistoryUseCase = GetHistoryUseCase(historyRepository)
        saveHistoryUseCase = SaveHistoryUseCase(historyRepository)

        // Доходы
        incomeFileManager = IncomeFileManager(
            filesDir = context.filesDir
        )
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
        backupRepository = BackupRepositoryImpl(
            filesDir = context.filesDir,
            historyDirName = context.getString(R.string.history),
            incomeDirName = DataConstants.INCOME_HISTORY_DIR
        )
        exportBackupUseCase = ExportBackupUseCase(backupRepository)
        importBackupUseCase = ImportBackupUseCase(backupRepository)

        // Репозиторий для квитанций
        receiptRepository = ReceiptRepositoryImpl(fileManager, Gson())
        saveReceiptUseCase = SaveReceiptUseCase(receiptRepository)
        getReceiptsUseCase = GetReceiptsUseCase(receiptRepository)
        deleteReceiptUseCase = DeleteReceiptUseCase(receiptRepository)
        receiptsViewModelFactory = ReceiptsViewModelFactory(getReceiptsUseCase, deleteReceiptUseCase, saveReceiptUseCase)
        gson = Gson()

        // Репозиторий для банков
        bankRepository = BankRepositoryImpl()
    }
}
