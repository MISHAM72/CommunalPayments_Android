package com.github.misham72.communalpayments.logic


// ИМПОРТЫ ДЛЯ ЦВЕТОВ И ФОНОВ


import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.github.misham72.communalpayments.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommunalPaymentsTheme {
                ControlBetweenScreens()
            }
        }
    }
}

data class InitialScreen(
    val icon: String,
    val name: String,
    val account: String,
    val fileKey: String,
    val screen: @Composable () -> Unit// ← "инструкция как нарисовать экран"
)

@Composable
fun getListInitialScreen(): List<InitialScreen> { // "Дай мне список всех услуг"
    return remember { // "Запомни этот список"
        listOf(// "Состоящий из:"
            InitialScreen(// "Услуги Электричество"
                "⚡",
                "Электричество",
                "ЛС: 2324 0001 3040",
                "electricity",
                { ElectricityScreen() }),
            InitialScreen("🔥", "Газ", "ЛС: 1230 0102 5113", "gas", { GasScreen() }),
            InitialScreen("💧", "Вода", "ЛС: 000 007 894", "water", { WaterScreen() }),
            InitialScreen("🌡️", "ZONT", "тел. +7(910) 133-00-85", "zont", { ZONTScreen() }),
            InitialScreen("📶", "Интернет", "ЛС: 2300 0343 3205", "internet", { InternetScreen() }),
            InitialScreen("📞", "МТС", "тел. +7(918) 48-48-989", "mts", { MTSScreen() }),
            InitialScreen("📲", "Тинькоф", "тел. +7(995) 00-585-44", "tinkof", { TinkoffScreen() }),
            InitialScreen("🗑️", "Мусор", "ЛС: 210 1010 8366", "garbage", { GarbageScreen() }),
            InitialScreen("💰", "Налоги ИП", "ИНН: 2323 0478 5694", "taxes", { TaxesScreen() }),
            InitialScreen("🚇", "Тройка", "4874 701 1", "troyka", { TroykaScreen() }),
            InitialScreen("🚗", "ОСАГО полис", "№ XXX 0574 944 292", "osago", { OsagoScreen() })
        )
    }
}

@Composable
fun ServiceTab(
    service: InitialScreen, isSelected: Boolean, onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF2196F3) else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = service.icon, fontSize = 18.sp, color = textColor
        )
    }
}

@Composable

fun ControlBetweenScreens() {
    var selectedService by remember { mutableIntStateOf(0) }
    var showHistory by remember { mutableStateOf(false) }
    val services = getListInitialScreen()

    if (showHistory) {
        SimpleHistoryScreen(
            onBack = { showHistory = false },


            initialService = services[selectedService].fileKey  // ← ВОТ ЭТА СТРОКА
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Коммунальные платежи", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                )

            }

            // Горизонтальные вкладки с иконками
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                services.forEachIndexed { index, service ->
                    ServiceTab(
                        service = service,
                        isSelected = selectedService == index,
                        onClick = { selectedService = index })

                }
            }
            // ЗАМЕНЯЕМ весь старый when (0 -> Row, 1 -> Row, и т.д.) на этот код:

            val service = services[selectedService]
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(service.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (service.account.isNotEmpty()) {
                    Text(service.account, color = Color.Red)
                }
            }

            // Преобразование в ключ для логики

            services[selectedService].screen()  // ← ВОТ ЭТА СТРОКА для покзза экрана


            Image(
                painter = painterResource(R.drawable.night),// 1. ГДЕ ФОТО
                contentDescription = "Фото 4", // 2. ДЛЯ СЛЕПЫХ. Приложение не пройдет проверку доступности Могут заблокировать в Google Play. Слепые не поймут что на фото
                contentScale = ContentScale.FillWidth,  // ← растягивает фото по ширине// 3. КАК РАСТЯГИВАТЬ
                modifier = Modifier
                    .fillMaxWidth()  //. ШИРИНА = ВЕСЬ ЭКРАН   Убирает белые полосы по бокам
                    //.height(150.dp) //Если хочешь точно задать размер фото.Для одинаковых по высоте элементов.Когда не нужно растягивать на все пространство
                    .weight(1f)  // ← делит пространство пополам. ВЫСОТА = ВСЕ СВОБОДНОЕ МЕСТО

            )
            Button(
                onClick = { showHistory = true }, modifier = Modifier.fillMaxWidth()

            ) {
                Text("История")
            }
        }
    }
}

