package com.immanlv.trem.presentation.screens.timetable.util

import android.content.res.Resources


fun Int.toPx(): Float = (this * Resources.getSystem().displayMetrics.density)