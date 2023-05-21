package com.example.myapplication.presentation.navigation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import com.example.myapplication.domain.model.AttendanceSubject
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Dashboard
    constructor(
        private val profileStore: DataStore<Profile>
    )
    :Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(profileStore = profileStore)
            }
        }
    }
}

@Composable
fun HomeScreen(profileStore: DataStore<Profile>) {
    val profile = profileStore.data.collectAsState(initial = Profile()).value
    LaunchedEffect(key1 = profile) {
        Log.d("TAG2", profile.attendance.toString())
    }
    Column(Modifier.padding(15.dp)) {
        Text(
            text = "Hi ${profile.name}",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge,
        )
        AttendanceListView(profile)

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
private fun AttendanceListView(profile: Profile) {
    Text(text = "Attendance", style = MaterialTheme.typography.headlineLarge)
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(200.dp)
    ) {
        items(profile.attendance.size) { i ->
            AttendanceCard(profile.attendance[i])
        }
        item { Spacer(modifier = Modifier.padding(100.dp)) }

    }

}

@Composable
private fun AttendanceCard(s: AttendanceSubject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .width(150.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CircularProgressbar3(
            progress = s.present / (s.present + s.absent).toFloat(),
            foregroundIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
            numberStyle = MaterialTheme.typography.labelSmall,
            size = 60.dp,
            indicatorThickness = 5.dp,
        )

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
//            HomeScreen()
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
    progress: Float = .70f,
    numberStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    size: Dp = 60.dp,
    indicatorThickness: Dp = 8.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    foregroundIndicatorColor: Color = MaterialTheme.colorScheme.primaryContainer,
    backgroundIndicatorColor: Color = Color.LightGray.copy(alpha = 0.0f)
) {


    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = progress * 100,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {

            // Background circle
            drawCircle(
                color = backgroundIndicatorColor,
                radius = size.toPx() / 2,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = (animateNumber.value / 100) * 360

            // Foreground circle
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

        // Text that shows number inside the circle
        Text(
            text = "${(progress * 100).toInt()}%",
            color = MaterialTheme.colorScheme.onPrimary,
            style = numberStyle
        )
    }

    Spacer(modifier = Modifier.height(32.dp))
}
