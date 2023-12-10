package com.immanlv.trem.presentation.screens.timetableBuilder.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp


@Composable
fun AdderCard(
    row: Int,
    addEvent: (Int) -> Unit,
    modifier: Modifier = Modifier,
    moveTo: (from: Pair<Int, Int>, to: Pair<Int, Int>) -> Unit
) {
    var a by remember{
        mutableStateOf(row)
    }

    LaunchedEffect(key1 = row, block = {a = row})


    DragDropItem(enableDrag = false, onClick = {
        addEvent(a)
    }) { isHovered, data ->
        if (isHovered && data != null) {
            Log.d("TAG", "Dragged Item: ${Pair(row, 99)} -> $data")
            moveTo(data as Pair<Int, Int>, Pair(row, 99))
        }

        Box(
            modifier = modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.onSecondary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "+")
            }
            if (isHovered) {
                val dowelColor = MaterialTheme.colorScheme.tertiary
                Box(modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(dowelColor)
                    .drawBehind {
                        drawCircle(
                            dowelColor,
                            radius = 12f,
                            center = Offset(4f, 0f)
                        )
                    })
            }
        }
    }
}
