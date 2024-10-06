package com.leeweeder.yluxthenics.ui.preparation

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.leeweeder.yluxthenics.R
import com.leeweeder.yluxthenics.feature_timer.data.CountDownTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PreparationViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val _remainingSeconds = mutableIntStateOf(5)
    val remainingSeconds: State<Int> = _remainingSeconds

    private var isTimerFinished = false

    private val mediaPlayer = MediaPlayer().apply {
        val afd = context.resources.openRawResourceFd(R.raw.beep)
        setDataSource(afd)
        setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        )
        afd.close()
    }

    private val countDownTimer = object : CountDownTimer(5 * 1000L, onTick = {
        _remainingSeconds.intValue = it
    }, onTimerFinish = {
        isTimerFinished = true
    }) {}

    fun onEvent(event: PreparationEvent) {
        when (event) {
            PreparationEvent.CancelTimer -> {
                countDownTimer.cancel()
                mediaPlayer.release()
            }
            is PreparationEvent.SetMediaPlayerCompletionListener -> {
                mediaPlayer.setOnCompletionListener {
                    event.onComplete(isTimerFinished)
                }
            }
            PreparationEvent.StartTimer -> {
                countDownTimer.start()
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        }
    }
}

sealed class PreparationEvent {
    data class SetMediaPlayerCompletionListener(val onComplete: (isTimerFinished: Boolean) -> Unit) :
        PreparationEvent()
    data object StartTimer : PreparationEvent()
    data object CancelTimer : PreparationEvent()
}