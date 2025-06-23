package com.xaarlox.todo_list.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    onLoadingFinished: () -> Unit
) {
    val totalBoxes = 4
    val boxSize = 40.dp
    val spaceBetween = 10.dp
    val durationSeconds = 6
    val animationDelayMs = 1000L

    val activeBox = remember { mutableIntStateOf(-1) }
    val startIndex = remember { Random.nextInt(0, totalBoxes) }

    LaunchedEffect(key1 = true) {
        delay(animationDelayMs)
        repeat(durationSeconds) { index ->
            activeBox.intValue = (startIndex + index) % totalBoxes
            delay(animationDelayMs)
        }
        onLoadingFinished()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = modifier) {
            for (index in 0 until totalBoxes) {
                val color = if (index == activeBox.intValue) Color.Red else Color.Green
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .background(color = color, shape = RoundedCornerShape(4.dp))
                )
                if (index != totalBoxes - 1) {
                    Spacer(modifier = Modifier.width(spaceBetween))
                }
            }
        }
    }
}