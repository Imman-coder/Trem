package com.immanlv.trem.presentation.screens.timetable.components

import androidx.compose.runtime.Composable
import com.immanlv.trem.presentation.screens.timetable.util.SubjectSummaryHolder
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.title

@Composable
fun SubjectSummaryCardDialog(state:MaterialDialogState, details: SubjectSummaryHolder, onDismiss:()->Unit ) {
    MaterialDialog(
        dialogState = state,
        onCloseRequest = { onDismiss() }
    ) {
        title("Sumary")
        message(details.toString())
    }
}