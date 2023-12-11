package com.immanlv.trem.presentation.screens.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable

@Composable
fun AttendanceCard2V(
    id: Int,
    item: AttendanceSubject,
    showDetails: (id: Int) -> Unit
) {
    val progress = item.present / (item.conducted).toFloat()
    val progressPercentage = (progress * 100).toInt()
    Card(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(15.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffe3dbfa)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(color = Color(0xffffe1cc))
                    .padding(15.dp)
            ) {
                Text("Last Updated: ${item.lastUpdated}")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .defaultMinSize(minHeight = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.fillMaxWidth(1f)) {
                    Text(text = item.code, fontWeight = FontWeight.Light)
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = item.name, fontWeight = FontWeight.Medium)
                        if(item.type == ClassType.Lab)
                            Row(modifier = Modifier
//                                .rotate(90F)
//                                .offset(y = (-10).dp)
                            ) {
                                TextChip(text = "LAB", color = Color(0xFFDCFFFE))
                            }
                    }

                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(progress = (progress), Modifier.fillMaxWidth(.85f))
                Text("${progressPercentage}%")
            }
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextChip(text = "Present: ${item.present}", color = Color(0xffffe1cc))
                TextChip(text = "Absent: ${item.absent}", color = Color(0xffffe1cc))
                TextChip(text = "Total: ${item.conducted}", color = Color(0xffffe1cc))
            }
        }
        Row(
            Modifier
                .height(70.dp)
                .fillMaxWidth(1f)
                .background(Color.White)
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Credit")
                Text(text = "${item.credit}")
            }
            Button(onClick = { showDetails(id) }) {
                Text(text = "View More")
            }
        }
    }

}

@Composable
fun AttendanceCard2(
    id: Int,
    item: AttendanceSubject,
    refreshLastUpdated: (String)-> Unit,
    showDetails: (id: Int) -> Unit
) {
    var progress = item.present / (item.conducted).toFloat()
    val progressPercentage = (progress * 100).toInt()
    if (progress.isNaN()) progress = 0.0f


    Card(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(15.dp)
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .padding(15.dp)
                    .noRippleClickable {
                        refreshLastUpdated(item.code)
                    }
            ) {
                Text("Last Updated: ${ item.lastUpdated }", color = MaterialTheme.colorScheme.onSecondary)

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .defaultMinSize(minHeight = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.fillMaxWidth(1f)) {
                    Text(text = item.code, fontWeight = FontWeight.Light)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.name, fontWeight = FontWeight.Medium)
                        if (item.type == ClassType.Lab)
                            Row(
                                modifier = Modifier
//                                .rotate(90F)
//                                .offset(y = (-10).dp)
                            ) {
                                TextChip(text = "LAB", color = MaterialTheme.colorScheme.tertiary,  textColor = MaterialTheme.colorScheme.onTertiary)
                            }
                    }

                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { (progress) },
                    modifier = Modifier.fillMaxWidth(.85f),
                )
                Text("${progressPercentage}%")
            }
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextChip(text = "Present: ${item.present}", color = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.onTertiary)
                TextChip(text = "Absent: ${item.absent}", color = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.onTertiary)
                TextChip(text = "Total: ${item.conducted}", color = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.onTertiary)
            }
        }
        Row(
            Modifier
                .height(70.dp)
                .fillMaxWidth(1f)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Credit", color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(text = "${item.credit}")
            }
            Button(onClick = { showDetails(id) }) {
                Text(text = "View More")
            }
        }
    }
}

@Composable
fun TextChip(text: String, color: Color,textColor:Color=Color.Unspecified) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = color)
            .padding(15.dp, 5.dp)
    ) {
        Text(text,color = textColor)
    }
}

@Preview
@Composable
fun AttendanceCard2Preview() {
//    AttendanceCard2(
//        0, AttendanceSubject(
//            name = "SOME UNKNOWN",
//            absent = 3,
//            present = 20,
//            conducted = 23,
//            code = "ETM202",
//            type = SubjectType.LAB,
//            credit = 3
//        )
//    ) {
//    }

}