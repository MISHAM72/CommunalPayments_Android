package com.github.misham72.communalpayments.data.local

import android.content.Context
import com.github.misham72.communalpayments.R
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileManager(private val context: Context) {

    // ✅ НОВЫЙ МЕТОД: Чтение истории
    fun readHistory(serviceKey: String): String {
        /** ✅Таким образом, эта функция подготавливает данные для визуального просмотра пользователем — показывает недавние платежи по выбранной услуге, собранные из отдельных файлов.*/
        return try { // ✅Эта функция возвращает результат try-блока, а если возникнет исключение, вернётся строка с ошибкой.

            val directory = File(context.filesDir, context.getString(R.string.history)) // ✅1 - создаёт объект File, представляющий папку history во внутреннем хранилище приложения.


            if (!directory.exists()) {   // ✅2 - Проверка существования папки:
                return context.getString(R.string.empty_history_calculation)
            }
            val files = directory.listFiles { file ->   // ✅3 - Это фильтрация: оставляем только файлы, чьи имена начинаются с serviceKey и заканчиваются на .txt.
                file.name.startsWith(serviceKey) && file.name.endsWith(".txt")
            }
            if (files == null || files.isEmpty()) {
                return context.getString(R.string.empty_history_calculation)   // ✅Нужно добавить проверку на null. Например, после получения списка файлов:
            }
            files.sortByDescending { it.lastModified() }   // ✅4 - Сортировка: files.sortByDescending { it.lastModified() } — сортирует по дате последнего изменения по убыванию (новые сверху).

            val maxFiles = minOf(files.size, 10)    //✅ 5 - Определение количества файлов для чтения: val maxFiles = minOf(files.size, 10) — берём не больше 10.

            val content = StringBuilder()   //✅ 6 - Создаём StringBuilder для накопления содержимого: val content = StringBuilder().

            for (i in 0 until maxFiles) {   //✅ 7 - Цикл for (i in 0 until maxFiles): для каждого из первых maxFiles файлов (самых новых) читаем содержимое files[i].readText() и добавляем в content, затем добавляем разделитель \n\n---\n\n.
                content.append(files[i].readText())   // ✅Склеивает содержимое этих файлов в одну строку, добавляя между ними разделитель --- для удобочитаемости.
                content.append("\n***\n")
            }
            content.toString()   // ✅8 - После цикла возвращаем content.toString().

        } catch (_: Exception) {
            context.getString(R.string.error_reading_file)
        }
    }

    // ✅ НОВЫЙ МЕТОД: Редактирование файла
    fun editFile(serviceKey: String, newContent: String): Boolean {
        return try {
            val directory = File(context.filesDir, context.getString(R.string.history)) //* * 🔴Теперь у вас есть «адрес», где лежат файлы истории.
            if (!directory.exists()) {
                return false
            }
            val files = directory.listFiles { file -> //🔴 — метод просит операционную систему заглянуть в папку directory и перебрать всё, что там лежит.
                file.name.startsWith(serviceKey) && file.name.endsWith(".txt") // 🔴Условие работает как сито: для каждого найденного файла проверяется, начинается ли его имя с serviceKey и заканчивается ли на .txt.
            }
            if (files == null || files.isEmpty()) return false
            files.sortByDescending { it.lastModified() }   // 🔴Сортируем файлы по дате последнего изменения, чтобы самый свежий (последний расчёт) оказался первым:
            val latestFile = files[0]   // 🔴Берём самый свежий файл:
            val records = newContent.split("\n***\n")//.filter { it.isNotBlank() }  // 🔴Разделяем newContent на отдельные записи с помощью разделителя ***.
            if (records.isEmpty()) return false
            val lastRecord = records.first()   //🔴 Извлекаем последнюю запись — это та, которую пользователь мог изменить
            latestFile.writeText(lastRecord)  //🔴 Перезаписываем последний файл этой записью:
            true
        } catch (_: Exception) {
            false
        }
    }

    // ✅ Общий метод сохранения
    fun saveToFile(content: String, fileName: String): Boolean {
        return try {
            val directory = File(context.filesDir, context.getString(R.string.history))
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            FileWriter(file).use { writer ->
                writer.write(content)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}