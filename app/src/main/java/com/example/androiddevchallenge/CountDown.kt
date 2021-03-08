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

@ExperimentalAnimationApi
@Composable
fun CountDown(
    formattedTime: String
) {
    val timeFormat = "00${stringResource(R.string.hour_abbrev)}:00${stringResource(R.string.minute_abbrev)}:00${stringResource(R.string.second_abbrev)}"
    var rememberedTime by remember { mutableStateOf(timeFormat) }
    Row {
        FormattedTime(
            formattedTime = formattedTime,
            rememberedTime
        )
        rememberedTime = formattedTime
    }
}

@ExperimentalAnimationApi
@Composable
private fun FormattedTime(
    formattedTime: String,
    rememberedTime: String
) = formattedTime.forEachIndexed { index, character ->
    when {
        character.isDigit() -> AnimatedDigit(
            currentDigit = rememberedTime.elementAtOrElse(index) { '0' },
            nextDigit = character
        )
        character == ':' -> DigitText(digit = character)
        else -> TimeUnitText(unit = character)
    }
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
    unit: Char
) {
    Text(
        text = unit.toString(),
        fontSize = 24.sp
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun CountDownPreview() {
    MyTheme {
        Surface(color = MaterialTheme.colors.background) {
            CountDown(formattedTime = "12h:34m:56s")
        }
    }
}
