package com.immanlv.trem.presentation.screens.timetableBuilder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.presentation.screens.timetableBuilder.util.simplifySubjectName
import com.immanlv.trem.presentation.screens.timetableBuilder.util.simplifyTeacherName


@Composable
fun SubjectCard(
    x: Int,
    y: Int,
    event: Event,
    selected:Boolean,
    onClick: () -> Unit = {},
    onLongClick: (it: DpOffset) -> Unit,
    onDrop: (from: Pair<Int, Int>, to: Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
) {
    DragDropItem(modifier = Modifier
        .fillMaxSize(),
        dataToDrop = Pair(x, y),
        onClick = {
            onClick()
        },
        onLongClick = {
            onLongClick(it)
        },
        dragPreview = {
            Column(
                modifier = Modifier
                    .size(50.dp, 100.dp)
            ) {
                val s =
                    "${simplifySubjectName(event.subjects[0].subject)}(${
                        simplifyTeacherName(event.subjects[0].teacher)
                    })"
                Text(text = s)
            }
        }
    ) { isHovered, data ->
        if (isHovered && data != null) {
            onDrop(data as Pair<Int, Int>, Pair(x, y))
        }

        Box(
            modifier = modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        width = 2.dp,
                        color = if(selected) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val s =
                    "${simplifySubjectName(event.subjects[0].subject)}(${
                        simplifyTeacherName(event.subjects[0].teacher)
                    })"
                Text(text = s)
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
