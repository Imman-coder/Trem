package com.immanlv.trem.presentation.screens.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.immanlv.trem.presentation.screens.login.components.DropdownMenu


@Composable
fun FakeLoginDialogBox(onDismiss: () -> Unit, login: (Int, Char, String, String, String) -> Unit) {
    val courseList = listOf("BTech")
    val branchList = listOf("CSE", "Ele", "Mech")
    val semList = listOf("1","2","3","4","5","6","7","8")

    var course by remember { mutableIntStateOf(-1) }
    var branch by remember { mutableIntStateOf(-1) }
    var sem by remember { mutableIntStateOf(-1) }

    var isValid by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = course){
        isValid = course!=-1 && branch != -1 && sem != -1
    }
    LaunchedEffect(key1 = branch){
        isValid = course!=-1 && branch != -1 && sem != -1
    }
    LaunchedEffect(key1 = sem){
        isValid = course!=-1 && branch != -1 && sem != -1
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)){
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenu(items = courseList, "Course", course) { course = it }
                Spacer(modifier = Modifier.height(8.dp))
                DropdownMenu(items = branchList, "Branch", branch) { branch = it }
                Spacer(modifier = Modifier.height(8.dp))
                DropdownMenu(items = semList, "Semester", sem) { sem = it }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "")
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = "By this you can only use timetable feature.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .4f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
//                            login( sem+1,courseList[course], branchList[branch])
                        },
                        enabled = isValid
                    ) {
                        Text("Submit")
                    }
                }
            }

        }
    }
}