//   Garbage  GarbageScreen
@Composable
fun ElectricityScreen() {
    val context = LocalContext.current  // ← вызов контента
    val newElectricity = remember { Electricity(context) }
    val fileManager = remember { FileManager(context) }

    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }


    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {

        OutlinedTextField(
            value = currentReading,
            onValueChange = { currentReading = it },
            label = { Text("Текущие показания") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = previousReading,
            onValueChange = { previousReading = it },
            label = { Text("Предыдущие показания") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tariff,
            onValueChange = { tariff = it },
            label = { Text("Тариф (руб/кВт·ч)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val current = currentReading.toDoubleOrNull() ?: 0.0
                val previous = previousReading.toDoubleOrNull() ?: 0.0
                val tariffValue = tariff.toDoubleOrNull() ?: 0.0

                val result = newElectricity.calculateElectricityData(current, previous, tariffValue)
                consumption = "%.2f кВт·ч".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
                newElectricity.saveElectricityData(result)
                // Сохранение теперь происходит автоматически в calculate()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Рассчитать и сохранить")
        }

        // ... отображение результатов ...
    }


    if (consumption.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Результат по свету:", fontWeight = FontWeight.Bold)
                Text("Расход: $consumption")
                //Text("К оплате: $payment")
                Text(
                    "К оплате: - $payment рублей..",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text("Дата: ${fileManager.getCurrentDateTime()}") // ← ИСПОЛЬЗУЕМ fileManager
            }
        }
    }
}

@Composable
fun WaterScreen() {
    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }

    val context = LocalContext.current  // ← вызов контента
    val newWater = remember { Water(context) } // Передаем context
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ ЭТУ СТРОКУ

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        OutlinedTextField(
            value = currentReading,
            onValueChange = { currentReading = it },
            label = { Text("Текущие показания (куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = previousReading,
            onValueChange = { previousReading = it },
            label = { Text("Предыдущие показания (куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tariff,
            onValueChange = { tariff = it },
            label = { Text("Тариф (руб/куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val current = currentReading.toDoubleOrNull() ?: 0.0
                val previous = previousReading.toDoubleOrNull() ?: 0.0
                val tariffValue = tariff.toDoubleOrNull() ?: 0.0

                val result = newWater.calculateWaterData(current, previous, tariffValue)
                consumption = "%.2f куб.м".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
                newWater.saveWaterData(result)
                // Сохранение теперь происходит автоматически в calculate()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Рассчитать и сохранить")
        }

    }

    if (consumption.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Результат по воде:", fontWeight = FontWeight.Bold)
                Text("Расход: $consumption")
                Text(
                    "К оплате: - $payment рублей..",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text("Дата: ${fileManager.getCurrentDateTime()}") // ← ИСПОЛЬЗУЕМ fileManager
            }
        }
    }
}

@Composable
fun GasScreen() {
    val context = LocalContext.current  // ← вызов контента
    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }


    val newGas = remember { Gas(context) } // Передаем context
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ ЭТУ СТРОКУ


    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        OutlinedTextField(
            value = currentReading,
            onValueChange = { currentReading = it },
            label = { Text("Текущие показания (куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = previousReading,
            onValueChange = { previousReading = it },
            label = { Text("Предыдущие показания (куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tariff,
            onValueChange = { tariff = it },
            label = { Text("Тариф (руб/куб.м)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {

                val current = currentReading.toDoubleOrNull() ?: 0.0
                val previous = previousReading.toDoubleOrNull() ?: 0.0
                val tariffValue = tariff.toDoubleOrNull() ?: 0.0

                val result = newGas.calculateGasData(current, previous, tariffValue)
                consumption = "%.2f куб.м".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
                newGas.saveGasData(result)

                // Сохранение теперь происходит автоматически в calculate()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Text("Рассчитать и сохранить")
        }

    }


    if (consumption.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Результат по газу:", fontWeight = FontWeight.Bold)
                Text("Расход: $consumption")
                Text(
                    "К оплате: - $payment рублей..",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text("Дата: ${fileManager.getCurrentDateTime()}")

            }
        }
    }
}

@Composable
fun ZONTScreen() {
    val context = LocalContext.current
    val newZONT = remember { ZONT(context) }
    val fileManager = remember { FileManager(context) }
    var newZONTData by remember { mutableStateOf<ZONT.ZONTData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newZONTData = newZONT.calculateZONTData()
                newZONT.saveZONTData(newZONTData!!)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newZONTData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результат по  ZONT:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newZONTData!!.previousPayment}")
                    Text("Следующая оплата: - ${newZONTData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newZONTData!!.daysFromPayment} дней назад.")
                    //Text("След. оплата через: ${newZONTData!!.daysUntilPayment} дней")
                    Text(
                        "След. оплата через: - ${newZONTData!!.daysUntilPayment} дней",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newZONTData!!.priceTariff} руб.")


                }
            }
        }
    }
}

@Composable
fun InternetScreen() {
    val context = LocalContext.current
    val newIntent = remember { Internet(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var newInternetData by remember { mutableStateOf<Internet.InternetData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newInternetData = newIntent.calculateInternetData()
                newIntent.saveInternetData(newInternetData!!)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newInternetData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Internet:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newInternetData!!.previousPayment}")
                    Text("Следующая оплата: - ${newInternetData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newInternetData!!.daysFromPayment} дней назад.")

                    Text(
                        "След. оплата через: - ${newInternetData!!.daysUntilPayment} дней",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newInternetData!!.priceTariff} руб.")
                }
            }
        }
    }
}

@Composable
fun MTSScreen() {
    val context = LocalContext.current
    val newMTS = remember { MTS(context) }
    val fileManager = remember { FileManager(context) }
    var newMTSData by remember { mutableStateOf<MTS.MTSData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newMTSData = newMTS.calculateMTSData()
                newMTS.saveMTSData(newMTSData!!)
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newMTSData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты МТС:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newMTSData!!.previousPayment}")
                    Text("Следующая оплата: - ${newMTSData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newMTSData!!.daysFromPayment} дней назад.")
                    Text(
                        "След. оплата через: - ${newMTSData!!.daysUntilPayment} дней.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newMTSData!!.priceTariff} руб.")


                }
            }
        }
    }
}

