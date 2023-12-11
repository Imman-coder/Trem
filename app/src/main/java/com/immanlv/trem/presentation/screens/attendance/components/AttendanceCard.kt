package com.immanlv.trem.presentation.screens.attendance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.immanlv.trem.domain.model.AttendanceSubject
import com.immanlv.trem.domain.model.ClassType


@Composable
fun AttendanceCard(
    id:Int,
    item: AttendanceSubject,
    showDetails: () -> Unit
) {



}

@Preview
@Composable
fun AttendanceCardPreview() {
    AttendanceCard(
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