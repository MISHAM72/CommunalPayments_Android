package com.github.misham72.communalpayments.presentation.screen.screens.internet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.userclasses.Internet


@Composable
fun DisplayInternetScreen() {
    val context = LocalContext.current
    val newInternet = remember { Internet(context) }
    val fileManager = remember { FileManager(context) }
    var newInternetData by remember { mutableStateOf<Internet.InternetData?>(null) }

    /**создай переменную "newInternetData", которая:
    - Может менять значение (var)
    - Запоминает значение между перерисовками экрана (remember)
    - Автоматически обновляет экран при изменении (mutableStateOf)
    - Может хранить либо данные об интернете, либо ничего (InternetData?)
    - Изначально ничего не хранит (null)*/


    //  1. Объявление состояния для полей ввода
    var dayOfPayment by remember { mutableStateOf("") } // День месяца (1-31)
    var periodMonths by remember { mutableStateOf("") } // Период в месяцах (1, 3, 12)
    var tariff by remember { mutableStateOf("") } // Тариф в рублях
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        // 2. Поля ввода, связанные с состояниями
        OutlinedTextField(
            value = dayOfPayment, // ← ОТОБРАЖАЕТ текущее значение
            onValueChange = { dayOfPayment = it },  // ← ПРИНИМАЕТ ВВОД (самый важный момент!)
            label = { Text(stringResource(R.string.day_of_payment_label)) }, modifier = Modifier.fillMaxWidth(),
        )
        // Поле для ввода периода в месяцах
        OutlinedTextField(
            value = periodMonths, onValueChange = { periodMonths = it }, label = { Text(stringResource(R.string.period_months_label)) }, modifier = Modifier.fillMaxWidth()
        )
        // Поле для ввода тарифа
        OutlinedTextField(
            value = tariff, onValueChange = { tariff = it }, label = { Text(stringResource(R.string.tariff_label)) }, modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                // 1. ПРЕОБРАЗОВАНИЕ строк в числа
                val paymentDay = dayOfPayment.toIntOrNull() ?: 0
                val period = periodMonths.toIntOrNull() ?: 0
                val tariffValue = tariff.toDoubleOrNull() ?: 0.0

                //
                // ПЕРЕДАЧА в класс Internet И ИСПОЛЬЗОВАНИЕ РЕЗУЛЬТАТА
                val result = newInternet.collectInternetData(paymentDay, period, tariffValue)

                // 3. ПРОВЕРКА: если result == null (ошибка валидации), прекращаем выполнение
                if (result == null) {
                    // Можно показать сообщение об ошибке пользователю
                    // Например: "Проверьте введенные данные!"
                    return@Button
                }

                // ВМЕСТО ЭТОГО ПРОСТО ИСПОЛЬЗУЕМ result:
                newInternetData = result  // result уже содержит ВСЕ рассчитанные данные

                // 5. СОХРАНЕНИЕ (передаем result, а не newInternetData)
                newInternet.saveInternetData(data = result)  // Сохраняем данные
            },
            modifier = Modifier.fillMaxWidth(),
            // enabled = dayOfPayment.isNotEmpty() && periodMonths.isNotEmpty() && tariff.isNotEmpty(),
            content = {
                Text(stringResource(R.string.calculate_and_save))
            })
    }

    // Отображение результатов
    if (newInternetData != null) {
        val data = newInternetData ?: return

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.result_internet), fontWeight = FontWeight.Bold)

                Text(stringResource(R.string.date, fileManager.getCurrentDateTime()))

                Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.previous_payment_label, data.previousPayment), fontWeight = FontWeight.Bold, color = Color(0xFF006400))

                Text(stringResource(R.string.days_from_payment_label, data.daysFromPayment), fontWeight = FontWeight.Bold, color = Color(0xFF006400))

                //Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.next_payment_label, data.nextPayment), fontWeight = FontWeight.Bold, color = Color.Red)
                Text(stringResource(R.string.days_until_payment_label, data.daysUntilPayment), fontWeight = FontWeight.Bold, color = Color.Red)
                Text(
                    stringResource(R.string.tariff_label, data.priceTariff), fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
