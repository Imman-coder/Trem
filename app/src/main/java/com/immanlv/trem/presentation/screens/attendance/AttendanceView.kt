package com.immanlv.trem.presentation.screens.attendance

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Attendance
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.SubjectType
import com.immanlv.trem.presentation.screens.attendance.components.AttendanceCard2

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AttendanceView(
    attendance: Attendance,
    attendanceUiState:AttendanceUiState,
    onEvent:(AttendanceViewEvent)->Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }

    when (attendanceUiState) {
        is AttendanceUiState.Idle -> {
            isRefreshing = false

        }

        is AttendanceUiState.Loading.Fetching -> {
            isRefreshing = true
        }

        is AttendanceUiState.Loading.Retrieving -> {
            Toast.makeText(LocalContext.current, "Attendance Sheet Refreshed!", Toast.LENGTH_SHORT)
                .show()
            isRefreshing = false
        }

        is AttendanceUiState.Error -> {
            isRefreshing = false

        }
    }

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { onEvent(AttendanceViewEvent.RefreshAttendanceView) })


    Box(
        modifier = Modifier.pullRefresh(pullRefreshState)
    ) {
    AttendanceViewColumn(
        attendance = attendance,
        refreshLastUpdated = { onEvent(AttendanceViewEvent.RefreshLateUpdated(it)) }
    )
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }

}

@Composable
private fun AttendanceViewColumn(attendance: Attendance,refreshLastUpdated: (String)-> Unit ={}) {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            itemsIndexed(attendance.subs) { id, item ->
                Row(modifier = Modifier.padding(vertical = 6.dp)) {
                    AttendanceCard2(id = id, item = item, refreshLastUpdated = refreshLastUpdated ) {

                    }
                }

            }

        }

}

@Preview
@Composable
private fun AttendanceViewColumnPreview() {
    AttendanceViewColumn(
        Attendance(
            subs = listOf(
                AttendanceSubject(
                    name = "SOME UNKNOWN 1",
                    absent = 4,
                    present = 10,
                    conducted = 14,
                    code = "ETM202",
                    type = SubjectType.THEORY,
                    credit = 3
                ),
                AttendanceSubject(
                    name = "SOME UNKNOWN 2",
                    absent = 3,
                    present = 20,
                    conducted = 23,
                    code = "ETM505",
                    type = SubjectType.THEORY,
                    credit = 1
                ),
                AttendanceSubject(
                    name = "SOME UNKNOWN 3",
                    absent = 10,
                    present = 13,
                    conducted = 23,
                    code = "ETM221",
                    type = SubjectType.LAB,
                    credit = 2
                )
            )
        )
    )
}