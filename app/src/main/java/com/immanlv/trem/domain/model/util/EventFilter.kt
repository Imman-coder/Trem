package com.immanlv.trem.domain.model.util

import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event

fun List<Event>.filter(type:List<ClassType>):List<Event> =
    this.filter { it.classType in type }
