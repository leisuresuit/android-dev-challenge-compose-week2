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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

@Composable
fun Keypad(
    enabled: Boolean = true,
    onClick: (Int) -> Unit
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    KeyRow(
        digits = charArrayOf('1', '2', '3'),
        enabled = enabled,
        onClick = onClick
    )
    Spacer(Modifier.height(1.dp))
    KeyRow(
        digits = charArrayOf('4', '5', '6'),
        enabled = enabled,
        onClick = onClick
    )
    Spacer(Modifier.height(1.dp))
    KeyRow(
        digits = charArrayOf('7', '8', '9'),
        enabled = enabled,
        onClick = onClick
    )
    Spacer(Modifier.height(1.dp))
    KeyRow(
        digits = charArrayOf('0', '\u232b'),
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
private fun KeyRow(
    digits: CharArray,
    enabled: Boolean,
    onClick: (Int) -> Unit
) = Row {
    val paddingXLarge = dimensionResource(R.dimen.padding_xlarge)
    val paddingXSmall = dimensionResource(R.dimen.padding_xsmall)
    for (i in 0 until 3 - digits.size) {
        Spacer(Modifier.width(paddingXLarge + paddingXSmall))
    }
    digits.forEach {
        Key(
            digit = it,
            enabled = enabled,
        ) {
            onClick(it - '0')
        }
        if (it != digits.last()) {
            Spacer(Modifier.width(paddingXSmall))
        }
    }
}

@Composable
private fun Key(
    digit: Char,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val keypadButtonSize = dimensionResource(R.dimen.keypad_button_size)
    Button(
        onClick = onClick,
        modifier = Modifier.size(keypadButtonSize, keypadButtonSize),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
        contentPadding = PaddingValues(
            start = 0.dp,
            top = 0.dp,
            end = 0.dp,
            bottom = 0.dp
        )
    ) {
        Text(
            text = "$digit",
            fontSize = 36.sp,
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun KeypadPreview() {
    MyTheme {
        Surface(color = MaterialTheme.colors.background) {
            Keypad {}
        }
    }
}
