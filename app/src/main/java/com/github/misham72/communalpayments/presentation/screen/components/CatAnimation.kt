package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.misham72.communalpayments.R

@Composable
fun CatAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.cat_animation)
    )
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(80.dp)
    )
}
