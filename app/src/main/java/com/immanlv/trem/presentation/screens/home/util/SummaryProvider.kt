package com.immanlv.trem.presentation.screens.home.util

import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.util.DataErrorType


fun Timetable.summaryProvider(): String {
    var labSummary = ""
    var classSummary = ""
    var finalSummary = ""

    val numClass = this.upcomingClassCount.first
    val numLab = this.upcomingClassCount.second

    if( error == DataErrorType.NoDataFound ) return "Please Contact the Admin.(No Timetable Found)"
    if( !hasClassesToday ) return "There aren't any class today!"
    if( classFinished ) return "No more class for today"


    // If there is only one Lab left
    if (numLab == 1) {
        labSummary = "a lab"
    }
    // If there are more labs
    else if (numLab > 1) {
        labSummary = "$numLab labs"
    }

    // If there is only one Class left
    if (numClass == 1) {
        classSummary = "$numClass class"
    }
    // If there are more classes
    else if (numClass > 1) {
        classSummary = "$numClass classes"
    }


    finalSummary = if (labSummary.isNotEmpty() && classSummary.isNotEmpty())
        "You've $classSummary and $labSummary"
    else
        "You've $classSummary $labSummary"

    finalSummary += if( classStarted ) "left" else "today"

    return finalSummary
}