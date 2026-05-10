package com.github.misham72.communalpayments.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(/* value = */ AndroidJUnit4::class)   // Зачем? Указывает Android, как запускать тесты (с поддержкой эмулятора/устройства).
class FileManagerTest {

    private lateinit var context: Context   // Зачем? Хранить экземпляры тестируемого класса и контекста.

    private lateinit var fileManager: FileManager   // – хранилище тестируемого объекта.

    @Before   // Зачем? Инициализировать окружение перед каждым тестом. Контекст через ApplicationProvider – стандарт.
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        fileManager = FileManager(context)
        // Очистка папки истории перед каждым тестом
        val historyDir = File(context.filesDir, context.getString(com.github.misham72.communalpayments.R.string.history))
        if (historyDir.exists()) {   // если папка уже существует (от предыдущих тестов), удаляем её для чистоты, проверяет, что файл создался
            historyDir.deleteRecursively()
        }
    }

    // ========== readHistory ==========
    @Suppress("HardcodedStringLiteral")
    @Test   // Зачем? JUnit находит и запускает методы с этой аннотацией.
    fun readHistory_returnsEmptyMessage_whenFileDoesNotExist() {
        runBlocking {   // runBlocking { ... } для тестирования suspend-функций, зачем? Превращает корутину в обычную блокирующую операцию (для тестов)
            val result = fileManager.readHistory("nonexistent_key")
            val expected = context.getString(com.github.misham72.communalpayments.R.string.empty_history_calculation)
            assertThat(result).isEqualTo(expected)    // Зачем? Проверяют, соответствует ли реальное поведение ожидаемому.
        }
    }

    @Suppress("HardcodedStringLiteral")
    @Test   // Зачем? JUnit находит и запускает методы с этой аннотацией.
    fun readHistory_returnsFileContent_whenFileExists() {
        runBlocking {   // runBlocking { ... } для тестирования suspend-функций, зачем? Превращает корутину в обычную блокирующую операцию (для тестов)
            val serviceKey = context.getString(com.github.misham72.communalpayments.R.string.service_display_name_electricity)
            val content = "2025-01-01: 100 kWh\n2025-02-01: 120 kWh"
            fileManager.saveToFile(content, "$serviceKey.txt")
            val result = fileManager.readHistory(serviceKey)
            assertThat(result).isEqualTo(content)    // Проверяет содержимое. Зачем? Проверяют, соответствует ли реальное поведение ожидаемому.
        }
    }

    // ========== saveToFile ==========
    @Suppress("HardcodedStringLiteral")
    @Test
    fun saveToFile_createsFileWithContent() {
        val fileName = "test_save.txt"
        val content = "Hello, World!"

        val result = fileManager.saveToFile(content, fileName)

        assertThat(result).isTrue()   // // Проверяет, что метод saveToFile вернул true (успех)
        val savedFile = File(context.filesDir, context.getString(com.github.misham72.communalpayments.R.string.history))
            .resolve(fileName)
        assertThat(savedFile).exists()   // // проверяет, что файл создался
        assertThat(savedFile.readText()).isEqualTo(content)   // Зачем? Проверяют, соответствует ли реальное поведение ожидаемому.
    }

    // ========== appendRecord ==========
    @Suppress("HardcodedStringLiteral")
    @Test
    fun appendRecord_createsFileWithFirstRecord() {
        runBlocking {   // runBlocking { ... } для тестирования suspend-функций, зачем? Превращает корутину в обычную блокирующую операцию (для тестов)
            val serviceKey = "water"
            val record = "2025-04-01: 10 m³"
            fileManager.appendRecord(serviceKey, record)
            val file = File(context.filesDir, context.getString(com.github.misham72.communalpayments.R.string.history))
                .resolve("$serviceKey.txt")
            assertThat(file).exists()   // // проверяет, что файл создался
            assertThat(file.readText()).isEqualTo(record)    // Зачем? Проверяют, соответствует ли реальное поведение ожидаемому.
        }
    }

    @Suppress("HardcodedStringLiteral")
    @Test
    fun appendRecord_addsNewRecordAtTop() {
        // runBlocking { } – только если тестируемый метод является suspend fun. Если нет – runBlocking не нужен.
        runBlocking {   // runBlocking { ... } для тестирования suspend-функций, зачем? Превращает корутину в обычную блокирующую операцию (для тестов).
            val serviceKey = "gas"
            val first = "2025-03-01: 50 m³"
            val second = "2025-04-01: 60 m³"
            fileManager.appendRecord(serviceKey, first)
            fileManager.appendRecord(serviceKey, second)
            val file = File(context.filesDir, context.getString(com.github.misham72.communalpayments.R.string.history))
                .resolve("$serviceKey.txt")
            val expected = "$second\n***\n$first"   // ожидаемый формат: новая запись сверху, разделитель, старая
            assertThat(file.readText()).isEqualTo(expected)   // Зачем? Проверяют, соответствует ли реальное поведение ожидаемому.
        }
    }
}