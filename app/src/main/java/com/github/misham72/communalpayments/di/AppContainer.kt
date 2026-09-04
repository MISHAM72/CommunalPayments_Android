package com.github.misham72.communalpayments.di

import android.content.Context
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
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.DataCollectorMeter
import com.github.misham72.communalpayments.domain.usecases.DataCollectorPeriodic
import com.github.misham72.communalpayments.domain.usecases.DeleteAllIncomeRecordsBySourceUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.ExportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.GetIncomeRecordsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetReceiptsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.ImportBackupUseCase
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.UpdateIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.google.gson.Gson

class AppContainer(context: Context) {
    val fileManager = FileManager(
        filesDir = context.filesDir,
        historyDirName = context.getString(R.string.history),
        emptyHistoryMessage = context.getString(R.string.empty_history_calculation)
    )
    private val sharedPrefs = context.getSharedPreferences(
        DataConstants.PREFS_NAME, Context.MODE_PRIVATE
    )
    private val accountPrefs = AccountPreferences(sharedPrefs)

    // Репозитории
    val settingsRepository = UserSettingsRepositoryImpl(accountPrefs)
    val providerRepository = ProviderRepositoryImpl(accountPrefs)

    // Репозитории для метрик
    val electricityRepository = ElectricityRepositoryImpl(
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
    val waterRepository = WaterRepositoryImpl(
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
    val gasRepository = GasRepositoryImpl(
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

    private val periodicRepository = PeriodicRepositoryImpl(
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
    val dataCollectorMeter = DataCollectorMeter(settingsRepository)
    val dataCollectorPeriodic = DataCollectorPeriodic(
        repository = periodicRepository,
        settingsRepository = settingsRepository,
        calculator = PeriodCalculatorImpl()
    )
    private val serviceDisplayNames = mapOf(
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
    private val pdfHistoryRepository = PdfHistoryRepositoryImpl(
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

    val pdfHistoryUseCase = PdfHistoryUseCase(pdfHistoryRepository)

    private val textHistoryRepository = TextHistoryRepositoryImpl(
        fileManager = fileManager,
        cacheDir = context.cacheDir,
        packageName = context.packageName,
        dateFormatPattern = context.getString(R.string.yyyy_mm_dd_hh_mm_ss),
        exportHistoryMessage = context.getString(R.string.export_history)
    )
    val textHistoryUseCase = TextHistoryUseCase(textHistoryRepository)

    // di/AppContainer.kt
    private val historyRepository = HistoryRepositoryImpl(fileManager)
    val getHistoryUseCase = GetHistoryUseCase(historyRepository)
    val saveHistoryUseCase = SaveHistoryUseCase(historyRepository)

    // Доходы
    private val incomeFileManager = IncomeFileManager(
        filesDir = context.filesDir
    )
    private val incomeRepository = IncomeRepositoryImpl(incomeFileManager)
    private val getYearlyIncomeUseCase = GetYearlyIncomeUseCase(incomeRepository)
    private val addIncomeUseCase = AddIncomeUseCase(incomeRepository)
    private val getIncomeRecordsUseCase = GetIncomeRecordsUseCase(incomeRepository)
    private val updateIncomeRecordUseCase = UpdateIncomeRecordUseCase(incomeRepository)
    private val deleteIncomeRecordUseCase = DeleteIncomeRecordUseCase(incomeRepository)
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
    val backupRepository = BackupRepositoryImpl(
        filesDir = context.filesDir,
        historyDirName = context.getString(R.string.history),
        incomeDirName = DataConstants.INCOME_HISTORY_DIR
    )
    val exportBackupUseCase = ExportBackupUseCase(backupRepository)
    val importBackupUseCase = ImportBackupUseCase(backupRepository)

    // Репозиторий для квитанций
    val receiptRepository = ReceiptRepositoryImpl(fileManager, Gson())
    val saveReceiptUseCase = SaveReceiptUseCase(receiptRepository)
    val getReceiptsUseCase = GetReceiptsUseCase(receiptRepository)
    val deleteReceiptUseCase = DeleteReceiptUseCase(receiptRepository)
    val receiptsViewModelFactory = ReceiptsViewModelFactory(getReceiptsUseCase, deleteReceiptUseCase, saveReceiptUseCase)
    val gson = Gson()

    // Репозиторий для банков
    val bankRepository = BankRepositoryImpl()
}

