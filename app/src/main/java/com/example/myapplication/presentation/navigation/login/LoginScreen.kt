package com.example.myapplication.presentation.navigation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.presentation.navigation.main.noRippleClickable
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClicked: (String, String, Boolean) -> Unit,
    onFakeLogin: (Int,String,String)->Unit
) {
    var email by remember { mutableStateOf("2101229079") }
    var password by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(false) }
    var saveCredentials by remember { mutableStateOf(true) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showFakeLoginDialog by remember { mutableStateOf(false) }

    LaunchedEffect(email) {
        isValid = email.length == 10 && password.length > 7 && password.length < 30
    }

    LaunchedEffect(password) {
        isValid = email.length == 10 && password.length > 7 && password.length < 30
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Image(
//                painter = painterResource(R.drawable.ic_login),
//                contentDescription = "Logo",
//                modifier = Modifier.size(128.dp)
//            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registration number") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },

                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = {
                    onLoginClicked(email, password, saveCredentials)
                }),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else painterResource(id = R.drawable.baseline_visibility_off_24)

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, description)
                    }
                }

            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Checkbox(
                    checked = saveCredentials,
                    onCheckedChange = { saveCredentials = !saveCredentials },
                    modifier = Modifier.absoluteOffset((-14).dp, 0.dp)
                )
                Text(
                    text = "Save Credential",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .absoluteOffset((-14).dp, 0.dp)
                        .noRippleClickable { saveCredentials = !saveCredentials }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLoginClicked(email, password, saveCredentials) },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid
            ) {
                Text("Log In")
            }
        }
        Text(text = "Don't want to login?",
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .noRippleClickable { showFakeLoginDialog = true },
            style = MaterialTheme.typography.bodyMedium
        )
        if (showFakeLoginDialog){
            FakeLoginBox(onDismiss = { showFakeLoginDialog=false }, login = onFakeLogin)
        }
    }
}


@Composable
private fun FakeLoginBox(onDismiss: () -> Unit, login: (Int,String,String,) -> Unit) {
    val courseList = listOf("BTech")
    val branchList = listOf("CSE", "Ele", "Mech")
    val semList = listOf("1","2","3","4","5","6","7","8")

    var course by remember { mutableStateOf(-1) }
    var branch by remember { mutableStateOf(-1) }
    var sem by remember { mutableStateOf(-1) }

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
                            login( sem+1,courseList[course], branchList[branch])
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(items: List<String>,lable:String , selected: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = if(selected!=-1) items[selected] else "-",
                label = { Text(text = lable) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed() { index,item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun Preview() {
    MyApplicationTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
//            LoginScreen ({ _, _, _ -> },{_, _, _ -> })
            FakeLoginBox(onDismiss = { /*TODO*/ }) {_,_,_->
            }
//            InputDialog(onSubmit = { _, _, _ -> })
        }
    }
}