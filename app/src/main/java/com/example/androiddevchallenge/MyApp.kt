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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.theme.MyTheme

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
fun MyApp(
    orientation: Int,
    timerState: MutableState<CountDownTimerState>,
    hours: MutableState<Int>,
    minutes: MutableState<Int>,
    seconds: MutableState<Int>,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = Surface(color = MaterialTheme.colors.background) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Portrait(
            timerState.value,
            hours = hours.value,
            minutes = minutes.value,
            seconds = seconds.value,
            onClearClick,
            onStartClick,
            onKeypadClick
        )
    } else {
        Landscape(
            timerState.value,
            hours = hours.value,
            minutes = minutes.value,
            seconds = seconds.value,
            onClearClick,
            onStartClick,
            onKeypadClick
        )
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun Portrait(
    timerState: CountDownTimerState,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    TimerDisplay(
        timerState,
        hours = hours,
        minutes = minutes,
        seconds = seconds,
        onClearClick,
        onStartClick
    )
    Spacer(Modifier.height(dimensionResource(R.dimen.padding_xlarge)))
    TimerKeypad(timerState, onKeypadClick)
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun Landscape(
    timerState: CountDownTimerState,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerDisplay(
            timerState = timerState,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            onClearClick,
            onStartClick
        )
    }
    Spacer(Modifier.width(dimensionResource(R.dimen.padding_xlarge)))
    TimerKeypad(timerState, onKeypadClick)
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
    timerState: CountDownTimerState,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {}
) {
    CountDown(
        hours = hours,
        minutes = minutes,
        seconds = seconds
    )
    Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
    TimerButtons(
        timerState = timerState,
        hasTime = hours > 0 || minutes > 0 || seconds > 0,
        onClearClick = onClearClick,
        onStartClick = onStartClick
    )
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
            modifier = Modifier
                .padding(horizontal = paddingMedium)
                .height(dimensionResource(R.dimen.button_height)),
            enabled = hasTime
        ) {
            Text(stringResource(R.string.clear))
        }
        Spacer(Modifier.height(paddingMedium))
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .padding(horizontal = paddingMedium)
                .height(dimensionResource(R.dimen.button_height)),
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
            timerState = mutableStateOf(CountDownTimerState.Running),
            hours = mutableStateOf(12),
            minutes = mutableStateOf(34),
            seconds = mutableStateOf(56)
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
            orientation = Configuration.ORIENTATION_PORTRAIT,
            timerState = mutableStateOf(CountDownTimerState.Running),
            hours = mutableStateOf(12),
            minutes = mutableStateOf(34),
            seconds = mutableStateOf(56)
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
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            timerState = mutableStateOf(CountDownTimerState.Running),
            hours = mutableStateOf(12),
            minutes = mutableStateOf(34),
            seconds = mutableStateOf(56)
        )
    }
}
