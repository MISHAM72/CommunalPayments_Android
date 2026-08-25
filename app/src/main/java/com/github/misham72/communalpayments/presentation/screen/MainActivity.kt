package com.github.misham72.communalpayments.presentation.screen

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme
import com.github.misham72.communalpayments.presentation.theme.ThemePrefs
import com.github.misham72.communalpayments.presentation.utils.LanguageManager
import com.github.misham72.communalpayments.presentation.viewmodel.BackupViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val backupViewModel by lazy {
        BackupViewModel(AppContainer.exportBackupUseCase, AppContainer.importBackupUseCase)
    }

    private val createBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            val outputStream = contentResolver.openOutputStream(it)
            if (outputStream == null) {
                Toast.makeText(this, getString(R.string.backup_open_write_error), Toast.LENGTH_SHORT).show()
                return@let
            }
            backupViewModel.exportBackup(outputStream)
        }
    }

    private val openBackupLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            if (inputStream == null) {
                Toast.makeText(this, getString(R.string.backup_open_read_error), Toast.LENGTH_SHORT).show()
                return@let
            }
            backupViewModel.importBackup(inputStream)
        }
    }

    // Список услуг для диалога (без Compose, просто данные)
    private val serviceItems by lazy {
        listOf(
            getString(R.string.service_display_name_electricity) to ServiceKeys.ELECTRICITY,
            getString(R.string.service_display_name_gas) to ServiceKeys.GAS,
            getString(R.string.service_display_name_water) to ServiceKeys.WATER,
            getString(R.string.service_display_name_garbage) to ServiceKeys.GARBAGE,
            getString(R.string.service_display_name_zont) to ServiceKeys.ZONT,
            getString(R.string.service_display_name_internet) to ServiceKeys.INTERNET,
            getString(R.string.service_display_name_mts) to ServiceKeys.MTS,
            getString(R.string.service_display_name_tinkoff) to ServiceKeys.TINKOFF,
            getString(R.string.service_display_name_taxes) to ServiceKeys.TAXES,
            getString(R.string.service_display_name_troyka) to ServiceKeys.TROYKA,
            getString(R.string.service_display_name_osago) to ServiceKeys.OSAGO,
            getString(R.string.service_display_name_hostel) to ServiceKeys.HOSTEL,
        )
    }

    private var pendingFileUri: Uri? = null
    private var pendingFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageManager.applySavedLanguage(this)

        val isSystemDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val themeMode = ThemePrefs.getThemeMode(this)
        val useDarkTheme = when (themeMode) {
            ThemePrefs.MODE_LIGHT -> false
            ThemePrefs.MODE_DARK -> true
            else -> isSystemDarkTheme
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100
            )
        }

        setContent {
            AppTheme(darkTheme = useDarkTheme, dynamicColor = false) {
                val backupState by backupViewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current

                LaunchedEffect(backupState) {
                    when {
                        backupState.isExportDone -> {
                            Toast.makeText(context, getString(R.string.backup_export_success), Toast.LENGTH_LONG).show()
                            backupViewModel.resetState()
                        }

                        backupState.isImportDone -> {
                            Toast.makeText(context, getString(R.string.backup_import_success), Toast.LENGTH_LONG).show()
                            backupViewModel.resetState()
                        }

                        backupState.error != null -> {
                            backupState.error?.let { errorId ->
                                Toast.makeText(context, getString(errorId), Toast.LENGTH_LONG).show()
                            }
                            backupViewModel.resetState()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    ControlBetweenScreens(
                        exportHistoryUseCase = AppContainer.exportHistoryUseCase,
                        getHistoryUseCase = AppContainer.getHistoryUseCase,
                        saveHistoryUseCase = AppContainer.saveHistoryUseCase,
                        getAllServicesYearlySummaryUseCase = AppContainer.getAllServicesYearlySummaryUseCase,
                        incomeViewModelFactory = AppContainer.incomeViewModelFactory,
                        settingsRepository = AppContainer.settingsRepository,
                        onExportBackup = { createBackupLauncher.launch("backup_${System.currentTimeMillis()}.zip") },
                        onImportBackup = {
                            openBackupLauncher.launch(arrayOf("application/zip"))
                        }
                    )
                }
            }
        }

        handleReceivedFile(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleReceivedFile(intent)
    }

    private fun handleReceivedFile(intent: Intent?) {
        if (intent == null) return
        var uri: Uri? = null

        when (intent.action) {
            Intent.ACTION_VIEW -> uri = intent.data
            Intent.ACTION_SEND -> uri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
            Intent.ACTION_SEND_MULTIPLE -> {
                val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                if (!uris.isNullOrEmpty()) uri = uris[0]
            }

            else -> { /* fallback */
            }
        }
        val clipData = intent.clipData
        if (uri == null && clipData != null && clipData.itemCount > 0) {
            val item = clipData.getItemAt(0)
            uri = item.uri
            if (uri == null) {
                val text = item.coerceToText(this).toString()
                if (text.startsWith(UiConstants.URI_SCHEME_CONTENT) || text.startsWith(UiConstants.URI_SCHEME_FILE)) {
                    uri = text.toUri()
                }
            }
        }

        if (uri != null) {
            pendingFileUri = uri
            pendingFileName = getFileNameFromUri(uri) ?: UiConstants.DEFAULT_RECEIPT_FILENAME_TEMPLATE.format(System.currentTimeMillis())
            showServicePickerDialog()
        } else {
            Toast.makeText(this, getString(R.string.error_intent_no_file), Toast.LENGTH_SHORT).show()
        }
    }

    // Диалог выбора услуги
    private fun showServicePickerDialog() {
        if (serviceItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.list_services_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val serviceNames = serviceItems.map { it.first }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_service_receipt))
            .setItems(serviceNames) { _, which ->
                val serviceKey = serviceItems[which].second
                pendingFileUri?.let { uri ->
                    pendingFileName?.let { fileName ->
                        saveReceiptWithService(uri, fileName, serviceKey)
                    }
                }
                pendingFileUri = null
                pendingFileName = null
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                pendingFileUri = null
                pendingFileName = null
            }
            .show()
    }

    // Сохранение с выбранной услугой
    private fun saveReceiptWithService(uri: Uri, fileName: String, serviceKey: String) {
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Toast.makeText(this, getString(R.string.error_read_file), Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                Toast.makeText(this@MainActivity, getString(R.string.saving_receipt), Toast.LENGTH_SHORT).show()
                val receipt = AppContainer.saveReceiptUseCase(serviceKey, inputStream, fileName)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.receipt_saved, receipt.fileName, receipt.filePath),
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, getString(R.string.error_saving_receipt, e.message), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                it.getString(nameIndex)
            } else null
        }
    }
}
