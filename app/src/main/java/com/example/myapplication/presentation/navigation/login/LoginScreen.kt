package com.example.myapplication.presentation.navigation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.datastore.core.DataStore
import com.example.myapplication.R
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClicked: (String, String, Boolean) -> Unit,
    credentialsStore:DataStore<Credentials>
) {
    val k = credentialsStore.data.collectAsState(initial = Credentials()).value;
    var email by remember { mutableStateOf(k.uid) }
    var password by remember { mutableStateOf(k.pass) }
    var saveCredentials by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = k){
        email = k.uid
        password = k.pass
        saveCredentials = k.hasCredentials
    }


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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next,keyboardType = KeyboardType.Number),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },

            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done,keyboardType = KeyboardType.Password),
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

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(painter  = image, description)
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
                    .clickable { saveCredentials = !saveCredentials }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLoginClicked(email, password, saveCredentials) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }
    }
}


@Preview
@Composable
fun preview() {
    MyApplicationTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
//            LoginScreen({ _, _, _ ->
//            })
        }
    }
}