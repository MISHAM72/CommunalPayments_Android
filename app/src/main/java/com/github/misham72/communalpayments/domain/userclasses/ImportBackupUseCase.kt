package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.repository.BackupRepository
import java.io.InputStream

class ImportBackupUseCase(private val repository: BackupRepository) {
    suspend operator fun invoke(inputStream: InputStream): Boolean =
        repository.importData(inputStream)
}
