package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.BackupRepository
import java.io.InputStream

class ImportBackupUseCase(private val repository: BackupRepository) {
    suspend operator fun invoke(inputStream: InputStream): Boolean =
        repository.importData(inputStream)
}
