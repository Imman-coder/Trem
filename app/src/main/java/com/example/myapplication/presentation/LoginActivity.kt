package com.example.myapplication.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.navigation.login.LoginScreen
import com.example.myapplication.presentation.navigation.login.LoginViewModel
import com.example.myapplication.presentation.navigation.login.LoginViewModel.LoginUiState
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.uiState.value.loggedInAs == null
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()
                }
            }
        }
    }

    @Composable
    private fun Main() {

//        val viewModel: LoginViewModel = viewModel()
        val uiState = viewModel.uiState.value


        //Handles the UI state when the user is logged in as a fake user.
        if (uiState.loggedInAs == LoginUiState.LoggedInUser.Fake) {
            val intent = Intent(this@LoginActivity, FakeMainActivity::class.java)
            startActivity(intent)
            finish()
        }


        //Handles the UI state when the actual user is logged in.
        if (uiState.loggedInAs == LoginUiState.LoggedInUser.Original) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Show Error message when any error occurred.
        when (uiState.error?.error) {
            LoginException.Error.NetworkError -> showToast("Network Error")
            LoginException.Error.InvalidCredentials -> showToast("Invalid Credentials")
            LoginException.Error.NoInternet -> showToast("No internet connection")
            else -> {}
        }


        Box {

            //Show Login screen if not logged in
            LoginScreen(
                onLoginClicked = viewModel::login,
                onFakeLogin = viewModel::setupFakeUser
            )


            //Show Progress Bar when logging in
            if (uiState.isLogging) LoggingInProgressDialog()
        }
    }

    @Composable
    fun LoggingInProgressDialog() {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Logging in...", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    @Composable
    @Preview
    fun Preview() {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoggingInProgressDialog()
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
