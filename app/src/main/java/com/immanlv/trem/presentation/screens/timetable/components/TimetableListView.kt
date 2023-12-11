package com.immanlv.trem.presentation.screens.timetable.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.presentation.screens.timetable.TimetableScreenEvent
import com.immanlv.trem.presentation.screens.timetable.util.getCurrentTimeIndexFromTime
import com.immanlv.trem.presentation.screens.timetable.util.intToTime
import com.immanlv.trem.presentation.screens.timetable.util.pxToDp


@Composable
fun TableListViewer(
    timetable: Timetable,
    week: Int,
    currentTime: Int,
    onEvent: (TimetableScreenEvent) -> Unit
) {
    Log.d("TAG", "TableStarted")

    val eventList = timetable.eventList
    val eventLine = timetable.eventTable[week]
    val timeList = timetable.timeList

    val backgroundColor: Color = MaterialTheme.colorScheme.background
    val mainColor: Color = MaterialTheme.colorScheme.primary

    val xOffest = .2f
    val yOffest = .2f
    val cardHeight = 60.dp
    val cardGap = 10.dp
    val circleRadius = 24f
    val gapBetween = cardHeight + cardGap
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0F)
    val textMeasure = rememberTextMeasurer()
    val textStyle =
        MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground)
    val allTopPadding = 10.dp

    val progress = remember {
        mutableStateOf(allTopPadding)
    }

    val totalContentHeight = remember {
        mutableStateOf(gapBetween * (timeList.size))
    }
    val dowelColor = MaterialTheme.colorScheme.onSurface


    var ln = 0;
    val nev = timetable.getCurrentTimeIndexFromTime(currentTime)
    val tdiff: Int
    val tp: Int
    val tpprogress: Float

    if (nev == 0 && currentTime < timeList[0]) {
        progress.value = allTopPadding
    } else if (currentTime > timeList[timeList.size - 1]) {
        progress.value =
            (cardHeight * (timeList.size - 1)) + allTopPadding + (cardGap / 2) + (cardGap * (timeList.size - 1))
    } else if (nev < timeList.size - 1) {
        tdiff = timeList[nev + 1] - timeList[nev]
        tp = currentTime - timeList[nev]
        tpprogress = tp / tdiff.toFloat()
        progress.value =
            cardHeight * (nev + tpprogress) + allTopPadding + cardGap / 2 + (cardGap * nev)
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .height(totalContentHeight.value)
                .width(80.dp)
        ) {
            drawLine(
                mainColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 2f,
                pathEffect = pathEffect
            )
            for (x in timeList.indices) {
                drawText(
                    textMeasurer = textMeasure,
                    text = intToTime(timeList[x]),
                    topLeft = Offset(
                        0f,
                        gapBetween.toPx() * x + (allTopPadding.toPx() * .4f)
                    ),
                    style = textStyle
                )
            }
        }

        Column(
            Modifier
                .padding(start = 80.dp)
                .padding(top = allTopPadding)
                .padding(start = pxToDp(circleRadius) * 2)

        ) {
            for (x in eventLine) {
                TimetableCard2(
                    Modifier
                        .clickable {
                            eventList[x - 1].subjects[0].subjectCode.let {
                                if (it.isNotEmpty()) onEvent(
                                    TimetableScreenEvent.ShowClassCard(it)
                                )
                            }

                            Log.d("TAG", "touched..")
                        },
                    eventList[x - 1],
                    ln,
                    timeList
                )
                ln += (eventList[x - 1].timeSpan)
            }
        }
        Canvas(
            modifier = Modifier
                .height(pxToDp(circleRadius) * 2)
                .fillMaxWidth()
                .offset(x = 0.dp, y = progress.value)
                .padding(start = 80.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Log.d("TAG", "Time divider touched")
                        }

                    )

                }
        ) {

            drawLine(
                dowelColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 4f,

                )
            drawCircle(
                dowelColor,
                radius = circleRadius - (circleRadius * .33f),
                center = Offset(0f, 0f)
            )

        }

    }
}
