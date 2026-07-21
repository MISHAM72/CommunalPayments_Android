package com.github.misham72.communalpayments.presentation.screen

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
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
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.repository.backup.BackupRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.ExportBackupUseCase
import com.github.misham72.communalpayments.domain.userclasses.ImportBackupUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme
import com.github.misham72.communalpayments.presentation.theme.ThemePrefs
import com.github.misham72.communalpayments.presentation.utils.LanguageManager
import com.github.misham72.communalpayments.presentation.viewmodel.BackupViewModel

class MainActivity : AppCompatActivity() {

    private val backupViewModel by lazy {
        val backupRepo = BackupRepositoryImpl(applicationContext)
        val exportUseCase = ExportBackupUseCase(backupRepo)
        val importUseCase = ImportBackupUseCase(backupRepo)
        BackupViewModel(exportUseCase, importUseCase)
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

                // Показываем Toast при изменении состояния
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
                            // используем ?.let для безопасного извлечения errorId
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
                    ControlBetweenScreens(onExportBackup = {
                        createBackupLauncher.launch("backup_${System.currentTimeMillis()}.zip")
                    }, onImportBackup = {
                        openBackupLauncher.launch(arrayOf("application/zip"))
                    })
                }
            }
        }
    }
}
