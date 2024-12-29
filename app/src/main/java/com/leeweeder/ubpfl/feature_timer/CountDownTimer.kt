package com.leeweeder.ubpfl.feature_timer

import android.os.CountDownTimer

open class CountDownTimer(
    duration: Long,
    private val onCountDown: (millisecondsRemaining: Long) -> Unit,
    private val onTimerFinish: () -> Unit
) :
    CountDownTimer(duration, 1L) {
    override fun onTick(millisUntilFinished: Long) {
        onCountDown(millisUntilFinished)
    }

    override fun onFinish() {
        onTimerFinish()
    }
}