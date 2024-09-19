package com.app.tensquare.utils

import android.os.CountDownTimer
import kotlin.properties.Delegates

abstract class CountUpTimer :
    CountDownTimer {

    private val INTERVAL_MS: Long = 1000
    private var duration by Delegates.notNull<Long>()

    constructor(millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    ) {
        this.duration = countDownInterval
    }

    override fun onTick(msUntilFinished: Long) {
        onTick(((duration - msUntilFinished) / 1000).toLong())
    }

    override fun onFinish() {
        onTick(duration / 1000);
    }

}