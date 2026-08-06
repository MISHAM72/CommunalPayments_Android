package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.BackupRepository
import java.io.OutputStream

class ExportBackupUseCase(private val repository: BackupRepository) {
    suspend operator fun invoke(outputStream: OutputStream): Boolean =
        repository.exportData(outputStream)
}
