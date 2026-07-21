package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.income.filemanager.IncomeFileManager
import com.github.misham72.communalpayments.data.local.income.parser.IncomeParser
import com.github.misham72.communalpayments.domain.model.ExpenseSummary
import com.github.misham72.communalpayments.domain.model.IncomeCategory
import com.github.misham72.communalpayments.domain.model.IncomeRecord
import com.github.misham72.communalpayments.domain.model.IncomeSummary
import com.github.misham72.communalpayments.domain.userclasses.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.presentation.screen.navigation.InitialScreen
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen
import com.github.misham72.communalpayments.presentation.utils.nameRes
import com.github.misham72.communalpayments.presentation.utils.rememberButtonBuckSoundPlayer
import kotlinx.coroutines.launch
import java.time.Year

private val chartColors = listOf(
    Color(0xFFE91E63), Color(0xFFFFEB3B), Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFF795548), Color(0xFF3F51B5), Color(0xFF8BC34A), Color(0xFFFF5722), Color(0xFF607D8B)
)

@Composable
fun AllServicesSummaryScreen(
    onBack: () -> Unit, getAllServicesYearlySummaryUseCase: GetAllServicesYearlySummaryUseCase, defaultErrorMessage: String = stringResource(R.string.error), incomeFactory: IncomeViewModelFactory
) {
    val allServices = getListInitialScreen()
    val expensesFactory = remember(allServices, defaultErrorMessage) {
        AllServicesSummaryViewModelFactory(
            useCase = getAllServicesYearlySummaryUseCase, defaultErrorMessage = defaultErrorMessage
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

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> ExpensesTab(expensesFactory)
                1 -> IncomesTab(incomeFactory)
            }
        }
    }
}

// ---------- Вкладка расходов ----------
@Composable
private fun ExpensesTab(factory: AllServicesSummaryViewModelFactory) {
    val viewModel: AllServicesSummaryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allServices = getListInitialScreen()

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
                    items(services) { (key, total) ->
                        val screen = allServices.find { it.fileKey == key }
                        val emoji = screen?.icon ?: "📊"
                        val displayName = screen?.name ?: key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$emoji $displayName", fontWeight = FontWeight.Bold)
                            Text(
                                text = stringResource(R.string.money_format).format(total), fontSize = 14.sp
                            )
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
                val emoji = screen?.icon ?: "📊"
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
                        text = "$emoji ${displayName.take(8)}", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.height(16.dp)
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
                            text = stringResource(category.nameRes()).take(8), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.height(16.dp)
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
                    .padding(top = 4.dp)
                    .zIndex(10f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(category.nameRes()), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    Spacer(modifier = Modifier.width(3.dp))
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
    val context = LocalContext.current
    val incomeFileManager = remember { IncomeFileManager(context) }
    val viewModel: IncomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSourceForEdit by remember { mutableStateOf<String?>(null) }
    var showDeleteSourceConfirm by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
            // Используем LazyColumn как корневой, без внешнего Column с verticalScroll
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Карточка общей суммы
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.total_income, Year.now().value), color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 16.sp
                            )
                            Text(
                                text = stringResource(R.string.money_format).format(summary.total), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // График доходов (теперь тоже будет скроллиться)
                item {
                    IncomesChart(summary)
                }

                val sources = summary.bySource.toList().sortedBy { (category, _) -> category.order }
                if (sources.isNotEmpty()) {
                    items(sources.size) { index ->
                        val (category, total) = sources[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(category.nameRes()), fontWeight = FontWeight.Bold, fontSize = 16.sp
                                )
                                Text(
                                    text = stringResource(R.string.money_format).format(total), fontSize = 16.sp
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = { selectedSourceForEdit = category.name }, modifier = Modifier.size(33.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { showDeleteSourceConfirm = category.name }, modifier = Modifier.size(33.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                // Кнопка добавления дохода
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Text(stringResource(R.string.add_income))
                    }
                }
            }
        }
    }

    // Диалоги остаются без изменений
    if (showAddDialog) {
        AddIncomeDialog(onDismiss = { showAddDialog = false }, onAdd = { source, amount ->
            viewModel.addIncome(source, amount)
            showAddDialog = false
        })
    }

    if (selectedSourceForEdit != null) {
        val source = selectedSourceForEdit ?: return
        SourceRecordsDialog(
            source = source,
            fileManager = incomeFileManager,
            onDismiss = {
                selectedSourceForEdit = null
                viewModel.loadIncome()
            },
        )
    }

    if (showDeleteSourceConfirm != null) {
        val source = showDeleteSourceConfirm ?: return
        AlertDialog(onDismissRequest = { showDeleteSourceConfirm = null }, title = { Text(stringResource(R.string.remove_all_incomes, source)) }, text = { Text(stringResource(R.string.this_action_is_irreversible)) }, confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    deleteAllSourceRecords(source, incomeFileManager, Year.now().value)
                    viewModel.loadIncome()
                    showDeleteSourceConfirm = null
                }
            }) { Text(stringResource(R.string.delete)) }
        }, dismissButton = {
            TextButton(onClick = { showDeleteSourceConfirm = null }) { Text(stringResource(R.string.cancel)) }
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
    source: String, fileManager: IncomeFileManager, onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val records = remember { mutableStateListOf<IncomeRecord>() }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<IncomeRecord?>(null) }

    fun reloadRecords() {
        scope.launch {
            val raw = fileManager.readIncome(Year.now().value)
            records.clear()
            records.addAll(IncomeParser.parse(raw).filter { it.source == source })
        }
    }

    LaunchedEffect(source) {
        reloadRecords()
    }

    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.income_dialog_title, source)) }, text = {
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
                                value = editSource, onValueChange = { editSource = it }, label = { Text(stringResource(R.string.source)) }, modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = editAmount, onValueChange = { editAmount = it }, label = { Text(stringResource(R.string.amount)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = {
                                    val newAmount = editAmount.toDoubleOrNull()
                                    if (newAmount != null && newAmount > 0 && editSource.isNotBlank()) {
                                        scope.launch {
                                            updateIncomeRecord(record, editSource, newAmount, fileManager, Year.now().value)
                                            reloadRecords()
                                            editIndex = null
                                        }
                                    }
                                }) { Text(stringResource(R.string.save)) }
                                TextButton(onClick = { editIndex = null }) { Text(stringResource(R.string.cancel)) }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.money_format).format(record.amount), fontSize = 14.sp
                                )
                                Text(
                                    text = record.date.toString(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Row {
                                IconButton(onClick = { editIndex = index }) {
                                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.editing))
                                }
                                IconButton(onClick = { showDeleteConfirm = record }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
    })

    val deleteRecord = showDeleteConfirm ?: return
    AlertDialog(onDismissRequest = { showDeleteConfirm = null }, title = { Text(stringResource(R.string.delete_record)) }, text = { Text(stringResource(R.string.delete_confirm_amount).format(deleteRecord.amount)) }, confirmButton = {
        TextButton(onClick = {
            scope.launch {
                deleteIncomeRecord(deleteRecord, fileManager, Year.now().value)
                reloadRecords()
                showDeleteConfirm = null
            }
        }) { Text(stringResource(R.string.delete)) }
    }, dismissButton = {
        TextButton(onClick = { showDeleteConfirm = null }) { Text(stringResource(R.string.cancel)) }
    })
}

