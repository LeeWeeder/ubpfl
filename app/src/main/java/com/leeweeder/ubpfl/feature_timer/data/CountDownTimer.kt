package com.leeweeder.ubpfl.feature_timer.data

import android.os.CountDownTimer

open class CountDownTimer(
    duration: Long,
    private val onTick: (secondsRemaining: Int) -> Unit,
    private val onTimerFinish: () -> Unit
) :
    CountDownTimer(duration, 1000L) {
    override fun onTick(millisUntilFinished: Long) {
        onTick((millisUntilFinished / 1000).toInt())
    }

    override fun onFinish() {
        onTimerFinish()
    }
}