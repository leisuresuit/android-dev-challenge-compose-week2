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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
fun CountDown(
    hours: Int,
    minutes: Int,
    seconds: Int,
) {
    var rememberedTime by remember { mutableStateOf(Triple(0, 0, 0)) }
    Row {
        FormattedTime(
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            rememberedTime
        )
        rememberedTime = Triple(hours, minutes, seconds)
    }
}

@ExperimentalAnimationApi
@ExperimentalStdlibApi
@Composable
private fun FormattedTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
    rememberedTime: Triple<Int, Int, Int>
) {
    // Format: 00h:00m:00s
    AnimatedDigitPair(
        value = hours,
        rememberedValue = rememberedTime.first
    )
    TimeUnitText(stringResource(R.string.hour_abbrev))
    DigitText(':')
    AnimatedDigitPair(
        value = minutes,
        rememberedValue = rememberedTime.second
    )
    TimeUnitText(stringResource(R.string.minute_abbrev))
    DigitText(':')
    AnimatedDigitPair(
        value = seconds,
        rememberedValue = rememberedTime.third
    )
    TimeUnitText(stringResource(R.string.second_abbrev))
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
private fun AnimatedDigitPair(
    value: Int,
    rememberedValue: Int
) {
    // TODO Handle values outside [0:9]
    AnimatedDigit(
        currentDigit = (rememberedValue / 10).digitToChar(),
        nextDigit = (value / 10).digitToChar()
    )
    AnimatedDigit(
        currentDigit = (rememberedValue % 10).digitToChar(),
        nextDigit = (value % 10).digitToChar()
    )
}

@ExperimentalAnimationApi
@Composable
private fun AnimatedDigit(
    currentDigit: Char,
    nextDigit: Char
) {
    Box {
        Digit(
            visible = currentDigit == nextDigit,
            digit = currentDigit
        )
        Digit(
            visible = currentDigit != nextDigit,
            digit = nextDigit
        )
    }
}

@ExperimentalAnimationApi
@Composable
private fun Digit(
    visible: Boolean,
    digit: Char
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        DigitText(digit)
    }
}

@Composable
private fun DigitText(
    digit: Char
) {
    Text(
        text = digit.toString(),
        fontSize = 42.sp
    )
}

@Composable
private fun TimeUnitText(
    unit: String
) {
    Text(
        text = unit,
        fontSize = 24.sp
    )
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview
@Composable
fun CountDownPreview() {
    MyTheme {
        Surface(color = MaterialTheme.colors.background) {
            CountDown(
                hours = 12,
                minutes = 34,
                seconds = 56
            )
        }
    }
}
