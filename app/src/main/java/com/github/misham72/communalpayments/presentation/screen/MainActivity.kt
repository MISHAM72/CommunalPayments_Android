package com.github.misham72.communalpayments.presentation.screen

import android.Manifest
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.app.CommunalPaymentsApp
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme
import com.github.misham72.communalpayments.presentation.theme.ThemePrefs
import com.github.misham72.communalpayments.presentation.utils.LanguageManager
import com.github.misham72.communalpayments.presentation.viewmodel.BackupViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appContainer: AppContainer
    private lateinit var saveReceiptUseCase: SaveReceiptUseCase

    private val backupViewModel by lazy {
        val app = application as CommunalPaymentsApp
        BackupViewModel(app.appContainer.exportBackupUseCase, app.appContainer.importBackupUseCase)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = (application as CommunalPaymentsApp).appContainer
        saveReceiptUseCase = appContainer.saveReceiptUseCase

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
                        exportHistoryUseCase = (application as CommunalPaymentsApp).appContainer.exportHistoryUseCase,
                        getHistoryUseCase = (application as CommunalPaymentsApp).appContainer.getHistoryUseCase,
                        saveHistoryUseCase = (application as CommunalPaymentsApp).appContainer.saveHistoryUseCase,
                        getAllServicesYearlySummaryUseCase = (application as CommunalPaymentsApp).appContainer.getAllServicesYearlySummaryUseCase,
                        incomeViewModelFactory = (application as CommunalPaymentsApp).appContainer.incomeViewModelFactory,
                        settingsRepository = (application as CommunalPaymentsApp).appContainer.settingsRepository,
                        onExportBackup = { createBackupLauncher.launch("backup_${System.currentTimeMillis()}.zip") },
                        onImportBackup = { openBackupLauncher.launch(arrayOf("application/zip")) }
                    )
                }
            }
        }

        handleReceivedFile(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleReceivedFile(intent)
    }

    private fun handleReceivedFile(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "application/pdf") {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            uri?.let { saveReceiptFromUri(it) }
        }
    }

    private fun saveReceiptFromUri(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Toast.makeText(this, "Не удалось прочитать файл", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = getFileNameFromUri(uri) ?: "квитанция_${System.currentTimeMillis()}.pdf"

        lifecycleScope.launch {
            try {
                Toast.makeText(this@MainActivity, "Сохранение квитанции...", Toast.LENGTH_SHORT).show()
                val receipt = saveReceiptUseCase("unknown", inputStream, fileName)
                Toast.makeText(
                    this@MainActivity,
                    "Квитанция сохранена: ${receipt.fileName}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
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
