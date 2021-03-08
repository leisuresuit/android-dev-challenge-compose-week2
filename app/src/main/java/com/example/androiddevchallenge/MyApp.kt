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
import android.content.res.Resources
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.theme.MyTheme

const val MAX_DIGITS_LEN = 6

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
fun MyApp(
    orientation: Int,
    entryDigits: List<Int>,
    secondsRemaining: Long,
    isTimerRunning: Boolean,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onKeypadClick: (Int) -> Unit = {}
) = Surface(color = MaterialTheme.colors.background) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Portrait(
            entryDigits,
            secondsRemaining,
            isTimerRunning,
            onClearClick,
            onStartClick,
            onKeypadClick
        )
    } else {
        Landscape(
            entryDigits,
            secondsRemaining,
            isTimerRunning,
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
    entryDigits: List<Int>,
    secondsRemaining: Long,
    isTimerRunning: Boolean,
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
        entryDigits,
        secondsRemaining,
        isTimerRunning,
        onClearClick,
        onStartClick
    )
    Spacer(Modifier.height(dimensionResource(R.dimen.padding_xlarge)))
    Keypad(
        enabled = secondsRemaining == 0L,
        onClick = onKeypadClick
    )
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun Landscape(
    entryDigits: List<Int>,
    secondsRemaining: Long,
    isTimerRunning: Boolean,
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
            entryDigits,
            secondsRemaining,
            isTimerRunning,
            onClearClick,
            onStartClick
        )
    }
    Spacer(Modifier.width(dimensionResource(R.dimen.padding_xlarge)))
    Keypad(
        enabled = secondsRemaining == 0L,
        onClick = onKeypadClick
    )
}

@ExperimentalAnimationApi
@Composable
private fun TimerDisplay(
    entryDigits: List<Int>,
    secondsRemaining: Long,
    isTimerRunning: Boolean,
    onClearClick: () -> Unit = {},
    onStartClick: () -> Unit = {}
) {
    CountDown(
        if (secondsRemaining > 0) {
            getTimeString(LocalContext.current.resources, secondsRemaining)
        } else {
            getTimeString(LocalContext.current.resources, entryDigits)
        }
    )
    Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
    TimerButtons(
        hasEnteredTime = entryDigits.isNotEmpty(),
        hasRunningTime = secondsRemaining > 0,
        isTimerRunning = isTimerRunning,
        onClearClick = onClearClick,
        onStartClick = onStartClick
    )
}

@Composable
private fun TimerButtons(
    hasEnteredTime: Boolean,
    hasRunningTime: Boolean,
    isTimerRunning: Boolean,
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
            enabled = hasEnteredTime || hasRunningTime
        ) {
            Text(stringResource(R.string.clear))
        }
        Spacer(Modifier.height(paddingMedium))
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .padding(horizontal = paddingMedium)
                .height(dimensionResource(R.dimen.button_height)),
            enabled = hasEnteredTime || hasRunningTime
        ) {
            Text(
                when {
                    isTimerRunning -> stringResource(R.string.pause)
                    hasRunningTime -> stringResource(R.string.resume)
                    else -> stringResource(R.string.start)
                }
            )
        }
    }
}

private fun getTimeString(
    resources: Resources,
    time: Long
): String {
    var hours = 0L
    var minutes = 0L
    val seconds: Long
    var remainder = time
    if (remainder >= 3600) {
        hours = remainder / 3600
        remainder -= hours * 3600
    }
    if (remainder >= 60) {
        minutes = remainder / 60
        remainder -= minutes * 60
    }
    seconds = remainder
    return "${pad(hours)}${resources.getString(R.string.hour_abbrev)}:${pad(minutes)}${resources.getString(R.string.minute_abbrev)}:${pad(seconds)}${resources.getString(R.string.second_abbrev)}"
}

private fun pad(number: Long) =
    "$number".padStart(2, '0')

private fun getTimeString(
    resources: Resources,
    digits: List<Int>
) = StringBuilder(
    digits.joinToString(separator = "")
        .padStart(MAX_DIGITS_LEN, '0')
)
    .append(resources.getString(R.string.second_abbrev))
    .insert(4, "${resources.getString(R.string.minute_abbrev)}:")
    .insert(2, "${resources.getString(R.string.hour_abbrev)}:")
    .toString()

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(
            orientation = Configuration.ORIENTATION_PORTRAIT,
            entryDigits = listOf(),
            secondsRemaining = 3600,
            isTimerRunning = true
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
            entryDigits = listOf(),
            secondsRemaining = 3600,
            isTimerRunning = true
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
            entryDigits = listOf(),
            secondsRemaining = 3600,
            isTimerRunning = true
        )
    }
}
