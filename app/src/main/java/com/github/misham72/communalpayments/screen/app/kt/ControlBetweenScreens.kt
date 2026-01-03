package com.github.misham72.communalpayments.screen.app.kt

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.screen.components.tabs.ServiceTab
import com.github.misham72.communalpayments.screen.components.history.SimpleHistoryScreen
import com.github.misham72.communalpayments.screen.navigation.getListInitialScreen


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
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.app_title), fontSize = 24.sp, fontWeight = FontWeight.Bold,
                )
            }

            // Горизонтальные вкладки с иконками
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                services.forEachIndexed { index, service ->
                    ServiceTab(
                        service = service, isSelected = selectedService == index, onClick = { selectedService = index })

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
                painter = painterResource(R.drawable.night),
                contentDescription = stringResource(R.string.summer_night), // 2. ДЛЯ СЛЕПЫХ. Приложение не пройдет проверку доступности Могут заблокировать в Google Play. Слепые не поймут что на фото contentScale = ContentScale.FillWidth,

                modifier = Modifier    // ← растягивает фото по ширине 3. КАК РАСТЯГИВАТЬ
                    .fillMaxWidth()    //. ШИРИНА = ВЕСЬ ЭКРАН   Убирает белые полосы по бокам //.height(150.dp) //Если хочешь точно задать размер фото.Для одинаковых по высоте элементов.Когда не нужно растягивать на все пространство
                    .weight(1f)  // ← делит пространство пополам. ВЫСОТА = ВСЕ СВОБОДНОЕ МЕСТО

            )
            Button(
                onClick = { showHistory = true }, modifier = Modifier.fillMaxWidth()

            ) {
                Text(stringResource(R.string.History))
            }
        }
    }
}
