package com.github.misham72.communalpayments.presentation.utils

import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.misham72.communalpayments.R

/**@Composable
fun rememberCoinSoundPlayer(): MediaPlayer {
val context = LocalContext.current
val mediaPlayer = remember { MediaPlayer.create(context, R.raw.coin_spin) }
DisposableEffect(Unit) {
onDispose { mediaPlayer.release() }
}
return mediaPlayer
}*/
// Приватная фабрика (не видна снаружи)
@Composable
private fun rememberSoundPlayer(@RawRes soundResId: Int): MediaPlayer? {
    val context = LocalContext.current
    val mediaPlayer: MediaPlayer? = remember {
        MediaPlayer.create(context, soundResId)
    }
    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }
    return mediaPlayer
}

// Публичные функции становятся однострочными:
@Composable
fun rememberCoinSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.coin_spin)

@Composable
fun rememberHistorySoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.open_old_door)

@Composable
fun rememberButtonBuckSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.close_door)

@Composable
fun rememberEditHistoryButtonSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.edit_history_sound)

@Composable
fun rememberSaveButtonSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.save_sound)

@Composable
fun rememberCancelButtonSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.cancel_sound)

@Composable
fun rememberClockCuCuSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.clock_cu_cu_sound)

@Composable
fun rememberChangeListSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.change_list_sound)

@Composable
fun rememberCopyButtonSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.copy_sound)

@Composable
fun rememberBankButtonSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.bank_sound)


@Composable
fun rememberlightSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.light)


@Composable
fun rememberGasSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.gas_sound)

@Composable
fun rememberWaterSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.water_sound)


@Composable
fun rememberGarbageSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.gagbage_sound)

@Composable
fun rememberBoilerSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.boiler_sound)


@Composable
fun rememberInternetSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.internet_sound)

@Composable
fun rememberMTSSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.mts_sound)

@Composable
fun rememberTinkoffSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.tinkoff_sound)

@Composable
fun rememberTaxesSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.taxes_sound)

@Composable
fun rememberCarSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.car_pass)

@Composable
fun rememberOsagoSoundPlayer(): MediaPlayer? = rememberSoundPlayer(R.raw.osago_sound)

