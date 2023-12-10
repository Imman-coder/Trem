package com.immanlv.trem.presentation.screens.timetable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Subject
import com.immanlv.trem.presentation.screens.timetable.util.intToTime


@Composable
fun TimetableCard2(modifier: Modifier = Modifier, event: Event, numElem: Int, timeList: List<Int>) {



    val cardHeight = (60.dp * (event.timeSpan)) + (10.dp * (event.timeSpan -1 ))

    val eventName = event.subjects[0].subject

    Box(
        modifier = modifier
            .padding(vertical = 4.90.dp)
            .height(cardHeight)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 10.dp),

        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = eventName, color = MaterialTheme.colorScheme.onPrimary)
            Text(
                text = "${intToTime(timeList[numElem])} - ${intToTime(timeList[numElem + event.timeSpan])}",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (event.classType != ClassType.Theory) {
            val t = when (event.classType) {
                ClassType.Notice -> "Notice"
                ClassType.Lab -> "Lab"
                else -> ""
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(5.dp),
                    text = t,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

    }
}


@Preview
@Composable
fun Preview() {
    TimetableCard(event = Event(0, listOf(Subject(subject="Design Analysis Algorithm", subjectCode ="", teacher="Sikheresh Barik")), ClassType.Lab))
}