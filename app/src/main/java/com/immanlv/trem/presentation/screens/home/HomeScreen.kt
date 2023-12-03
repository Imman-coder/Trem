package com.immanlv.trem.presentation.screens.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.presentation.screens.timetable.util.getCurrentEventTableIndexFromTime
import com.immanlv.trem.presentation.screens.timetable.util.getSystemDayOfWeekInt
import com.immanlv.trem.presentation.screens.timetable.util.getSystemTimeInt

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val timetable = viewModel.timetable.value
    HomeScreenView(profile = viewModel.profile.value, timetable)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenView(profile: Profile, timetable: Timetable) {


    var classNotificationSummary by remember {
        mutableStateOf("")
    }

    var truncatedUpcomingClasses by remember { mutableStateOf(true) }
    val context = LocalContext.current

    var ongoingClass by remember {
        mutableStateOf<Event?>(null)
    }
    var upcomingClass by remember { mutableStateOf<List<Event>?>(null) }


    fun updateScreen() {
        if (timetable == Timetable()) return

        // If No timetable found
        if(timetable.Error == DataErrorType.NoDataFound ) {
            classNotificationSummary = "Please Contact the Admin.(No Timetable Found)"
            return
        }

        // Has Classes for current Day
        if (getSystemDayOfWeekInt() in 0..timetable.EventTable.size && timetable.EventTable[getSystemDayOfWeekInt()].isNotEmpty()) {
            val classes: Pair<Int, Int>

            // If Classes has not started
            if (getSystemTimeInt() < timetable.TimeList[0]) {
                // Clearing previous classes
                ongoingClass = null
                val k = mutableListOf<Event>()

                val todayClasses = timetable.EventTable[getSystemDayOfWeekInt()]
                todayClasses.forEach {
                    println(it)
                    k += timetable.EventList[it - 1]
                }
                upcomingClass = k.toList()

                // Generating summary
                classes = countClasses(timetable = timetable)
                classNotificationSummary =
                    "You've ${provideSummary(classes.first, classes.second)} today"
            }
            // If Classes are going on
            else if (getSystemTimeInt() in timetable.TimeList[0]..timetable.TimeList[timetable.TimeList.size - 1]) {

                // Clearing previous classes
                ongoingClass = null
                upcomingClass = listOf()

                // Grouping current class and upcoming class
                val currentEventIndex =
                    timetable.getCurrentEventTableIndexFromTime(getSystemTimeInt())
                val todayClasses = timetable.EventTable[getSystemDayOfWeekInt()]
                ongoingClass = timetable.EventList[todayClasses[currentEventIndex] - 1]
                for (x in currentEventIndex + 1 until todayClasses.size) {
                    upcomingClass = upcomingClass!! + timetable.EventList[todayClasses[x] - 1]
                }

                // Generating summary for the day
                val k = mutableListOf<Event>()
                k += ongoingClass!!
                for (upc in upcomingClass!!) {
                    k += upc
                }
                classes = countClassesFromList(k.toList())
                classNotificationSummary =
                    "You've ${provideSummary(classes.first, classes.second)} left"
            }
            // If Classes are over
            else {
                classNotificationSummary = "No more classes for today!"
            }


        }
        // If there's no class on current day
        else {
            classNotificationSummary = " There aren't any class today!"
            Log.d("TAG", "updateScreen: Day : ${getSystemDayOfWeekInt()}")
        }
    }

    if (timetable != Timetable())
        LaunchedEffect(key1 = timetable, block = {
            Log.d("TAG", "Timetable: $timetable")
            updateScreen()
        })

    DisposableEffect(Unit) {

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateScreen()
            }
        }

//        context.registerReceiver(receiver, intentFilter)

        onDispose {
//            context.unregisterReceiver(receiver)
        }
    }

    Column(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        PullToRefreshContainer(state = rememberPullToRefreshState())
        Row {
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Hello, ")
                }
                withStyle(style = SpanStyle(color = Color.Unspecified)) {
                    append(profile.name)
                }
            })
        }
        Text(
            text = classNotificationSummary,
            style = MaterialTheme.typography.titleLarge
        )
        if (ongoingClass != null) {
            Column {
                Text(text = "Ongoing Class")
                TimetableCard(
                    ongoingClass
                )
            }
        }
        if(upcomingClass != null) {
            Column {
                Text(text = "Upcoming Classes")
                when (upcomingClass!!.size) {
                    0 -> {}

                    1 -> {
                        TimetableCard(upcomingClass!![0])
                    }

                    2 -> {
                        TimetableCard(upcomingClass!![0])
                        TimetableCard(upcomingClass!![1])
                    }

                    else -> {
                        if (truncatedUpcomingClasses) {
                            TimetableCard(upcomingClass!![0])
                            Box(contentAlignment = Alignment.BottomCenter) {
                                TimetableCard(upcomingClass!![1])
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    MaterialTheme.colorScheme.surface.copy(alpha = .6f),
                                                    MaterialTheme.colorScheme.surface
                                                )
                                            )
                                        ),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        "Show more",
                                        modifier = Modifier.clickable {
                                            truncatedUpcomingClasses = false
                                        })
                                }
                            }
                        } else {
                            upcomingClass!!.forEach { event ->
                                TimetableCard(event)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

fun provideSummary(numClass: Int, numLab: Int): String {
    var labSummary = ""
    var classSummary = ""
    var finalSummary = ""
    if (numLab == 0 && numClass == 0) return "no class"
    if (numLab == 1) {
        labSummary = "a lab"
    } else if (numLab > 1) {
        labSummary = "$numLab labs"
    }
    if (numClass == 1) {
        classSummary = "a class"
    } else if (numClass > 1) {
        classSummary = "$numClass classes"
    }
    finalSummary = if (labSummary.isNotEmpty() && classSummary.isNotEmpty())
        "$classSummary and $labSummary"
    else "$classSummary $labSummary"
    return finalSummary

}

@Composable
fun TimetableCard(event: Event? = null) {
    val subject = event?.subjects?.get(0)?.subject ?: "Operating System"
    val teacher = event?.subjects?.get(0)?.teacher ?: "Hello Hello"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp, horizontal = 15.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text(text = subject)
                Text(text = teacher)

            }
            Column {
                Text(text = event?.class_type.toString() ?: "")
            }

        }

    }
}

fun countClassesFromList(list: List<Event>): Pair<Int, Int> {
    var countClasses = 0
    var countLabs = 0
    list.forEach { i ->
        if (i.class_type == ClassType.Lab)
            countLabs++
        else if (i.class_type == ClassType.Theory)
            countClasses++
    }
    return Pair(countClasses, countLabs)
}

fun countClasses(timetable: Timetable): Pair<Int, Int> {
    var currentDay = getSystemDayOfWeekInt()
    val l = mutableListOf<Event>()
    if (currentDay !in 0..5) currentDay = 0
    for (i in timetable.EventTable[currentDay]) {
        l += timetable.EventList[i - 1]
    }
    return countClassesFromList(l)
}


@Preview
@Composable
private fun HomeScreenPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeScreenView(
            Profile(
                name = "Immanuel Mundary",
                regdno = 2101229079
            ),
            Timetable()
        )
    }

}