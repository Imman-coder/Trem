package com.immanlv.trem.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Event


@Composable
fun HomeTimetableCard(event: Event? = null) {
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
                Text(text = event?.classType.toString() ?: "")
            }

        }

    }
}