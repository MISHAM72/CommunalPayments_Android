package com.github.misham72.communalpayments.presentation.screen.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.github.misham72.communalpayments.R

private fun openDeepSeek(context: Context) {       // ← отдельная функция
    val intent = Intent(Intent.ACTION_VIEW, "https://chat.deepseek.com/".toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, R.string.no_browser, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AskAiButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        onClick = { openDeepSeek(context) },          // ← 1 строка
        modifier = modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.ask_ai))
    }
}
