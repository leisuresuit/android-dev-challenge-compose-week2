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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.androiddevchallenge.ui.theme.MyTheme

private const val COUNTDOWN_INTERVAL = 1000L

@ExperimentalStdlibApi
@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    private val secondsRemaining = mutableStateOf<Long>(0)
    private val entryDigits = mutableStateListOf<Int>()
    private val timer = mutableStateOf<CountDownTimer?>(null)
    private companion object {
        const val SECONDS_REMAINING = "seconds_remaining"
        const val ENTRY_DIGITS = "entry_digits"
        const val IS_TIMER_RUNNING = "is_timer_running"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.run {
            secondsRemaining.value = getLong(SECONDS_REMAINING).also {
                if (it > 0 && getBoolean(IS_TIMER_RUNNING)) {
                    startTimer(it)
                }
            }
            getIntArray(ENTRY_DIGITS)?.forEach {
                entryDigits.add(it)
            }
        }

        setContent {
            MyTheme {
                MyApp(
                    orientation = resources.configuration.orientation,
                    entryDigits = entryDigits,
                    secondsRemaining = secondsRemaining.value,
                    isTimerRunning = timer.value != null,
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
            putLong(SECONDS_REMAINING, secondsRemaining.value)
            putIntArray(ENTRY_DIGITS, entryDigits.toIntArray())
            putBoolean(IS_TIMER_RUNNING, timer.value != null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.value?.cancel()
    }

    private fun onClearClick() {
        timer.value?.cancel()
        timer.value = null
        secondsRemaining.value = 0
        entryDigits.clear()
    }

    private fun onStartClick() {
        timer.value?.let {
            it.cancel()
            timer.value = null
        } ?: run {
            if (secondsRemaining.value == 0L) {
                secondsRemaining.value = updateSecondsRemaining()
            }
            startTimer(secondsRemaining.value)
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
    }

    private fun updateSecondsRemaining() =
        getTimeValues(entryDigits).run {
            (first + second + third).toLong()
        }

    private fun getTimeValues(digits: List<Int>) =
        digits.asReversed().let {
            Triple(
                getValue(digits = it, fromIndex = 0),
                60 * getValue(digits = it, fromIndex = 2),
                3600 * getValue(digits = it, fromIndex = 4),
            )
        }

    private fun getValue(
        digits: List<Int>,
        fromIndex: Int
    ) = digits.getOrElse(fromIndex) { 0 } + 10 * digits.getOrElse(fromIndex + 1) { 0 }

    private fun startTimer(startTime: Long) {
        secondsRemaining.value = startTime
        timer.value = object : CountDownTimer(secondsRemaining.value * 1000, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                secondsRemaining.value = 0
                timer.value = null
            }
        }.start()
    }
}
