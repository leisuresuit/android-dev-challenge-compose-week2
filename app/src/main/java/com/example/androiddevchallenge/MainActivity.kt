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
import com.example.androiddevchallenge.ui.theme.MyTheme

private const val COUNTDOWN_INTERVAL = 1000L

@ExperimentalStdlibApi
@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    private val uiState = CountDownUiState()
    private var timer: CountDownTimer? = null
    private companion object {
        const val HOURS = "hours"
        const val MINUTES = "minutes"
        const val SECONDS = "seconds"
        const val LAST_TIMESTAMP = "last_timestamp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            resumeTimer(it)
        }

        setContent {
            MyTheme {
                MyApp(
                    uiState = uiState,
                    onClearClick = ::onClearClick,
                    onStartClick = ::onStartClick
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState) {
            putInt(HOURS, uiState.hours.value)
            putInt(MINUTES, uiState.minutes.value)
            putInt(SECONDS, uiState.seconds.value)
            if (uiState.timerState.value == CountDownTimerState.Running) {
                putLong(LAST_TIMESTAMP, System.currentTimeMillis())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }

    private fun resumeTimer(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            with(uiState) {
                val millisRemaining = getCountDownTimeMillis(
                    hours = getInt(HOURS).also { hours.value = it },
                    minutes = getInt(MINUTES).also { minutes.value = it },
                    seconds = getInt(SECONDS).also { seconds.value = it }
                )
                if (millisRemaining > 0) {
                    // Subtract the time when the activity was destroyed and re-created.
                    val lastTimeStamp = getLong(LAST_TIMESTAMP)
                    if (lastTimeStamp > 0) {
                        val timeDelta = System.currentTimeMillis() - lastTimeStamp
                        updateCountDownTime(millisRemaining - timeDelta)
                        startTimer()
                    }
                }
            }
        }
    }

    private fun onClearClick() {
        timer?.cancel()
        resetState()
    }

    private fun resetState() {
        timer = null
        with(uiState) {
            hours.value = 0
            minutes.value = 0
            seconds.value = 0
            timerState.value = CountDownTimerState.Stopped
        }
    }

    private fun onStartClick() {
        timer?.let {
            it.cancel()
            timer = null
            uiState.timerState.value = CountDownTimerState.Paused
        } ?: run {
            startTimer()
        }
    }

    /**
     * Updates the countdown time from the timer
     */
    private fun updateCountDownTime(millis: Long) {
        (millis / 1000).toInt().let {
            with(uiState) {
                hours.value = (it / 3600)
                minutes.value = ((it % 3600) / 60)
                seconds.value = it - (3600 * hours.value + 60 * minutes.value)
            }
        }
    }

    private fun getCountDownTimeMillis(
        hours: Int,
        minutes: Int,
        seconds: Int
    ) = (3600 * hours + 60 * minutes + seconds) * 1000L

    private fun startTimer() {
        val millis = with(uiState) {
            getCountDownTimeMillis(hours.value, minutes.value, seconds.value)
        }
        timer = object : CountDownTimer(millis, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                updateCountDownTime(millisUntilFinished)
                uiState.timerState.value = CountDownTimerState.Running
            }

            override fun onFinish() {
                resetState()
            }
        }.start()
    }
}
