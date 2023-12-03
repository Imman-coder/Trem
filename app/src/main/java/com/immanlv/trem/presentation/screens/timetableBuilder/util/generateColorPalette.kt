package com.immanlv.trem.presentation.screens.timetableBuilder.util

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


fun colorDiff(color1: Color, color2: Color): Float {
    val r1 = color1.red
    val g1 = color1.green
    val b1 = color1.blue
    val r2 = color2.red
    val g2 = color2.green
    val b2 = color2.blue
    return sqrt(
        (r1 - r2).toDouble().pow(2.0) + (g1 - g2).toDouble().pow(2.0) + (b1 - b2).toDouble()
            .pow(2.0)
    ).toFloat()
}


fun randomColor(alpha: Int = 255) = Color(
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
    alpha = alpha
)

fun generateColorPalette(
    backgroundColor: Color,
    textColor: Color,
    opacity: Int
): MutableList<Color> {
    val colorTable = mutableListOf<Color>()

    var color: Color
    var bgDiff = 0f
    var textDiff = 0f

    for (x in 0..50) {

//        while (bgDiff < 20 || textDiff < 20) {
        color = randomColor(opacity)
//            bgDiff =colorDiff(color,backgroundColor)
//            textDiff =colorDiff(color,textColor)
//        }
        colorTable.add(color)
    }
    return colorTable
}