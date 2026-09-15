package com.github.misham72.communalpayments.domain.model

/**
 * Одна запись из истории платежей.
 *
 * @param rawBlock    оригинальный текст блока (включая разделитель 🟩×12),
 *                    используется для поиска и замены блока в файле
 * @param dateTime    дата-время из блока ("2026-08-13 22:00:16")
 * @param serviceKey  ключ услуги ("hostel", "electricity", ...)
 * @param attachments список вложений (может быть пустым)
 */
data class HistoryRecord(
    val rawBlock: String,
    val dateTime: String,
    val serviceKey: String,
    val attachments: List<Attachment> = emptyList()
) {
    val hasAttachments: Boolean get() = attachments.isNotEmpty()
    val attachmentsCount: Int get() = attachments.size
}
