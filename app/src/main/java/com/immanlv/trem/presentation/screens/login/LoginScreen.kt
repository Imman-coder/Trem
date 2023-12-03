package com.immanlv.trem.presentation.screens.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.immanlv.trem.R
import com.immanlv.trem.presentation.screens.login.components.FakeLoginDialogBox
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    navController: NavController, viewModel: LoginViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf((email.value.length == 10) && (password.value.length in 8..29)) }
    val saveCredentials = remember { mutableStateOf(true) }
    val passwordVisible = remember { mutableStateOf(false) }
    var loadingBarMessage by remember { mutableStateOf<String?>(null) }
    var showFakeLoginDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    isValid = (email.value.length == 10) && (password.value.length in 8..29)

    LaunchedEffect(key1 = viewModel ){
            email.value = viewModel.credential.uid
            password.value = viewModel.credential.pass
    }


    LaunchedEffect(key1 = true, block = {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginUiEvent.LogInProgress -> {
                    loadingBarMessage = event.message
                }

                is LoginUiEvent.LoggedIn -> {
                    loadingBarMessage = null
//                    navController.navigate(Screen.MainNavGraph.route){
//                        popUpTo(Screen.AuthNavGraph.route){
//                            inclusive = true
//                        }
//                    }
                }

                is LoginUiEvent.ShowToast -> {
                    loadingBarMessage = null
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    })

    fun login() {
        viewModel.onEvent(LoginEvent.Login(email.value, password.value, saveCredentials.value))
    }

    if (!loadingBarMessage.isNullOrBlank()) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = loadingBarMessage!!)
                }


            }
        }
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
            OutlinedTextField(value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Registration number") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Number
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },

                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = {
                    login()
                }),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image =
                        if (passwordVisible.value) painterResource(id = R.drawable.baseline_visibility_24)
                        else painterResource(id = R.drawable.baseline_visibility_off_24)

                    // Please provide localized description for accessibility services
                    val description =
                        if (passwordVisible.value) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(painter = image, description)
                    }
                }

            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Checkbox(
                    checked = saveCredentials.value,
                    onCheckedChange = { saveCredentials.value = !saveCredentials.value },
                    modifier = Modifier.absoluteOffset((-14).dp, 0.dp)
                )
                Text(text = "Save Credential",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .absoluteOffset((-14).dp, 0.dp)
                        .noRippleClickable { saveCredentials.value = !saveCredentials.value })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { login() }, modifier = Modifier.fillMaxWidth(), enabled = isValid
            ) {
                Text("Log In")
            }
        }
        Text(text = "Don't want to login?",
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .combinedClickable(onClick = {},
                    onLongClick = { Log.d("TAG", "LoginScreen: LongClicked") })
//                .noRippleClickable { showFakeLoginDialog = true }
            ,
            style = MaterialTheme.typography.bodyMedium)
        if (showFakeLoginDialog) {
            FakeLoginDialogBox(onDismiss = {
                showFakeLoginDialog = false
            }) { sem, section, branch, batch, program ->
                viewModel.onEvent(LoginEvent.FakeLogin(sem, section, branch, batch, program))
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}