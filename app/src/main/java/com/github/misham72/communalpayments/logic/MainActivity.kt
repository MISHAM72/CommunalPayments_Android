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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }

    val context = LocalContext.current  // ← вызов контента
    val electricityApp = remember { Electricity(context) } // Передаем context


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

                val result = electricityApp.calculate(current, previous, tariffValue)
                consumption = "%.2f кВт·ч".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
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
                Text("К оплате: $payment")
                Text("Дата: ${electricityApp.getCurrentDateTime()}")
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
    val waterApp = remember { Water(context) } // Передаем context

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

                val result = waterApp.calculate(current, previous, tariffValue)
                consumption = "%.2f куб.м".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
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
                Text("К оплате: $payment")
                Text("Дата: ${waterApp.getCurrentDateTime()}")
            }
        }
    }
}

@Composable
fun GasScreen() {
    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }
    val context = LocalContext.current  // ← вызов контента
    val gasApp = remember { Gas(context) } // Передаем context

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

                val result = gasApp.calculate(current, previous, tariffValue)
                consumption = "%.2f куб.м".format(result.consumption)
                payment = "%.2f руб.".format(result.payment)
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
                Text("К оплате: $payment")
                Text("Дата: ${gasApp.getCurrentDateTime()}")

            }
        }
    }
}


// КАЛЬКУЛЯТОР МТС

@Composable
fun ZONTScreen() {
    val context = LocalContext.current
    val zontApp = remember { ZONT(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var zontData by remember { mutableStateOf<ZONT.ZONTData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                zontData = zontApp.calculateZONTData()
                zontApp.saveZONTData(
                    zontData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                ) // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (zontData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результат по  ZONT:", fontWeight = FontWeight.Bold)
                    Text("Предыдущая оплата: ${zontData!!.previousPayment}")
                    Text("Следующая оплата: ${zontData!!.nextPayment}")
                    Text("Оплата была: ${zontData!!.daysFromPayment} дней назад.")
                    Text("След. оплата через: ${zontData!!.daysUntilPayment} дней")
                    Text("Стоимость тарифа: ${zontData!!.priceTariff} руб.")
                    Text("Дата: ${fileManager.getCurrentDateTime()}") // ← ИСПОЛЬЗУЕМ FileManager

                }
            }
        }
    }
}

@Composable
fun InternetScreen() {
    val context = LocalContext.current
    val internetApp = remember { Internet(context) }
    var internetData by remember { mutableStateOf<Internet.InternetData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                internetData = internetApp.calculateInternetData()
                internetApp.saveInternetData(
                    internetData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                )  // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (internetData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Internet:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${internetData!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${internetData!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${internetData!!.priceTariff} руб.")
                    Text("Следующая оплата: ${internetData!!.nextPayment}")
                    Text("Предыдущая оплата: ${internetData!!.previousPayment}")
                    Text("Дата расчета: ${internetData!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun MTSScreen() {
    val context = LocalContext.current
    val mtsApp = remember { MTS(context) }
    var mtsData by remember { mutableStateOf<MTS.MTSData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                mtsData = mtsApp.calculateMTSData()
                mtsApp.saveMTSData(
                    mtsData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                )  // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (mtsData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты МТС:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${mtsData!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${mtsData!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${mtsData!!.priceTariff} руб.")
                    Text("Следующая оплата: ${mtsData!!.nextPayment}")
                    Text("Предыдущая оплата: ${mtsData!!.previousPayment}")
                    Text("Дата расчета: ${mtsData!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun TinkoffScreen() {
    val context = LocalContext.current
    val tinkoffApp = remember { Tinkoff(context) }
    var tinkoffData by remember { mutableStateOf<Tinkoff.TinkoffData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                tinkoffData = tinkoffApp.calculateTinkoffData()
                tinkoffApp.saveTinkoffData(
                    tinkoffData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                ) // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (tinkoffData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${tinkoffData!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${tinkoffData!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${tinkoffData!!.priceTariff} руб.")
                    Text("Следующая оплата: ${tinkoffData!!.nextPayment}")
                    Text("Предыдущая оплата: ${tinkoffData!!.previousPayment}")
                    Text("Дата расчета: ${tinkoffData!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun GarbageScreen() {
    val context = LocalContext.current
    val garbageApp = remember { Garbage(context) }
    var garbageData by remember { mutableStateOf<Garbage.GarbageData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                garbageData = garbageApp.calculateGarbageData()
                garbageApp.saveGarbageData(
                    garbageData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                ) // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (garbageData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Мусор:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${garbageData!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${garbageData!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${garbageData!!.priceTariff} руб.")
                    Text("Следующая оплата: ${garbageData!!.nextPayment}")
                    Text("Предыдущая оплата: ${garbageData!!.previousPayment}")
                    Text("Дата расчета: ${garbageData!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun TaxesScreen() {
    val context = LocalContext.current
    val taxesApp = remember { Taxes(context) }
    var taxesData by remember { mutableStateOf<Taxes.TaxesData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                taxesData = taxesApp.calculateTaxesData()
                taxesApp.saveTaxesData(
                    taxesData!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                )  // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (taxesData != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${taxesData!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${taxesData!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${taxesData!!.priceTariff} руб.")
                    Text("Следующая оплата: ${taxesData!!.nextPayment}")
                    Text("Предыдущая оплата: ${taxesData!!.previousPayment}")
                    Text("Дата расчета: ${taxesData!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun TroykaScreen() {
    val accessToSystemFiles = LocalContext.current
    val troykaCardManager = remember { Troyka(accessToSystemFiles) }
    var troykaCardManagerResult by remember { mutableStateOf<Troyka.TroykaData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                troykaCardManagerResult = troykaCardManager.calculateTroykaData()
                troykaCardManager.saveTroykaData(
                    troykaCardManagerResult!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                ) // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Рассчитать и Сохранить ")
        }

        if (troykaCardManagerResult != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тройка:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${troykaCardManagerResult!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${troykaCardManagerResult!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${troykaCardManagerResult!!.priceTariff} руб.")
                    Text("Следующая оплата: ${troykaCardManagerResult!!.nextPayment}")
                    Text("Предыдущая оплата: ${troykaCardManagerResult!!.previousPayment}")
                    Text("Дата расчета: ${troykaCardManagerResult!!.formattedDateTime}")
                }
            }
        }
    }
}

@Composable
fun OsagoScreen() {
    val accessToSystemFiles = LocalContext.current
    val osagoCardManager = remember { Osago(accessToSystemFiles) }
    var osagoCardManagerResult by remember { mutableStateOf<Osago.OsagoData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                osagoCardManagerResult = osagoCardManager.calculateOsagoData()
                osagoCardManager.saveOsagoData(
                    osagoCardManagerResult!!, "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>"
                )  // ← цветной статус
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Сохранить и Рассчитать")
        }

        if (osagoCardManagerResult != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Результаты Тинькоф:", fontWeight = FontWeight.Bold)
                    Text("Оплата через: ${osagoCardManagerResult!!.daysUntilPayment} дней")
                    Text("С момента оплаты прошло: ${osagoCardManagerResult!!.daysFromPayment} дней")
                    Text("Стоимость тарифа: ${osagoCardManagerResult!!.priceTariff} руб.")
                    Text("Следующая оплата: ${osagoCardManagerResult!!.nextPayment}")
                    Text("Предыдущая оплата: ${osagoCardManagerResult!!.previousPayment}")
                    Text("Дата расчета: ${osagoCardManagerResult!!.formattedDateTime}")
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
