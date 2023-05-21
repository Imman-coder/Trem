package com.example.myapplication.presentation.navigation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import com.example.myapplication.R
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.ResultSubject
import com.example.myapplication.domain.model.Sem
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import kotlin.math.ceil



@Composable
fun ProfileScreen(profileStore:DataStore<Profile>) {

    val profile = profileStore.data.collectAsState(initial = Profile()).value

    LaunchedEffect(key1 = profile.result){

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn{
            item {
                ProfileCard(profile)
                ResultSection(profile)
            }
        }
    }

}

@Composable
private fun ResultSection(profile:Profile) {
    val expanded = remember { mutableStateOf(-1) }
    Column(Modifier.padding(15.dp)) {
        Text(text = "Results")
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            profile.result.sems.map {
                SemCard(it, it.sem == expanded.value) {
                    if (expanded.value != it.sem) {
                        expanded.value = it.sem
                    } else {
                        expanded.value = -1
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.padding(50.dp))
        }
//    }
}

@Composable
private fun SemCard(sem: Sem, expanded: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(15.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            .noRippleClickable { onClick() }
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${sem.sem}", color = MaterialTheme.colorScheme.onPrimary)
            Text(text = "${sem.sgpa}", color = MaterialTheme.colorScheme.onPrimary)
        }

        val k = animateFloatAsState(
            targetValue = if (expanded) 1.0f else 0.0f,
            animationSpec = tween(durationMillis = 400)
        )
        Divider(Modifier.alpha(ceil(k.value.toDouble()).toFloat()))

        AnimatedVisibility(visible = expanded) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Subject",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.width(200.dp)
                    )

                        Text(
                            text = "Credit",
                            color = MaterialTheme.colorScheme.onPrimary,

                            )
                        Text(
                            text = "Grade",
                            color = MaterialTheme.colorScheme.onPrimary,

                            )

                }
                for (x in sem.subjects) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = x.name,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.width(200.dp)
                        )
                        Row(
                            Modifier.width(100.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "${x.credit}",
                                color = MaterialTheme.colorScheme.onPrimary,

                                )
                            Text(
                                text = "${x.grade}",
                                color = MaterialTheme.colorScheme.onPrimary,

                                )
                        }
                    }
                    Spacer(modifier = Modifier.padding(3.dp))
                }
            }


        }
    }

}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
private fun ProfileCard(profile: Profile) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "",
            Modifier
                .clip(
                    CircleShape
                )
                .size(150.dp)
        )
        Text(text = profile.name, color = MaterialTheme.colorScheme.onPrimary)
        Text(
            text = profile.redgno.toString(),
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(PaddingValues(vertical = 8.dp)),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.program,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(text = "Program", color = MaterialTheme.colorScheme.onPrimary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.result.cgpa.toString(),
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(text = "CGPA", color = MaterialTheme.colorScheme.onPrimary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.sem.toString(),
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(text = "Semester", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

data class Result(val sem: Int, val sgpa: Double, val topics: List<Topic>)
data class Topic(val sub: String, val credit: Int, val grade: Char);

var Results = listOf(
    Result(
        2, 8.57, listOf(
            Topic("1", 0, 'A'),
            Topic("no", 3, 'E'),
        )
    ),
    Result(
        1, 8.32, listOf(
            Topic("ok", 0, 'A'),
            Topic("no", 3, 'E'),
        )
    ),
    Result(
        3, 8.52, listOf(
            Topic("ok", 0, 'A'),
            Topic("no", 3, 'E'),
        )
    ),
)


@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    MyApplicationTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//            ProfileScreen()
        }
    }
}