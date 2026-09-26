package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ExpensesViewModelFactory
import com.github.misham72.communalpayments.di.IncomeViewModelFactory
import com.github.misham72.communalpayments.domain.usecases.GetExpensesUseCase
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.BankingTabSwitcher
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.expenses.ExpensesTab
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.income.IncomesTab
import com.github.misham72.communalpayments.presentation.screen.screens.services.getListInitialScreen
import com.github.misham72.communalpayments.presentation.utils.rememberButtonBuckSoundPlayer


@Composable
fun AnalyticsScreen(
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
