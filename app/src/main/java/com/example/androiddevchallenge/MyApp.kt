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

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.theme.MyTheme

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
fun MyApp(
    uiState: CountDownUiState,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    orientation: Int = LocalConfiguration.current.orientation
) {
    Surface(color = MaterialTheme.colors.background) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Portrait(
                uiState = uiState,
                onClearClick = onClearClick,
                onStartClick = onStartClick,
                onKeypadClick = { updateCountDownTime(uiState, it) }
            )
        } else {
            Landscape(
                uiState = uiState,
                onClearClick = onClearClick,
                onStartClick = onStartClick,
                onKeypadClick = { updateCountDownTime(uiState, it) }
            )
        }
    }
}

private fun updateCountDownTime(uiState: CountDownUiState, key: Int) {
    with(uiState) {
        if (key < 0 || key > 9) {
            // Remove the last digit.
            // Shift the digits to the right.
            seconds.value = (minutes.value % 10) * 10 + (seconds.value / 10)
            minutes.value = (hours.value % 10) * 10 + (minutes.value / 10)
            hours.value = hours.value / 10
        } else if (uiState.hours.value / 10 == 0) {
            // Add the digit at the end.
            // Shift the digits to the left.
            hours.value = (hours.value % 10) * 10 + (minutes.value / 10)
            minutes.value = (minutes.value % 10) * 10 + (seconds.value / 10)
            seconds.value = (seconds.value % 10) * 10 + key
        }
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun Portrait(
    uiState: CountDownUiState,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = TimerDisplay(
    modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    uiState = uiState,
    onClearClick = onClearClick,
    onStartClick = onStartClick
) {
    Spacer(Modifier.height(dimensionResource(R.dimen.padding_xlarge)))
    TimerKeypad(
        timerState = uiState.timerState.value,
        onKeypadClick = onKeypadClick
    )
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun Landscape(
    uiState: CountDownUiState,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
) {
    TimerDisplay(
        uiState = uiState,
        onClearClick = onClearClick,
        onStartClick = onStartClick
    )
    Spacer(Modifier.width(dimensionResource(R.dimen.padding_xlarge)))
    TimerKeypad(
        timerState = uiState.timerState.value,
        onKeypadClick = onKeypadClick
    )
}

@Composable
private fun TimerKeypad(
    timerState: CountDownTimerState,
    onKeypadClick: (Int) -> Unit = {}
) {
    Keypad(
        enabled = timerState == CountDownTimerState.Stopped,
        onClick = onKeypadClick
    )
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun TimerDisplay(
    modifier: Modifier = Modifier,
    uiState: CountDownUiState,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    additionalContent: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        with(uiState) {
            CountDown(
                hours = hours.value,
                minutes = minutes.value,
                seconds = seconds.value
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
            TimerButtons(
                timerState = timerState.value,
                hasTime = hours.value > 0 || minutes.value > 0 || seconds.value > 0,
                onClearClick = onClearClick,
                onStartClick = onStartClick
            )
        }
        additionalContent()
    }
}

@Composable
private fun TimerButtons(
    timerState: CountDownTimerState,
    hasTime: Boolean,
    onClearClick: () -> Unit,
    onStartClick: () -> Unit,
) {
    Row {
        val paddingMedium = dimensionResource(R.dimen.padding_medium)
        Button(
            onClick = onClearClick,
            modifier = Modifier.height(dimensionResource(R.dimen.button_height)),
            enabled = hasTime
        ) {
            Text(stringResource(R.string.clear))
        }
        Spacer(Modifier.width(paddingMedium))
        Button(
            onClick = onStartClick,
            modifier = Modifier.height(dimensionResource(R.dimen.button_height)),
            enabled = hasTime
        ) {
            Text(
                when (timerState) {
                    CountDownTimerState.Running -> stringResource(R.string.pause)
                    CountDownTimerState.Paused -> stringResource(R.string.resume)
                    CountDownTimerState.Stopped -> stringResource(R.string.start)
                }
            )
        }
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(
            orientation = Configuration.ORIENTATION_PORTRAIT,
            uiState = CountDownUiState(
                hours = 12,
                minutes = 34,
                seconds = 56,
                timerState = CountDownTimerState.Running
            )
        )
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp(
            uiState = CountDownUiState(
                hours = 12,
                minutes = 34,
                seconds = 56,
                timerState = CountDownTimerState.Running
            ),
            orientation = Configuration.ORIENTATION_PORTRAIT,
        )
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Landscape", widthDp = 640, heightDp = 360)
@Composable
fun LandscapePreview() {
    MyTheme {
        MyApp(
            uiState = CountDownUiState(
                hours = 12,
                minutes = 34,
                seconds = 56,
                timerState = CountDownTimerState.Running
            ),
            orientation = Configuration.ORIENTATION_LANDSCAPE,
        )
    }
}
