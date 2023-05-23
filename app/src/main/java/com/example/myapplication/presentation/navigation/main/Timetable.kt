package com.example.myapplication.presentation.navigation.main

import android.content.res.Resources
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme

val timelist = listOf(555,610,665,720,775,825,875,925,975)

@OptIn(ExperimentalTextApi::class)
@Composable
fun TimetableScreen() {

    val backgroundColor: Color = MaterialTheme.colorScheme.background
    val mainColor: Color = MaterialTheme.colorScheme.primary
//    Row(Modifier.verticalScroll(rememberScrollState())) {

//    LazyColumn{
//        items(30){
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "Hello")
//                Canvas(modifier = Modifier
//                    .padding(start = 5.dp)
//                    .height(60.dp)
//                    .width(16.dp)){
//                    drawLine(mainColor, start = Offset(center.x,0f), end = Offset(center.x,size.height))
//                    drawCircle(backgroundColor, radius = 24f ,center=center)
//                    if (it%3==0)
//                        drawCircle(mainColor, radius = 16f ,center=center)
//                    drawCircle(mainColor, radius = 24f ,center=center, style = Stroke(2f))
//                }
//                TimetableCard()
//            }
//
//        }
//    }
    val xOffest = .2f
    val yOffest = .2f
    val cardHeight = 50.dp
    val cardGap = 10.dp
    val circleRadius = 24f
    val gapbetween = cardHeight + cardGap
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0F)
    val textMeasure = rememberTextMeasurer()
    val textStyle =
        MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground)
    val items = 20
//        Box(
//            modifier = Modifier
//                .fillMaxHeight()
//                .padding(horizontal = 10.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//    LazyRow {
//        item {
//            Canvas(
//                modifier = Modifier
////                .fillMaxHeight()
//                .height(IntrinsicSize.Max)
////                    .height(2000.dp)
//                    .width(80.dp)
//            ) {
//
//            }
//        }
//        item {
//            Row {
//                LazyColumn(
//                    Modifier
//                        .padding(top = gapbetween * .55f, start = circleRadius.dp * 2)
//                        .fillMaxHeight(),
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    items(items) {
//                        TimetableCard()
//                    }
//                }
//            }
//        }
//    }
    Row(Modifier.verticalScroll(rememberScrollState())) {
        Canvas(
            modifier = Modifier
                .height(gapbetween * (items + .5f))
                .width(80.dp)
        ) {
            drawLine(
                    mainColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                    pathEffect = pathEffect
                )
                for (x in 0 until items) {
//                    drawCircle(
//                        backgroundColor,
//                        radius = circleRadius,
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5)
//                    )
//                    drawCircle(
//                        mainColor,
//                        radius = circleRadius - (circleRadius * .33f),
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5)
//                    )
//                    drawCircle(
//                        mainColor,
//                        radius = circleRadius,
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5),
//                        style = Stroke(2f)
//                    )
                    drawText(
                        textMeasurer = textMeasure,
                        text = "$x",
                        topLeft = Offset(
                            0f,
                            (gapbetween.toPx() * x) - ((textStyle.fontSize.toPx() * .5f) + gapbetween.toPx()/2 )
                        ),
                        style = textStyle
                    )
                }
        }
        Box(Modifier.padding(top = cardHeight/2 + pxToDp(circleRadius) - 5.dp )) {
            Column {
                for(x in 1 until items) {
                    if(x==3)
                        TimetableCard(
                            Modifier
                                .padding(start = pxToDp(circleRadius) * 2)
                                .height(120.dp))
                    else
                        TimetableCard(Modifier.padding(start = pxToDp(circleRadius) *2))
                }
            }
            Canvas(
                modifier = Modifier
                    .height(gapbetween * (items))
                    .fillMaxWidth()
            ){
                drawLine(
                    Color.Red,
                    start = Offset(0f,100f),
                    end = Offset(size.width,100f),
                    strokeWidth = 4f,

                )
                drawCircle(
                    Color.Red,
                    radius = circleRadius - (circleRadius * .33f),
                    center = Offset(0f, 100f)
                )

            }
        }


    }
}

    @OptIn(ExperimentalTextApi::class)
    @Composable
    fun tableListViewer(){

        val backgroundColor: Color = MaterialTheme.colorScheme.background
        val mainColor: Color = MaterialTheme.colorScheme.primary


        val xOffest = .2f
        val yOffest = .2f
        val cardHeight = 50.dp
        val cardGap = 10.dp
        val circleRadius = 24f
        val gapbetween = cardHeight + cardGap
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0F)
        val textMeasure = rememberTextMeasurer()
        val textStyle =
            MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground)
        val items = 20

        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .height(gapbetween * (items + .5f))
                    .width(80.dp)
            ) {
                drawLine(
                    mainColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                    pathEffect = pathEffect
                )
                for (x in 0 until items) {
//                    drawCircle(
//                        backgroundColor,
//                        radius = circleRadius,
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5)
//                    )
//                    drawCircle(
//                        mainColor,
//                        radius = circleRadius - (circleRadius * .33f),
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5)
//                    )
//                    drawCircle(
//                        mainColor,
//                        radius = circleRadius,
//                        center = Offset(size.width, gapbetween.toPx() * x + gapbetween.toPx()/2 + 5),
//                        style = Stroke(2f)
//                    )
                    drawText(
                        textMeasurer = textMeasure,
                        text = "$x",
                        topLeft = Offset(
                            0f,
                            (gapbetween.toPx() * x) - ((textStyle.fontSize.toPx() * .5f) + gapbetween.toPx() / 2)
                        ),
                        style = textStyle
                    )
                }
            }
        }
    }

    @Composable
    fun TimetableCard(modifier: Modifier=Modifier) {

        Row(
            modifier = modifier
                .padding(vertical = 4.90.dp)
                .height(50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,


        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = "Hellows", color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = "1:10-2:00",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        MyApplicationTheme(darkTheme = true, dynamicColor = false) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TimetableScreen()
            }
        }
    }

    fun pxToDp(px: Float): Dp {
        return (px / Resources.getSystem().displayMetrics.density).dp
    }