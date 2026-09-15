package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ExpensesViewModelFactory
import com.github.misham72.communalpayments.di.IncomeViewModelFactory
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.ExpenseSummary
import com.github.misham72.communalpayments.domain.model.incomes.IncomeCategory
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import com.github.misham72.communalpayments.domain.usecases.GetExpensesUseCase
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.navigation.InitialScreen
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen
import com.github.misham72.communalpayments.presentation.utils.nameRes
import com.github.misham72.communalpayments.presentation.utils.rememberButtonBuckSoundPlayer
import java.io.File
import java.time.Year

private val chartColors = listOf(
    Color(0xFFE91E63), Color(0xFFFFEB3B), Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFF795548), Color(0xFF3F51B5), Color(0xFF8BC34A), Color(0xFFFF5722), Color(0xFF607D8B)
)

@Composable
fun ExpensesScreen(
    onBack: () -> Unit, getExpensesUseCase: GetExpensesUseCase, defaultErrorMessage: String = stringResource(R.string.error), incomeFactory: IncomeViewModelFactory, appContainer: AppContainer
) {
    val allServices = getListInitialScreen(appContainer)
    val expensesFactory = remember(allServices, defaultErrorMessage) {
        ExpensesViewModelFactory(
            useCase = getExpensesUseCase,
            defaultErrorMessage = defaultErrorMessage
        )
    }


    val buttonBuckSound = rememberButtonBuckSoundPlayer()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.Expenses), stringResource(R.string.income))

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                buttonBuckSound?.start()
                onBack()
            }) {
                Text(stringResource(R.string.back))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.Annual_report), fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            BankingTabSwitcher(
                tabs = tabs,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "tabContent"
            ) { tab ->
                when (tab) {
                    0 -> ExpensesTab(expensesFactory, appContainer)
                    1 -> IncomesTab(incomeFactory)
                }
            }
        }
    }
}

