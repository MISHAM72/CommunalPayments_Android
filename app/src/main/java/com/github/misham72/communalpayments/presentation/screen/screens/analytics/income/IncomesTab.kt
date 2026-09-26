package com.github.misham72.communalpayments.presentation.screen.screens.analytics.income

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.IncomeViewModelFactory
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.Period
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModel
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.BankingTabSwitcher
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.MonthSelector
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.chartColors
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.monthName
import com.github.misham72.communalpayments.presentation.utils.nameRes

// ---------- Вкладка доходов ----------
@Composable
fun IncomesTab(factory: IncomeViewModelFactory) {
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
                text = uiState.error ?: stringResource(R.string.error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        uiState.summary != null -> {
            val summary = uiState.summary ?: return
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (uiState.period == Period.Year) {
                                    stringResource(R.string.total_income, uiState.selectedYear)
                                } else {
                                    stringResource(
                                        R.string.total_income_month,
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
                                animationSpec = tween(durationMillis = 900, delayMillis = index * 80)
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
                                        text = stringResource(category.nameRes()).take(1),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = barColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
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
        AlertDialog(
            onDismissRequest = { showDeleteSourceConfirm = null },
            title = { Text(stringResource(R.string.remove_all_incomes, source)) },
            text = { Text(stringResource(R.string.this_action_is_irreversible)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllRecordsBySource(source)
                    showDeleteSourceConfirm = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSourceConfirm = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
