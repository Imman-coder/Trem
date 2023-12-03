package com.immanlv.trem.presentation.screens.timetable.util

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun pxToDp(px: Float): Dp {
    return (px / Resources.getSystem().displayMetrics.density).dp
}