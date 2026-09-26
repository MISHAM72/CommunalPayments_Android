package com.github.misham72.communalpayments.presentation.screen.screens.analytics.income

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.incomes.IncomeCategory
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.components.chartColors
import com.github.misham72.communalpayments.presentation.utils.nameRes

// График доходов по источникам
@Composable
fun IncomesChart(summary: IncomeSummary) {
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
