package com.immanlv.trem.presentation.screens.attendance.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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

@Composable
fun AttendanceCard3(
    id: Int,
    item: AttendanceSubject,
    showDetails: (Int) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Present")
                Row(
                    Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Cyan)) {
                    Image(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = "Show Details"
                    )
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = item.code, fontWeight = FontWeight.Light)
                        TextChip(text = "Lab", color = Color.Cyan)
                    }
                    Text(text = item.name, fontWeight = FontWeight.Medium)
                }
                Column(
                    Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Cyan)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(text = "Last Updated")
                    Text(text = item.lastUpdated)

                }
            }
        }
    }

}

@Preview
@Composable
fun AttendanceCard3Preview() {
    AttendanceCard3(
        0, AttendanceSubject(
            name = "SOME UNKNOWN",
            absent = 3,
            present = 20,
            conducted = 23,
            code = "ETM202",
            type = ClassType.Lab,
            credit = 3
        )
    ) {

    }

}