package com.example.myapplication.presentation.navigation.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.AttendanceSubject
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.presentation.components.shimmerEffect
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme

@Composable
fun HomeScreen(attendanceState: DataState<Attendance>, profileState: DataState<Profile>) {

    val attendance = attendanceState.data
    val profile = profileState.data
    val isLoading = attendanceState.dataBy == DataState.DataBy.NotAvailable

    Column(Modifier.padding(horizontal = 15.dp)) {
        Text(
            text = "Hi ${profile?.name}",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge,
        )

        Text(text = "Attendance", style = MaterialTheme.typography.headlineLarge)

        var showExpandedAttendance by remember { mutableStateOf<AttendanceSubject?>(null) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (!isLoading) {
                items(attendance?.subs?.size?:0) { i ->
                    attendance?.subs?.get(i)?.let { AttendanceCard(it){showExpandedAttendance = it} }
                }
            }
            else {
                items(20) {
                    Spacer(modifier  = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .width(150.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .shimmerEffect())
                }
            }
        }
        if(showExpandedAttendance!=null){
            Dialog(onDismissRequest = { showExpandedAttendance=null }) {
                Card {
                    Column(Modifier.padding(15.dp)) {
                        showExpandedAttendance?.let {
                            Text(text = "Subject : ${it.name}")
                            Text(text = "Total : ${it.conducted}")
                            Text(text = "Present : ${it.present}")
                            Text(text = "Absent : ${it.absent}")
                         }
                    }
                }
            }
        }

    }
}

@Composable
private fun WelcomeCard() {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceTint,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(200.dp)
            .padding(12.dp)
    ) {
        Text(text = "Welcome")
    }
}

@Composable
private fun AttendanceCard(s: AttendanceSubject, onClick:()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .width(150.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        var progress = (s.present / s.conducted.toFloat())
        if (!progress.isNaN()) {
            CircularProgressbar3(
                progress = progress,
                foregroundIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                backgroundIndicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .4f),
                numberStyle = MaterialTheme.typography.labelSmall,
                size = 60.dp,
                indicatorThickness = 5.dp,
            )
        }

        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = getInitials(s.name),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = s.code,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,

                )
        }
    }


}

@Preview
@Composable
private fun MainPreview() {
    MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val profile = Profile(
                name = "John Doe",
                redgno = 101102341,
                phoneno = 123,
                sem = 3,
                program = "BTech"
            )
            Column(Modifier.padding(15.dp)) {
                Text(
                    text = "Hi ${profile.name}",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge,
                )
//                AttendanceListView(profile)

            }

        }
    }
}

fun getInitials(name: String): String {
    val words = name.split(" ")
    val initials = StringBuilder()

    for (word in words) {
        if (word.isNotEmpty() && word[0].isUpperCase()) {
            initials.append(word[0].uppercaseChar())
        }
    }

    return initials.toString()
}


@Composable
fun CircularProgressbar3(
    progress: Float = .0f,
    numberStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    size: Dp = 60.dp,
    indicatorThickness: Dp = 8.dp,
    foregroundIndicatorColor: Color = MaterialTheme.colorScheme.primaryContainer,
    backgroundIndicatorColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {
            drawArc(
                color = backgroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = progress * 360

            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(progress * 100).toInt()}%",
            color = MaterialTheme.colorScheme.onPrimary,
            style = numberStyle
        )
    }

    Spacer(modifier = Modifier.height(32.dp))
}