@Composable
fun TinkoffScreen() {
    val context = LocalContext.current
    val newTinkoff = remember { Tinkoff(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var newTinkoffData by remember { mutableStateOf<Tinkoff.TinkoffData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newTinkoffData = newTinkoff.calculateTinkoffData()
                newTinkoff.saveTinkoffData(newTinkoffData!!)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newTinkoffData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newTinkoffData!!.previousPayment}")
                    Text("Следующая оплата: - ${newTinkoffData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newTinkoffData!!.daysFromPayment} дней назад.")
                    Text(
                        "След. оплата через: - ${newTinkoffData!!.daysUntilPayment} дней.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО )

                    Text("Стоимость тарифа: - ${newTinkoffData!!.priceTariff} руб.")


                }
            }
        }
    }
}

@Composable
fun GarbageScreen() {
    val context = LocalContext.current
    val newGarbage = remember { Garbage(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var newGarbageData by remember { mutableStateOf<Garbage.GarbageData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newGarbageData = newGarbage.calculateGarbageData()
                newGarbage.saveGarbageData(newGarbageData!!)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newGarbageData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Мусор:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Предыдущая оплата: - ${newGarbageData!!.previousPayment}")
                    Text("Следующая оплата: - ${newGarbageData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Оплата была: - ${newGarbageData!!.daysFromPayment} дней назад.")
                    Text(
                        "След. оплата через: - ${newGarbageData!!.daysUntilPayment} дней.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newGarbageData!!.priceTariff} руб.")


                }
            }
        }
    }
}

@Composable
fun TaxesScreen() {
    val context = LocalContext.current
    val newTaxes = remember { Taxes(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var newTaxesData by remember { mutableStateOf<Taxes.TaxesData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newTaxesData = newTaxes.calculateTaxesData()
                newTaxes.saveTaxesData(newTaxesData!!)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newTaxesData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newTaxesData!!.previousPayment}")
                    Text("Следующая оплата: - ${newTaxesData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newTaxesData!!.daysFromPayment} дней")
                    Text(
                        "След. оплата через: - ${newTaxesData!!.daysUntilPayment} дней.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newTaxesData!!.priceTariff} руб.")



                }
            }
        }
    }
}

@Composable
fun TroykaScreen() {
    val context = LocalContext.current
    val newTroyka = remember { Troyka(context) }
    val fileManager = remember { FileManager(context) }
    var newTroykaData by remember { mutableStateOf<Troyka.TroykaData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = {
            newTroykaData = newTroyka.calculateTroykaData()
            newTroyka.saveTroykaData(newTroykaData!!)
        }
        ) {
            Text("Рассчитать и сохранить")
        }
    }

    if (newTroykaData != null) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Результаты Тройка:", fontWeight = FontWeight.Bold)
                Text("Дата: ${fileManager.getCurrentDateTime()}") // ← ИСПОЛЬЗУЕМ FileManager
                Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                Text("Предыдущая оплата: - ${newTroykaData!!.previousPayment}")
                Text("Следующая оплата: - ${newTroykaData!!.nextPayment}")
                Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                Text("Оплата была: - ${newTroykaData!!.daysFromPayment} дней")
                Text(
                    "След. оплата через: - ${newTroykaData!!.daysUntilPayment} дней.",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                Text("Стоимость тарифа: - ${newTroykaData!!.priceTariff} руб.")
            }
        }
    }
}


