package com.example.myapplication.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.navigation.login.LoginScreen
import com.example.myapplication.presentation.navigation.login.SplashScreen
import com.example.myapplication.presentation.navigation.main.mainViewModel
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {


    private val model: mainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme() {
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
        val creds =
            model.credentialStore.data.collectAsState(initial = Credentials(uid = "nothing")).value

        val loginStatus = remember {
            mutableStateOf(true)
        }
        LaunchedEffect(key1 = creds) {
//                            delay(8000)
            if (creds.uid != "nothing") {
                if (creds.hasCredentials) {
                    val i = Intent(this@LoginActivity, MainActivity2::class.java)
                    startActivity(i)
                    finish()
                } else {
                    loginStatus.value = false
                }
            }
        }
        if (loginStatus.value) {
            SplashScreen()
        } else {
            LoginScreen(onLoginClicked = { uid, pass, b ->
                CoroutineScope(IO).launch {
                    try {
                        makeToast("Logging in..")
                        model.updateProfile(uid, pass)
                        if (b) model.setCredentials(uid, pass)
                        makeToast("Logged In")
                        val i = Intent(this@LoginActivity, MainActivity2::class.java)
                        startActivity(i)
                        finish()
                    } catch (e: LoginException) {
                        makeToast(e.toastMessage)
                    }
                }
            }, model.credentialStore)
        }
    }

    private suspend fun makeToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    @Preview
    fun Preview() {
        Main()
    }


}

fun isInternetAvailable(): Boolean {
    return try {
        val ipAddr = InetAddress.getByName("www.google.com")
        !ipAddr.equals("")
    } catch (e: Exception) {
        false
    }
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    return false
}