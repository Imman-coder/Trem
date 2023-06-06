package com.example.myapplication.presentation.navigation.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.domain.model.ClassType
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.presentation.findNear
import com.example.myapplication.presentation.intToTime
import com.example.myapplication.presentation.timeToInt
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

//class TimetableScreen : Fragment() {
//
//     private var receiver: BroadcastReceiver?=null
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        return ComposeView(requireContext()).apply {
//            val model = ViewModelProvider(requireActivity())[MainViewModel::class.java]
//            setContent {
//                val timetable =
//                    model.getLProfileStore().data.collectAsState(initial = Profile()).value.timetable
//                var currentTime by remember { mutableStateOf(0) }
//                var selectedDay by remember { mutableStateOf(-1) }
//                var todayDay by remember { mutableStateOf(0) }
//
//
//                fun UpdateTime() {
//                    Log.d("Intent","Time updated")
//                    val sdf = SimpleDateFormat("hh:mma")
//                    val currentDate = sdf.format(Date())
//                    currentTime = timeToInt(currentDate)
//
//                    val calendar = Calendar.getInstance()
//                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
//                    println(dayOfWeek)
//                    todayDay = dayOfWeek
//
//                    if(selectedDay==-1){
//                        selectedDay = todayDay
//                    }
//                }
//                UpdateTime()
//                if(receiver==null)
//                    receiver = object : BroadcastReceiver() {
//                        override fun onReceive(context: Context?, intent: Intent?) {
//                            UpdateTime()
//                        }
//                    }
//
//                Column {
//                    DaySelector(
//                        selectedDay = selectedDay,
//                        todayDay=todayDay
//                    ) { idx ->
//                        selectedDay = idx
//                    }
//                    if (timetable.EventList.isNotEmpty() && selectedDay > 0) {
//                        TableListViewer(
//                            timetable.EventList,
//                            timetable.EventTable[selectedDay],
//                            timetable.TimeList,
//                            currentTime
//                        )
//                    }
//                }
//
//            }
//        }
//    }
//
//
//    override fun onStart() {
//        super.onStart()
//
//        val intentFilter = IntentFilter().apply {
//            addAction(Intent.ACTION_TIME_TICK)
//            addAction(Intent.ACTION_TIME_CHANGED)
//            addAction(Intent.ACTION_TIMEZONE_CHANGED)
//            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
//        }
//        try {
//            context?.let { receiver?.let { it1 ->
//                it.registerReceiver(
//                    it1, intentFilter)
//            } }
//        }
//        catch (_:Exception){}
//    }
//
//    override fun onStop() {
//        super.onStop()
//        try {
//            context?.unregisterReceiver(receiver)
//        } catch (_: Exception) {}
//    }
//}

@Composable
fun DaySelector(selectedDay: Int,todayDay:Int, modifier: Modifier = Modifier.fillMaxWidth(), onSelect: (Int) -> Unit) {


    val dc = MaterialTheme.colorScheme.secondaryContainer

    fun canva(t: DrawScope) {
        return t.drawCircle(
            dc,
            radius = 16f,
            center = Offset(t.center.x, t.size.height)
        )
    }

    Column {
        LazyRow(
            modifier = modifier
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items(days.size) {
                val bg: Color
                val te: Color
                if (it == selectedDay) {
                    bg = MaterialTheme.colorScheme.primary
                    te = MaterialTheme.colorScheme.onPrimary
                } else {
                    bg = MaterialTheme.colorScheme.secondary
                    te = MaterialTheme.colorScheme.onSecondary
                }
                Column(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(bg)
                        .drawBehind {
                            if (it == todayDay) canva(this)
                        }
                        .clickable { onSelect(it) },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = days[it],
                        color = te,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "day",
                        color = te,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TableListViewer(
    eventList: List<Event>,
    eventLine: List<Int>,
    timelist: List<Int>,
    currentTime: Int
) {
    Log.d("TAG", "TableStarted")


    val backgroundColor: Color = MaterialTheme.colorScheme.background
    val mainColor: Color = MaterialTheme.colorScheme.primary

    val xOffest = .2f
    val yOffest = .2f
    val cardHeight = 60.dp
    val cardGap = 10.dp
    val circleRadius = 24f
    val gapbetween = cardHeight + cardGap
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0F)
    val textMeasure = rememberTextMeasurer()
    val textStyle =
        MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground)
    val allTopPadding = 10.dp

    val progress = remember {
        mutableStateOf(allTopPadding)
    }

    val context = LocalContext.current
    val totalContentHeight = remember {
        mutableStateOf(gapbetween * (timelist.size))
    }
    val dowelColor = MaterialTheme.colorScheme.onSurface


    var ln = 0;
    val nev = timelist.findNear(currentTime)
    val tdiff: Int
    val tp: Int
    val tpprogress: Float

    if (nev == 0 && currentTime < timelist[0]) {
        progress.value = allTopPadding
    } else if (currentTime > timelist[timelist.size - 1]) {
        progress.value =
            (cardHeight * (timelist.size - 1)) + allTopPadding + (cardGap / 2) + (cardGap * (timelist.size - 1))
    } else if (nev < timelist.size - 1) {
        tdiff = timelist[nev + 1] - timelist[nev]
        tp = currentTime - timelist[nev]
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
            for (x in timelist.indices) {
                drawText(
                    textMeasurer = textMeasure,
                    text = intToTime(timelist[x]),
                    topLeft = Offset(
                        0f,
                        gapbetween.toPx() * x + (allTopPadding.toPx() * .4f)
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
                TimetableCard(
                    Modifier
                        .clickable { Log.d("TAG", "touched..") },
                    eventList[x - 1],
                    ln,
                    timelist
                )
                ln += (eventList[x - 1].time_span)
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

@Composable
fun TimetableCard(modifier: Modifier = Modifier, event: Event, numElem: Int, timelist: List<Int>) {

    val cardHeight = (60.dp * event.time_span) + (10.dp * (event.time_span - 1))

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
                text = "${intToTime(timelist[numElem])} - ${intToTime(timelist[numElem + event.time_span])}",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (event.class_type != ClassType.Theory) {
            val t = when (event.class_type) {
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
private fun Preview() {
    MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
//            val s =
//                """{"EventTable":[[1,2,3,4,5,6],[5,4,7,8,3,1,9,10],[4,8,7,1,3,11],[5,9,4,12,3,1,6,10],[1,13,3,8,15,5],[15,5,6,8,3]],"TimeList":[555,610,665,720,775,825,875,925,975],"EventList":{"1":{"time_span":1,"subjects":[{"subject":"Design Analysis Algorithm","subject_code":"","teacher":"Sikheresh Barik"}],"class_type":0},"2":{"time_span":"3","subjects":[{"subject":"Design Analysis Algorithm","subject_code":"","teacher":"Sikheresh Barik / S G"}],"class_type":"1"},"3":{"time_span":1,"subjects":[{"subject":"Lunch Break","subject_code":"","teacher":""}],"class_type":"2"},"4":{"time_span":1,"subjects":[{"subject":"Java Programming","subject_code":"","teacher":"Mousami Acharya"}],"class_type":0},"5":{"time_span":1,"subjects":[{"subject":"Theory Of Computation","subject_code":"","teacher":"Sudeep Gochayat"}],"class_type":0},"6":{"time_span":1,"subjects":[{"subject":"Organizational Behavior","subject_code":"","teacher":"Sushant Kuma N"}],"class_type":0},"7":{"time_span":1,"subjects":[{"subject":"Discrete Structure","subject_code":"","teacher":"Ranjak Kumar Jati"}],"class_type":0},"8":{"time_span":1,"subjects":[{"subject":"Computer Architecture Organization","subject_code":"","teacher":"S T"}],"class_type":0},"9":{"time_span":1,"subjects":[{"subject":"Soft Skill","subject_code":"","teacher":"Mamta Banarjee"}],"class_type":0},"10":{"time_span":1,"subjects":[{"subject":"Library","subject_code":"","teacher":""}],"class_type":"2"},"11":{"time_span":"3","subjects":[{"subject":"Java Programming","subject_code":"","teacher":"Mousami Acharya / R D"}],"class_type":"1"},"12":{"time_span":1,"subjects":[{"subject":"Constution Of India","subject_code":"","teacher":"Leena Patnaik"}],"class_type":0},"13":{"time_span":"3","subjects":[{"subject":"M A T","subject_code":"","teacher":"S T / N T"}],"class_type":"1"},"14":{"time_span":1,"subjects":[],"class_type":0},"15":{"time_span":1,"subjects":[{"subject":"Discrete Structure","subject_code":"","teacher":"S K"}],"class_type":0}}}"""
//            val gson = Gson()
//
//            val k =
//                TimetableDtoMapper().mapToDomainModel(gson.fromJson(s, TimetableDto::class.java))
//            DaySelector(k)
        }
    }
}

fun pxToDp(px: Float): Dp {
    return (px / Resources.getSystem().displayMetrics.density).dp
}

