package com.immanlv.trem.presentation.screens.timetable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.util.Constants.WeekList
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable
import com.immanlv.trem.presentation.screens.timetable.components.SubjectSummaryCardDialog
import com.immanlv.trem.presentation.screens.timetable.util.getSystemDayOfWeekInt
import com.immanlv.trem.presentation.screens.timetable.components.TableListViewer
import com.immanlv.trem.presentation.screens.timetable.util.SubjectSummaryHolder
import com.immanlv.trem.presentation.screens.timetable.util.getSystemTimeInt
import com.immanlv.trem.presentation.screens.timetable.util.shimmerEffect
import com.immanlv.trem.presentation.screens.timetable.util.toPx
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Tts(
    timetable: Timetable,
    timetableScreenState: TimetableScreenState,
    onEvent: (TimetableScreenEvent) -> Unit
) {

    val context = LocalContext.current
    val dialogState = rememberMaterialDialogState()
    var subjectSummaryHolder by remember {
        mutableStateOf<SubjectSummaryHolder?>(null)
    }

    var currentTime by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(-1) }
    var todayDay by remember { mutableIntStateOf(0) }

    fun updateTime() {
        println("Updated Time")

        currentTime = getSystemTimeInt()

        todayDay = getSystemDayOfWeekInt()

        if (selectedDay == -1) {
            selectedDay = if (todayDay in 0..5) todayDay
            else 0
            Log.d("today", selectedDay.toString())
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

//        context.registerReceiver(receiver, intentFilter)

        onDispose {
//            context.unregisterReceiver(receiver)
        }
    }


    LaunchedEffect(Unit) {
        updateTime()
    }

    val kSize by remember { mutableStateOf(false) }
    val cSize: Float by animateFloatAsState(targetValue = if (kSize) 1f else .8f, label = "")


    var isTimetableLoading by remember { mutableStateOf(false) }

    when (timetableScreenState.timetableState) {
        is TimetableState.Error -> {
            isTimetableLoading = false
        }

        TimetableState.Loading.Fetching -> {
            isTimetableLoading = true
        }

        TimetableState.Idle -> {
            isTimetableLoading = false
        }

        TimetableState.Loading.Retrieving -> {
            isTimetableLoading = false
        }
    }

    when (timetableScreenState.classCardState) {
        ClassCardState.Idle -> {
            subjectSummaryHolder = null
            dialogState.hide()
        }

        is ClassCardState.ShowClassDetailCardState -> {
            subjectSummaryHolder = timetableScreenState.classCardState.data
            dialogState.show()
        }
    }


    val pullRefreshState = rememberPullRefreshState(refreshing = isTimetableLoading,
        onRefresh = { onEvent(TimetableScreenEvent.RefreshTimetable) })

    subjectSummaryHolder?.let {
        SubjectSummaryCardDialog(dialogState, it) { onEvent(TimetableScreenEvent.HideClassCard) }
    }

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            if (timetable.error == DataErrorType.NoDataFound) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .pullRefresh(pullRefreshState),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Timetable Not Found")
                }
            } else {
                Row(modifier = Modifier) {
                    Column(
                        Modifier.fillMaxWidth(cSize)
                    ) {
                        if (timetable != Timetable() && (selectedDay in 0..6)) {
                            println(timetable)
                            TableListViewer(
                                timetable, selectedDay, currentTime, onEvent
                            )
                        } else {
                            LazyColumn(
                                Modifier.padding(horizontal = 10.dp)
                            ) {
                                items(7) {
                                    Row {
                                        Row(Modifier.width(80.dp)) {
                                            Spacer(
                                                modifier = Modifier
                                                    .width(30.dp)
                                                    .height(8.dp)
                                                    .shimmerEffect()
                                            )
                                        }
                                        Spacer(
                                            modifier = Modifier
                                                .padding(vertical = 4.90.dp)
                                                .fillMaxWidth(1f)
                                                .height(70.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .shimmerEffect()
                                        )
                                    }
                                }
                            }
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
                        val starto: Float by animateFloatAsState(targetValue = ((selectedDay) * 75).toPx())
                        val bg = MaterialTheme.colorScheme.background
                        val dot = MaterialTheme.colorScheme.tertiary

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    if (selectedDay in 0..6) {

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
                                            trianglePath, bg, style = Stroke(
                                                width = 1.dp.toPx(),
                                                pathEffect = PathEffect.cornerPathEffect(4.dp.toPx())
                                            )
                                        )
                                        drawPath(
                                            trianglePath2, bg
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
                                    bg =
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .5f)
                                    te = MaterialTheme.colorScheme.onSecondaryContainer
                                }
                                Column(
                                    Modifier
                                        .size(50.dp)
                                        .noRippleClickable { selectedDay = k }
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(bg),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(text = WeekList[k][0].uppercase(Locale.ROOT), color = te)
                                }
                                Spacer(modifier = Modifier.size(25.dp))
                            }
                        }

                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isTimetableLoading,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}