@Composable
fun OsagoScreen() {
    val context = LocalContext.current
    val newOsago = remember { Osago(context) }
    val fileManager = remember { FileManager(context) }
    var newOsagoData by remember { mutableStateOf<Osago.OsagoData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newOsagoData = newOsago.calculateOsagoData()
                newOsago.saveOsagoData(newOsagoData!!)
            }, modifier = Modifier.align(Alignment.CenterHorizontally)

        ) {
            Text("Сохранить и Рассчитать")
        }

        if (newOsagoData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Дата: ${fileManager.getCurrentDateTime()}") // ← ИСПОЛЬЗУЕМ FileManager
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Предыдущая оплата: - ${newOsagoData!!.previousPayment}")
                    Text("Следующая оплата: - ${newOsagoData!!.nextPayment}")
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text("Оплата была: - ${newOsagoData!!.daysFromPayment} дней назад.")
                    Text(
                        "След. оплата через: - ${newOsagoData!!.daysUntilPayment} дней.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text("Стоимость тарифа: - ${newOsagoData!!.priceTariff} руб.")
                }
            }
        }
    }
}

@Composable
fun SimpleHistoryScreen(
    onBack: () -> Unit, initialService: String  // ← ПРИНИМАЕМ УСЛУГУ
) {
    val context = LocalContext.current
    var fileContent by remember { mutableStateOf("Загрузка...") }

    var isEditing by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(initialService) }  // ← ИСПОЛЬЗУЕМ ПЕРЕДАННУЮ УСЛУГУ

    val fileManager = remember { FileManager(context) }

    fun loadHistoryData() {
        fileContent = fileManager.loadFromFile(selectedService)
    }

    LaunchedEffect(selectedService) {
        loadHistoryData()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Назад")
        }

        Text("История расчетов", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Выбор услуги
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .horizontalScroll(rememberScrollState()),  // ← ДОБАВЬ ПРОКРУТКУ в истории расчетов это
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                //Ключ: "electricity" (для файлов и логики) Название: "Электричество" (для отображения)
                // FilterChip в истории сравнивает ключи у
                //// 📍 2) СТРОКА В ИСТОРИИ ПОД СЛОВАМИ "ИСТОРИЯ РАСЧЕТОВ"
                //// Источник: список FilterChip в SimpleHistoryScreen
                //// Данные: Берется ВТОРОЙ элемент пары (отображаемое имя)
                "electricity" to "Электричество", // ← ключ : отображаемое имя
                "gas" to "Газ",
                "water" to "Вода",
                "zont" to "ZONT",
                "internet" to "Интернет",
                "mts" to "МТС",
                "tinkoff" to "Тинькоф",
                "garbage" to "Мусор",
                "taxes" to "Налоги ИП",
                "troyka" to "Тройка",
                "осаго" to "Осаго"

            ).forEach { (service, name) ->
                FilterChip(
                    selected = selectedService == service,
                    onClick = { selectedService = service },
                    label = { Text(name) })// ← показывает "Электричество", "Газ" и т.д
            }
        }

        if (isEditing) {
            OutlinedTextField(
                value = fileContent,
                onValueChange = { fileContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    fileManager.editFile(selectedService, fileContent)
                    isEditing = false
                    loadHistoryData()
                }) {
                    Text("Сохранить")
                }

                Button(onClick = {
                    loadHistoryData()
                    isEditing = false
                }) {
                    Text("Отмена")
                }
            }
        } else {
            Card(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)  // ← БЕЛЫЙ ФОН
            ) {
                // ЗАМЕНЯЕМ Text на AndroidView с HTML-поддержкой:
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            text = HtmlCompat.fromHtml(
                                fileContent, HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                            textSize = 16f
                            setTextIsSelectable(true)
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            Button(
                onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Редактировать историю")
            }
        }
    }
}


// ТЕМА - САМАЯ ПОСЛЕДНЯЯ ФУНКЦИЯ
@Composable
fun CommunalPaymentsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
