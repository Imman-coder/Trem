package com.example.myapplication.presentation.navigation.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.presentation.timeToInt
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun Tts(model: mainViewModel) {

    val context = LocalContext.current
    val timetable = model.getLProfileStore().data.collectAsState(initial = Profile()).value.timetable
//    val timetable = Timetable()

    var currentTime by remember { mutableStateOf(0) }
    var selectedDay by remember { mutableStateOf(-1) }
    var todayDay by remember { mutableStateOf(0) }

    fun updateTime() {
        println("Updated Time")
        val sdf = SimpleDateFormat("hh:mma")
        val currentDate = sdf.format(Date())
        currentTime = timeToInt(currentDate)

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
        todayDay = dayOfWeek

        if (selectedDay == -1) {
            selectedDay = todayDay
        }
    }


    DisposableEffect(Unit) {

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateTime()
            }
        }

        context.registerReceiver(receiver, intentFilter)



        onDispose {
            context.unregisterReceiver(receiver)
        }
    }


    LaunchedEffect(Unit) {
        updateTime()
    }


    Row {
        Column(Modifier.fillMaxWidth(.8f)) {
//            DaySelector(
//                selectedDay = selectedDay,
//                todayDay = todayDay
//            ) { idx ->
//                selectedDay = idx
//            }
            if (timetable.EventList.isNotEmpty()) {
                TableListViewer(
                    timetable.EventList,
                    timetable.EventTable[selectedDay],
                    timetable.TimeList,
                    currentTime
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val starto : Float by animateFloatAsState(targetValue = ((selectedDay) * 75).toPx())
            val bg = MaterialTheme.colorScheme.background
            val dot = MaterialTheme.colorScheme.tertiary

            Column(
                Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (selectedDay >= 0) {

                            val height = 50.dp.toPx()
                            val width = 12.dp.toPx()
                            val startOffset = starto

                            val trianglePath = Path().apply {
                                moveTo(-2f, 6f + startOffset)
                                lineTo(-2f, -5f + startOffset)
                                lineTo(0f, 10f + startOffset)
                                lineTo(width, height / 2 + startOffset)
                                lineTo(0f, height - 10 + startOffset)
                                lineTo(-2f, height + 5 + startOffset)
                            }
                            val trianglePath2 = Path().apply {
                                moveTo(-2f, 6f + startOffset)
                                lineTo(-2f, 6f + startOffset)
                                lineTo(width - 2, height / 2 - 2 + startOffset)
                                lineTo(width - 2, height / 2 + 2 + startOffset)
                                lineTo(-1f, height - 9 + startOffset)
                            }
                            drawPath(
                                trianglePath,
                                bg,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.cornerPathEffect(4.dp.toPx())
                                )
                            )
                            drawPath(
                                trianglePath2,
                                bg
                            )
                            drawCircle(
                                dot,
                                radius = 8f,
                                center = Offset(width / 3, height / 2 + startOffset)
                            )
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (k in 0..5) {
                    val bg: Color
                    val te: Color
                    if (k == selectedDay) {
                        bg = MaterialTheme.colorScheme.secondaryContainer
                        te = MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        bg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .5f)
                        te = MaterialTheme.colorScheme.onSecondaryContainer
                    }
                    Column(
                        Modifier
                            .size(50.dp)
                            .clickable { selectedDay = k }
                            .clip(RoundedCornerShape(15.dp))
                            .background(bg),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = days[k][0].uppercase(Locale.ROOT), color = te)
                    }
                    Spacer(modifier = Modifier.size(25.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        MyApplicationTheme {
//            Tts()
        }
    }
}

fun Int.toPx():Float = (this * Resources.getSystem().displayMetrics.density)