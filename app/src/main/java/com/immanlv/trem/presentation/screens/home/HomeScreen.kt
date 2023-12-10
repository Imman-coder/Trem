package com.immanlv.trem.presentation.screens.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.model.util.filter
import com.immanlv.trem.presentation.screens.home.components.HomeTimetableCard
import com.immanlv.trem.presentation.screens.home.util.summaryProvider
import com.immanlv.trem.presentation.screens.timetable.util.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profile: Profile,
    timetable: Timetable,
) {
    var classNotificationSummary by remember { mutableStateOf("") }
    var isUpcomingClassTruncated by remember { mutableStateOf(true) }
    var ongoingClass by remember { mutableStateOf<Event?>(null) }
    var upcomingClass by remember { mutableStateOf<List<Event>>(listOf()) }

    var isLoading by remember { mutableStateOf(false) }

    fun updateScreenApi() {
        if(timetable == Timetable()){
            isLoading = true
            return
        }
        isLoading = false

        ongoingClass = timetable.ongoingClass
        upcomingClass = timetable.upcomingClasses.filter(listOf(ClassType.Lab, ClassType.Theory))
        classNotificationSummary = timetable.summaryProvider()
    }
    LaunchedEffect(key1 = timetable, block = {
//        Log.d("TAG", "Timetable: $timetable")
        updateScreenApi()
    })


    // Update Screen every minute to keep in sync
    DisposableEffect(Unit) {

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateScreenApi()
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
                HomeTimetableCard(
                    ongoingClass!!
                )
            }
        }
        if (upcomingClass.isNotEmpty()) {
            Column {
                Text(text = "Upcoming Classes")
                when (upcomingClass.size) {
                    0 -> {}

                    1 -> {
                        HomeTimetableCard(upcomingClass[0])
                    }

                    2 -> {
                        HomeTimetableCard(upcomingClass[0])
                        HomeTimetableCard(upcomingClass[1])
                    }

                    else -> {
                        if (isUpcomingClassTruncated) {
                            HomeTimetableCard(upcomingClass[0])
                            Box(contentAlignment = Alignment.BottomCenter) {
                                HomeTimetableCard(upcomingClass[1])
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
                                            isUpcomingClassTruncated = false
                                        })
                                }
                            }
                        } else {
                            upcomingClass!!.forEach { event ->
                                HomeTimetableCard(event)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            Profile(
                name = "",
                regdno = 2101229079
            ),
            Timetable()
        )
    }

}