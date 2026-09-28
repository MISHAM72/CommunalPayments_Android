package com.github.misham72.communalpayments.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.github.misham72.communalpayments.R
import androidx.compose.material3.Typography


val PochaevskFontFamily = FontFamily(
    Font(R.font.pochaevsk_regular, FontWeight.Normal)
)

val PonomarFontFamily = FontFamily(
    Font(R.font.ponomar_regular, FontWeight.Normal)
)
val AppTypography = Typography().let { t ->
    t.copy(
        bodyLarge = t.bodyLarge.copy(fontFamily = PonomarFontFamily),
        bodyMedium = t.bodyMedium.copy(fontFamily = PonomarFontFamily),
        bodySmall = t.bodySmall.copy(fontFamily = PonomarFontFamily),
        titleLarge = t.titleLarge.copy(fontFamily = PochaevskFontFamily),
        titleMedium = t.titleMedium.copy(fontFamily = PochaevskFontFamily),
        titleSmall = t.titleSmall.copy(fontFamily = PochaevskFontFamily),
        labelLarge = t.labelLarge.copy(fontFamily = PonomarFontFamily),
        labelMedium = t.labelMedium.copy(fontFamily = PonomarFontFamily),
        labelSmall = t.labelSmall.copy(fontFamily = PonomarFontFamily),
    )
}
