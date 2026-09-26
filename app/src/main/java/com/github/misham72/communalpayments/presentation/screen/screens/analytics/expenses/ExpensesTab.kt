package com.github.misham72.communalpayments.presentation.screen.screens.analytics.expenses

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ExpensesViewModelFactory
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.ExpensesViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.Period
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.BankingTabSwitcher
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.MonthSelector
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.chartColors
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.monthName
import com.github.misham72.communalpayments.presentation.screen.screens.services.getListInitialScreen


// ---------- Вкладка расходов ----------
@Composable
fun ExpensesTab(factory: ExpensesViewModelFactory, appContainer: AppContainer) {
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
                // 1. Переключатель Год/Месяц
                item {
                    BankingTabSwitcher(
                        tabs = listOf(
                            stringResource(R.string.period_year),
                            stringResource(R.string.period_month)
                        ),
                        selectedIndex = if (uiState.period == Period.Year) 0 else 1,
                        onSelect = {
                            viewModel.setPeriod(if (it == 0) Period.Year else Period.Month)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                // 2. Стрелки месяца (если Месяц)
                if (uiState.period == Period.Month) {
                    item {
                        MonthSelector(
                            month = uiState.selectedMonth,
                            year = uiState.selectedYear,
                            onPrevious = { viewModel.previousMonth() },
                            onNext = { viewModel.nextMonth() }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                // 3. Карточка "Общая сумма"
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (uiState.period == Period.Year) {
                                    stringResource(R.string.total_expenses_summary, uiState.selectedYear)
                                } else {
                                    stringResource(
                                        R.string.total_expenses_summary_month,
                                        monthName(uiState.selectedMonth),
                                        uiState.selectedYear
                                    )
                                },
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 16.sp
                            )
                            Text(
                                text = stringResource(R.string.money_format).format(summary.total),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                // 4. График ← ЭТО ПРОПАЛО
                item {
                    ExpensesChart(summary, allServices)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // 5. Список услуг
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