@Composable
fun BankingTabSwitcher(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedIndex by animateIntAsState(
        targetValue = selectedIndex,
        animationSpec = tween(durationMillis = 250),
        label = "tabIndex"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val tabWidth = maxWidth / tabs.size

            // Плавающая белая капсула под активной вкладкой
            Box(
                modifier = Modifier
                    .offset { IntOffset((tabWidth * animatedIndex).roundToPx(), 0) }
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSelect(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.SemiBold
                            else FontWeight.Normal,
                            color = if (selectedIndex == index)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ---------- Вкладка расходов ----------
@Composable
private fun ExpensesTab(factory: ExpensesViewModelFactory, appContainer: AppContainer) {
    val viewModel: ExpensesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allServices = getListInitialScreen(appContainer)

    LaunchedEffect(Unit) {
        viewModel.loadSummary(allServices.map { it.fileKey })
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Text(
                text = uiState.error ?: stringResource(R.string.error), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge
            )
        }

        uiState.data != null -> {
            val summary = uiState.data ?: return
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.total_expenses_summary, Year.now().value), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 16.sp
                            )
                            Text(
                                text = stringResource(R.string.money_format).format(summary.total), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                item {
                    ExpensesChart(summary, allServices)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                val services = summary.byService.toList().sortedBy { (key, _) -> allServices.indexOfFirst { it.fileKey == key } }
                if (services.isNotEmpty()) {
                    itemsIndexed(services) { index, (key, total) ->
                        val screen = allServices.find { it.fileKey == key }
                        val displayName = screen?.name ?: key

                        // Эмодзи — всё, что идёт до первой буквы (сам смайл + пробел)
                        val emoji = displayName.takeWhile { !it.isLetter() }.trim().ifBlank { "📊" }
                        // Чистое название без эмодзи
                        val cleanName = displayName.drop(emoji.length).trim().ifBlank { displayName }

                        val percent = if (summary.total > 0) (total / summary.total * 100) else 0.0
                        val barColor = chartColors[index % chartColors.size]
                        // 👇 ВОТ ЭТО ДОБАВЛЯЕМ
                        val progressAnim = remember { Animatable(0f) }
                        LaunchedEffect(percent) {
                            progressAnim.animateTo(
                                targetValue = (percent / 100.0).toFloat().coerceIn(0f, 1f),
                                animationSpec = tween(
                                    durationMillis = 900,
                                    delayMillis = index * 80
                                )
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Аватар с эмодзи
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = barColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cleanName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = UiConstants.PERCENT_FORMAT.format(percent),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = barColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(R.string.money_format).format(total),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // Мини-прогресс-бар
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressAnim.value)
                                                .fillMaxHeight()
                                                .background(
                                                    color = barColor,
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// График расходов (исправленный – с эмодзи и русскими именами)
@Composable
private fun ExpensesChart(
    summary: ExpenseSummary, allServices: List<InitialScreen>
) {
    val services = summary.byService.toList().sortedBy { (key, _) -> allServices.indexOfFirst { it.fileKey == key } }

    val hasData = services.any { it.second > 0 }
    if (services.isEmpty() || !hasData) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_expenses_data))
        }
        return
    }

    val maxTotal = services.maxOfOrNull { it.second } ?: 1.0
    val maxColumnHeight = 200.dp
    var pressedInfo by remember { mutableStateOf<Pair<String, Double>?>(null) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 30.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            services.forEachIndexed { index, (key, total) ->
                val heightFraction = if (maxTotal > 0) (total / maxTotal).toFloat() else 0f
                val barHeight = maxColumnHeight * heightFraction
                val barColor = chartColors[index % chartColors.size]

                val screen = allServices.find { it.fileKey == key }
                val displayName = screen?.name ?: key

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(300.dp)
                        .pointerInput(key) {
                            detectTapGestures(
                                onPress = {
                                    pressedInfo = Pair(displayName, total)
                                    tryAwaitRelease()
                                    pressedInfo = null
                                })
                        }, verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (total > 0) {
                        Text(
                            text = stringResource(R.string.money_format_no_cents).format(total), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(barHeight)
                            .shadow(2.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                                ), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayName.take(8), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.height(16.dp)
                    )
                }
            }
        }

        pressedInfo?.let { (name, total) ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .zIndex(10f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.money_format).format(total), fontSize = 18.sp, color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}

// График доходов по источникам
@Composable
private fun IncomesChart(summary: IncomeSummary) {
    val sources = summary.bySource.toList().sortedBy { (category, _) -> category.order }

    if (sources.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_income_data))
        }
        return
    }

    val maxTotal = sources.maxOfOrNull { it.second } ?: 1.0
    val maxColumnHeight = 200.dp
    var pressedInfo by remember { mutableStateOf<Pair<IncomeCategory, Double>?>(null) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 30.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sources.forEachIndexed { index, (category, total) ->
                val heightFraction = if (maxTotal > 0) (total / maxTotal).toFloat() else 0f
                val barHeight = maxColumnHeight * heightFraction
                val barColor = chartColors[index % chartColors.size]

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(category) {
                            detectTapGestures(
                                onPress = {
                                    pressedInfo = Pair(category, total)
                                    tryAwaitRelease()
                                    pressedInfo = null
                                })
                        }, verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (total > 0) {
                            Text(
                                text = stringResource(R.string.money_format_no_cents).format(total), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(barHeight)
                                .shadow(2.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                                    ), shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(category.nameRes()).take(6),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        if (pressedInfo != null) {
            val pressed = pressedInfo ?: return
            val (category, total) = pressed
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .zIndex(10f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(category.nameRes()), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.money_format).format(total), fontSize = 18.sp, color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}

// ---------- Вкладка доходов ----------
@Composable
private fun IncomesTab(factory: IncomeViewModelFactory) {
    val viewModel: IncomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSourceForEdit by remember { mutableStateOf<String?>(null) }
    var showDeleteSourceConfirm by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadIncome()
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Text(
                text = uiState.error ?: stringResource(R.string.error), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge
            )
        }

        uiState.summary != null -> {
            val summary = uiState.summary ?: return
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.total_income, Year.now().value), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 16.sp
                            )
                            Text(
                                text = stringResource(R.string.money_format).format(summary.total), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                item {
                    IncomesChart(summary)
                }
                val sources = summary.bySource.toList().sortedBy { (category, _) -> category.order }
                if (sources.isNotEmpty()) {
                    items(sources.size) { index ->
                        val (category, total) = sources[index]
                        val percent = if (summary.total > 0) (total / summary.total * 100) else 0.0
                        val barColor = chartColors[index % chartColors.size]
                        val progressAnim = remember { Animatable(0f) }
                        LaunchedEffect(percent) {
                            progressAnim.animateTo(
                                targetValue = (percent / 100.0).toFloat().coerceIn(0f, 1f),
                                animationSpec = tween(
                                    durationMillis = 900,
                                    delayMillis = index * 80
                                )
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Цветной квадратик с первой буквой категории
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)//Цветной квадратик 36×36 dp,
                                        .background(
                                            color = barColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(category.nameRes()).take(1),// внутри — первая буква категории (например, «З» для Зарплаты)
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = barColor// Цвет берётся из barColor с прозрачностью 15%.
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))//Отступ между квадратиком и текстом.

                                Column(modifier = Modifier.weight(1f)) {//Основной блок с текстом. Занимает всё свободное место. Внутри:
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(category.nameRes()),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        // Процент справа от названия
                                        Text(
                                            text = UiConstants.PERCENT_FORMAT.format(percent),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = barColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(R.string.money_format).format(total),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // Мини-прогресс-бар доли
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressAnim.value)
                                                .fillMaxHeight()
                                                .background(
                                                    color = barColor,
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = { selectedSourceForEdit = category.name },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { showDeleteSourceConfirm = category.name },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Text(stringResource(R.string.add_income))
                    }
                }
            }
        }
    }

    // Диалог добавления дохода
    if (showAddDialog) {
        AddIncomeDialog(onDismiss = { showAddDialog = false }, onAdd = { source, amount ->
            viewModel.addIncome(source, amount)
            showAddDialog = false
        })
    }

    // Диалог записей по источнику
    if (selectedSourceForEdit != null) {
        val source = selectedSourceForEdit ?: return
        val records = viewModel.recordsBySource.value[source] ?: emptyList()
        SourceRecordsDialog(
            source = source,
            records = records,
            viewModel = viewModel,
            onDismiss = {
                selectedSourceForEdit = null
                viewModel.loadIncome()
            })
    }

    // Диалог подтверждения удаления всех записей источника
    if (showDeleteSourceConfirm != null) {
        val source = showDeleteSourceConfirm ?: return
        AlertDialog(onDismissRequest = { showDeleteSourceConfirm = null }, title = { Text(stringResource(R.string.remove_all_incomes, source)) }, text = { Text(stringResource(R.string.this_action_is_irreversible)) }, confirmButton = {
            TextButton(onClick = {
                viewModel.deleteAllRecordsBySource(source)
                showDeleteSourceConfirm = null
            }) {
                Text(stringResource(R.string.delete))
            }
        }, dismissButton = {
            TextButton(onClick = { showDeleteSourceConfirm = null }) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIncomeDialog(
    onDismiss: () -> Unit, onAdd: (source: String, amount: Double) -> Unit
) {
    val categories = IncomeCategory.entries
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.add_income)) }, text = {
        Column {
            OutlinedTextField(
                value = amountText, onValueChange = { amountText = it }, label = { Text(stringResource(R.string.amount)) }, singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.source), style = MaterialTheme.typography.labelMedium)
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = category }
                        .padding(vertical = 4.dp)) {
                    RadioButton(
                        selected = selectedCategory == category, onClick = { selectedCategory = category })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(category.nameRes()))
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = {
            val amount = amountText.toDoubleOrNull()
            if (amount != null && amount > 0) {
                onAdd(selectedCategory.name, amount)  // ← передаём английский ключ
            }
        }) {
            Text(stringResource(R.string.add))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    })
}

@Composable
private fun SourceRecordsDialog(
    source: String,
    records: List<IncomeRecord>,
    viewModel: IncomeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var editIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<IncomeRecord?>(null) }
    var recordForAttach by remember { mutableStateOf<IncomeRecord?>(null) }
    var attachmentToDelete by remember { mutableStateOf<Pair<IncomeRecord, Attachment>?>(null) }
    // 📎 — любой файл
    val attachLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val record = recordForAttach
        if (uri != null && record != null) {
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val fileName = getFileNameFromUri(context, uri) ?: "file_${System.currentTimeMillis()}"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null && bytes.isNotEmpty()) {
                viewModel.attachAttachment(record, bytes, fileName, mimeType)
            }
        }
        recordForAttach = null
    }

    // 📷 — камера
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        val record = recordForAttach
        if (bitmap != null && record != null) {
            val tempFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            val bytes = tempFile.readBytes()
            tempFile.delete()
            if (bytes.isNotEmpty()) {
                viewModel.attachAttachment(
                    record, bytes,
                    "photo_${System.currentTimeMillis()}.jpg",
                    "image/jpeg"
                )
            }
        }
        recordForAttach = null
    }

    // 🖼 — галерея
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val record = recordForAttach
        if (uri != null && record != null) {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = getFileNameFromUri(context, uri) ?: "image_${System.currentTimeMillis()}"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null && bytes.isNotEmpty()) {
                viewModel.attachAttachment(record, bytes, fileName, mimeType)
            }
        }
        recordForAttach = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.income_dialog_title, source)) },
        text = {
            if (records.isEmpty()) {
                Text(stringResource(R.string.no_records))
            } else {
                LazyColumn {
                    items(records.size) { index ->
                        val record = records[index]
                        val isEditing = editIndex == index

                        if (isEditing) {
                            var editSource by remember { mutableStateOf(record.source) }
                            var editAmount by remember { mutableStateOf(record.amount.toString()) }

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                OutlinedTextField(
                                    value = editSource,
                                    onValueChange = { editSource = it },
                                    label = { Text(stringResource(R.string.source)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = editAmount,
                                    onValueChange = { editAmount = it },
                                    label = { Text(stringResource(R.string.amount)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(onClick = {
                                        val newAmount = editAmount.toDoubleOrNull()
                                        if (newAmount != null && newAmount > 0 && editSource.isNotBlank()) {
                                            // ✅ сохраняем вложения при редактировании
                                            val newRecord = record.copy(
                                                amount = newAmount,
                                                source = editSource
                                            )
                                            viewModel.updateRecord(record, newRecord)
                                            editIndex = null
                                        }
                                    }) {
                                        Text(stringResource(R.string.save))
                                    }
                                    TextButton(onClick = { editIndex = null }) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(R.string.money_format).format(record.amount),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = record.date.toString(),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            attachLauncher.launch("*/*")
                                        }) {
                                            Icon(
                                                Icons.Default.AttachFile,
                                                contentDescription = stringResource(R.string.attach_file),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            cameraLauncher.launch(null)
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoCamera,
                                                contentDescription = stringResource(R.string.open_camera),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            galleryLauncher.launch("image/*")
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoLibrary,
                                                contentDescription = stringResource(R.string.open_gallery),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { editIndex = index }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.editing),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { showDeleteConfirm = record }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Вложения
                                record.attachments.forEach { attachment ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📎 ${attachment.name}",
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    openIncomeAttachment(context, attachment, viewModel)
                                                },
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                attachmentToDelete = record to attachment
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
//✅ Это диалог подтверждения удаления записи дохода
    val deleteRecord = showDeleteConfirm
    if (deleteRecord != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text(stringResource(R.string.delete_record)) },
            text = { Text(stringResource(R.string.delete_confirm_amount).format(deleteRecord.amount)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRecord(deleteRecord)
                    showDeleteConfirm = null
                    onDismiss()
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    // Диалог подтверждения удаления вложения
    val pairToDelete = attachmentToDelete
    if (pairToDelete != null) {
        val (record, attachment) = pairToDelete
        AlertDialog(
            onDismissRequest = { attachmentToDelete = null },
            title = { Text(stringResource(R.string.delete_attachment)) },
            text = { Text(stringResource(R.string.delete_attachment_confirm, attachment.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeAttachment(record, attachment)
                    attachmentToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { attachmentToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
// ─── Вспомогательные функции (добавь в конец файла) ───

private fun openIncomeAttachment(
    context: android.content.Context,
    attachment: Attachment,
    viewModel: IncomeViewModel
) {
    val file = viewModel.getAttachmentFile(attachment.path) ?: return
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, attachment.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, attachment.name))
}

private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) it.getString(nameIndex) else null
    }
}