private suspend fun updateIncomeRecord(
    oldRecord: IncomeRecord, newSource: String, newAmount: Double, fileManager: IncomeFileManager, year: Int
) {
    val raw = fileManager.readIncome(year)
    val dateString = oldRecord.date.toString()

    @Suppress("HardcodedStringLiteral") val oldBlock = "$dateString\nИсточник: ${oldRecord.source}\nСумма: ${"%.2f".format(oldRecord.amount).replace(',', '.')}"

    @Suppress("HardcodedStringLiteral") val newBlock = "$dateString\nИсточник: $newSource\nСумма: ${"%.2f".format(newAmount).replace(',', '.')}"
    val updated = raw.replace(oldBlock, newBlock)
    fileManager.saveIncome(year, updated)
}

private suspend fun deleteIncomeRecord(
    record: IncomeRecord, fileManager: IncomeFileManager, year: Int
) {
    val raw = fileManager.readIncome(year)
    val dateString = record.date.toString()

    @Suppress("HardcodedStringLiteral") val block = "$dateString\nИсточник: ${record.source}\nСумма: ${"%.2f".format(record.amount).replace(',', '.')}"
    val updated = raw.replace("$block\n***", "").replace(block, "")
    fileManager.saveIncome(year, updated)
}

@Suppress("HardcodedStringLiteral")
private suspend fun deleteAllSourceRecords(
    source: String, fileManager: IncomeFileManager, year: Int
) {
    val raw = fileManager.readIncome(year)
    val blocks = raw.split("***").filter { it.isNotBlank() }
    val filtered = blocks.filter { block ->
        val lines = block.lines()
        val blockSource = lines.firstOrNull { it.startsWith("Источник:") }?.substringAfter("Источник:")?.trim()
        blockSource != source
    }
    val updated = filtered.joinToString("\n***\n")
    fileManager.saveIncome(year, updated)
}
