package com.immanlv.trem.presentation.screens.timetableBuilder.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.immanlv.trem.presentation.screens.timetable.util.intToTime
import com.immanlv.trem.presentation.screens.timetable.util.timeToInt
import com.immanlv.trem.presentation.screens.timetableBuilder.EntryData
import com.immanlv.trem.presentation.screens.timetableBuilder.PickerData
import com.immanlv.trem.presentation.screens.timetableBuilder.TimePickerData
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.input
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CustomTimePicker(
    data: PickerData
) {

    val state = MaterialDialogState()

    when (data) {
        is TimePickerData -> {
            var pickedTime by remember {
                mutableStateOf(
                    try {
                        LocalTime.parse(
                            intToTime(data.data),
                            DateTimeFormatter.ofPattern("hh:mma")
                        )
                    } catch (_: Exception) {
                        LocalTime.MIDNIGHT
                    }
                )
            }

            state.show()

            MaterialDialog(dialogState = state, buttons = {
                positiveButton("Ok") {
                    data.onSet(
                        timeToInt(
                            DateTimeFormatter
                                .ofPattern("hh:mma")
                                .format(pickedTime)
                        )
                    )
                }
                negativeButton("Dismiss") {
                    data.onDismiss()
                }
            }, onCloseRequest = { data.onDismiss() }) {
                timepicker(
                    initialTime = pickedTime,
                    title = "Pick time",
                ) { pickedTime = it }
            }
        }

        is EntryData -> {

            var input by remember { mutableStateOf(data.data) }

            state.show()

            MaterialDialog(dialogState = state, buttons = {
                positiveButton("Ok") {
                    data.onSet(input)
                }
                negativeButton("Dismiss") {
                    data.onDismiss()
                }
            }, onCloseRequest = { data.onDismiss() }) {
                this.input(label = data.title) {
                    input = it
                }
            }
        }
    }
}
