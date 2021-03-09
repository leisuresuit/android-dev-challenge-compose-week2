/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.mutableStateOf
import com.example.androiddevchallenge.ui.theme.MyTheme

private const val COUNTDOWN_INTERVAL = 1000L

@ExperimentalStdlibApi
@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    private val timerState = mutableStateOf(CountDownTimerState.Stopped)
    private val hours = mutableStateOf(0)
    private val minutes = mutableStateOf(0)
    private val seconds = mutableStateOf(0)
    private val entryDigits = mutableListOf<Int>()
    private var timer: CountDownTimer? = null
    private companion object {
        const val MAX_DIGITS_LEN = 6
        const val LAST_TIMESTAMP = "last_timestamp"
        const val TIME_REMAINING = "time_remaining"
        const val TIMER_STATE = "timer_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.run {
            @Suppress("UNCHECKED_CAST")
            (getSerializable(TIME_REMAINING) as? Triple<Int, Int, Int>)?.let {
                val millisRemaining = getCountDownTimeMillis(
                    hours = it.first,
                    minutes = it.second,
                    seconds = it.third
                )
                if (millisRemaining > 0) {
                    if (getSerializable(TIMER_STATE) == CountDownTimerState.Running) {
                        // Subtract the time when the activity was destroyed and re-created.
                        val timeDelta = System.currentTimeMillis() - getLong(LAST_TIMESTAMP)
                        updateCountDownTime(millisRemaining - timeDelta)
                        startTimer()
                    } else {
                        updateCountDownTime(millisRemaining)
                    }
                }
            }
        }

        setContent {
            MyTheme {
                MyApp(
                    orientation = resources.configuration.orientation,
                    timerState = timerState,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    onClearClick = ::onClearClick,
                    onStartClick = ::onStartClick,
                    onKeypadClick = ::onKeypadClick
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState) {
            putSerializable(TIME_REMAINING, Triple(hours.value, minutes.value, seconds.value))
            putSerializable(TIMER_STATE, timerState.value)
            if (timerState.value == CountDownTimerState.Running) {
                putLong(LAST_TIMESTAMP, System.currentTimeMillis())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }

    private fun onClearClick() {
        timer?.cancel()
        resetState()
    }

    private fun resetState() {
        timer = null
        hours.value = 0
        minutes.value = 0
        seconds.value = 0
        timerState.value = CountDownTimerState.Stopped
        entryDigits.clear()
    }

    private fun onStartClick() {
        timer?.let {
            it.cancel()
            timer = null
            timerState.value = CountDownTimerState.Paused
        } ?: run {
            startTimer()
        }
    }

    private fun onKeypadClick(key: Int) {
        if (key < 0 || key > 9) {
            entryDigits.removeLastOrNull()
        } else if (entryDigits.size < MAX_DIGITS_LEN &&
            (entryDigits.isNotEmpty() || key != 0)
        ) {
            entryDigits.add(key)
        }
        updateCountDownTime(entryDigits)
    }

    /**
     * Updates the countdown time from user entry
     */
    private fun updateCountDownTime(digits: List<Int>) {
        digits.asReversed().let {
            seconds.value = getValue(digits = it, fromIndex = 0)
            minutes.value = getValue(digits = it, fromIndex = 2)
            hours.value = getValue(digits = it, fromIndex = 4)
        }
    }

    /**
     * Updates the countdown time from the timer
     */
    private fun updateCountDownTime(millis: Long) {
        (millis / 1000).toInt().let {
            hours.value = (it / 3600)
            minutes.value = ((it % 3600) / 60)
            seconds.value = it - (3600 * hours.value + 60 * minutes.value)
        }
    }

    private fun getValue(
        digits: List<Int>,
        fromIndex: Int
    ) = digits.getOrElse(fromIndex) { 0 } + 10 * digits.getOrElse(fromIndex + 1) { 0 }

    private fun getCountDownTimeMillis(
        hours: Int,
        minutes: Int,
        seconds: Int
    ) = (3600 * hours + 60 * minutes + seconds) * 1000L

    private fun startTimer() {
        val millis = getCountDownTimeMillis(hours.value, minutes.value, seconds.value)
        timer = object : CountDownTimer(millis, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                updateCountDownTime(millisUntilFinished)
                timerState.value = CountDownTimerState.Running
            }

            override fun onFinish() {
                resetState()
            }
        }.start()
    }
}
