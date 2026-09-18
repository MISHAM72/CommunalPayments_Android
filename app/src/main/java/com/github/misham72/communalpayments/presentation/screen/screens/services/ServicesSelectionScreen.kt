package com.github.misham72.communalpayments.presentation.screen.screens.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ServicesSelectionViewModelFactory
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.components.CatAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesSelectionScreen(
    appContainer: AppContainer,
    onBack: () -> Unit,
    onSaved: () -> Unit = onBack,
) {
    val electricity = stringResource(R.string.service_display_name_electricity)
    val gas = stringResource(R.string.service_display_name_gas)
    val water = stringResource(R.string.service_display_name_water)
    val garbage = stringResource(R.string.service_display_name_garbage)
    val zont = stringResource(R.string.service_display_name_zont)
    val internet = stringResource(R.string.service_display_name_internet)
    val mts = stringResource(R.string.service_display_name_mts)
    val tinkoff = stringResource(R.string.service_display_name_tinkoff)
    val taxes = stringResource(R.string.service_display_name_taxes)
    val troyka = stringResource(R.string.service_display_name_troyka)
    val osago = stringResource(R.string.service_display_name_osago)
    val hostel = stringResource(R.string.service_display_name_hostel)

    val meterItems = listOf(
        ServiceItem(ServiceKeys.ELECTRICITY, electricity, "⚡"),
        ServiceItem(ServiceKeys.GAS, gas, "🔥"),
        ServiceItem(ServiceKeys.WATER, water, "💧"),
    )

    val periodicItems = listOf(
        ServiceItem(ServiceKeys.GARBAGE, garbage, "🗑️"),
        ServiceItem(ServiceKeys.ZONT, zont, "🌡️"),
        ServiceItem(ServiceKeys.INTERNET, internet, "📶"),
        ServiceItem(ServiceKeys.MTS, mts, "📱"),
        ServiceItem(ServiceKeys.TINKOFF, tinkoff, "🟦"),
        ServiceItem(ServiceKeys.TAXES, taxes, "💰"),
        ServiceItem(ServiceKeys.TROYKA, troyka, "🚇"),
        ServiceItem(ServiceKeys.OSAGO, osago, "🚗"),
        ServiceItem(ServiceKeys.HOSTEL, hostel, "🏢"),
    )
    val groupTitles = stringResource(R.string.group_meters) to stringResource(R.string.group_periodic)

    val factory = remember {
        ServicesSelectionViewModelFactory(appContainer, meterItems, periodicItems, groupTitles)
    }
    val viewModel: ServicesSelectionViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_services_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                state.groups.forEach { group ->
                    item(key = UiConstants.HEADER_KEY_PREFIX + group.title) {
                        Text(
                            text = group.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(group.items, key = { it.key }) { item ->
                        ServiceRow(
                            item = item,
                            checked = item.key in state.selectedKeys,
                            onToggle = { viewModel.toggle(item.key) }
                        )
                    }
                }
            }
            CatAnimation()
            Button(
                onClick = {
                    viewModel.save()
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun ServiceRow(
    item: ServiceItem,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.emoji, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
        Text(
            text = item.displayName,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
    }
}
